package chessengine;

import com.chess.engine.player.Player;

/**
 * Any class using BoardView shall implement this interface to get moves and game states
 * @see chessbet.app.com.BoardActivity
 * @see chessbet.app.com.GameAnalysisActivity
 */
public interface OnMoveDoneListener{
    void getMoves(MoveLog move);
    void isCheckMate(Player player);
    void isStaleMate(Player player);
    void isCheck(Player player);
    void isDraw();
    void onGameResume();
}