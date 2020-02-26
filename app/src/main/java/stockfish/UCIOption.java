package stockfish;

import androidx.annotation.NonNull;

public enum UCIOption {
    SKILL_LEVEL("Skill Level"),
    UCI_LimitStrength("UCI_LimitStrength"),
    UCI_Elo("UCI_Elo");

    private String option;
    private long value;
    private boolean bool;

    UCIOption(String option) {
        this.option = option;
    }

    public UCIOption setValue(long value){
        this.value = value;
        return this;
    }

    public UCIOption setBool(boolean bool) {
        this.bool = bool;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return "setoption name " + option +  " value " + value + "\n";
    }

    public String toBoolString() {
        return "setoption name " + option +  " value " + bool + "\n";
    }
}
