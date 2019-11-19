package chessengine;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class GameUtil {
    private static MediaPlayer mp;

    public static void  initialize(int resource, Context context){{
        mp = MediaPlayer.create(context, resource);
    }

    }
    static void playSound(){
        try{
            mp.start();
        }
        catch (Exception ex){
            Log.e("MEDIA_PLAYER",ex.getMessage());
        }
    }

    public static MediaPlayer getMediaPlayer() {
        return mp;
    }
}
