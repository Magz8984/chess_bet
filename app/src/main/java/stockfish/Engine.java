package stockfish;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Objects;

import chessbet.Application;

public class Engine implements UCImpl {
    static {
        System.loadLibrary("nativeutil");
    }

    private String engineDir;
    private static boolean isReady = false;
    private static boolean isUCIEnabled = true;
    private Context context;
    private Process process;
    public void start(){
        try {
            context = Application.getContext();
            handleProcessCreation();
            startProcess();
//            if(!isReady){
            isReady(response -> {
                if (response.equals("readyok")){
                    isReady = true;
                }
                Log.d("RESPONSE_ENG", response);
            });
//            }
            setUCI(response -> {
                if(response.equals("uciok")){
                    isUCIEnabled = true;
                }
                Log.d("RESPONSE_ENG", response);
            });

        } catch (Exception e) {
            Log.e("ERROR MESSAGE", Objects.requireNonNull(e.getMessage()));
            e.printStackTrace();
        }
    }

    private void handleProcessCreation(){
        String enginePath = enginePath();
        engineDir = copyFiles(enginePath);
        chmod(engineDir);
    }

    // Start Process
    private void startProcess(){
        File engineWorkingDirectory = new File(engineDir);
        boolean state = engineWorkingDirectory.setExecutable(true, false);
        ProcessBuilder processBuilder = new ProcessBuilder(engineDir);
        processBuilder.redirectErrorStream(true);
        if(engineWorkingDirectory.canRead() && engineWorkingDirectory.canExecute()){
                try {
                    if(state){
                        process = processBuilder.start();
                        // Set IO streams from process
                        EngineUtil.setBufferedReader(new BufferedReader(new InputStreamReader(process.getInputStream())));
                        EngineUtil.setBufferedWriter(new BufferedWriter(new OutputStreamWriter(process.getOutputStream())));
                    }
                    java.lang.reflect.Field f = process.getClass().getDeclaredField("pid");
                    f.setAccessible(true);
                    int pid = f.getInt(process);
                    changePriority(pid, 1);

                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    private String enginePath(){
        if(Build.CPU_ABI.startsWith("x86_64")){
            return "x86_64" + "/stockfish";
        }else if (Build.CPU_ABI.startsWith("x86")){
            return "x86" + "/stockfish";
        }else if (Build.CPU_ABI.startsWith("armeabi-v7a")){
            return "armeabi-v7a" + "/stockfish";
        }else if (Build.CPU_ABI.startsWith("arm64-v8a")){
            return "arm64-v8a" + "/stockfish";
        }
        return null;
    }

    /** Copy engine executable file to other sections */
    private String copyFiles(String enginePath) {
        if(enginePath == null){
            throw new RuntimeException("Engine path cannot be found");
        }
        File toFile = new File(context.getFilesDir(), "engine.exe");
        // If file exists then return the file path
        if(toFile.exists()){
            return toFile.getAbsolutePath();
        }

        try {
            try(InputStream inputStream = context.getAssets().open(enginePath);
                OutputStream outputStream = new FileOutputStream(toFile)){
                byte[] buffer = new byte[8200];

                while(true){
                    int len =  inputStream.read(buffer);
                    if(len <= 0){
                        break;
                    }
                    outputStream.write(buffer, 0, len);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  toFile.getAbsolutePath();
    }

    @Override
    public void setUCI(EngineUtil.Response response) {
      EngineUtil.pipe(UCIOption.UCI.command(),response);
    }

    @Override
    public void isReady(EngineUtil.Response response) {
        EngineUtil.pipe(UCIOption.IS_READY.command(), response);
    }

    @Override
    public void getBestMove(int depth, long ms, long pv) {

    }

    @Override
    public void getBestMove(int depth, long ms) {

    }

    public boolean isEngineRunning(){
        return isUCIEnabled && isReady;
    }


    static boolean isRunning(){
        return  isUCIEnabled && isReady;
    }


    /** Destroy Engine Process */
    public void destroyEngineProcess(){
        process.destroy();
    }
    /**Sets engine as an executable*/
    static native boolean chmod(String exePath);
    /** Sets process priority*/

    static native void changePriority(int pid,int priority);
}
