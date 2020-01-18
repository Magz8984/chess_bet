package chessbet.api;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;

import chessbet.domain.User;

public class NotificationAPI {
    private static NotificationAPI INSTANCE = new NotificationAPI();

    public static NotificationAPI get(){
        return INSTANCE;
    }

    public void getNotificationToken(final TokenRetrieved tokenRetrieved){
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult() != null){
                tokenRetrieved.onNotificationTokenReceived(task.getResult().getToken());
            } else {
                tokenRetrieved.onNotificationTokenErrorReceived(task.getException());
            }
        });
    }

    public void updateUserToken(final String token, User user){
        AccountAPI.get().getAUser(user.getUid(), user1 -> {
            user1.setFcmToken(token);
            AccountAPI.get().updateUser(user1, new AccountAPI.UserUpdated() {
                @Override
                public void onUserUpdate() {
                    Log.d(NotificationAPI.class.getSimpleName(), "Token Updated " + token);
                }

                @Override
                public void onUserUpdateFail(Exception e) {
                    Crashlytics.logException(e);
                }
            });
        });
    }


    public interface TokenRetrieved {
        void onNotificationTokenReceived(String token);
        void onNotificationTokenErrorReceived(Exception e);
    }
}
