package chessbet.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * @author  Collins Magondu
 */
public class GameManager {
    public static boolean isConnected(Context context){
        try{
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager != null) {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                Log.d("CONNECTE",Boolean.toString(networkInfo.isConnected()));
                return networkInfo.isConnected();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
}
