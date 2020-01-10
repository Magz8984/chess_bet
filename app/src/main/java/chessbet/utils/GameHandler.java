package chessbet.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import chessbet.api.AccountAPI;
import chessbet.api.MatchAPI;
import chessbet.domain.Match;
import chessbet.domain.MatchResult;
import chessbet.domain.MatchableAccount;
import chessbet.services.OpponentListener;

public class GameHandler extends AsyncTask<Integer,Void,Void> {
    private static GameHandler INSTANCE = new GameHandler();

    private MatchResult matchResult;

    public static final int GAME_INTERRUPTED_FLAG = 18305;
    public static final int GAME_FINISHED_FLAG = 40934;
    public static final int GAME_DRAWN_FLAG = 13780;
    public static final int GAME_TIMER_LAPSED = 472489;

    public void setMatchResult(MatchResult matchResult) {
        this.matchResult = matchResult;
    }

    public static GameHandler getInstance() {
        return INSTANCE;
    }

    /**
     * Resets Instance Object
     */
    public void resetInstance(){
        INSTANCE = new GameHandler();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Integer... flags) {
        MatchAPI.get().evaluateMatch(matchResult);
        matchResult = null;
        return null;
    }


    /**
     * To be used in the background to collect data opponent for reference
     */
    public static class BackgroundMatchBuilder extends AsyncTask<Context,Void,Void> {
        private MatchableAccount matchableAccount;
        private OpponentListener opponentListener;

        public void setMatchableAccount(MatchableAccount matchableAccount) {
            this.matchableAccount = matchableAccount;
        }

        @Override
        protected Void doInBackground(Context... contexts) {
            AccountAPI.get().getAUser(matchableAccount.getOpponentId(), user -> {
                if(user != null){
                   SQLDatabaseHelper sqlDatabaseHelper = new SQLDatabaseHelper(contexts[0]);
                   sqlDatabaseHelper.addMatch(matchableAccount.getMatchId(), user.getProfile_photo_url(), user.getUser_name());
                   Match match = DatabaseUtil.getMatchFromLocalDB(sqlDatabaseHelper.getMatch(matchableAccount.getMatchId()));
                   MatchAPI.get().setCurrentMatch(match);
                    assert match != null;
                    opponentListener.onOpponentReceived(match.getOpponentUserName());
                }
            });
            return null;
        }

        @Override
        protected void onPreExecute() {
            Log.d(BackgroundMatchBuilder.class.getSimpleName(), "START");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d(BackgroundMatchBuilder.class.getSimpleName(), "DONE");
        }

        public void setOpponentListener(OpponentListener opponentListener) {
            this.opponentListener = opponentListener;
        }
    }
}