package chessbet.domain;

public enum MatchStatus {
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
    }
}
