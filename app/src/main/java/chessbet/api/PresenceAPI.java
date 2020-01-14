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
        DatabaseUtil.connectedRefrence().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    setUserOnline();
                    userOnline.amOnline(true);
                } else {
                    userOnline.amOnline(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                userOnline.amOnline(false);
                Crashlytics.logException(databaseError.toException());
            }
        });
    }

    private void setUserOnline(){
        user.setOnline(true);
        user.setLastSeen(System.currentTimeMillis());
        db.getReference().child(PRESENCE_PATH).child(user.getUid()).setValue(user);
        db.getReference().child(PRESENCE_PATH).child(user.getUid()).onDisconnect().removeValue();
    }

    public void setUserOffline(){
        user.setOnline(false);
        db.getReference().child(PRESENCE_PATH).child(user.getUid()).setValue(user);
    }

    public interface UserOnline{
        void amOnline(boolean isOnline);
    }
}
