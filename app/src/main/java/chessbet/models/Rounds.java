package chessbet.models;

public class Rounds {
    private String matchUrl, playerNumber, result, scheduledColor;

    public Rounds() {
    }

    public Rounds(String matchUrl, String playerNumber, String result, String scheduledColor) {
        this.matchUrl = matchUrl;
        this.playerNumber = playerNumber;
        this.result = result;
        this.scheduledColor = scheduledColor;
    }

    public String getMatchUrl() {
        return matchUrl;
    }

    public void setMatchUrl(String matchUrl) {
        this.matchUrl = matchUrl;
    }

    public String getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(String playerNumber) {
        this.playerNumber = playerNumber;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getScheduledColor() {
        return scheduledColor;
    }

    public void setScheduledColor(String scheduledColor) {
        this.scheduledColor = scheduledColor;
    }

    @Override
    public String toString() {
        return "Rounds{" +
                "matchUrl='" + matchUrl + '\'' +
                ", playerNumber='" + playerNumber + '\'' +
                ", result='" + result + '\'' +
                ", scheduledColor='" + scheduledColor + '\'' +
                '}';
    }
}
