package chessbet.utils;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import io.github.erehmi.countdown.CountDownTask;
import io.github.erehmi.countdown.CountDownTimers;

public class GameTimer {
    private Builder builder;
    private CountDownTask moveCountDownTask;

    public GameTimer(final Builder builder){
        this.builder = builder;
    }

    public void setGameCountDownTask(int milliseconds,int interval) {
        CountDownTask gameCountDownTask = CountDownTask.create();
        long targetMilliseconds = CountDownTask.elapsedRealtime() + milliseconds;
        gameCountDownTask.until(builder.txtGameTimer, targetMilliseconds, interval, new CountDownTimers.OnCountDownListener() {
            @Override
            public void onTick(View view, long millisUntilFinished) {
                Log.d("TIMER" ,Long.toString(millisUntilFinished));
                ((TextView) view).setText(timeConverter(millisUntilFinished));
            }

            @Override
            public void onFinish(View view) {
                builder.onTimerElapsed.moveGameElapsed();
             }
        });
    }

    public void setMoveCountDownTask(int milliseconds,int interval) {
        moveCountDownTask = CountDownTask.create();
        long targetMilliseconds = CountDownTask.elapsedRealtime() + milliseconds;
        Log.d("TIMER",Long.toString(targetMilliseconds));
        moveCountDownTask.until(builder.txtMoveTimer, targetMilliseconds, interval, new CountDownTimers.OnCountDownListener() {
            @Override
            public void onTick(View view, long millisUntilFinished) {
                TextView txtMoveTimer = (TextView) view;
                txtMoveTimer.setText(timeConverter(millisUntilFinished/interval));
                if(millisUntilFinished / 1000 <= 10 ){
                    txtMoveTimer.setTextColor(Color.RED);
                }
            }

            @Override
            public void onFinish(View view) {
                builder.onTimerElapsed.moveTimerElapsed();
            }
        });
    }

     private static String timeConverter(long milliseconds){
        long secondsRemaining = milliseconds % 60;
        String secondsString = Long.toString(secondsRemaining);
         if(secondsRemaining < 10){
             secondsString = "0" + secondsString;
         }
        if(milliseconds > 1000){
            long seconds = milliseconds / 60 ;
            long minutes = seconds / 60 ;
            return minutes + ":" + secondsString;
        }
        else {
            return "00 : "+ secondsString;
        }
    }

    public void setResult(String result) {
        String result1 = result;
    }

    public void invalidateTimer(){
        moveCountDownTask.cancel(builder.txtMoveTimer);
        // TODO Implement this at a later stage
//        gameCountDownTask.cancel(builder.txtGameTimer);
    }

    public static class Builder{
        private TextView txtGameTimer;
        private TextView txtMoveTimer;
        private OnTimerElapsed onTimerElapsed;

        public Builder setTxtGameTimer(final TextView txtGameTimer){
            this.txtGameTimer = txtGameTimer;
            return this;
        }

        public Builder setTxtMoveTimer(final TextView txtMoveTimer){
            this.txtMoveTimer = txtMoveTimer;
            this.txtMoveTimer.setTextColor(Color.WHITE);
            return this;
        }

        public Builder setOnMoveTimerElapsed(OnTimerElapsed onTimerElapsed) {
            this.onTimerElapsed = onTimerElapsed;
            return this;

        }

        public GameTimer build(){
            return new GameTimer(this);
        }

    }

    public enum GameDuration{
        TEN_MINUTES{
            @NonNull
            @Override
            public String toString() {
                return "10 min";
            }
        },
        FIFTEEN_MINUTES{
            @NonNull
            @Override
            public String toString() {
                return "15 min";
            }
        },
        TWENTY_MINUTES{
            @NonNull
            @Override
            public String toString() {
                return "20 min";
            }
        },
        THIRTY_MINUTES{
            @NonNull
            @Override
            public String toString() {
                return "30 min";
            }
        }
    }
}