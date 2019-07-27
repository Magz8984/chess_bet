package chessbet.services;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import chessbet.api.MatchAPI;
import chessbet.app.com.BoardActivity;
import chessbet.app.com.R;
import chessbet.domain.MatchableAccount;
import chessbet.utils.DatabaseUtil;

public class MatchService extends Service implements MatchListener {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        MatchAPI matchAPI = new MatchAPI();
        matchAPI.setMatchListener(this);
        matchAPI.getAccount();
    }

    @Override
    public  int  onStartCommand(Intent intent,int flags, int startId){
        Log.d("SERVICE","STARTED");
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

    @Override
    public void onMatch(MatchableAccount matchableAccount) {
        try{
            String channel_id="default";

            if(Build.VERSION.SDK_INT >=   Build.VERSION_CODES.O){
                channel_id = getNotificationChannel("match_service","Background Service");
            }

            Intent target= new Intent(this, BoardActivity.class);
            target.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bundle = new Bundle();
            bundle.putParcelable(DatabaseUtil.matchables,matchableAccount);
            target.putExtras(bundle);

            int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
            PendingIntent intent1 = PendingIntent.getActivity(this,uniqueInt,target,PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new NotificationCompat.Builder( this,channel_id)
                    .setContentTitle("Match")
                    .setContentText("Matched with " + matchableAccount.getOpponent())
                    .setSmallIcon(R.drawable.chesslogo)
                    .setPriority(1)
                    .setContentIntent(intent1)
                    .build();
            startForeground(1, notification);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onMatchError() {

    }
}
