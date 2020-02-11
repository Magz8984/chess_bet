package chessbet.utils;

import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;

import java.util.Date;

/**
 * @author Collins
 */

public class Util {
    /** Get current time*/
    public static String now(){
        Date date = new Date(System.currentTimeMillis());
        return (String) DateFormat.format("yyyy-MM-dd hh:mm:ss", date);
    }

    public static boolean textViewHasText(TextView textView){
        Log.d("UTILDFATA", textView.getText().toString().trim().length() + " " + textView.getText());
        return textView.getText().toString().trim().length() >= 1;
    }
}
