package chessengine;

import android.util.Log;

import androidx.annotation.NonNull;

import com.crashlytics.android.Crashlytics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import chessbet.app.com.BoardActivity;

/**
 * @author Collins 27/01/2020
 */
public class PGNHolder {
    private String white;
    private String black;
    private String event;
    private String date;
    private String pgn;
    private StringBuilder pgnBuilder;

    private PGNHolder(){
        pgnBuilder = new StringBuilder();
    }

    public void setBlack(String black) {
        this.black = black;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public void setPgn(String pgn) {
        this.pgn = pgn;
    }

    private StringBuilder getPgnBuilder() {
        return pgnBuilder;
    }

    public void setWhite(String white) {
        this.white = white;
    }

    public String getBlack() {
        return black;
    }

    public String getDate() {
        return date;
    }

    public String getEvent() {
        return event;
    }

    public String getPgn() {
        return pgn;
    }

    public String getWhite() {
        return white;
    }


    public String getPlayers(){
        return white +  " vs " + black;
    }

    @NonNull
    @Override
    public String toString() {
        return black + " vs " + white  + " Event  : " + event + " Date : " + date  + "\n PGN \n" + pgn;
    }

    public static List<PGNHolder> getPGNHoldersFromFileInputStream(InputStream inputStream){
        List<PGNHolder> pgnHolders = new ArrayList<>();
        try {
            if(inputStream != null){
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String string;
                int nonInfo = 0;
                PGNHolder pgnHolder = new PGNHolder();
                String pgn = sanitizePGNText(reader);
                Log.d(PGNHolder.class.getSimpleName(), pgn);

                reader = new BufferedReader(new StringReader(pgn));
                while ((string = reader.readLine()) != null) {
                    if (!string.contains("[")){
                        nonInfo++;
                    }
                    string = string.trim();

                    if(string.contains("Event")){
                        pgnHolder.setEvent(filterPGNString(string, "Event"));
                    } else if (string.contains("Date")){
                        pgnHolder.setDate(filterPGNString(string, "Date"));
                    } else if (string.contains("Black ")){
                        pgnHolder.setBlack(filterPGNString(string, "Black"));
                    } else if (string.contains("White ")){
                        pgnHolder.setWhite(filterPGNString(string, "White"));
                    }  else if (nonInfo > 0 && !string.contains("[")) {
                        pgnHolder.getPgnBuilder().append(" ");
                        pgnHolder.getPgnBuilder().append(string);
                        pgnHolder.setPgn(pgnHolder.getPgnBuilder().toString());

                    }
                    if(string.endsWith("1-0") ||
                            string.endsWith("0-1") ||
                            string.endsWith("1/2-1/2") ||
                            string.endsWith("*")){
                        pgnHolders.add(pgnHolder);
                        pgnHolder = new PGNHolder();
                    }
                }
                inputStream.close();
            }

            for (PGNHolder pgnHolder: pgnHolders) {
                if(pgnHolder.getPgn() == null || pgnHolder.getPgn().length() < 4){
                    pgnHolders.remove(pgnHolder);
                }
            }
        }catch (Exception ex){
            Crashlytics.logException(ex);
        }
        return pgnHolders;
    }

    private static String sanitizePGNText(BufferedReader bufferedReader) throws IOException {
        String string;
        StringBuilder builder = new StringBuilder();
        while ((string = bufferedReader.readLine())!= null){
            builder.append(string);
            builder.append(System.getProperty("line.separator"));
        }

        StringBuilder pgnBuilder = new StringBuilder();
        boolean commentFound = false;
        for (char c: builder.toString().toCharArray()) {
            if(c == '{'){
                commentFound = true;
            }
            if(!commentFound){
                pgnBuilder.append(c);
            }

            if(c == '}'){
                commentFound = false;
            }
        }
        return pgnBuilder.toString();
    }

    private static String filterPGNString(String string, String flag) {
       return string.replace("\"", "")
               .replace(flag + " ", "")
               .replace("[" , "")
               .replace("]", "");
    }
}
