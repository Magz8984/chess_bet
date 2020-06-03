package chessbet.domain;

import java.util.ArrayList;

/**
 * @author Elias Baya
 */

public class Tournaments {
    private Amount amount;
    private String authorUid;
    private String dateOfStart;
    private String id ;
    private Boolean isLocked;
    private int matchDuration;
    private String name;
    private int numbeOfRoundsScheduled;
    private String paringAlgorithm;
    private ArrayList<Players> playersArrayList;
    private int rounds;
//    private Teams teams;
    private long timeStamp;
    private String typeOfTournament;

    public Tournaments() {
    }

    public Tournaments(Amount amount, String authorUid, String dateOfStart, String id, Boolean isLocked, int matchDuration, String name, int numbeOfRoundsScheduled, String paringAlgorithm, ArrayList<Players> playersArrayList, int rounds, long timeStamp, String typeOfTournament) {
        this.amount = amount;
        this.authorUid = authorUid;
        this.dateOfStart = dateOfStart;
        this.id = id;
        this.isLocked = isLocked;
        this.matchDuration = matchDuration;
        this.name = name;
        this.numbeOfRoundsScheduled = numbeOfRoundsScheduled;
        this.paringAlgorithm = paringAlgorithm;
        this.playersArrayList = playersArrayList;
        this.rounds = rounds;
        this.timeStamp = timeStamp;
        this.typeOfTournament = typeOfTournament;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public String getAuthorUid() {
        return authorUid;
    }

    public void setAuthorUid(String authorUid) {
        this.authorUid = authorUid;
    }

    public String getDateOfStart() {
        return dateOfStart;
    }

    public void setDateOfStart(String dateOfStart) {
        this.dateOfStart = dateOfStart;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    public int getMatchDuration() {
        return matchDuration;
    }

    public void setMatchDuration(int matchDuration) {
        this.matchDuration = matchDuration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumbeOfRoundsScheduled() {
        return numbeOfRoundsScheduled;
    }

    public void setNumbeOfRoundsScheduled(int numbeOfRoundsScheduled) {
        this.numbeOfRoundsScheduled = numbeOfRoundsScheduled;
    }

    public String getParingAlgorithm() {
        return paringAlgorithm;
    }

    public void setParingAlgorithm(String paringAlgorithm) {
        this.paringAlgorithm = paringAlgorithm;
    }

    public ArrayList<Players> getPlayersArrayList() {
        return playersArrayList;
    }

    public void setPlayersArrayList(ArrayList<Players> playersArrayList) {
        this.playersArrayList = playersArrayList;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTypeOfTournament() {
        return typeOfTournament;
    }

    public void setTypeOfTournament(String typeOfTournament) {
        this.typeOfTournament = typeOfTournament;
    }

    @Override
    public String toString() {
        return "Tournaments{" +
                "amount=" + amount +
                ", authorUid='" + authorUid + '\'' +
                ", dateOfStart='" + dateOfStart + '\'' +
                ", id='" + id + '\'' +
                ", isLocked=" + isLocked +
                ", matchDuration=" + matchDuration +
                ", name='" + name + '\'' +
                ", numbeOfRoundsScheduled=" + numbeOfRoundsScheduled +
                ", paringAlgorithm='" + paringAlgorithm + '\'' +
                ", playersArrayList=" + playersArrayList +
                ", rounds=" + rounds +
                ", timeStamp=" + timeStamp +
                ", typeOfTournament='" + typeOfTournament + '\'' +
                '}';
    }
}
