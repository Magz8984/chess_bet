package chessbet.services;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import chessbet.app.com.R;

public class MatchService extends Service{
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        Log.d("SERVICE","STARTED");
        startNotification();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }


    @RequiresApi(Build.VERSION_CODES.O)
    public String getNotificationChannel(String id, String name){
        NotificationChannel notificationChannel = new NotificationChannel(id,name, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(notificationChannel);
        return  id;
    }

    /**
     * Notify user on the time lapse once a match is made
     */
    private void startNotification(){
        try{
            String channel_id="default";

            if(Build.VERSION.SDK_INT >=   Build.VERSION_CODES.O){
                channel_id = getNotificationChannel("match_service","Background Service");
            }
            Notification notification = new NotificationCompat.Builder( this,channel_id)
                    .setContentTitle("Online DatabaseMatch")
                    .setContentText("DatabaseMatch will end if left idle for 30 seconds")
                    .setSmallIcon(R.drawable.chesslogo)
                    .setPriority(1)
                    .build();
            startForeground(1, notification);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
