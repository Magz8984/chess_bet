package chessbet;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.google.firebase.FirebaseApp;

import chessbet.recievers.ConnectivityReceiver;

public class Application extends MultiDexApplication{
    private static Application INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new ConnectivityReceiver(),intentFilter);
        FirebaseApp.initializeApp(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Application getInstance() {
        return INSTANCE;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener connectivityListener){
        ConnectivityReceiver.connectivityReceiverListener = connectivityListener;
    }

    public static Context getContext(){
        return INSTANCE;
    }
}
