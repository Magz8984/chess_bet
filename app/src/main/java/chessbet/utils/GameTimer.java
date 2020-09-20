package chessbet.utils;

import android.graphics.Color;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chess.engine.Alliance;

import chessbet.domain.Player;
import chessengine.GameUtil;
import io.github.erehmi.countdown.CountDownTask;
import io.github.erehmi.countdown.CountDownTimers;

public class GameTimer {
    private static GameTimer INSTANCE;
    private Builder builder;
    private CountDownTask opponentCountDownTask;
    private CountDownTask ownerCountDownTask;
    private int opponentTimeLeft;
    private volatile int ownerTimeLeft;
    private Player currentPlayer;
    private boolean isTimerLapsed = false;

    private GameTimer(final Builder builder){
        this.builder = builder;
        opponentCountDownTask = CountDownTask.create();
        ownerCountDownTask = CountDownTask.create();
    }

    public void setOpponentTimeLeft(int opponentTimeLeft) {
        this.opponentTimeLeft = opponentTimeLeft;
    }

    public void setOwnerTimeLeft(int ownerTimeLeft) {
        this.ownerTimeLeft = ownerTimeLeft;
    }

    public static GameTimer get() {
        return INSTANCE;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setOpponentTimer(){
        opponentCountDownTask.cancel();
        opponentCountDownTask = CountDownTask.create();
        long targetMilliseconds = CountDownTask.elapsedRealtime() + opponentTimeLeft;
        opponentCountDownTask.until(builder.txtOpponent, targetMilliseconds, 1000, new CountDownTimers.OnCountDownListener() {
            @Override
            public void onTick(View view, long millisUntilFinished) {
                TextView txtBlackTimer = (TextView) view;
                opponentTimeLeft = (int) millisUntilFinished;
                txtBlackTimer.setText(timeConverter(millisUntilFinished / 1000));
                if(millisUntilFinished / 1000 <= 10 ){
                    txtBlackTimer.setTextColor(Color.RED);
                }
            }

            @Override
            public void onFinish(View view) {
                // Black Lost On Time
                isTimerLapsed = true;
                builder.onTimerElapsed.playerTimeLapsed(GameUtil.getPlayerFromAlliance(builder.opponentAlliance));
            }
        });
    }

    public void startTimer() {
        if(builder.ownerAlliance == Alliance.WHITE){
            setOwnerTimer();
        } else {
            setOpponentTimer();
        }
    }

    public Alliance getOwnerAlliance() {
        return builder.ownerAlliance;
    }

    public Alliance getOpponentAlliance() {
        return builder.opponentAlliance;
    }

    public void setBuilder(Builder builder) {
        this.builder = builder;
    }

    public void stopTimer(Player player){
        if(player.getAlliance().equals(builder.ownerAlliance)){
            ownerCountDownTask.cancel(); // Cancel owner timer
            setOpponentTimer();
            currentPlayer = getPlayer(builder.opponentAlliance);
        }
        else if (player.getAlliance().equals(builder.opponentAlliance)){
            opponentCountDownTask.cancel(); // Cancel opponent timer
            setOwnerTimer();
            currentPlayer = getPlayer(builder.ownerAlliance);
        }
    }

    /**
     * Get player from alliance
     * @param alliance Alliance
     * @return player from alliance
     */
    private Player getPlayer(Alliance alliance){
        return (alliance.equals(Player.WHITE.getAlliance())) ? Player.WHITE : Player.BLACK;
    }

    public void stopAllTimers(){
        try{
            if(INSTANCE != null){
                if(!isTimerLapsed){
                    if(opponentCountDownTask != null){
                        opponentCountDownTask.cancel();
                    }
                    if(ownerCountDownTask != null){
                        ownerCountDownTask.cancel();
                    }
                }
                INSTANCE = null;

            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void setGameTimer(GameTimer gameTimer){
        INSTANCE = gameTimer;
    }

    public void setOwnerTimer(){
        ownerCountDownTask.cancel();
        ownerCountDownTask = CountDownTask.create();
        long targetMilliseconds = CountDownTask.elapsedRealtime() + ownerTimeLeft;
        ownerCountDownTask.until(builder.txtOwner, targetMilliseconds, 1000, new CountDownTimers.OnCountDownListener() {
            @Override
            public void onTick(View view, long millisUntilFinished) {
                TextView txtWhiteTimer = (TextView) view;
                ownerTimeLeft = (int) millisUntilFinished;
                txtWhiteTimer.setText(timeConverter(millisUntilFinished/1000));
                if(millisUntilFinished / 1000 <= 10 ){
                    txtWhiteTimer.setTextColor(Color.RED);
                }
            }

            @Override
            public void onFinish(View view) {
                // Black Lost On Time
                isTimerLapsed = true;
                builder.onTimerElapsed.playerTimeLapsed(GameUtil.getPlayerFromAlliance(builder.ownerAlliance));
            }
        });
    }

    public int getOpponentTimeLeft() {
        return opponentTimeLeft;
    }

    public int getOwnerTimeLeft() {
        //Add a 1 second delay
        return ownerTimeLeft - 1000;
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
        opponentCountDownTask.cancel();
        ownerCountDownTask.cancel();
    }

    /**
     * Game timer builder
     */
    public static class Builder{
        private TextView txtOpponent;
        private Alliance ownerAlliance;
        private Alliance opponentAlliance;
        private TextView txtOwner;
        private OnTimerElapsed onTimerElapsed;

        public Builder setTxtOwner(TextView txtOwner) {
            this.txtOwner = txtOwner;
            return this;
        }

        public Builder setOnTimerElapsed(OnTimerElapsed onTimerElapsed) {
            this.onTimerElapsed = onTimerElapsed;
            return this;
        }

        public Builder setTxtOpponent(TextView txtOpponent) {
            this.txtOpponent = txtOpponent;
            return this;
        }

        public Builder setOwnerAlliance(Alliance alliance){
            this.ownerAlliance = alliance;
            return this;
        }

        public Builder setOpponentAlliance(Alliance alliance){
            this.opponentAlliance = alliance;
            return this;
        }


        public GameTimer build(){
            GameTimer gameTimer =  new GameTimer(this);
            GameTimer.INSTANCE = gameTimer; // Set It Equivalent to the instance
            return gameTimer;
        }
    }

    public enum GameDuration{
        ONE_MINUTE{
            @Override
            public int getDuration() {
                return 1;
            }

            @NonNull
            @Override
            public String toString() {
                return "1 min";
            }
        },
        FIVE_MINUTES{
            @Override
            public int getDuration() {
                return 5;
            }

            @NonNull
            @Override
            public String toString() {
                return "5 min";
            }
        },
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