package chessengine;

import android.content.Context;
import android.media.MediaPlayer;

public class GameUtil {
    static MediaPlayer mp;
    static void playSound(int resource, Context context){
        mp= MediaPlayer.create(context, resource);
        mp.start();
    }

    public static MediaPlayer getMediaPlayer() {
        return mp;
    }
}
