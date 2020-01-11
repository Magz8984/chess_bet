package chessbet.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;


import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ListenerRegistration;

import chessbet.api.AccountAPI;
import chessbet.api.ChallengeAPI;
import chessbet.app.com.BoardActivity;
import chessbet.app.com.R;
import chessbet.domain.Account;
import chessbet.domain.Challenge;
import chessbet.domain.Constants;
import chessbet.domain.User;

public class ChallengeService extends Service{
    private User challengerUser;
    private Account challengerAccount;
    private ListenerRegistration listenerRegistration;
    private ListenerRegistration challengeListener;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        listenToAccountChallenge();
        listenToChallengeReceived();
        Log.d(ChallengeService.class.getSimpleName(),"STARTED");
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy(){
        if(listenerRegistration != null){
            listenerRegistration.remove();
        }

        if(challengeListener != null){
            challengeListener.remove();
        }
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
     * Listen to account for new challenge
     */
    private void listenToAccountChallenge(){
         listenerRegistration = AccountAPI.get().getAccountReference().addSnapshotListener((documentSnapshot, e) -> {
            if(e != null){
                Crashlytics.logException(e);
            } else if (documentSnapshot != null) {
                Account account = documentSnapshot.toObject(Account.class);
                assert account != null;
                if(account.getCurrent_challenge_timestamp() > (System.currentTimeMillis() - 40000)){
                    ChallengeAPI.get().getChallenge(account.getCurrent_challenge_id(), challenge -> AccountAPI.get().getAUser(challenge.getOwner(), user -> {
                        ChallengeAPI.get().setOnChallenge(true);
                        challengerAccount = account;
                        challengerUser = user;
                        startChallengeNotification(challengerUser, challengerAccount);
                    }));
                }
            }
        });
    }

    private void listenToChallengeReceived(){
        DocumentReference documentReference = ChallengeAPI.get().getCurrentChallengeReference();
        if(documentReference != null){
           challengeListener =  documentReference.addSnapshotListener((documentSnapshot, e) -> {
               assert documentSnapshot != null;
               Challenge challenge = documentSnapshot.toObject(Challenge.class);
               if(challenge != null){
                   if(challenge.isAccepted()){
                       startChallengeAcceptedNotification();
                   }
               }
            });
        }
    }

    /**
     * Notify user on they have been challenged
     */
    private void startChallengeNotification(User user, Account account){
        try{
            String channel_id="default";

            Intent intent = new Intent(this, BoardActivity.class);
            ChallengeAPI.get().setCurrentChallengeId(account.getCurrent_challenge_id());

            intent.putExtra(Constants.CHALLENGE_ID, account.getCurrent_challenge_id());

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


            if(Build.VERSION.SDK_INT >=   Build.VERSION_CODES.O){
                channel_id = getNotificationChannel(getResources().getString(R.string.challenge_service),"Background Service");
            }
            Notification notification = new NotificationCompat.Builder( this,channel_id)
                    .setContentTitle("You have been challenged")
                    .setContentText(user.getUser_name() + " has challenged you")
                    .setSmallIcon(R.drawable.chesslogo)
                    .setContentIntent(contentIntent)
                    .setPriority(1)
                    .build();
            startForeground(1, notification);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void startChallengeAcceptedNotification(){
        try{
            String channel_id = "default";
            if(Build.VERSION.SDK_INT >=   Build.VERSION_CODES.O){
                channel_id = getNotificationChannel(getResources().getString(R.string.challenge_service),"Background Service");
            }
            Notification notification = new NotificationCompat.Builder( this,channel_id)
                    .setContentTitle("Your challenge is accepted")
                    .setContentText(ChallengeAPI.get().getLastChallengedUser().getUser_name() + " has accepted your challenge")
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
