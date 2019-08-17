package chessbet.domain;

import androidx.annotation.NonNull;

public enum MatchType {
    PLAY_ONLINE{
        @NonNull
        @Override
        public String toString(){
            return "PLAY_ONLINE";
        }
    },
    TWO_PLAYER{
        @NonNull
        @Override
        public String toString(){
            return "TWO_PLAYER";
        }
    },
    BET_ONLINE{
        @NonNull
        @Override
        public String toString(){
            return "BET_ONLINE";
        }
    },
    SINGLE_PLAYER{
        @NonNull
        @Override
        public String toString(){
            return "SINGLE_PLAYER";
        }
    }
}
