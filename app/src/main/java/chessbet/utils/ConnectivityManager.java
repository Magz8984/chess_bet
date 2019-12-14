/*
  @author Collins Magondu 1/12/2019
 */
package chessbet.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import chessbet.domain.Constants;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ConnectivityManager {
    private NetworkTester networkTester = new NetworkTester();

    public void setConnectionStateListener(ConnectionStateListener connectionStateListener){
        networkTester.setConnectionStateListener(connectionStateListener);
    }

    public void stopListening(){
        networkTester.stopListening();
    }

    public void startListening(){
        networkTester.execute();
    }

    public static class NetworkTester extends AsyncTask<Void,Void,Void>{
        private long startTime;
        private ConnectionStateListener connectionStateListener;
        private long endTime;
        private long fileSize;
        OkHttpClient client = new OkHttpClient();
        Timer timer =  new Timer();


        private void setConnectionStateListener(ConnectionStateListener connectionStateListener) {
            this.connectionStateListener = connectionStateListener;
        }

        private void stopListening (){
            timer.cancel();
        }

        @Override
        protected Void doInBackground(Void... voids) {
           timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    startTime = System.currentTimeMillis(); // Start time of execution
                    Request request = new Request.Builder()
                            .url(Constants.UTILITY_PROFILE).build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            connectionStateListener.onConnectionStateChanged(ConnectionQuality.UNKNOWN);
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if(!response.isSuccessful()){
                                connectionStateListener.onConnectionStateChanged(ConnectionQuality.UNKNOWN);
                                throw new IOException(response.message());
                            }

                            try (InputStream inputStream = Objects.requireNonNull(response.body()).byteStream()) {
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                byte[] buffer = new byte[1024];
                                while (inputStream.read(buffer) != -1) {
                                    byteArrayOutputStream.write(buffer);
                                }

                                fileSize = byteArrayOutputStream.size();
                            }

                            endTime = System.currentTimeMillis();
                            double timeTaken = Math.floor( endTime - startTime);
                            double timeTakenSeconds = timeTaken / 1000;
                            final int kbps = (int) Math.floor(1024 / timeTakenSeconds);
                            if(kbps >= ConnectionQuality.EXCELLENT.getSpeed()){
                                connectionStateListener.onConnectionStateChanged(ConnectionQuality.EXCELLENT);
                            } else if (kbps >= ConnectionQuality.GOOD.getSpeed()){
                                connectionStateListener.onConnectionStateChanged(ConnectionQuality.GOOD);
                            } else if (kbps >= ConnectionQuality.AVERAGE.getSpeed()){
                                connectionStateListener.onConnectionStateChanged(ConnectionQuality.AVERAGE);
                            } else {
                                connectionStateListener.onConnectionStateChanged(ConnectionQuality.POOR);
                            }

                            double speed = fileSize/ timeTaken;
                            Log.d("SPEED_NET", speed + "Bytes Per Sec");
                            Log.d("SPEED_NET", kbps + "KBPS Per Sec");
                        }
                    });
                }
            },3000, 1000);
            return null;
        }
    }
    /** To be implemented by the listener class */
    public interface ConnectionStateListener{
        void onConnectionStateChanged(ConnectionQuality connectionQuality);
    }
    /**
     * Specifies the minimum down speed at Kilobytes per second
     */
    public enum ConnectionQuality {
        EXCELLENT {
            @Override
            int getSpeed() {
                return 2500;
            }

            @Override
            public String toString() {
                return "EXCELLENT";
            }
        },
        AVERAGE{
            @Override
            int getSpeed() {
                return 550;
            }

            @Override
            public String toString() {
                return "AVERAGE";
            }
        },
        POOR{
            @Override
            int getSpeed() {
                return 150;
            }

            @Override
            public String toString() {
                return "POOR";
            }
        },
        GOOD {
            @Override
            int getSpeed() {
                return 2000;
            }

            @Override
            public String toString() {
                return "GOOD";
            }
        },
        UNKNOWN {
            @Override
            int getSpeed() {
                return 0;
            }

            @Override
            public String toString() {
                return "UNKNOWN";
            }
        };
        abstract int getSpeed();
    }
}
