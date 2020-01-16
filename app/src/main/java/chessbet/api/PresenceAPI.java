package chessbet.api;

import androidx.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import chessbet.domain.User;
import chessbet.utils.DatabaseUtil;

/**
 * @author Collins Magondu 13/01/2020
 */
public class PresenceAPI {
    private static PresenceAPI INSTANCE = new PresenceAPI();
    private static final String PRESENCE_PATH = "presence";
    private ValueEventListener userValueEventListener;
    private ValueEventListener customUserListener; // TODO SET THIS AS S LIST OF LISTENERS

    private FirebaseDatabase db;
    private User user;

    private PresenceAPI(){
        db = FirebaseDatabase.getInstance();
    }

    public void setUser(User user){
        this.user = user;
    }

    public static PresenceAPI get() {
        return INSTANCE;
    }

    // Gets to see if user is online
    public void getAmOnline(final UserOnline userOnline){
        userValueEventListener = DatabaseUtil.getConnected().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    setUserOnline();
                    userOnline.onUserOnline(user, true);
                } else {
                    userOnline.onUserOnline(user, false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                userOnline.onUserOnline(user,false);
                Crashlytics.logException(databaseError.toException());
            }
        });
    }

    public void getUserOnline(final User user, UserOnline userOnline){
        customUserListener = db.getReference().child(PRESENCE_PATH).child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    User customUser = (User) dataSnapshot.getValue();
                    userOnline.onUserOnline(customUser, true);
                } else {
                userOnline.onUserOnline(user, false);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                userOnline.onUserOnline(user, false);
            }
        });
    }

    private void setUserOnline(){
        user.setOnline(true);
        user.setLastSeen(System.currentTimeMillis());
        db.getReference().child(PRESENCE_PATH).child(user.getUid()).setValue(user);
        db.getReference().child(PRESENCE_PATH).child(user.getUid()).onDisconnect().removeValue();
    }

//    public void setUserOffline(){
//        user.setOnline(false);
//        db.getReference().child(PRESENCE_PATH).child(user.getUid()).setValue(user);
//    }

    public interface UserOnline{
        void onUserOnline(User user,boolean isOnline);
    }


    /**
     * Remove listeners
     */
    public void stopListening(){
        if(userValueEventListener != null) {
            DatabaseUtil.getConnected().removeEventListener(userValueEventListener);
        }
        if(customUserListener != null){
            DatabaseUtil.getConnected().removeEventListener(customUserListener);
        }
    }
}
