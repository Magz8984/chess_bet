package stockfish;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Collins Magondu
 */

public class EngineUtil {
    private static BufferedWriter bufferedWriter;
    private static BufferedReader bufferedReader;
    private static Thread listenerThread;

    static void setBufferedReader(BufferedReader bufferedReader) {
        EngineUtil.bufferedReader = bufferedReader;
    }

    static void setBufferedWriter(BufferedWriter bufferedWriter) {
        EngineUtil.bufferedWriter = bufferedWriter;
    }

    static void pipe(String line, Response response){
         try {
             bufferedWriter.write(line + "\n");
             bufferedWriter.flush();
             String endResult = "";
             ensureBufferedReaderIsReady();
             while (bufferedReader.ready()){
                 endResult =  bufferedReader.readLine();
             }
             response.onResponseReceived(endResult);

         }catch (Exception ex){
             ex.printStackTrace();
         }
    }

    public static void startListening(){

        listenerThread = new Thread(() -> {
             if(bufferedReader != null){
                 String endResult;
                 while (true){
                     try {
                         endResult =  bufferedReader.readLine();
                         Log.d("MESSAGEX", endResult);
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                 }
             }
        });
        listenerThread.start();
    }

    public Thread getListenerThread() {
        return listenerThread;
    }

    private static void ensureBufferedReaderIsReady(){
        for (int i = 0; i < 10; i++){
            try {
                Log.d("Here", bufferedReader.ready() + "");
                if(!bufferedReader.ready()){
                    if(!Engine.isRunning()){
                        Thread.sleep(100);
                    }
                    else {
                        Thread.sleep(1000);
                    }
                }
                else {
                    if(Engine.isRunning()){
                        Thread.sleep(1100);
                    }
                    break;
                }
            } catch (Exception e) {
                Log.e("ERROR_MSG", Objects.requireNonNull(e.getMessage()));
                e.printStackTrace();
            }
        }
     }
     public interface Response{
         void onResponseReceived(String response);
    }

    /**
     * Filter through an STOCKFISH engine response tries to find a list of moves
     * @param response
     * @return Move String
     */
    public static String movesSearch(String response) {
        Log.d("Move", response);
        StringBuilder builder = new StringBuilder();
        // Handle
        if(response.startsWith("best")){
            return response.split(" ")[1];
        }
        else if(response.startsWith("info")){
            List<String> list = Arrays.asList(response.split(" "));
            // Make sure we have pv recorded
            if(list.contains("pv")){
                int index = list.indexOf("pv");
                index++;
                while (index < list.size()){
                   String move = list.get(index);
                   builder.append(move.substring(0, 4));
                   builder.append(" ");
                   index++;
                }
            }
        }
        return builder.toString();
    }
}
