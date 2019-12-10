package stockfish;

/**
 * @author Collins Magondu
 */
public interface UCImpl {
    void setUCI(EngineUtil.Response response);
    void isReady(EngineUtil.Response response);
    void getBestMove(int depth, long ms, long pv);
    void getBestMove(int depth, long ms);
}
