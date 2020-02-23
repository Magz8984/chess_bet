package stockfish;

import androidx.annotation.NonNull;

public enum UCIOption {
    SKILL_LEVEL("Skill Level");

    private String option;
    private long value;

    UCIOption(String option) {
        this.option = option;
    }

    public UCIOption setValue(long value){
        this.value = value;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return "setoption name " + option +  " value " + value + "\n";
    }
}
