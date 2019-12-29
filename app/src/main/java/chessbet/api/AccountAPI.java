package chessbet.api;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import chessbet.domain.Account;
import chessbet.domain.Match;
import chessbet.domain.MatchDetails;
import chessbet.domain.MatchStatus;
import chessbet.domain.Puzzle;
import chessbet.domain.User;
import chessbet.services.AccountListener;
import chessbet.services.PuzzleListener;
import chessbet.services.UserListener;
import chessbet.utils.EventBroadcast;

/**
 * @author Collins Magondu
 */
public class AccountAPI {
    private static String USER_COLLECTION = "users";
    private AccountListener accountListener;
    private UserListener userListener;
    private PuzzleListener puzzleListener;
    private static String ACCOUNT_COLLECTION = "accounts";
    private static String TAG = AccountAPI.class.getSimpleName();
    private static AccountAPI INSTANCE = new AccountAPI();
    private FirebaseFirestore db;
    private FirebaseUser user;
    private Account currentAccount;
    private ListenerRegistration listenerRegistration;
    private User currentUser = null;
    private DocumentReference accountReference;

    private AccountAPI() {
         db = FirebaseFirestore.getInstance();
    }

    public static AccountAPI get(){
        return INSTANCE;
    }

    public void getAccount() {
        try {
            Query query = db.collection(AccountAPI.ACCOUNT_COLLECTION).whereEqualTo("owner", user.getUid());
            query.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())){
                        accountReference = document.getReference();
                        currentAccount = document.toObject(Account.class);
                        accountListener.onAccountReceived(currentAccount);
                        // Overkill we already have a listener on main activity
                        EventBroadcast.get().broadCastAccountUpdate();
                    }
                }
                else {
                    Log.d(TAG, Objects.requireNonNull(task.getException()).getMessage());
                }
            });
        }catch (Exception ex){
            Log.d(TAG, ex.getMessage());
        }
    }

    public void getUser(){
        db.collection(AccountAPI.USER_COLLECTION).document(user.getUid()).get().addOnCompleteListener(task -> {
           if(task.isSuccessful()){
               currentUser = Objects.requireNonNull(task.getResult()).toObject(User.class);
               accountListener.onUserReceived(currentUser);
           }
           else {
               Log.d(TAG, Objects.requireNonNull(task.getException()).getMessage());
           }
        });
    }

    public void setUser(FirebaseUser user){
        this.user = user;
    }

    public Account getCurrentAccount() {
        return currentAccount;
    }

    public void setUserListener(UserListener userListener) {
        this.userListener = userListener;
    }

    public User getCurrentUser() { return currentUser; }

    public void setAccountListener(AccountListener accountListener) {
        this.accountListener = accountListener;
    }

    public DocumentReference getUserPath(){
        return db.collection(AccountAPI.USER_COLLECTION).document(user.getUid());
    }

    public void setPuzzleListener(PuzzleListener puzzleListener) {
        this.puzzleListener = puzzleListener;
    }

    public void sendPuzzle(Puzzle puzzle){
        db.collection("puzzles").add(puzzle).addOnCompleteListener(task -> {
           if(task.isSuccessful()){
               puzzleListener.onPuzzleSent(true);
           }
           else{
               puzzleListener.onPuzzleSent(false);
               Log.d("ERROR MESSAGE : ", Objects.requireNonNull(task.getException()).getMessage());
           }
       });
    }

    public void updateUser(){
        db.collection(AccountAPI.USER_COLLECTION).document(user.getUid()).set(currentUser).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                userListener.onUserUpdated(true);
            } else {
                userListener.onUserUpdated(false);
            }
        });
    }

    public void updateAccount(){
        accountReference.set(currentAccount).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                accountListener.onAccountUpdated(true);
            } else {
                accountListener.onAccountUpdated(false);
            }
        });
    }

    public void listenToAccountUpdate(){
        listenerRegistration = accountReference.addSnapshotListener((documentSnapshot, e) -> {
           if(e !=null){
               Crashlytics.logException(e);
           }

           if(documentSnapshot != null && documentSnapshot.exists()){
               Account account = documentSnapshot.toObject(Account.class);
               if (account!= null){
                   if(currentAccount.getElo_rating() != account.getElo_rating()){
                       listenerRegistration.remove();
                   }
                   currentAccount = account;
                   EventBroadcast.get().broadCastAccountUpdate();
               }

//               listenerRegistration.remove();
           }
       });
    }


    public void getAUser(String uid, OnUserReceived onUserReceived){
        db.collection(AccountAPI.USER_COLLECTION).document(uid).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                onUserReceived.onUserReceived(Objects.requireNonNull(task.getResult()).toObject(User.class));
            } else {
                onUserReceived.onUserReceived(null);
            }
        });
    }

    /** Results are got from firestore **/
    public List<Match> assignMatchResults(List<Match> matches){
        List<Match> newList = new ArrayList<>();
        for (Match match : matches){
            for (MatchDetails matchDetails: currentAccount.getMatches()) {
                if(matchDetails.getMatch_result().getMatchId().equals(match.getMatchId())){
                    if (matchDetails.getMatch_result().getMatchStatus().equals(MatchStatus.DRAW)){
                        match.setMatchStatus(MatchStatus.DRAW);
                    } else if(matchDetails.getMatch_result().getLoss().equals(currentAccount.getOwner())){
                        match.setMatchStatus(MatchStatus.LOSS);
                    } else if(matchDetails.getMatch_result().getGain().equals(currentAccount.getOwner())) {
                        match.setMatchStatus(MatchStatus.WON);
                    }
                    match.setMatchResult(matchDetails.getMatch_result());
                    newList.add(match);
                }
            }
        }
        return newList;
    }

    public interface OnUserReceived {
        void onUserReceived(User user);
    }
}
