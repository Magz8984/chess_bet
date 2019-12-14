package chessbet.services;

/**
 * @author Collins Magondu
 * @see chessbet.utils.GameManager
 */
public interface GameListener {
    boolean onGameInterrupted();
    boolean onGameDraw();
    boolean onGameFinished();
}
