package com.example.myautoapplication.toolutils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ToolUtils {

    private static final String TAG = "MainActivity";
    public static final int MY_PERMISSIONS_REQUEST_PERMISSION = 101;
    public static boolean checkPermission(Activity activity) {

        String[] permission = getStrings();

        boolean needRequestPermissions = false;
        for (String s : permission) {
            Log.d(TAG, "检查" + s + "权限，in before ");
            if (ContextCompat.checkSelfPermission(activity, s)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "没有" + s + "权限，正在申请权限 in before");
                needRequestPermissions = true;
            } else {
                Log.d(TAG, "已经有" + s + "权限， in before");
            }
        }
        if (needRequestPermissions) {
            ActivityCompat.requestPermissions(activity, permission, MY_PERMISSIONS_REQUEST_PERMISSION);
        }
        return !needRequestPermissions;
    }


    private static String[] getStrings() {
        String[] permission = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = new String[]{
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO,
            };
        } else {
            permission = new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
            };
        }
        return permission;
    }
}
