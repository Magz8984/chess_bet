package chessbet.domain;

public enum MatchStatus{
    WON{
        @Override
        public String toString() {
            return "WON";
        }
    },
    DRAW{
        @Override
        public String toString() {
            return "DRAW";
        }
    },
    INTERRUPTED{
        @Override
        public String toString() {
            return "INTERRUPTED";
        }
    },
    LOSS {
        @Override
        public String toString() {
            return "LOSS";
        }
    },
    TIMER_LAPSED {
        @Override
        public String toString() {
            return "TIMER LAPSED";
        }
    },
    ABANDONMENT {
        @Override
        public String toString(){
            return "ABANDONMENT";
        }
    },
    GAME_ABORTED{
        @Override
        public String toString() {
            return "GAME_ABORTED";
        }
    },
    DISCONNECTED{
        @Override
        public String toString() {
            return "DISCONNECTED";
        }
    }
}
