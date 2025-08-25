package com.dten.myservicedemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class MyService extends Service {

    private static final String TAG = "MyService";

    private String mName = "";

    private final IMyService.Stub mServiceStub = new IMyService.Stub() {

        @Override
        public String getName() throws RemoteException {
            return mName;
        }

        @Override
        public void setName(String name) throws RemoteException {
            Log.d(TAG, "name " + name);
            mName = name;
        }
    };

    public MyService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        // TODO: Return the communication channel to the service.
        return mServiceStub;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "My Service start!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "My Service destroy!");
    }
}