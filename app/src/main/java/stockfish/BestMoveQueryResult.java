package stockfish;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class BestMoveQueryResult extends QueryResult {
    private static final Pattern MOVE_LAN_REGEXP = Pattern.compile("([a-h][0-8])([a-h][0-8])");

    BestMoveQueryResult(String response) {
        super(response);
    }

    @Override
    public List<String> filteredResponse() {
        List<String> responses = new ArrayList<>();
        for(String response: this.response.split(" ")){
            if(MOVE_LAN_REGEXP.matcher(response).matches()){
                responses.add(response);
            }
        }
        return responses;
    }

}
