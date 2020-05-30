package chessbet.models;

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
    private Players players;
    private String rounds;
    private Teams teams;
    private int timeStamp;
    private String typeOfTournament;

    public Tournaments() {
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

    public Players getPlayers() {
        return players;
    }

    public void setPlayers(Players players) {
        this.players = players;
    }

    public String getRounds() {
        return rounds;
    }

    public void setRounds(String rounds) {
        this.rounds = rounds;
    }

    public Teams getTeams() {
        return teams;
    }

    public void setTeams(Teams teams) {
        this.teams = teams;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
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
                ", players=" + players +
                ", rounds='" + rounds + '\'' +
                ", teams=" + teams +
                ", timeStamp=" + timeStamp +
                ", typeOfTournament='" + typeOfTournament + '\'' +
                '}';
    }
}
