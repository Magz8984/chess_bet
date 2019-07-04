package chessbet.domain;

public class RemoteMove {
    public int to;
    private String owner;
    public int from;

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public String getOwner() {
        return owner;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }
}
