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
    }
}
