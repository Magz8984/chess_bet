package chessbet.utils;

import android.graphics.Color;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import chessbet.domain.Player;
import io.github.erehmi.countdown.CountDownTask;
import io.github.erehmi.countdown.CountDownTimers;

public class GameTimer {
    private static GameTimer INSTANCE;
    private Builder builder;
    private CountDownTask blackCountDownTask;
    private CountDownTask whiteCountDownTask;
    private int blackTimeLeft;
    private int whiteTimeLeft;
    private Player currentPlayer;

    private GameTimer(final Builder builder){
        this.builder = builder;
        blackCountDownTask = CountDownTask.create();
        whiteCountDownTask = CountDownTask.create();
    }

    public void setBlackTimeLeft(int blackTimeLeft) {
        this.blackTimeLeft = blackTimeLeft;
    }

    public void setWhiteTimeLeft(int whiteTimeLeft) {
        this.whiteTimeLeft = whiteTimeLeft;
    }


    public static GameTimer get() {
        return INSTANCE;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setBlackGameTimer(){
        blackCountDownTask.cancel();
        blackCountDownTask = CountDownTask.create();
        long targetMilliseconds = CountDownTask.elapsedRealtime() + blackTimeLeft;
        blackCountDownTask.until(builder.txtBlackMoveTimer, targetMilliseconds, 1000, new CountDownTimers.OnCountDownListener() {
            @Override
            public void onTick(View view, long millisUntilFinished) {
                TextView txtBlackTimer = (TextView) view;
                blackTimeLeft = (int) millisUntilFinished;
                txtBlackTimer.setText(timeConverter(millisUntilFinished/1000));
                if(millisUntilFinished / 1000 <= 10 ){
                    txtBlackTimer.setTextColor(Color.RED);
                }
            }

            @Override
            public void onFinish(View view) {
                // Black Lost On Time
                builder.onTimerElapsed.playerTimeLapsed(Player.BLACK);
            }
        });
    }

    public void setBuilder(Builder builder) {
        this.builder = builder;
    }

    public void stopTimer(Player player){
        if(player == Player.BLACK){
            blackCountDownTask.cancel();
            setWhiteGameTimer(); // Restart White with time left
            currentPlayer = Player.WHITE;
        }
        else if (player == Player.WHITE){
            whiteCountDownTask.cancel();
            setBlackGameTimer();
            currentPlayer = Player.BLACK;
        }
    }

    public void stopAllTimers(){
        try{
            if(INSTANCE != null){
                blackCountDownTask.cancel();
                whiteCountDownTask.cancel();
                INSTANCE = null;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void setGameTimer(GameTimer gameTimer){
        INSTANCE = gameTimer;
    }

    public void setWhiteGameTimer(){
        whiteCountDownTask.cancel();
        whiteCountDownTask = CountDownTask.create();
        long targetMilliseconds = CountDownTask.elapsedRealtime() + whiteTimeLeft;
        whiteCountDownTask.until(builder.txtWhiteMoveTimer, targetMilliseconds, 1000, new CountDownTimers.OnCountDownListener() {
            @Override
            public void onTick(View view, long millisUntilFinished) {
                TextView txtBlackTimer = (TextView) view;
                whiteTimeLeft = (int) millisUntilFinished;
                txtBlackTimer.setText(timeConverter(millisUntilFinished/1000));
                if(millisUntilFinished / 1000 <= 10 ){
                    txtBlackTimer.setTextColor(Color.RED);
                }
            }

            @Override
            public void onFinish(View view) {
                // Black Lost On Time
                builder.onTimerElapsed.playerTimeLapsed(Player.WHITE);
            }
        });
    }

    public int getBlackTimeLeft() {
        return blackTimeLeft;
    }

    public int getWhiteTimeLeft() {
        return whiteTimeLeft;
    }

    private static String timeConverter(long seconds){
        long secondsRemaining = seconds % 60;
        String secondsString = Long.toString(secondsRemaining);
         if(secondsRemaining < 10){
             secondsString = "0" + secondsString;
         }
         long minutes = seconds / 60 ;
         if(minutes >= 1){
            return minutes + " : " + secondsString;
         }
         else {
            return "00 : "+ secondsString;
        }
    }

    public void invalidateTimer(){
        blackCountDownTask.cancel();
        whiteCountDownTask.cancel();
    }

    public static class Builder{
        private TextView txtBlackMoveTimer;
        private TextView txtWhiteMoveTimer;
        private OnTimerElapsed onTimerElapsed;

        public Builder setTxtBlackMoveTimer(TextView txtBlackMoveTimer) {
            this.txtBlackMoveTimer = txtBlackMoveTimer;
            return this;
        }

        public Builder setOnTimerElapsed(OnTimerElapsed onTimerElapsed) {
            this.onTimerElapsed = onTimerElapsed;
            return this;
        }

        public Builder setTxtWhiteMoveTimer(TextView txtWhiteMoveTimer) {
            this.txtWhiteMoveTimer = txtWhiteMoveTimer;
            return this;
        }

        public GameTimer build(){
            GameTimer gameTimer =  new GameTimer(this);
            GameTimer.INSTANCE = gameTimer; // Set It Equivalent to the instance
            return gameTimer;
        }
    }

    public enum GameDuration{
        TEN_MINUTES{
            @Override
            public int getDuration() {
                return 10;
            }

            @NonNull
            @Override
            public String toString() {
                return "10 min";
            }
        },
        FIFTEEN_MINUTES{
            @Override
            public int getDuration() {
                return 15;
            }

            @NonNull
            @Override
            public String toString() {
                return "15 min";
            }
        },
        TWENTY_MINUTES{
            @Override
            public int getDuration() {
                return 20;
            }

            @NonNull
            @Override
            public String toString() {
                return "20 min";
            }
        },
        THIRTY_MINUTES{
            @Override
            public int getDuration() {
                return 30;
            }

            @NonNull
            @Override
            public String toString() {
                return "30 min";
            }
        };

        public abstract int getDuration();
    }
}