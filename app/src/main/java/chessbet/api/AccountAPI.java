package chessbet.api;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

import chessbet.domain.Account;
import chessbet.domain.User;
import chessbet.services.AccountListener;

/**
 * @author Collins Magondu
 */
public class AccountAPI {
    private static  String USER_COLLECTION = "users";
    private AccountListener accountListener;
    private static  String ACCOUNT_COLLECTION = "accounts";
    private static String TAG = AccountAPI.class.getSimpleName();
    private static AccountAPI INSTANCE = new AccountAPI();
    private FirebaseFirestore db;
    private FirebaseUser user;
    private Account currentAccount;

    private AccountAPI() {
         db = FirebaseFirestore.getInstance();
    }

    public static AccountAPI get(){
        return INSTANCE;
    }

    public void getAccount() {
        Query query = db.collection(AccountAPI.ACCOUNT_COLLECTION).whereEqualTo("owner", user.getUid());
        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())){
                    currentAccount = document.toObject(Account.class);
                     accountListener.onAccountReceived(currentAccount);
                }
            }
            else {
                Log.d(TAG, Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    public void getUser(){
        db.collection(AccountAPI.USER_COLLECTION).document(user.getUid()).get().addOnCompleteListener(task -> {
           if(task.isSuccessful()){
                   accountListener.onUserReceived(Objects.requireNonNull(task.getResult()).toObject(User.class));
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

    public void setAccountListener(AccountListener accountListener) {
        this.accountListener = accountListener;
    }

    public DocumentReference getUserPath(){
        return  db.collection(AccountAPI.USER_COLLECTION).document(user.getUid());
    }
}
