package chessbet.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

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
import chessbet.app.com.MainActivity;
import chessbet.app.com.R;

public class FCMService extends FirebaseMessagingService  implements  AccountAPI.UserUpdated{
    FirebaseUser user;
    @Override
    public void onCreate() {
        super.onCreate();
        user = FirebaseAuth.getInstance().getCurrentUser();
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
        try {
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
                    .setSound(defaultSoundUri)
                    .setPriority(1)
                    .build();
            startForeground(1, notification);
        }catch (Exception ex){
            Crashlytics.logException(ex);
        }
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

    @Override
    public void onUserUpdateFail(Exception ex) {
        Crashlytics.logException(ex);
    }
}
