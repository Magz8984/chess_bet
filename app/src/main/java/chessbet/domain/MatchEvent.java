package chessbet.domain;


import androidx.annotation.NonNull;

public enum MatchEvent {
    IN_PROGRESS{
        @NonNull
        @Override
        public String toString() {
            return "IN_PROGRESS";
        }
    },
    FINISHED{
        @NonNull
        @Override
        public String toString() {
            return "FINISHED";
        }
    },
    INTERRUPTED {
        @NonNull
        @Override
        public String toString() {
            return "INTERRUPTED";
        }
    }
}
