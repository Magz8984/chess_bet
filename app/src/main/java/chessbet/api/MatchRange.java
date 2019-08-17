package chessbet.api;

public class MatchRange {
    private int startAt = 100;
    private int endAt = 100;

    public int getEndAt() {
        return endAt;
    }

    public int getStartAt() {
        return startAt;
    }

    public void setEndAt(int endAt) {
        this.endAt = endAt;
    }

    public void setStartAt(int startAt) {
        this.startAt = startAt;
    }
}
