package stockfish;

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
            response = removePawnPromotionPieceTypes(response);
            if(MOVE_LAN_REGEXP.matcher(response).matches()){
                responses.add(response);
            }
        }
        return responses;
    }


    private String removePawnPromotionPieceTypes(String response){
        if(response.endsWith("q") ||  response.endsWith("r") || response.endsWith("n") || response.endsWith("b")) {
            return response.replace("q", "")
                    .replace("r", "")
                    .replace("b", "")
                    .replace("n", "");
        }
        return response;
    }

}
