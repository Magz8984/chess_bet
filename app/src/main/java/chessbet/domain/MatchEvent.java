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
    },
    DRAW {
        @Override
        public String toString() {
            return "DRAW";
        }
    },
    TIMER_LAPSED {
        @Override
        public String toString() {
            return "TIMER_LAPSED";
        }
    },
    DISCONNECTED{
        @Override
        public String toString() {
            return "DISCONNECTED";
        }
    },
    ONLINE{
        @NonNull
        @Override
        public String toString() {
            return "ONLINE";
        }
    },
    NO_EVENT{
        @Override
        public String toString() {
            return "NO_EVENT";
        }
    }
}
