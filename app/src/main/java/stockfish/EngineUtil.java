package stockfish;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Collins Magondu
 */

public class EngineUtil {
    private static BufferedWriter bufferedWriter;
    private static Executor executor = Executors.newSingleThreadExecutor(); // Manage input handling
    private static Executor callback = Executors.newSingleThreadExecutor(); // Manage output handling
    private static BufferedReader bufferedReader;
    private static long skillLevel;


    public static long getSkillLevel() {
        return skillLevel;
    }

    static void setBufferedReader(BufferedReader bufferedReader) {
        EngineUtil.bufferedReader = bufferedReader;
    }

    public static long getEloFromSkillLevel() {
        return (((skillLevel - 1) * 75) + 1350);
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
        executor.execute(() -> {
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
            callback.execute(() -> responseCallback.onResponse(finalResponses));
        });
    }

    public static void submit(String command, String responseIdentifier, ResponseCallback responseCallback){
        executor.execute(() -> {
            handleInput(command);
            callback.execute(() -> responseCallback.onResponse(Collections.singletonList(handleOutput(responseIdentifier))));
        });
    }

    public static void setSkillLevel(long skill) {
        skillLevel = skill;
        Log.d("UCIOption",UCIOption.SKILL_LEVEL.setValue(skillLevel).toString());
        handleInput(UCIOption.SKILL_LEVEL.setValue(skillLevel).toString());
        submit("isready\n", "readyok", responses -> Log.d("RESPONSE", responses.get(0)));

    }

    public static void setUCIELORating(long rating) {
        handleInput(UCIOption.UCI_LimitStrength.setBool(true).toBoolString());
        handleInput(UCIOption.UCI_Elo.setValue(rating).toString());
        submit("isready\n", "readyok", responses -> Log.d("RESPONSE", responses.get(0)));
    }

    public interface OnResponseListener{
        void onResponse(String moves);
        void onStockfishFullResponse(String response);
    }

    public interface ResponseCallback {
        void onResponse(List<String> responses);
    }
}
