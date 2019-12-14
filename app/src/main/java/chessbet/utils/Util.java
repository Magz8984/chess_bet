package chessbet.utils;

import android.text.format.DateFormat;

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
}
