package chessbet.utils;

import android.database.Cursor;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import chessbet.domain.DatabaseMatch;
import chessbet.domain.MatchResult;

public class DatabaseUtil {
    // Tables
    public static final String matchables = "matchables";
    private static final String matches = "matches";
    private static final String match_task_node = "tasks";
    private static final String evaluationQueue = "evaluationQueue";
    private static final String info = ".info";
    private static final String connected = "connected";
    private static final String PRESENCE_PATH = "presence";


    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private DatabaseUtil(){
        throw new RuntimeException("Cannot be instantiated");
    }

    /**
     * Get user matchable account
     * @param uid user unique identifier
     * @return DatabaseReference
     */
    public static DatabaseReference getAccount(String uid) {
        return databaseReference.child(DatabaseUtil.matchables).child(uid);
    }

    public static DatabaseReference sendRemoteMove(String matchId, String self) {
        return databaseReference.child(DatabaseUtil.matches).child(matchId)
                .child("players")
                .child(self);
    }

    public static DatabaseReference getOpponentRemoteMove (String matchId, String opponent){
        return  databaseReference.child(DatabaseUtil.matches).child(matchId)
                .child("players")
                .child(opponent);
    }

    public static DatabaseReference getMatch (String matchId){
        return  databaseReference.child(DatabaseUtil.matches).child(matchId);
    }

    public static DatabaseReference getOnlineUsers(){
        return databaseReference.child(PRESENCE_PATH);
    }

    public static DatabaseReference getMatchTask(MatchResult matchResult){
        return databaseReference.child(DatabaseUtil.evaluationQueue).child(DatabaseUtil.match_task_node);
    }

    public static List<DatabaseMatch> getMatchesFromLocalDB(Cursor cursor){
        List<DatabaseMatch> databaseMatches = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                DatabaseMatch databaseMatch = new DatabaseMatch();
                databaseMatch.setMatchId(cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.COLUMN_MATCH_ID)));
                databaseMatch.setOpponentPic(cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.COLUMN_OPPONENT_PIC)));
                databaseMatch.setOpponentUserName(cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.COLUMN_OPPONENT_USERNAME)));
                databaseMatches.add(databaseMatch);
            }while (cursor.moveToNext());
        }
        return databaseMatches;
    }

    public static DatabaseReference getConnected(){
        return databaseReference.child(info).child(connected);
    }

    static DatabaseMatch getMatchFromLocalDB(Cursor cursor){
        if(cursor.moveToFirst()){
            DatabaseMatch databaseMatch = new DatabaseMatch();
            databaseMatch.setMatchId(cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.COLUMN_MATCH_ID)));
            databaseMatch.setOpponentPic(cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.COLUMN_OPPONENT_PIC)));
            databaseMatch.setOpponentUserName(cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.COLUMN_OPPONENT_USERNAME)));
            return databaseMatch;
        }
        return null;
    }
}
