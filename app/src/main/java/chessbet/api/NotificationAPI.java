package chessbet.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import chessbet.domain.User;

public class NotificationAPI {
    private static NotificationAPI INSTANCE = new NotificationAPI();

    public static NotificationAPI get(){
        return INSTANCE;
    }

    public void getNotificationToken(final TokenRetrieved tokenRetrieved){
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(task.isSuccessful() && task.getResult()!= null){
                    tokenRetrieved.onNotificationTokenReceived(task.getResult().getToken());
                } else {
                    tokenRetrieved.onNotificationTokenErrorReceived(task.getException());
                }
            }
        });
    }

    public void updateUserToken(final String token, User user){
        AccountAPI.get().getAUser(user.getUid(), new AccountAPI.OnUserReceived() {
            @Override
            public void onUserReceived(User user) {
                user.setFcmToken(token);
                AccountAPI.get().updateAUser(user, new AccountAPI.UserUpdate() {
                    @Override
                    public void onUserUpdate() {
                        Log.d(NotificationAPI.class.getSimpleName(), "Token Updated " + token);
                    }

                    @Override
                    public void onUserUpdateFail(Exception e) {
                        Crashlytics.logException(e);
                    }
                });
            }
        });
    }


    public interface TokenRetrieved {
        void onNotificationTokenReceived(String token);
        void onNotificationTokenErrorReceived(Exception e);
    }
}
