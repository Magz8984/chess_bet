package chessbet.domain;

/**
 * @author Collins Magondu 21/11/19
 */
public class Challenge {
    private String owner;
    private String requester;
    private boolean accepted;
    private long timeStamp;
    private long eloRating;
    private long duration;
    private MatchType matchType;
    private String dateCreated;

    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
    }

    public void setEloRating(long eloRating) {
        this.eloRating = eloRating;
    }

    public long getEloRating() {
        return eloRating;
    }

    public MatchType getMatchType() {
        return matchType;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getOwner() {
        return owner;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public boolean isAccepted() {
        return this.accepted;
    }

    public String getRequester() {
        return requester;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
