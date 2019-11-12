package chessbet.api;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;
import java.util.Objects;

import chessbet.domain.Account;
import chessbet.domain.User;
import chessbet.services.AccountListener;
import chessbet.services.MatchMetricsUpdateListener;

/**
 * @author Collins Magondu
 */
public class AccountAPI {
    private static  String USER_COLLECTION = "users";
    private AccountListener accountListener;
    private MatchMetricsUpdateListener matchMetricsUpdateListener;
    private static  String ACCOUNT_COLLECTION = "accounts";
    private static String TAG = AccountAPI.class.getSimpleName();
    private static AccountAPI INSTANCE = new AccountAPI();
    private FirebaseFirestore db;
    private FirebaseUser user;
    private Account currentAccount;
    private User currentUser = null;
    private DocumentSnapshot accountSnapshot = null;

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
                        currentAccount = document.toObject(Account.class);
                        accountListener.onAccountReceived(currentAccount);
                    }

                    // Only one account is expected
                    accountSnapshot = task.getResult().getDocuments().get(0);
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

    public void setMatchMetricsUpdateListener(MatchMetricsUpdateListener matchMetricsUpdateListener) {
        this.matchMetricsUpdateListener = matchMetricsUpdateListener;
    }

    public void updateAccountMatchDetails(){
        currentAccount.setLast_date_modified(new Date().toString());
        currentAccount.setLast_matchable_time(new Date().getTime());
        if(accountSnapshot != null){
            accountSnapshot.getReference().set(currentAccount).addOnCompleteListener(task -> {
               if(task.isSuccessful()){
                    matchMetricsUpdateListener.onUpdate();
               }
               else {
                   Crashlytics.logException(new RuntimeException("Match Metrics Not Updated"));
               }
            });
        }
    }
}
