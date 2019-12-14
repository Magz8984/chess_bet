package chessbet.utils;

import android.database.Cursor;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import chessbet.domain.Match;
import chessbet.domain.MatchResult;

public class DatabaseUtil {
    // Tables
    public static String matchables = "matchables";
    private static String matches = "matches";
    private static String match_task_node = "tasks";
    private static String evaluationQueue = "evaluationQueue";


    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private DatabaseUtil(){
        throw new RuntimeException("Cannot be instantiated");
    }

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

    public static List<Match> getMatchesFromLocalDB(Cursor cursor){
        List<Match> matches = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                Match match = new Match();
                match.setMatchId(cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.COLUMN_MATCH_ID)));
                match.setOpponentPic(cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.COLUMN_OPPONENT_PIC)));
                match.setOpponentUserName(cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.COLUMN_OPPONENT_USERNAME)));
                matches.add(match);
            }while (cursor.moveToNext());
        }
        return matches;
    }
}
