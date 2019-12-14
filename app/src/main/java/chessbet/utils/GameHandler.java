package chessbet.utils;

import android.os.AsyncTask;

import chessbet.api.MatchAPI;
import chessbet.domain.MatchEvent;
import chessbet.domain.MatchResult;
import chessbet.domain.MatchableAccount;
import chessbet.domain.RemoteMove;
import chessbet.services.GameListener;

public class GameHandler extends AsyncTask<Integer,Void,Void> implements GameListener {
    private int flag = 0;
    private static GameHandler INSTANCE = new GameHandler();

    private MatchResult matchResult;
    private MatchableAccount matchableAccount;

    public static final int GAME_INTERRUPTED_FLAG = 18305;
    public static final int GAME_FINISHED_FLAG = 40934;
    public static final int GAME_DRAWN_FLAG = 13780;


    public void setMatchResult(MatchResult matchResult) {
        this.matchResult = matchResult;
    }

    public void setMatchableAccount(MatchableAccount matchableAccount) {
        this.matchableAccount = matchableAccount;
    }

    public static GameHandler getInstance() {
        return INSTANCE;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Integer... flags) {
        flag = flags[0];
        MatchAPI.get().evaluateMatch(matchResult);
        matchResult = null;
        return null;
    }

    public void sendMatchEvent(){
        switch (flag){
            case GAME_DRAWN_FLAG:
                onGameDraw();
                break;
            case GAME_FINISHED_FLAG:
                onGameFinished();
                break;
            case GAME_INTERRUPTED_FLAG:
                onGameInterrupted();
                break;
            default:
                break;
        }
        // CLEAR SINGLETON MEMBERS
        matchableAccount = null;
        matchResult  = null;
        flag = 0;
    }

    @Override
    public boolean onGameInterrupted() {
        return sendMatchEvent(MatchEvent.INTERRUPTED);
    }

    @Override
    public boolean onGameDraw() {
        return sendMatchEvent(MatchEvent.DRAW);
    }

    @Override
    public boolean onGameFinished() {
        return sendMatchEvent(MatchEvent.FINISHED);
    }

    private boolean sendMatchEvent(MatchEvent matchEvent){
        // Game has started
        if(RemoteMove.get().getEvents().size() != 0){
            RemoteMove.get().addEvent(matchEvent);
            RemoteMove.get().send(matchableAccount.getMatchId(), matchableAccount.getSelf());
            return true;
        }
        return false;
    }
}