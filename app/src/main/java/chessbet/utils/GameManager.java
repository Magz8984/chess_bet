package chessbet.utils;

import android.os.Handler;

/**
 * @author  Collins Magondu
 */
public class GameManager {
    public static final String GAME_FILE = "Chess_Bet_PGN_%s.pgn";
    public static void delayedTask(Runnable task, int mills){
       new Handler().postDelayed(task, mills);
    }
}
