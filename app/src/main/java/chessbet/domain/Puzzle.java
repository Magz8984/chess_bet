package chessbet.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Puzzle implements Serializable {
    private String pgn;
    private List<Move> moves = new ArrayList<>();
    private String title;
    private String description;
    private long timestamp;
    private String playerType;
    private String owner;
    public static class Move implements Serializable {
        private int fromCoordinate;
        private int toCoordinate;
        private String moveCoordinates;

        public void setFromCoordinate(int fromCoordinate) {
            this.fromCoordinate = fromCoordinate;
        }

        public void setMoveCoordinates(String moveCoordinates) {
            this.moveCoordinates = moveCoordinates;
        }

        public void setToCoordinate(int toCoordinate) {
            this.toCoordinate = toCoordinate;
        }

        public int getFromCoordinate() {
            return fromCoordinate;
        }

        public int getToCoordinate() {
            return toCoordinate;
        }

        public String getMoveCoordinates() {
            return moveCoordinates;
        }
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public void setPlayerType(String playerType) {
        this.playerType = playerType;
    }

    public String getPlayerType() {
        return playerType;
    }

    public void addMove(Move move){
        this.moves.add(move);
    }

    public String getPgn() {
        return pgn;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public void setPgn(String pgn) {
        this.pgn = pgn;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
