package chessbet.api;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import chessbet.domain.Challenge;
import chessbet.domain.MatchRange;

public class ChallengeAPI {
    private static ChallengeAPI INSTANCE = new ChallengeAPI();
    private static String CHALLENGE_COLLECTION = "challenges";
    private ChallengeHandler challengeHandler;
    private Challenge currentChallenge = null;
    private Challenge challenge;
    private MatchRange matchRange;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private int referenceCounter = 0;
    private String currentChallengeId = null;
    private List<DocumentReference> availableMatches = new ArrayList<>();
    private ChallengeAPI(){
        db = FirebaseFirestore.getInstance();
    }
    public static ChallengeAPI get() {
        return INSTANCE;
    }

    private int getMinEloRating(){
        return AccountAPI.get().getCurrentAccount().getElo_rating() - matchRange.getStartAt();
    }

    private int getMaxEloRating(){
        return AccountAPI.get().getCurrentAccount().getElo_rating() + matchRange.getEndAt();
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public void setMatchRange(MatchRange matchRange) {
        this.matchRange = matchRange;
    }

    public void setChallengeHandler(ChallengeHandler challengeHandler) {
        this.challengeHandler = challengeHandler;
    }

    public void sendChallenge(Challenge challenge){
        db.collection(CHALLENGE_COLLECTION).add(challenge).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if(task.getResult() != null){
                    this.currentChallenge = challenge;
                    this.currentChallengeId = task.getResult().getId();
                    this.challengeHandler.challengeSent(task.getResult().getId());
                }
            }
        });
    }

    /**
     * Deletes challenges created by user
     * To be used when match ends
     */
    public void deleteChallenge(){
        if(currentChallenge != null && currentChallenge.getOwner().equals(AccountAPI.get().getCurrentAccount().getOwner())){
            db.collection(CHALLENGE_COLLECTION).document(currentChallengeId).delete().addOnFailureListener(Crashlytics::logException);
        }
    }

    private void getChallenge(){
        // TODO set time to a universal standard time
        db.collection(CHALLENGE_COLLECTION)
                .whereEqualTo("matchType", challenge.getMatchType())
                .whereEqualTo("duration", challenge.getDuration())
                .whereEqualTo("accepted", false)
                .limit(30)
                .get()
                .addOnCompleteListener(task -> {
                     if (task.isSuccessful() && task.getResult() != null){
                         for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                             Challenge candidateChallenge = documentSnapshot.toObject(Challenge.class);
                             if(isWithinRange(candidateChallenge)){
                                 availableMatches.add(documentSnapshot.getReference());
                             }
                         }

                         db.runTransaction(transaction -> {
                             DocumentReference documentReference = (availableMatches.size() != 0) ?
                                     (availableMatches.size() > referenceCounter ?
                                             availableMatches.get(referenceCounter) : null) : null;
                             if(documentReference != null){
                                 DocumentSnapshot documentSnapshot = transaction.get(documentReference);
                                 Challenge setChallenge = documentSnapshot.toObject(Challenge.class);
                                 if(setChallenge != null && !setChallenge.isAccepted()) {
                                     // We found a challenge
                                     this.currentChallengeId = documentReference.getId();
                                     currentChallenge = setChallenge;

                                     transaction.update(documentReference, "accepted", true);
                                     transaction.update(documentReference, "requester", user.getUid());
                                     // In case we get an error after another member accepts the match iterate though a list of the remaining available candidates
                                     referenceCounter ++;
                                     return documentSnapshot.getId();
                                 }
                                 else {
                                     return null;
                                 }
                             }
                             throw new FirebaseFirestoreException("Not Found", FirebaseFirestoreException.Code.ABORTED);
                         }).addOnSuccessListener(s -> this.challengeHandler.challengeFound(s))
                           .addOnFailureListener(e -> this.challengeHandler.challengeNotFound());
                     }
                });
    }

    /**
     * Ensures match happens between people who choose the same rating
     * @param challenge
     * @return
     */
    private boolean isWithinRange(Challenge challenge){
        return ((challenge.getEloRating() >= this.challenge.getMinRating() &&  challenge.getEloRating() <= this.challenge.getMaxRating())
                && (this.challenge.getEloRating() >= challenge.getMinRating() && this.challenge.getEloRating() <= challenge.getMaxRating()))
                && (challenge.getTimeStamp() > System.currentTimeMillis() - 40000);
    }

    public void getExistingChallenges(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection(CHALLENGE_COLLECTION).whereEqualTo("owner", user.getUid()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if (task.getResult() != null){
                    if(task.getResult().size() != 0){
                        task.getResult().getDocuments().get(0).getReference().delete().addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()){
                                // Make sure to remove previous challenges before adding a new one
                                getChallenge();
                            }
                        });
                    }
                    else {
                        getChallenge();
                    }
                }
                else {
                    getChallenge();
                }
            }
        });
    }

    public interface ChallengeHandler{
        void challengeSent(String id);
        void challengeFound(String id);
        void challengeNotFound();
    }
}
