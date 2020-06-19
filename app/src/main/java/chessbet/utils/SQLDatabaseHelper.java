package chessbet.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import chessbet.app.com.BuildConfig;

public class SQLDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = BuildConfig.DATABASE_NAME;
    private static final String MATCHES_TABLE = "matches";
    static final String COLUMN_MATCH_ID = "id";
    static final String COLUMN_OPPONENT_PIC = "opic";
    static final String COLUMN_OPPONENT_USERNAME = "ouname";
    private static final String COLUMN_MATCH_TIMESTAMP = "timestamp";
    static String COLUMN_MATCH_TYPE = "matchtype";
    private static final int DATABASE_VERSION = 5;

    public SQLDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = " CREATE TABLE IF NOT EXISTS " + MATCHES_TABLE + " (" + COLUMN_MATCH_ID + " VARCHAR PRIMARY KEY, " + COLUMN_OPPONENT_PIC +
                " VARCHAR(200), " + COLUMN_OPPONENT_USERNAME + " VARCHAR(100), " + COLUMN_MATCH_TIMESTAMP + " INT)";

        sqLiteDatabase.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sql = "DROP TABLE IF EXISTS " + MATCHES_TABLE + ";";
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }


    void addMatch(String matchId, String opponentPic, String opponentUserName, String matchType){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_MATCH_ID, matchId);
        contentValues.put(COLUMN_OPPONENT_PIC, opponentPic);
        contentValues.put(COLUMN_OPPONENT_USERNAME, opponentUserName);
        contentValues.put(COLUMN_MATCH_TYPE, matchType);
        contentValues.put(COLUMN_MATCH_TIMESTAMP, System.currentTimeMillis());
        sqLiteDatabase.insert(MATCHES_TABLE, null, contentValues);
    }

    public Cursor getMatches(){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String sql =  "SELECT * FROM " + MATCHES_TABLE + " ORDER BY " + COLUMN_MATCH_TIMESTAMP + " DESC LIMIT 10;";
        return sqLiteDatabase.rawQuery(sql, null);
    }

    Cursor getMatch(String id){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String sql = "SELECT * FROM " + MATCHES_TABLE + " WHERE " + COLUMN_MATCH_ID +  " = \"" + id  +"\";";
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
