package chessbet.domain;

public class Match {
    private String opponentUserName;
    private String opponentPic;
    private String matchId;
    private MatchStatus matchStatus;
    private MatchResult matchResult;

    public void setMatchResult(MatchResult matchResult) {
        this.matchResult = matchResult;
    }

    public MatchResult getMatchResult() {
        return matchResult;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public void setOpponentPic(String opponentPic) {
        this.opponentPic = opponentPic;
    }

    public void setOpponentUserName(String opponentUserName) {
        this.opponentUserName = opponentUserName;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getOpponentPic() {
        return opponentPic;
    }

    public void setMatchStatus(MatchStatus matchStatus) {
        this.matchStatus = matchStatus;
    }

    public MatchStatus getMatchStatus() {
        return matchStatus;
    }

    public String getOpponentUserName() {
        return (opponentUserName == null) ? "anonymous" : opponentUserName;
    }

    @Override
    public String toString() {
        return matchId + " " + opponentPic + " " + opponentUserName;
    }
}
