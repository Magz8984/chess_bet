package chessbet.utils;

import android.content.Context;
import android.os.Handler;

import chessbet.domain.MatchableAccount;

/**
 * @author  Collins Magondu
 */
public class GameManager{
    public static final String GAME_FILE_NAME = "Chess_Bet_PGN";
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
