package com.example.myautoapplication.toolutils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ToolUtils {

    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_REQUEST_PERMISSION = 101;
    public static boolean checkPermission(Activity activity) {

        String[] permession = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };

        boolean needRequestPermissions = false;
        List<String> stringList = new ArrayList<>(Arrays.asList(permession));
        for (int i = 0; i < permession.length; i++) {
            Log.d(TAG, "检查" + permession[i] + "权限，in before ");
            if (ContextCompat.checkSelfPermission(activity,
                    permession[i])
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "没有" + permession[i] + "权限，正在申请权限 in before");
                needRequestPermissions = true;
            } else {
                Log.d(TAG, "已经有" + permession[i] + "权限， in before");
                stringList.remove(permession[i]);//不用申请这个权限，移除掉
            }
        }
        if (needRequestPermissions) {
            String[] needToRequestPermission = new String[stringList.size()];
            ActivityCompat.requestPermissions(activity,
                    stringList.toArray(needToRequestPermission),
                    MY_PERMISSIONS_REQUEST_PERMISSION);
        }
        return true;
    }
}
