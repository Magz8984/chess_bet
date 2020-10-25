package chessbet.domain;

/**
 * @author Collins Magondu
 */
public class AcceptChallengeDTO {
    public String uid;
    public String challengeId;

    public AcceptChallengeDTO(String uid, String challengeId) {
        this.uid = uid;
        this.challengeId = challengeId;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setChallengeId(String challengeId) {
        this.challengeId = challengeId;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public String getUid() {
        return uid;
    }
}
