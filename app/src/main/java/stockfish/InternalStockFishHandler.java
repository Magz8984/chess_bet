package stockfish;

import android.util.Log;

import java.util.Locale;

public class InternalStockFishHandler {
    public void askStockFishMove(String fen, long time, int depth){
        String message;
        message = (String.format(Locale.US," position fen %s \n go movetime %d depth", fen, time, depth));
        Log.d("Move", message);
        new Thread(() -> EngineUtil.pipe(message)).start();
    }
}
