package stockfish;

import android.util.Log;

import java.util.Locale;

public class InternalStockFishHandler {


    private EngineUtil.Response stockFishResponse;

    public void setStockFishResponse(EngineUtil.Response stockFishResponse) {
        this.stockFishResponse = stockFishResponse;
    }

    public void askStockFishMove(String fen, long time, int depth){
        String message;
        message = (String.format(Locale.US," position fen %s \n go movetime %d", fen, time));
        Log.d("Move", message);
        new Thread(() -> EngineUtil.pipe(message, stockFishResponse)).start();
    }
}
