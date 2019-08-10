package chessbet.domain;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import chessbet.utils.DatabaseUtil;

public class MatchableAccount implements Parcelable {
    private int elo_rating;
    private String matchId;
    private String match_type;
    private boolean matchable;
    private boolean matched;
    private boolean online;
    private String opponent;
    private String owner;

    public MatchableAccount(Parcel in) {
        elo_rating = in.readInt();
        matchId = in.readString();
        match_type = in.readString();
        matchable = in.readByte() != 0;
        matched = in.readByte() != 0;
        online = in.readByte() != 0;
        opponent = in.readString();
        owner = in.readString();
    }

    public MatchableAccount(){

    }
    public static final Creator<MatchableAccount> CREATOR = new Creator<MatchableAccount>() {
        @Override
        public MatchableAccount createFromParcel(Parcel in) {
            return new MatchableAccount(in);
        }

        @Override
        public MatchableAccount[] newArray(int size) {
            return new MatchableAccount[size];
        }
    };

    public void setElo_rating(int elo_rating) {
        this.elo_rating = elo_rating;
    }

    public void setMatch_type(String match_type) {
        this.match_type = match_type;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public void setMatchable(boolean matchable) {
        this.matchable = matchable;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getElo_rating() {
        return elo_rating;
    }

    public String getMatch_type() {
        return match_type;
    }

    public String getMatchId() {
        return matchId;
    }

    public boolean getOnline() {
        return online;
    }

    public String getOpponent() {
        return opponent;
    }

    public String getOwner() {
        return owner;
    }

    public boolean isMatchable(){
        return matchable;
    }

    public boolean isMatched() {
        return matched;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(elo_rating);
        dest.writeString(matchId);
        dest.writeString(match_type);
        dest.writeByte((byte) (matchable ? 1 : 0));
        dest.writeByte((byte) (matched ? 1 : 0));
        dest.writeByte((byte) (online ? 1 : 0));
        dest.writeString(opponent);
        dest.writeString(owner);
    }

    public String getSelf() {
        return opponent.equals("WHITE") ? "BLACK" : "WHITE";
    }

    public static MatchableAccount CREATE_MATCHABLE_ACCOUNT_ON_RESPONSE(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        MatchableAccount matchableAccount = new MatchableAccount();
        matchableAccount.setMatchId(jsonObject.getString("matchId"));
        matchableAccount.setMatch_type(jsonObject.getString("match_type"));
        matchableAccount.setOnline(jsonObject.getBoolean("online"));
        matchableAccount.setOwner(jsonObject.getString("owner"));
        matchableAccount.setOpponent(jsonObject.getString("opponent"));
        matchableAccount.setElo_rating(jsonObject.getInt("elo_rating"));
        matchableAccount.setMatchable(jsonObject.getBoolean("matchable"));
        matchableAccount.setMatched(jsonObject.getBoolean("matched"));
        return matchableAccount;
    }

    public void endMatch(){
        DatabaseUtil.getAccount(this.owner).removeValue();
        DatabaseUtil.getMatch(this.matchId).removeValue();
    }
}
