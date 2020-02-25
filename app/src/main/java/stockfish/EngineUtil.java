package stockfish;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Collins Magondu
 */

public class EngineUtil {
    private static BufferedWriter bufferedWriter;
    private static ExecutorService executor = Executors.newFixedThreadPool(2); // Manage input handling
    private static ExecutorService callback = Executors.newSingleThreadExecutor(); // Manage output handling
    private static BufferedReader bufferedReader;
    private static long skillLevel;


    public static long getSkillLevel() {
        return skillLevel;
    }

    static void setBufferedReader(BufferedReader bufferedReader) {
        EngineUtil.bufferedReader = bufferedReader;
    }

    /**
     * Allows for relatively easy games for easier levels
     * @return
     */
    public static long getDepthFromSkillLevel() {
        if(skillLevel > 15) {
            return 13;
        } else if (skillLevel > 10) {
            return 6;
        } else if (skillLevel > 4) {
            return 3;
        } else if (skillLevel >= 0) {
            return 1;
        }
        return 13;
    }

    static void setBufferedWriter(BufferedWriter bufferedWriter) {
        EngineUtil.bufferedWriter = bufferedWriter;
    }

    static void pipe(String line){
         try {
             bufferedWriter.write(line + "\n");
             bufferedWriter.flush();
         }catch (Exception ex){
             ex.printStackTrace();
         }
    }

    // Handle stockfish output
    private static String handleOutput(String identifier) {
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null){
                if(line.contains(identifier)){
                    return line;
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    // Hanlde stockfish input
    private static void handleInput(String input) {
        try {
            bufferedWriter.write(input);
            bufferedWriter.flush();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void submit(Query query, ResponseCallback responseCallback) {
        executor.submit(() -> {
            String output;
            List<String> responses = new ArrayList<>();
            handleInput(query.toString());
            if (query.getType() == QueryType.BEST_MOVE) {
                output = Objects.requireNonNull(handleOutput("best"));
                responses = new BestMoveQueryResult(output).filteredResponse();
            } else if  (query.getType() == QueryType.CENTI_PAWN_VALUE){
                responses = Collections.singletonList(handleOutput("cp"));
            }
            List<String> finalResponses = responses;
            callback.submit(() -> responseCallback.onResponse(finalResponses));
        });
    }

    public static void submit(String command, String responseIdentifier, ResponseCallback responseCallback){
        executor.submit(() -> {
            handleInput(command);
            callback.submit(() -> responseCallback.onResponse(Collections.singletonList(handleOutput(responseIdentifier))));
        });
    }

    public static void setSkillLevel(long skill) {
        skillLevel = skill;
        Log.d("UCIOption",UCIOption.SKILL_LEVEL.setValue(skillLevel).toString());
        handleInput(UCIOption.SKILL_LEVEL.setValue(skillLevel).toString());
        submit("isready\n", "readyok", responses -> Log.d("ISREADY", responses.get(0)));
    }

    public interface OnResponseListener{
        void onResponse(String moves);
        void onStockfishFullResponse(String response);
    }

    public interface ResponseCallback {
        void onResponse(List<String> responses);
    }
}
