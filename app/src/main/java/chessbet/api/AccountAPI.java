package chessbet.api;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

import chessbet.domain.Account;
import chessbet.domain.AccountStatus;
import chessbet.domain.User;
import chessbet.services.AccountListener;

/**
 * @author Collins Magondu
 */
public class AccountAPI {
    private static  String USER_COLLECTION = "users";
    private AccountListener accountListener;
    private static  String ACCOUNT_COLLECTION = "accounts";
    private static AccountAPI INSTANCE = new AccountAPI();
    private FirebaseFirestore db;
    private FirebaseUser user;

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
                     accountListener.onAccountReceived(document.toObject(Account.class));
                }
            }
        });
    }

    public void getUser() {
        db.collection(AccountAPI.USER_COLLECTION).document(user.getUid()).get().addOnCompleteListener(task -> {
           if(task.isSuccessful()){
                   accountListener.onUserReceived(Objects.requireNonNull(task.getResult()).toObject(User.class));
           }
        });
    }

    public void setUser(FirebaseUser user){
        this.user = user;
    }

    public void setAccountListener(AccountListener accountListener) {
        this.accountListener = accountListener;
    }
}
