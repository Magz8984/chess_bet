package chessbet.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

public class Permissions{
    private Context context;
    public Permissions(Context context) {
        this.context = context;
    }

    public boolean checkCameraPermission() {
        //check if storage permission is enabled
        //return true if enabled
        //return false if not enabled
        boolean result = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    public boolean checkStoragePermission() {
        //check if storage permission is enabled
        //return true if enabled
        //return false if not enabled
        boolean result = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
}
