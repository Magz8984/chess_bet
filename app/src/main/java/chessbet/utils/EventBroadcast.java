package chessbet.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import chessbet.api.AccountAPI;
import chessbet.domain.Account;
import chessbet.domain.User;

public class EventBroadcast {
    private static EventBroadcast INSTANCE = new EventBroadcast();
    private List<UserUpdate> userUpdateObservers = new ArrayList<>();
    private List<UserLoaded> userLoadedObservers = new ArrayList<>();
    private List<AccountUpdated> accountUpdatedObservers = new ArrayList<>();
    private List<AccountUserUpdate> accountUserUpdatedObservers = new ArrayList<>();


    private EventBroadcast(){}

    public void addUserUpdateObserver(UserUpdate userUpdate){
        this.userUpdateObservers.add(userUpdate);
    }

    public void addUserLoadedObserver(UserLoaded userLoaded){
        this.userLoadedObservers.add(userLoaded);
    }

    public void addAccountUpdated(AccountUpdated accountUpdated){
        this.accountUpdatedObservers.add(accountUpdated);
    }

    public void addAccountUserUpdated(AccountUserUpdate accountUserUpdate){
        this.accountUserUpdatedObservers.add(accountUserUpdate);
    }

    public void broadcastUserUpdate(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        for (UserUpdate observer: userUpdateObservers) {
            observer.onFirebaseUserUpdate(user);
        }
    }

    public void broadcastUserLoaded(){
        for (UserLoaded observer: userLoadedObservers) {
            observer.onUserLoaded();
        }
    }

    public void broadCastAccountUpdate(){
        for (AccountUpdated accountUpdated : accountUpdatedObservers){
            accountUpdated.onAccountUpdated(AccountAPI.get().getCurrentAccount());
        }
    }

    public void broadCastAccountUserUpdate(){
        for (AccountUserUpdate accountUserUpdate : accountUserUpdatedObservers){
            accountUserUpdate.onAccountUserUpdate(AccountAPI.get().getCurrentUser());
        }
    }

    public static EventBroadcast get(){
        return INSTANCE;
    }


    public interface UserUpdate{
        void onFirebaseUserUpdate(FirebaseUser user);
    }

    public interface AccountUserUpdate {
        void onAccountUserUpdate(User user);
    }

    public interface UserLoaded{
        void onUserLoaded();
    }

    public interface AccountUpdated{
        void onAccountUpdated(Account account);
    }
}
