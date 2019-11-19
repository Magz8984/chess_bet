package chessengine;

import com.chess.engine.player.Player;

public interface OnMoveDoneListener{
    void getMoves(MoveLog move);
    void isCheckMate(Player player);
    void isStaleMate(Player player);
    void isCheck(Player player);
    void isDraw();
    void onGameResume();
}