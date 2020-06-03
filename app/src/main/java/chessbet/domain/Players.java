package chessbet.domain;

public class Players {
    private int fideNumber;
    private int fideRating;
    private String email;
    private Boolean isActive;
    private String name;
    private int points;
    private int rank;
    private int rankNumber;
    private Rounds rounds;

    public Players(int fideNumber, int fideRating, String email, Boolean isActive, String name, int points, int rank, int rankNumber, Rounds rounds) {
        this.fideNumber = fideNumber;
        this.fideRating = fideRating;
        this.email = email;
        this.isActive = isActive;
        this.name = name;
        this.points = points;
        this.rank = rank;
        this.rankNumber = rankNumber;
        this.rounds = rounds;
    }

    public int getFideNumber() {
        return fideNumber;
    }

    public void setFideNumber(int fideNumber) {
        this.fideNumber = fideNumber;
    }

    public int getFideRating() {
        return fideRating;
    }

    public void setFideRating(int fideRating) {
        this.fideRating = fideRating;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getRankNumber() {
        return rankNumber;
    }

    public void setRankNumber(int rankNumber) {
        this.rankNumber = rankNumber;
    }

    public Rounds getRounds() {
        return rounds;
    }

    public void setRounds(Rounds rounds) {
        this.rounds = rounds;
    }

    @Override
    public String toString() {
        return "Player{" +
                "fideNumber=" + fideNumber +
                ", fideRating=" + fideRating +
                ", email='" + email + '\'' +
                ", isActive=" + isActive +
                ", name='" + name + '\'' +
                ", points=" + points +
                ", rank=" + rank +
                ", rankNumber=" + rankNumber +
                ", rounds=" + rounds +
                '}';
    }
}
