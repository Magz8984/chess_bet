package chessbet.domain;

import java.util.ArrayList;
import java.util.List;

public class TargetedChallenge {
    private String id;
    private String owner;
    private String ownerName;
    private MatchType matchType;
    private String target;
    private String targetName;
    private boolean accepted;
    private long timeStamp;
    private String dateCreated;
    private List<String> users = new ArrayList<>();

    public void setOwner(String owner) {
        this.owner = owner;
    }

    private void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    private void setMatchType(MatchType matchType) {
        this.matchType = matchType;
    }

    private void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    private void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getOwner() {
        return owner;
    }

    public String getId() {
        return id;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public List<String> getUsers() {
        return users;
    }

    public MatchType getMatchType() {
        return matchType;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getTarget() {
        return target;
    }

    public String getTargetName() {
        return targetName;
    }

    public static TargetedChallenge targetChallengeFactory (String owner, String ownerName, String target, String targetName, MatchType matchType) {
        TargetedChallenge targetedChallenge = new TargetedChallenge();
        targetedChallenge.setOwner(owner);
        targetedChallenge.setOwnerName(ownerName);
        targetedChallenge.setTarget(target);
        targetedChallenge.setAccepted(false);
        targetedChallenge.setMatchType(matchType);
        targetedChallenge.setTargetName(targetName);
        return targetedChallenge;
    }
}
