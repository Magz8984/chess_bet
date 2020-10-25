package chessbet.services;

/**
 * @author Collins Magondu 21/10/2020
 */

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
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import chessbet.api.AccountAPI;
import chessbet.api.ChallengeAPI;
import chessbet.api.MatchAPI;
import chessbet.app.com.BoardActivity;
import chessbet.app.com.R;
import chessbet.app.com.activities.MainActivity;
import chessbet.domain.Constants;
import chessbet.domain.FCMMessage;
import chessbet.domain.MatchableAccount;
import chessbet.utils.DatabaseUtil;

import static chessbet.Application.getContext;

public class MessagingService extends FirebaseMessagingService implements  AccountAPI.UserUpdated{
    private static MessagingService INSTANCE;
    private NotificationManager notificationManager;
    private FirebaseUser user;
    private Uri uri;

    @Override
    public void onCreate() {
        INSTANCE = this;
        super.onCreate();
//        user = FirebaseAuth.getInstance().getCurrentUser();
        uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String messageType = remoteMessage.getData().get(Constants.MESSAGE_TYPE);
        try {
            if(messageType != null){
                if(messageType.equals(FCMMessage.FCMMessageType.NEW_CHALLENGE.toString())){
                    configureNewChallengeNotification(Objects.requireNonNull(remoteMessage.getData().get(Constants.FROM_USER)),
                            Objects.requireNonNull(remoteMessage.getData().get(Constants.FCM_MESSAGE)));
                } else if (messageType.equals(FCMMessage.FCMMessageType.ACCEPT_CHALLENGE.toString())) {
                    configureAcceptedChallengeNotification(Objects.requireNonNull(remoteMessage.getData().get(Constants.FROM_USER)),
                            Objects.requireNonNull(remoteMessage.getData().get(Constants.FCM_MESSAGE)));
                }
            } else {
                configureInformationNotification(remoteMessage);
            }
        }catch (Exception ex){
            Crashlytics.logException(ex);
        }
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

    private NotificationManager getNotificationManager(){
        if(notificationManager == null){
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    private void  showNotification(@NotNull String title, @NotNull String message, PendingIntent pendingIntent) {
        String channelId = getResources().getString(R.string.default_notification_channel_id);
        // Cancel All Notification
        getNotificationManager().cancelAll();
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int randomNotificationId = new Random().nextInt(10000);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            channelId = getNotificationChannel(getResources().getString(R.string.default_notification_channel_id), FCMService.class.getSimpleName());
            // Send Notification For Android Oreo and above
            NotificationChannel channel = new NotificationChannel(channelId, FCMService.class.getSimpleName(), NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLightColor(Color.GRAY);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getNotificationManager().createNotificationChannel(channel);
            Notification.Builder notificationBuilder = new Notification.Builder(getApplicationContext(), channelId)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.chesslogo)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setTimeoutAfter(-1)
                    .setSound(uri)
                    .setPriority(Notification.PRIORITY_HIGH);
            getNotificationManager().notify(randomNotificationId, notificationBuilder.build());
        } else {
            Notification notification = new NotificationCompat.Builder(getApplicationContext(), channelId)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.chesslogo)
                    .setContentIntent(pendingIntent)
                    .setTimeoutAfter(-1)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setPriority(1)
                    .build();
//            startForeground(102, notification);
            getNotificationManager().notify(randomNotificationId, notification);
        }
    }

    /**
     * Shows information messages that are not triggered by our APIS
     * @param remoteMessage from FCM Servers
     */
    private void configureInformationNotification(RemoteMessage remoteMessage){
        Intent intent  = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        this.showNotification(Objects.requireNonNull(remoteMessage.getNotification()).getTitle(),remoteMessage.getNotification().getBody(),pendingIntent);
    }

    private void configureNewChallengeNotification(@NonNull String from, @NonNull String message) {
            Intent intent  = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(Constants.MESSAGE_TYPE, FCMMessage.FCMMessageType.NEW_CHALLENGE.toString());
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            this.showNotification(from, message ,pendingIntent);
    }

    private void configureAcceptedChallengeNotification(@NonNull String from, @NonNull String message) {
        Intent intent  = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constants.MESSAGE_TYPE, FCMMessage.FCMMessageType.NEW_CHALLENGE.toString());
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        this.showNotification(from, message ,pendingIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getNotificationChannel(String id, String name) {
        NotificationChannel notificationChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(notificationChannel);
        return  id;
    }

    public static boolean startExternalIntentFromFCM(Bundle bundle, Context context){
        if(bundle != null){
            String messageType = bundle.getString(Constants.MESSAGE_TYPE);
            if(messageType != null){
                if(messageType.equals(FCMMessage.FCMMessageType.NEW_CHALLENGE.toString())){
                    Intent intent  = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra(Constants.MESSAGE_TYPE, FCMMessage.FCMMessageType.NEW_CHALLENGE.toString());
                    context.startActivity(intent);
                } else if (messageType.equals(FCMMessage.FCMMessageType.ACCEPT_CHALLENGE.toString())){
                    Intent intent  = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra(Constants.MESSAGE_TYPE, FCMMessage.FCMMessageType.ACCEPT_CHALLENGE.toString());
                    context.startActivity(intent);
                } else {
                    Intent intent  = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra(Constants.MESSAGE_TYPE, FCMMessage.FCMMessageType.INFORMATION.toString());
                    context.startActivity(intent);
                }
                return true;
            }
        }
        return false;
    }

    public static MessagingService get() {
        return INSTANCE;
    }

    @Override
    public void onUserUpdate() {
    }

    @Override
    public void onUserUpdateFail(Exception ex) {
        Crashlytics.logException(ex);
    }
}
