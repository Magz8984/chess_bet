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
import com.google.firebase.firestore.ListenerRegistration;

import chessbet.api.AccountAPI;
import chessbet.api.ChallengeAPI;
import chessbet.app.com.BoardActivity;
import chessbet.app.com.R;
import chessbet.domain.Account;
import chessbet.domain.Challenge;
import chessbet.domain.Constants;
import chessbet.domain.User;

public class ChallengeService extends Service implements AccountAPI.OnUserReceived {
    private Account challengerAccount;
    private Challenge currentChallenge;
    private ListenerRegistration listenerRegistration;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        listenToAccountChallenge();
        Log.d(ChallengeService.class.getSimpleName(),"STARTED");
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(listenerRegistration != null){
            listenerRegistration.remove();
        }
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
        try {
            listenerRegistration = AccountAPI.get().getAccountReference().addSnapshotListener((documentSnapshot, e) -> {
                if(e != null){
                    Crashlytics.logException(e);
                } else if (documentSnapshot != null) {
                    Account account = documentSnapshot.toObject(Account.class);
                    assert account != null;
                    if(account.getCurrent_challenge_timestamp() > (System.currentTimeMillis() - 40000)){
                        ChallengeAPI.get().getChallenge(account.getCurrent_challenge_id(), challenge -> {
                            challengerAccount = account;
                            currentChallenge = challenge;
                            if(Challenge.isAccepted(challenge)){
                                AccountAPI.get().getAUser(challenge.getRequester(), this);
                            } else {
                                AccountAPI.get().getAUser(challenge.getOwner(), this);
                            }
                        });
                    }
                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Notify user on they have been challenged
     */
    private void startChallengeNotification(User user, Account account, Challenge challenge){
        try{
            String channel_id="default";

            Intent intent = new Intent(this, BoardActivity.class);
            ChallengeAPI.get().setCurrentChallengeId(account.getCurrent_challenge_id());

            intent.putExtra(Constants.CHALLENGE_ID, account.getCurrent_challenge_id());
            intent.putExtra(Constants.IS_CHALLENGER, isChallengeOwner(challenge));
            intent.putExtra(Constants.CHALLENGER, challenge.getOwner());

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


            if(Build.VERSION.SDK_INT >=   Build.VERSION_CODES.O){
                channel_id = getNotificationChannel(getResources().getString(R.string.default_notification_channel_id),"Background Service");
            }
            Notification notification = new NotificationCompat.Builder( this,channel_id)
                    .setContentTitle(getContentTitle(challenge))
                    .setContentText(getContextText(challenge, user))
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

    private String getContentTitle(Challenge challenge){
        if(!ChallengeAPI.get().isChallengeOwner(challenge)){
            return "You have been challenged";
        } else {
            return "Your challenge has been accepted";
        }
    }

    private String getContextText(Challenge challenge,User user){
        if(!ChallengeAPI.get().isChallengeOwner(challenge)){
            return user.getUser_name() + " has challenged you" ;
        } else {
            return "Challenge accepted";
        }
    }

    /**
     * Check is the current user is the owner of the challenge
     * @param challenge Evaluated challenge
     * @return boolean value
     */
    private boolean isChallengeOwner(Challenge challenge){
        return ChallengeAPI.get().isChallengeOwner(challenge);
    }

    @Override
    public void onUserReceived(User user) {
        ChallengeAPI.get().setOnChallenge(true);
        startChallengeNotification(user, challengerAccount,currentChallenge);
    }
}
