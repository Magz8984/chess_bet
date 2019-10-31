package chessbet.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class EventBroadcast {
    private static EventBroadcast INSTANCE = new EventBroadcast();
    private List<UserUpdate> userUpdateObservers = new ArrayList<>();
    private List<UserLoaded> userLoadedObservers = new ArrayList<>();

    private EventBroadcast(){}

    public void addUserUpdateObserver(UserUpdate userUpdate){
        this.userUpdateObservers.add(userUpdate);
    }

    public void addUserLoadedObserver(UserLoaded userLoaded){
        this.userLoadedObservers.add(userLoaded);
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


    public static EventBroadcast get(){
        return INSTANCE;
    }


    public interface UserUpdate{
        void onFirebaseUserUpdate(FirebaseUser user);
    }

    public interface UserLoaded{
        void onUserLoaded();
    }
}
