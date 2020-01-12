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
import java.util.Date;
import java.util.List;
import java.util.Objects;

import chessbet.domain.Challenge;
import chessbet.domain.Constants;
import chessbet.domain.MatchRange;
import chessbet.domain.MatchType;
import chessbet.domain.MatchableAccount;
import chessbet.domain.User;
import chessbet.services.MatchListener;

public class ChallengeAPI {
    private static ChallengeAPI INSTANCE = new ChallengeAPI();
    private static String CHALLENGE_COLLECTION = "challenges";
    private ChallengeHandler challengeHandler;
    private Challenge currentChallenge = null;
    private User lastChallengedUser;
    private DocumentReference currentChallengeReference;
    private Challenge challenge;
    private MatchRange matchRange;
    private boolean isLoaded = false;
    private boolean isOnChallenge = false;
    private FirebaseFirestore db;
    private ChallengeAccepted challengeAccepted;
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

    private void sendChallenge(Challenge challenge){
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
     * Used to make sure matchable account is created before a challenge is created
     * @param accountOwner the account challenged
     * @param challengeSent Callback
     */
    private void setMatchableAccountListener(String accountOwner, ChallengeSent challengeSent){
        // Delete all challenges
        MatchListener matchListener = new MatchListener() {
            @Override
            public void onMatchMade(MatchableAccount matchableAccount) {
                if(challengeAccepted  != null){
                    challengeAccepted.onChallengeAccepted(matchableAccount);
                }
            }

            /**
             * Challenge created after matchable account is created
             */
            @Override
            public void onMatchableCreatedNotification() {
                MatchAPI.get().getAccount();
                Challenge challenge = createFriendChallenge();
                currentChallenge = challenge;
                // Delete all challenges
                deleteAllChallenges(() -> db.collection(CHALLENGE_COLLECTION).add(challenge).addOnCompleteListener(task -> {
                    if (task.isComplete()) {
                        challenge.setId(Objects.requireNonNull(task.getResult()).getId());
                        currentChallengeReference = task.getResult();
                        currentChallengeId = challenge.getId();
                        currentChallenge = challenge;
                        setChallengeByAccount(accountOwner, currentChallenge.getId(), challengeSent);
                    } else {
                        challengeSent.onChallengeNotSent();
                    }
                }));
            }

            @Override
            public void onMatchError() {

            }
        };
        MatchAPI.get().setMatchListener(matchListener);
    }

    public void setCurrentChallengeId(String currentChallengeId) {
        this.currentChallengeId = currentChallengeId;
    }

    private Challenge createFriendChallenge(){
        Challenge challenge = new Challenge();
        challenge.setTimeStamp(System.currentTimeMillis());
        challenge.setDateCreated(new Date().toString());
        challenge.setDuration(Constants.DEFAULT_MATCH_DURATION); // TODO Set Custom Duration
        challenge.setMatchType(MatchType.PLAY_ONLINE);
        challenge.setOwner(AccountAPI.get().getCurrentUser().getUid());
        challenge.setEloRating(AccountAPI.get().getCurrentAccount().getElo_rating());
        challenge.setFriendly(true); // Ensures match goes to the right requester
        challenge.setAccepted(false);
        return challenge;
    }

    public void challengeAccount(String accountOwner, ChallengeSent challengeSent){
        isOnChallenge = true;
        setMatchableAccountListener(accountOwner, challengeSent);

        MatchableAccount matchableAccount = new MatchableAccount();
        matchableAccount.setOwner(AccountAPI.get().getCurrentUser().getUid());
        matchableAccount.setMatch_type(MatchType.PLAY_ONLINE.toString());
        matchableAccount.setDuration(Constants.DEFAULT_MATCH_DURATION);
        matchableAccount.setElo_rating(AccountAPI.get().getCurrentAccount().getElo_rating());
        MatchAPI.get().createUserMatchableAccountImplementation(matchableAccount);
    }

    public DocumentReference getCurrentChallengeReference() {
        return currentChallengeReference;
    }

    /**
     * @param accountOwner Account Owner UID
     * @param challengeId Set challenge Id
     */
    private void setChallengeByAccount(String accountOwner, String challengeId, ChallengeSent challengeSent){
        AccountAPI.get().getAccount(accountOwner, account -> {
            long time = currentChallenge.getTimeStamp();
            if((account.getCurrent_challenge_timestamp() < (time - Constants.MAX_MATCHING_DURATION)) ||
                    (account.getCurrent_challenge_timestamp() <= 0)){ // Timer lapsed or no challenge found
                if(challengeId != null){ // Make sure we don't send nulls
                    account.setCurrent_challenge_id(challengeId);
                    account.setCurrent_challenge_timestamp(System.currentTimeMillis());
                    // Update account with current challenge data
                    AccountAPI.get().updateAccount(account);
                    challengeSent.onChallengeSent();
                } else {
                    challengeSent.onChallengeNotSent();
                }
            } else {
                challengeSent.onChallengeNotSent();
            }
        });
    }

    public void setLastChallengedUser(User lastChallengedUser) {
        this.lastChallengedUser = lastChallengedUser;
    }


    public void getChallenge(String currentChallengeId, ChallengeReceived challengeReceived){
        db.collection(CHALLENGE_COLLECTION).document(currentChallengeId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult() != null){
                Challenge challenge = task.getResult().toObject(Challenge.class); // Challenge From Account
                challengeReceived.onChallengeReceived(challenge);
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

    public void getChallenge(){
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
                             if(isWithinRange(candidateChallenge) && !candidateChallenge.isFriendly()){
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

    public void acceptChallenge(String challengeId){
        db.collection(CHALLENGE_COLLECTION).document(challengeId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult() != null){
                Challenge challenge = task.getResult().toObject(Challenge.class);
                DocumentReference documentReference = task.getResult().getReference();
                db.runTransaction(transaction -> {
                    DocumentSnapshot documentSnapshot = transaction.get(documentReference);
                    this.currentChallengeId = documentReference.getId();
                    currentChallenge = challenge;

                    transaction.update(documentReference, "accepted", true);
                    transaction.update(documentReference, "requester", AccountAPI.get().getCurrentAccount().getOwner());
                  return documentSnapshot; // For promise completion
                });
            }
        });
    }

    public void setChallengeAccepted(ChallengeAccepted challengeAccepted) {
        this.challengeAccepted = challengeAccepted;
    }

    /**
     * Ensures match happens between people who choose the same rating
     * @param challenge challenge from query
     * @return challenge is within range
     */
    private boolean isWithinRange(Challenge challenge){
        return ((challenge.getEloRating() >= this.challenge.getMinRating() &&  challenge.getEloRating() <= this.challenge.getMaxRating())
                && (this.challenge.getEloRating() >= challenge.getMinRating() && this.challenge.getEloRating() <= challenge.getMaxRating()))
                && (challenge.getTimeStamp() > (this.challenge.getTimeStamp() - 40000));
    }

    /**
     * Makes sure to delete any previously created challenge before sending another challenge
     * @param challenge Created challenge
     */
    public void  deleteSendChallenge(Challenge challenge){
        user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection(CHALLENGE_COLLECTION).whereEqualTo("owner", user.getUid()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if (task.getResult() != null){
                    if(task.getResult().size() != 0){
                        task.getResult().getDocuments().get(0).getReference().delete().addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()){
                                sendChallenge(challenge);
                            }
                        });
                    } else {
                        sendChallenge(challenge);
                    }
                }
            } else {
                sendChallenge(challenge);
            }
        });
    }

    public boolean isCurrentChallengeValid (){
        if(currentChallenge == null){
            return false;
        }
        long time =  System.currentTimeMillis();
        return currentChallenge.getTimeStamp() > (time - Constants.MAX_MATCHING_DURATION);
    }

    public interface ChallengeHandler{
        void challengeSent(String id);
        void challengeFound(String id);
        void challengeNotFound();
    }

    public interface ChallengeReceived{
        void onChallengeReceived(Challenge challenge);
    }

    public interface ChallengeSent{
        void onChallengeSent();
        void onChallengeNotSent();
    }

    public interface DeleteChallenge {
        void onChallengeDeleted();
    }

    public interface ChallengeAccepted {
        void onChallengeAccepted(MatchableAccount matchableAccount);
    }

    public User getLastChallengedUser() {
        return lastChallengedUser;
    }

    public void deleteAllChallenges(DeleteChallenge deleteChallange){
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            db.collection(CHALLENGE_COLLECTION).whereEqualTo("owner", user.getUid()).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                   for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult()).getDocuments()){
                       documentSnapshot.getReference().delete().addOnCompleteListener(task1 -> {
                            if(!task1.isSuccessful()){
                                Crashlytics.logException(task1.getException());
                            }
                       });
                   }
                }
                deleteChallange.onChallengeDeleted();
                isLoaded = true;
            });
        }
    }

    public void setOnChallenge(boolean onChallenge) {
        isOnChallenge = onChallenge;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public boolean isOnChallenge() {
        return isOnChallenge;
    }
}
