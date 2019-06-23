package chessengine;

import android.content.Context;
import android.media.MediaPlayer;

public class GameUtil {
    static MediaPlayer mp;

    public static void  initialize(int resource, Context context){{
        mp = MediaPlayer.create(context, resource);
    }

    }
    static void playSound(){
        mp.start();
    }

    public static MediaPlayer getMediaPlayer() {
        return mp;
    }
}
