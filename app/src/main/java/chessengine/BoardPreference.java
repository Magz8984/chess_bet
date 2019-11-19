package chessengine;

/**
 * @author Collins Magondu
 *
 * Class allows one to set any sharedPreference required for Board Activity
 */

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
        return  sharedPreferences.getInt("Dark", Color.GRAY);
    }
    public int getWhite() {
        return  sharedPreferences.getInt("White", Color.WHITE);
    }
    public void setDark(int dark) {
        editor.putInt("Dark",dark);
        editor.commit();
    }
    public void setWhite(int white) {
        editor.putInt("White",white);
        editor.commit();
    }
    public void setBoardState(String state){

    }
    public void setPGNState(String state){
        editor.putString("state",state);
    }

    public String getGameState(){
        return sharedPreferences.getString("state", null);
    }
}
