package chessbet.utils;

import android.content.Context;
import android.os.Handler;

import chessbet.domain.MatchableAccount;

/**
 * @author  Collins Magondu
 */

// TODO All functionality of this class should be in the Game Handler
public class GameManager{
    public static final String GAME_FILE_NAME = "Chess_MVP_PGN";
    public static final String FULL_GAME_FILE = GAME_FILE_NAME + "_%s.pgn";
    /**
     * @see MatchableAccount#endMatch(Context)
     * @param task
     * @param mills
     */
    public static void delayedTask(Runnable task, int mills){
       new Handler().postDelayed(task, mills);
    }

}
