package chessbet.domain;

import android.support.annotation.NonNull;

public enum TimerEvent {
    MOVE_TIMER_ELAPSED{
        @Override
        public String getMessage() {
            return "Move time has elapsed";
        }

        @NonNull
        @Override
        public String toString(){
            return "MOVE TIMEOUT";
        }
    },
    GAME_TIMER_ELAPSED{
        @Override
        public String getMessage() {
            return null;
        }

        @NonNull
        @Override
        public String toString(){
            return "GAME TIMEOUT";
        }
    };
    public abstract String getMessage();
}
