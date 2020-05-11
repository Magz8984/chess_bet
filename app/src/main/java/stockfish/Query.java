package stockfish;

import androidx.annotation.NonNull;

import java.util.Locale;

/**
 * @author Collins Magondu
 */

public class Query {
    private String fen;
    private QueryType type;
    private long depth;
    private long time;
    private long threads;

    public void setDepth(long depth) {
        this.depth = depth;
    }

    private void setFen(String fen) {
        this.fen = fen;
    }

    public void setThreads(long threads) {
        this.threads = threads;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setType(QueryType type) {
        this.type = type;
    }

    public QueryType getType() {
        return type;
    }

    @NonNull
    @Override
    public String toString() {
    return String.format(Locale.US,"setoption name Threads value %d \n " +
            " position fen %s \n" +
            " go movetime %d depth %d \n", this.threads,  this.fen , this.time, this.depth);
    }

    public static class Builder {
        private Query query;

        public Builder() {
            query = new Query();
        }

        public Builder setFen(String fen) {
            this.query.setFen(fen);
            return this;
        }

        public Builder setDepth(long depth) {
            this.query.setDepth(depth);
            return this;
        }

        public Builder setTime(long ms) {
            this.query.setTime(ms);
            return this;
        }

        public Builder setThreads(long threads) {
            this.query.setThreads(threads);
            return this;
        }

        public Builder setQueryType(QueryType type) {
            this.query.setType(type);
            return this;
        }

        public Query build() {
            return this.query;
        }
    }
}
