package chessengine;

import android.content.Context;

import com.chess.engine.ECOBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import chessbet.Application;

/**
 * ECO Book moves Execute
 */

public class ECOBook {
    private ECOBuilder builder;
    private String moveLog;
    private OnGetECOListener listener;
    private Context context;

    ECOBook(){
        context = Application.getContext();
        File bookDatFile = new File(context.getFilesDir(), "eco.dat");
        builder = new ECOBuilder().setFile(bookDatFile);
        if(!bookDatFile.exists()){
            File file = copyEcoMoves();
            if(file == null){
                throw new RuntimeException("ECO BOOK ERROR");
            }
            boolean status = builder.writeObjects(file);
            if(!status){
                throw new RuntimeException("ECO BOOK ERROR");
            }
            else{
                builder.build();
            }
        }
        else {
            builder.readObjects(bookDatFile);
        }
    }

    private File copyEcoMoves(){
        try {
            File file = new File(context.getFilesDir(), "eco.pgn");
            if(file.exists()){
                return file;
            }
            InputStream inputStream = context.getAssets().open("eco.pgn");
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[8200];

            while (true) {
                int len = inputStream.read(buffer);
                if (len <= 0) {
                    break;
                }
                outputStream.write(buffer, 0, len);
            }
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    void setMoveLog(String moveLog) {
        this.moveLog = moveLog;
    }

    void setListener(OnGetECOListener listener) {
        this.listener = listener;
    }

    void startListening(){
       new Thread(() -> {
           for (ECOBuilder.ECO eco: builder.getOpenings()) {
               if(eco.getMoveLog().equals(moveLog)){
                   this.listener.onGetECO(eco);
               }
           }
       }).start();
    }

    public interface OnGetECOListener{
        void onGetECO(ECOBuilder.ECO eco);
    }
}
