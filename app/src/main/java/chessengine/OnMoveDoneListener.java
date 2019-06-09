package chessengine;

import com.chess.engine.Move;
import com.chess.engine.player.Player;

public interface OnMoveDoneListener{
    void getMove(Move move);
    void isCheckMate(Player player);
    void isStaleMate(Player player);
    void isCheck(Player player);
}