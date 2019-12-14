package chessbet.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
}
