package chessbet.domain;

public enum AccountStatus {
    PENDING{
        @Override
        public String toString() {
            return "PENDING";
        }
    },
    ACTIVE{
        @Override
        public String toString() {
            return "ACTIVE";
        }
    },
    SUSPENDED{
        @Override
        public String toString() {
            return "SUSPENDED";
        }
    }
}
