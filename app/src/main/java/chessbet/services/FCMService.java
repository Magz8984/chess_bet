package chessbet.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import chessbet.api.AccountAPI;
import chessbet.api.ChallengeAPI;
import chessbet.app.com.BoardActivity;
import chessbet.app.com.MainActivity;
import chessbet.app.com.R;
import chessbet.domain.Challenge;
import chessbet.domain.Constants;
import chessbet.domain.FCMMessage;
import chessbet.domain.User;

public class FCMService extends FirebaseMessagingService  implements  AccountAPI.UserUpdated, ChallengeAPI.ChallengeReceived, AccountAPI.OnUserReceived {
    private static FCMService INSTANCE;
    private FirebaseUser user;
    private String from;
    private String challengeId;
    private Challenge currentChallenge;
    private NotificationManager notificationManager;
    private Uri uri;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        user = FirebaseAuth.getInstance().getCurrentUser();
        uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Map<String, Object> values = new HashMap<>();
        values.put("fcmToken", s);
        if(user != null){
            AccountAPI.get().updateUser(user.getUid(), values, this);
        }
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // Start  notification channel
        String messageType = remoteMessage.getData().get(Constants.MESSAGE_TYPE);
        try {
            if(messageType != null){
                from = remoteMessage.getData().get(Constants.FROM_USER);
                if(messageType.equals(FCMMessage.FCMMessageType.CHALLENGE.toString())){
                    // Handle Challenge Configuration
                    challengeId = remoteMessage.getData().get(Constants.DATA);
                    ChallengeAPI.get().getChallenge(challengeId, this);
                }
            } else {
              configureInformationNotification(remoteMessage);
            }
        }catch (Exception ex){
            Crashlytics.logException(ex);
        }
    }

    private void configureInformationNotification(RemoteMessage remoteMessage){
        String channelId = getResources().getString(R.string.default_notification_channel_id);
        Intent intent  = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            channelId = getNotificationChannel(getResources().getString(R.string.default_notification_channel_id), FCMService.class.getSimpleName());
        }

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(Objects.requireNonNull(remoteMessage.getNotification()).getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setSmallIcon(R.drawable.chesslogo)
                .setContentIntent(pendingIntent)
                .setTimeoutAfter(-1)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(1)
                .build();
        startForeground(1, notification);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getNotificationChannel(String id, String name) {
        NotificationChannel notificationChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(notificationChannel);
        return  id;
    }

    @Override
    public void onUserUpdate() {

    }

    public void stop(){
        this.stopForeground(false);
    }

    public static FCMService get(){
        return INSTANCE;
    }

    @Override
    public void onUserUpdateFail(Exception ex) {
        Crashlytics.logException(ex);
    }

    @Override
    public void onChallengeReceived(Challenge challenge) {
        currentChallenge = challenge;
            if(Challenge.isAccepted(challenge)){
                AccountAPI.get().getAUser(challenge.getRequester(), this);
            } else {
                AccountAPI.get().getAUser(challenge.getOwner(), this);
            }
    }

    @Override
    public void onUserReceived(User user) {
        ChallengeAPI.get().setOnChallenge(true);
        createChallengeNotification(currentChallenge);
    }

    private String getContentTitle(Challenge challenge){
        if(!ChallengeAPI.get().isChallengeOwner(challenge)){
            return "You have been challenged";
        } else {
            return "Your challenge has been accepted";
        }
    }

    private String getContextText(Challenge challenge){
        if(!ChallengeAPI.get().isChallengeOwner(challenge)){
            return from + " has challenged you" ;
        } else {
            return from + " has accepted you challenge";
        }
    }


    private void createChallengeNotification(Challenge challenge){
        // Make sure we do not display two notifications for the same challenge
        if(!ChallengeAPI.get().isNotify()){
            return;
        }

        ChallengeAPI.get().setNotify(false);
        String channel_id;
        Intent intent = new Intent(this, BoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ChallengeAPI.get().setCurrentChallengeId(challengeId);

        intent.putExtra(Constants.CHALLENGE_ID, challengeId);
        intent.putExtra(Constants.IS_CHALLENGER, isChallengeOwner(challenge));
        intent.putExtra(Constants.CHALLENGER, challenge.getOwner());

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(Build.VERSION.SDK_INT >=   Build.VERSION_CODES.O){
            channel_id = getNotificationChannel(getResources().getString(R.string.default_notification_channel_id), FCMService.class.getSimpleName());
            NotificationChannel channel = new NotificationChannel(channel_id, FCMService.class.getSimpleName(), NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLightColor(Color.GRAY);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getNotificationManager().createNotificationChannel(channel);
            Notification.Builder notificationBuilder = new Notification.Builder(getApplicationContext(), channel_id)
                    .setContentTitle(getContentTitle(challenge))
                    .setContentText(getContextText(challenge))
                    .setSmallIcon(R.drawable.chesslogo)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setTimeoutAfter(-1)
                    .setSound(uri)
                    .setPriority(Notification.PRIORITY_HIGH);
            getNotificationManager().notify(101, notificationBuilder.build());
        } else {
            try {
                @SuppressLint({"NewApi", "LocalSuppress"})
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                        .setContentTitle(getContentTitle(challenge))
                        .setContentText(getContextText(challenge))
                        .setSmallIcon(R.drawable.chesslogo)
                        .setContentIntent(contentIntent)
                        .setTimeoutAfter(-1)
                        .setAutoCancel(true)
                        .setSound(uri)
                        .setPriority(Notification.PRIORITY_HIGH);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                assert notificationManager != null;
                notificationManager.notify((int) (System.currentTimeMillis()%10000), notificationBuilder.build());

            }catch (Exception ex){
                Crashlytics.logException(ex);
            }
        }
    }

    private NotificationManager getNotificationManager(){
        if(notificationManager == null){
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    /**
     * Check is the current user is the owner of the challenge
     * @param challenge Evaluated challenge
     * @return boolean value
     */
    private boolean isChallengeOwner(Challenge challenge){
        return ChallengeAPI.get().isChallengeOwner(challenge);
    }

    /**
     * Used when the app is in the background
     */
    public static class FCMBackgroundService{
        private Context context;
        private Bundle bundle;

        public FCMBackgroundService(Context context, Bundle bundle){
             this.bundle = bundle;
             this.context = context;
        }
        /**
         * Finds destination activity for notification data
         */
        public boolean foundDestination(){
            if(bundle != null){
                String messageType = bundle.getString(Constants.MESSAGE_TYPE);
                if(messageType != null){
                    if(messageType.equals(FCMMessage.FCMMessageType.CHALLENGE.toString())){
                        String challengeId = bundle.getString(Constants.DATA);
                        ChallengeAPI.get().getChallenge(challengeId, challenge -> {
                            Intent intent = new Intent(context, BoardActivity.class);
                            intent.putExtra(Constants.CHALLENGE_ID, challengeId);
                            intent.putExtra(Constants.IS_CHALLENGER, ChallengeAPI.get().isChallengeOwner(challenge));
                            intent.putExtra(Constants.CHALLENGER, challenge.getOwner());
                            context.startActivity(intent);
                        });
                    }
                    return true;
                }
            }
            return false;
        }
    }
}
