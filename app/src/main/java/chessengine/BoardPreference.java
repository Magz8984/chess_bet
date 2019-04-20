package chessengine;

import android.content.SharedPreferences;
import android.graphics.Color;

public class BoardPreference{
    private SharedPreferences.Editor editor;
    private  SharedPreferences sharedPreferences;
    public  BoardPreference(SharedPreferences sharedPreferences){
        this.sharedPreferences=sharedPreferences;
        editor=sharedPreferences.edit();
        editor.apply();
    }
    public int getDark() {
        return  sharedPreferences.getInt("Dark", Color.MAGENTA);
    }
    public int getWhite() {
        return  sharedPreferences.getInt("White", Color.CYAN);
    }
    public void setDark(int dark) {
        editor.putInt("Dark",dark);
        editor.commit();
    }
    public void setWhite(int white) {
        editor.putInt("White",white);
        editor.commit();
    }
}
