package chessbet.domain;

public class MatchResult {
    private String matchId;
    private MatchStatus matchStatus;
    private String gain;
    private String loss;
    private String pgnText;

    public void setGain(String gain) {
        this.gain = gain;
    }

    public void setLoss(String loss) {
        this.loss = loss;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public void setMatchStatus(MatchStatus matchStatus) {
        this.matchStatus = matchStatus;
    }

    public String getMatchId() {
        return matchId;
    }

    public MatchStatus getMatchStatus() {
        return matchStatus;
    }

    public String getGain() {
        return gain;
    }

    public String getLoss() {
        return loss;
    }

    public void setPgnText(String pgnText) {
        this.pgnText = pgnText;
    }

    public String getPgnText() {
        return pgnText;
    }
}
