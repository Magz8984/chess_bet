package chessbet.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "chess_bet";
    private static final String MATCHES_TABLE = "matches";
    public static final String COLUMN_MATCH_ID = "id";
    public static final String COLUMN_OPPONENT_PIC = "opic";
    public static final String COLUMN_OPPONENT_USERNAME = "ouname";
    private static final int DATABASE_VERSION = 1;

    public SQLDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = " CREATE TABLE IF NOT EXISTS " + MATCHES_TABLE + " (" + COLUMN_MATCH_ID + " VARCHAR PRIMARY KEY, " + COLUMN_OPPONENT_PIC +
                " VARCHAR(200), " + COLUMN_OPPONENT_USERNAME + " VARCHAR(100))";
        sqLiteDatabase.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sql = "DROP TABLE IF EXISTS " + MATCHES_TABLE;
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }


    void addMatch(String matchId, String opponentPic, String opponentUserName){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_MATCH_ID, matchId);
        contentValues.put(COLUMN_OPPONENT_PIC, opponentPic);
        contentValues.put(COLUMN_OPPONENT_USERNAME, opponentUserName);
        sqLiteDatabase.insert(MATCHES_TABLE, null, contentValues);
    }

    public Cursor getMatches(){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String sql =  "SELECT * FROM " + MATCHES_TABLE + " LIMIT 10;";
        return sqLiteDatabase.rawQuery(sql, null);
    }

    public void deleteAllMatches(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String sql = "DELETE  FROM "  + MATCHES_TABLE + ";";
        sqLiteDatabase.execSQL(sql);
    }

    public void close(){
        this.getReadableDatabase().close();
    }
}
