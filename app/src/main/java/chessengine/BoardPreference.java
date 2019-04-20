package chessengine;

import android.content.SharedPreferences;
import android.graphics.Color;

public class BoardPreference{
    private SharedPreferences.Editor editor;
    private  SharedPreferences sharedPreferences;
    BoardPreference(SharedPreferences sharedPreferences){
        this.sharedPreferences=sharedPreferences;
        editor=sharedPreferences.edit();
    }
    public int getDark() {
        return  sharedPreferences.getInt("Dark", Color.GRAY);
    }
    public int getWhite() {
        return  sharedPreferences.getInt("White", Color.GRAY);
    }
    public void setDark(int dark) {
        editor.putInt("Dark",dark);
        editor.commit();
    }
    public void setWhite(int white) {
        editor.putInt("Dark",white);
        editor.commit();
    }
}
