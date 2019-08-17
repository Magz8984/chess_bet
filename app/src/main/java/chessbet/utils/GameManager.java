package chessbet.utils;

import android.os.Handler;

/**
 * @author  Collins Magondu
 */
public class GameManager {
    public static void delayedTask(Runnable task, int mills){
       new Handler().postDelayed(task, mills);
    }
}
