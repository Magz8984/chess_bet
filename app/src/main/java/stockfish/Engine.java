package stockfish;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

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

public class Engine {
    static {
        System.loadLibrary("nativeutil");
    }
    private String engineDir;
    private Context context;

    public void start(){
        try {
            context = Application.getContext();
            findEnginePath();
            startProcess();
        } catch (Exception e) {
            Log.e("ERROR MESSAGE", Objects.requireNonNull(e.getMessage()));
            e.printStackTrace();
        }
    }

    private void findEnginePath(){
        String enginePath = enginePath();
        engineDir = copyFiles(enginePath);
        chmod(engineDir);
    }

    /**
     * Creates a process from a process builder that is reused.
     * @return Process
     */
    private Process createProcess() {
        try {
            File engineWorkingDirectory = new File(engineDir);
            boolean state = engineWorkingDirectory.setExecutable(true, false);
            ProcessBuilder processBuilder = new ProcessBuilder(engineDir);
            processBuilder.redirectErrorStream(true);
            if (engineWorkingDirectory.canRead() && engineWorkingDirectory.canExecute()) {
                if (state) {
                    return processBuilder.start();
                }
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        return null;
    }

    // Start Process
    private void startProcess(){
        Process process = createProcess();
        if(process != null){
            try {
                // Set IO streams from process
                EngineUtil.setBufferedReader(new BufferedReader(new InputStreamReader(process.getInputStream())));
                EngineUtil.setBufferedWriter(new BufferedWriter(new OutputStreamWriter(process.getOutputStream())));
                java.lang.reflect.Field f;
                f = process.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                int pid = f.getInt(process);
                changePriority(pid, 1);
            } catch (Exception e) {
                Crashlytics.logException(e);
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

    /**Sets engine as an executable*/
    static native boolean chmod(String exePath);
    /** Sets process priority*/

    static native void changePriority(int pid,int priority);
}
