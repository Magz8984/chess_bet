package chessengine;

import com.chess.engine.Move;

import java.util.ArrayList;
import java.util.List;

public class MoveLog {

    private final List<Move> moves;

    MoveLog(){
        this.moves =new ArrayList<>();
    }

    public List<Move> getMoves() {return  this.moves;};

    void addMove(final Move move){
        this.moves.add(move);
    }

    public int size() {return  this.moves.size(); }

    public void clear() { this.moves.clear(); }

    public boolean removeMove(Move move) { return  this.moves.remove(move);}

    public Move removeMove (int index) { return  this.moves.remove(index);}

    Move getMove (int index) {
        return moves.get(index);
    }
}
