package chessbet.api;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import chessbet.domain.Account;
import chessbet.domain.Constants;
import chessbet.domain.DatabaseMatch;
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
    static String ACCOUNT_COLLECTION = "accounts";
    private static String OWNER_FIELD= "owner";
    private static String TAG = AccountAPI.class.getSimpleName();
    private static AccountAPI INSTANCE = new AccountAPI();
    private FirebaseFirestore db;
    private FirebaseUser user;
    private Account currentAccount;
    private ListenerRegistration listenerRegistration;
    private User currentUser = null;
    private DocumentReference accountReference;
    private UsersReceived usersReceived;

    private AccountAPI() {
         db = FirebaseFirestore.getInstance();
         this.user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public static AccountAPI get(){
        return INSTANCE;
    }

    public void getAccount() {
        try {
            Query query = db.collection(AccountAPI.ACCOUNT_COLLECTION).whereEqualTo(OWNER_FIELD, user.getUid());
            query.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())){
                        accountReference = document.getReference();
                        currentAccount = document.toObject(Account.class);
                        currentAccount.setId(accountReference.getId());
                        accountListener.onAccountReceived(currentAccount);
                        // Overkill we already have a listener on main activity
                        EventBroadcast.get().broadCastAccountUpdate();
                    }
                }
                else {
                    Log.d(TAG, Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()));
                }
            });
        }catch (Exception ex){
            Log.d(TAG, Objects.requireNonNull(ex.getMessage()));
        }
    }

    public void getUser(){
        try {
            assert user != null;
            db.collection(AccountAPI.USER_COLLECTION).document(user.getUid()).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    currentUser = Objects.requireNonNull(task.getResult()).toObject(User.class);
                    updateCurrentUserProfile();
                }
                else {
                    Log.d(TAG, Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()));
                }
            });
        }catch (Exception ex){
            Crashlytics.logException(ex);
        }
    }

    /**
     * Used when using a federated sign in authority like google or facebook
     */
    private void updateCurrentUserProfile(){
        boolean isUpdateAble = false;
        if(currentUser != null){
            if(user.getPhotoUrl() != null && currentUser.getProfile_photo_url().equals(Constants.UTILITY_PROFILE)){
                FirebaseUser user = this.user;
                String path = user.getPhotoUrl().toString();
                currentUser.setProfile_photo_url(path);
                isUpdateAble = true;
            }

            if(user.getDisplayName() != null &&  currentUser.getUser_name().equals("anonymous")){
                currentUser.setUser_name(user.getDisplayName());
                isUpdateAble = true;
            }

            if(isUpdateAble){
                updateUser(currentUser, new UserUpdated() {
                    @Override
                    public void onUserUpdate() {
                        accountListener.onUserReceived(currentUser);
                    }

                    @Override
                    public void onUserUpdateFail(Exception ex) {
                        Crashlytics.logException(ex);
                    }
                });
            }
            accountListener.onUserReceived(currentUser);
        }
    }

    /**
     * Gets the current user by unique identifier
     * @param uid current user uid
     */
    public void getUserByUid(String uid){
        db.collection(AccountAPI.USER_COLLECTION).document(uid).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                currentUser = Objects.requireNonNull(task.getResult()).toObject(User.class);
                accountListener.onUserReceived(currentUser);
            }
            else {
                Log.d(TAG, Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()));
            }
        });
    }

    public void getUserByEmailAddress(String email, UsersReceived onUserReceived){
        db.collection(AccountAPI.USER_COLLECTION).whereEqualTo("email", email).get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult() != null){
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())){
                    onUserReceived.onUserReceived(Collections.singletonList(document.toObject(User.class)));
                }
            } else if (task.getResult() == null) {
                onUserReceived.onUserNotFound();
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

    public FirebaseUser getFirebaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public void setAccountListener(AccountListener accountListener) {
        this.accountListener = accountListener;
    }

    public void setUsersRecived(UsersReceived usersRecived) {
        this.usersReceived = usersRecived;
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
               Log.d("ERROR MESSAGE : ", Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()));
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

    void updateUser(User user, UserUpdated userUpdated){
        db.collection(AccountAPI.USER_COLLECTION).document(user.getUid()).set(user).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                userUpdated.onUserUpdate();
            } else {
                userUpdated.onUserUpdateFail(task.getException());
            }
        });
    }

    public void updateUser(String uid, Map<String,Object> values, UserUpdated userUpdated){
        db.collection(AccountAPI.USER_COLLECTION).document(uid).update(values).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                userUpdated.onUserUpdate();
            } else {
                userUpdated.onUserUpdateFail(task.getException());
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

    /**
     * Used to get an account on user Id
     * @param owner userId
     */
    public void getAccount(String owner, AccountReceived accountReceived){
        Query query = db.collection(AccountAPI.ACCOUNT_COLLECTION).whereEqualTo("owner", owner);
        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())){
                    if (accountReceived != null) {
                        Account account = document.toObject(Account.class);
                        account.setId(document.getId());
                        accountReceived.onAccountReceived(account);
                    }
                }
            }
            else {
                Log.d(TAG, Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()));
            }
        });
    }

    void updateAccount(Account account){
        db.collection(ACCOUNT_COLLECTION).whereEqualTo("owner", account.getOwner()).get().addOnCompleteListener(task -> {
            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())){
                DocumentReference documentReference = document.getReference();
                documentReference.set(account);
            }
        });
    }

    private void setListenerRegistration(){
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

    public void listenToAccountUpdate(){
        if(accountReference == null){
            getAccountReference(documentReference -> {
                accountReference = documentReference;
                setListenerRegistration();
            });
        } else {
            setListenerRegistration();
        }
    }

    /**
     * Get a list of users matching username
     * @param username Text Username
     */
    public void getUsersByUserName(String username){
       List<User> users = new ArrayList<>();
       Query query = db.collection(AccountAPI.USER_COLLECTION).whereEqualTo("user_name", username).limit(10);
       query.get().addOnCompleteListener(task -> {
            for(QueryDocumentSnapshot documentSnapshot: Objects.requireNonNull(task.getResult())){
                users.add(documentSnapshot.toObject(User.class));
            }
            if (users.size() > 0) {
                usersReceived.onUserReceived(users);
            } else {
                usersReceived.onUserNotFound();
            }
       }).addOnFailureListener(e -> usersReceived.onUserNotFound());
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
    public List<DatabaseMatch> assignMatchResults(List<DatabaseMatch> databaseMatches){
        List<DatabaseMatch> newList = new ArrayList<>();
        for (DatabaseMatch databaseMatch : databaseMatches){
            for (MatchDetails matchDetails: currentAccount.getMatches()) {
                if(matchDetails.getMatch_result().getMatchId().equals(databaseMatch.getMatchId())){
                    if (matchDetails.getMatch_result().getMatchStatus().equals(MatchStatus.DRAW)){
                        databaseMatch.setMatchStatus(MatchStatus.DRAW);
                    } else if(matchDetails.getMatch_result().getLoss().equals(currentAccount.getOwner())){
                        databaseMatch.setMatchStatus(MatchStatus.LOSS);
                    } else if(matchDetails.getMatch_result().getGain().equals(currentAccount.getOwner())) {
                        databaseMatch.setMatchStatus(MatchStatus.WON);
                    }
                    databaseMatch.setMatchResult(matchDetails.getMatch_result());
                    newList.add(databaseMatch);
                }
            }
        }
        return newList;
    }

    public boolean isUser(String uid){
        return (currentUser != null) && currentUser.getUid().equals(uid);
    }

    private void getAccountReference(GetDocumentReference getDocumentReference) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            db.collection(AccountAPI.ACCOUNT_COLLECTION).whereEqualTo(OWNER_FIELD, user.getUid()).get().addOnCompleteListener(task -> {
                for(QueryDocumentSnapshot documentSnapshot: Objects.requireNonNull(task.getResult())){
                    currentAccount = documentSnapshot.toObject(Account.class);
                    currentAccount.setId(documentSnapshot.getReference().getId());
                    getDocumentReference.onGetDocumentReference(documentSnapshot.getReference());
                    EventBroadcast.get().broadCastAccountUpdate();
                }
            });
        }
    }

    public interface OnUserReceived {
        void onUserReceived(User user);
    }

    public void setCurrentAccount(Account currentAccount) {
        this.currentAccount = currentAccount;
    }

    public interface UsersReceived {
        void onUserReceived(List<User> user);
        void onUserNotFound();
    }

    public interface UserUpdated{
        void onUserUpdate();
        void onUserUpdateFail(Exception ex);
    }

    public interface GetDocumentReference{
        void onGetDocumentReference(DocumentReference documentReference);
    }

    /**
     * Get other users account
     */
    public interface AccountReceived{
        void onAccountReceived(Account account);
    }
}
