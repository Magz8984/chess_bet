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
    private long minRating;
    private long maxRating;
    private long duration;
    private boolean isFriendly = false;
    private MatchType matchType;
    private String dateCreated;
    private String id;

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

    public void setMaxRating(long maxRating) {
        this.maxRating = maxRating;
    }

    public void setMinRating(long minRating) {
        this.minRating = minRating;
    }

    public long getMaxRating() {
        return maxRating;
    }

    public long getMinRating() {
        return minRating;
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

    public void setFriendly(boolean friendly) {
        isFriendly = friendly;
    }

    public boolean isFriendly() {
        return isFriendly;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static boolean isAccepted(Challenge challenge){
        return !(challenge.requester == null || challenge.requester.isEmpty());
    }
}
