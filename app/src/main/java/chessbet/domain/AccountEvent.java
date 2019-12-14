package chessbet.domain;

public class AccountEvent {
    private Event name;
    private String date_created;
    private boolean done;

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public void setName(Event name) {
        this.name = name;
    }

    public boolean isDone() {
        return done;
    }

    public String getDate_created() {
        return date_created;
    }

    public Event getName() {
        return name;
    }

    public enum Event{
        TERMS_OF_SERVICE_ACCEPTED,
        CREATED
    }
}
