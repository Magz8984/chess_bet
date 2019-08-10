package chessbet.domain;

import java.util.ArrayList;
import java.util.List;

import chessbet.utils.DatabaseUtil;

public class RemoteMove {
    private static  RemoteMove INSTANCE = new RemoteMove();
    private int to;
    private String owner;
    private int from;
    private List<MatchEvent> events;

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public void setEvents(ArrayList<MatchEvent> events) {
        this.events = events;
    }

    public List<MatchEvent> getEvents() {
        return events;
    }

    public void addEvent(MatchEvent matchEvent){
        this.events.add(matchEvent);
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
    // TODO Change self to enum BLACK || WHITE
    public void send(String matchId, String self){
        DatabaseUtil.sendRemoteMove(matchId,self)
                .setValue(this).addOnSuccessListener(aVoid -> {

        });
    }

    public static RemoteMove get(){
        return INSTANCE;
    }
}
