package stockfish;

public enum UCIOption {
    IS_READY{
        @Override
        String command(String... strings) {
            return "isready";
        }

        @Override
        String response() {
            return "readyok";
        }
    },

    UCI{
        @Override
        String command(String... strings) {
            return "uci";
        }

        @Override
        String response() {
            return "uciok";
        }
    };

    abstract String command(String... strings);
    abstract String response();
}