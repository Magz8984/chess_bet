package chessbet.domain;
/**
 * @author Collins Magondu
 */
public class MatchEvaluationResponse {
    private String ownerOne;
    private String ownerTwo;
    private int ownerOneElo;
    private int ownerTwoElo;

    public void setOwnerOne(String ownerOne) {
        this.ownerOne = ownerOne;
    }

    public void setOwnerTwo(String ownerTwo) {
        this.ownerTwo = ownerTwo;
    }

    public String getOwnerOne() {
        return ownerOne;
    }

    public String getOwnerTwo() {
        return ownerTwo;
    }

    public void setOwnerTwoElo(int ownerTwoElo) {
        this.ownerTwoElo = ownerTwoElo;
    }

    public void setOwnerOneElo(int ownerOneElo) {
        this.ownerOneElo = ownerOneElo;
    }

    public int getOwnerOneElo() {
        return ownerOneElo;
    }

    public int getOwnerTwoElo() {
        return ownerTwoElo;
    }
}
