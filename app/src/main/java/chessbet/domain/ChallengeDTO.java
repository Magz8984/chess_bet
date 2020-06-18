package chessbet.domain;

/**
 * @author Collins Magondu 27/03/2020
 */

public class ChallengeDTO {
    private String owner;
    private int duration;
    private int eloRating;
    private Amount amount;
    private Challenge.Type type;
    private int minEloRating;
    private int maxEloRating;

    public String getOwner() {
        return owner;
    }

    public int getDuration() {
        return duration;
    }

    public int getEloRating() {
        return eloRating;
    }

    public int getMaxEloRating() {
        return maxEloRating;
    }

    public int getMinEloRating() {
        return minEloRating;
    }

    public Challenge.Type getType() {
        return type;
    }

    public static class Builder{
        private ChallengeDTO challengeDTO;
        public Builder(){
            challengeDTO = new ChallengeDTO();
            challengeDTO.amount = new Amount();
            challengeDTO.amount.setCurrency("KES");
        }
        public Builder setOwner(String owner){
            this.challengeDTO.owner = owner;
            return this;
        }
        public Builder setDuration(int duration){
            this.challengeDTO.duration = duration;
            return this;
        }
        public Builder setEloRating(int eloRating){
            this.challengeDTO.eloRating = eloRating;
            return this;
        }
        public Builder setType(Challenge.Type type){
            this.challengeDTO.type = type;
            return this;
        }

        public Builder setMinEloRating(int minEloRating){
            this.challengeDTO.minEloRating = minEloRating;
            return this;
        }

        public Builder setMaxEloRating(int maxEloRating){
            this.challengeDTO.maxEloRating = maxEloRating;
            return this;
        }

        public Builder setAmount (int amount) {
            this.challengeDTO.amount.setAmount(amount);
            this.challengeDTO.amount.setCurrency("USD");
            return this;
        }

        public ChallengeDTO build(){
            return this.challengeDTO;
        }
    }
}
