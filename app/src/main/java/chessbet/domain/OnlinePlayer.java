package chessbet.domain;

import java.util.ArrayList;

public class OnlinePlayer {
    private String owner;
    private ArrayList<MatchEvent> events;
    private int elo_rating;
    private String type;

    public void setEvents(ArrayList<MatchEvent> events) {
        this.events = events;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setElo_rating(int elo_rating) {
        this.elo_rating = elo_rating;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOwner() {
        return owner;
    }

    public int getElo_rating() {
        return elo_rating;
    }

    public ArrayList<MatchEvent> getEvents() {
        return events;
    }

    public String getType() {
        return type;
    }
}
