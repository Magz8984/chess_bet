package chessbet.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import io.github.erehmi.countdown.CountDownTask;
import io.github.erehmi.countdown.CountDownTimers;

public class GameTimer {
    private Context context;

    public GameTimer(Context context){
        this.context=context;
    }

    public void setGameCountDownTask(TextView txtCountDown,int milliseconds,int interval) {
        CountDownTask gameCountDownTask = CountDownTask.create();
        long targetMilliseconds = CountDownTask.elapsedRealtime() + milliseconds;
        gameCountDownTask.until(txtCountDown, targetMilliseconds, interval, new CountDownTimers.OnCountDownListener() {
            @Override
            public void onTick(View view, long millisUntilFinished) {
                Log.d("TIMER" ,Long.toString(millisUntilFinished));
                ((TextView) view).setText(timeConverter(millisUntilFinished));
            }

            @Override
            public void onFinish(View view) {
                ((TextView) view).setText("DONE");
            }
        });
    }

    public void setMoveCountDownTask(TextView txtCountDown,int milliseconds,int interval) {
        CountDownTask moveCountDownTask = CountDownTask.create();
        long targetMilliseconds = CountDownTask.elapsedRealtime() + milliseconds;
        moveCountDownTask.until(txtCountDown, targetMilliseconds, interval, new CountDownTimers.OnCountDownListener() {
            @Override
            public void onTick(View view, long millisUntilFinished) {
                ((TextView) view).setText(timeConverter(millisUntilFinished/interval));
            }

            @Override
            public void onFinish(View view) {
                ((TextView) view).setText("DONE");
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
            return Long.toString(minutes) + ":" + secondsString;
        }
        else {
            return "00 : "+ secondsString;
        }
    }
}
