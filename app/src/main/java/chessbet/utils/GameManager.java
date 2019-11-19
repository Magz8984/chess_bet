package chessbet.utils;

import android.os.Handler;

/**
 * @author  Collins Magondu
 */
public class GameManager {
    public static final String GAME_FILE_NAME = "Chess_Bet_PGN";
    public static final String FULL_GAME_FILE = GAME_FILE_NAME + "_%s.pgn";
    public static void delayedTask(Runnable task, int mills){
       new Handler().postDelayed(task, mills);
    }
}
