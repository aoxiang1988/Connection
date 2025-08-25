package com.dten.myservicedemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    private MyService mService = null;
    private boolean isBind = false;

    private Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        startService(new Intent(mContext, MyService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
//        bindService(
//                new Intent(mContext, MyService.class),
//                this,
//                BIND_ALLOW_OOM_MANAGEMENT
//        );//绑定服务
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//        MyService.MyServiceBinder binder = (MyService.MyServiceBinder) iBinder;
//        mService = binder.getMyService();
        isBind = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        isBind = false;
        mService = null;
    }
}