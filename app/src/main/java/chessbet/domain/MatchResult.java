package chessbet.domain;

public class MatchResult {
    private String matchId;
    private MatchStatus matchStatus;
    private String gain;
    private String loss;
    private String pgnText;
    private Amount amount;

    private void setGain(String gain) {
        this.gain = gain;
    }

    private void setLoss(String loss) {
        this.loss = loss;
    }

    private void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    private void setMatchStatus(MatchStatus matchStatus) {
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

    private void setPgnText(String pgnText) {
        this.pgnText = pgnText;
    }

    public String getPgnText() {
        return pgnText;
    }

    public Amount getAmount() {
        return amount;
    }

    public static class Builder{
        private MatchResult matchResult;

        Builder(){
            matchResult = new MatchResult();
        }

        Builder setMatchId(String matchId){
            matchResult.setMatchId(matchId);
            return this;
        }

        Builder setMatchStatus(MatchStatus matchStatus){
            matchResult.setMatchStatus(matchStatus);
            return this;
        }

        Builder setGain(String gain){
            matchResult.setGain(gain);
            return this;
        }

        Builder setLoss(String loss){
            matchResult.setLoss(loss);
            return this;
        }

        Builder setPgnText(String pgn){
            matchResult.setPgnText(pgn);
            return this;
        }

        Builder setAmount(Amount amount) {
            matchResult.amount = amount;
            return this;
        }

        public MatchResult build(){
            return matchResult;
        }
    }
}
