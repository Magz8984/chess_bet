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

    private void removeMove (int index) { this.moves.remove(index);}

    Move getMove(int index){
        return this.moves.get(index);
    }

    public void removeMoves(int index){
        ArrayList<Move> validMoves = new ArrayList<>();
        for (int i = 0; i < moves.size(); i++){
            if(i <= index){
                validMoves.add(moves.get(i));
            }
        }
        this.moves.clear();
        this.moves.addAll(validMoves);
    }

    public com.chess.gui.MoveLog convertToEngineMoveLog(){
        com.chess.gui.MoveLog moveLog = new com.chess.gui.MoveLog();
        moveLog.setMoves((ArrayList<Move>) this.moves);
        return moveLog;
    }
}
