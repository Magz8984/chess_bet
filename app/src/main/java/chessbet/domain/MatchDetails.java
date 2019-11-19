package chessbet.domain;

import java.util.ArrayList;

public class MatchDetails {
    private MatchType match_type;
    private MatchResult match_result;
    private ArrayList<OnlinePlayer> players;

    public void setMatch_type(MatchType match_type) {
        this.match_type = match_type;
    }

    public void setMatch_result(MatchResult match_result) {
        this.match_result = match_result;
    }

    public void setPlayers(ArrayList<OnlinePlayer> players) {
        this.players = players;
    }

    public MatchType getMatch_type() {
        return match_type;
    }

    public ArrayList<OnlinePlayer> getPlayers() {
        return players;
    }

    public MatchResult getMatch_result() {
        return match_result;
    }
}
