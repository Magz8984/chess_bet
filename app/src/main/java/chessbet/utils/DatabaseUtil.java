package chessbet.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseUtil {
    // Tables
    public static String matchables = "matchables";
    public static String matches = "matches";
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
}
