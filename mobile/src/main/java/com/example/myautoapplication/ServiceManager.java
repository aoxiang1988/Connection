// ServiceManager.java
package com.example.myautoapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class ServiceManager {
    private MyService mService = null;
    private boolean isBind = false;
    private final Context context;
    private final MainViewModel viewModel;
    
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MyServiceBinder binder = (MyService.MyServiceBinder) service;
            mService = binder.getMyService();
            isBind = true;
            viewModel.setService(mService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = false;
            viewModel.setService(null);
        }
    };

    public ServiceManager(Context context, MainViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;
    }

    public void startService() {
        context.startService(new Intent(context, MyService.class));
    }

    public void bindService() {
        context.bindService(new Intent(context, MyService.class), serviceConnection, Context.BIND_ALLOW_OOM_MANAGEMENT);
    }

    public void unbindService() {
        if (isBind) {
            context.unbindService(serviceConnection);
            isBind = false;
        }
    }

    public MyService getService() {
        return mService;
    }
}
