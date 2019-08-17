package chessbet.domain;

import java.time.LocalDateTime;

public class AccountEvent {
    private String name;
    private LocalDateTime date_created;
    private boolean done;

    public void setDate_created(LocalDateTime date_created) {
        this.date_created = date_created;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDone() {
        return done;
    }

    public LocalDateTime getDate_created() {
        return date_created;
    }

    public String getName() {
        return name;
    }
}
