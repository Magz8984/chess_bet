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
    public static String matchables = "matchables";
    private static String matches = "matches";
    private static String match_task_node = "tasks";
    private static String evaluationQueue = "evaluationQueue";
    private static String info = ".info";
    private static String connected = "connected";


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

    public static DatabaseMatch getMatchFromLocalDB(Cursor cursor){
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
