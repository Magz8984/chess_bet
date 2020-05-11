package stockfish;

import java.util.List;

abstract class QueryResult {
    String response;

    QueryResult(String response){
        this.response = response;
    }

    public abstract List<String> filteredResponse();

    public String getResponse() {
        return response;
    }
}
