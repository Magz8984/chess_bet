package chessbet.api;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

import chessbet.domain.Account;
import chessbet.domain.Puzzle;
import chessbet.domain.User;
import chessbet.services.AccountListener;
import chessbet.services.PuzzleListener;
import chessbet.utils.EventBroadcast;

/**
 * @author Collins Magondu
 */
public class AccountAPI {
    private static String USER_COLLECTION = "users";
    private AccountListener accountListener;
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
}
