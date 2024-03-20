package com.example.nativelib;

import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import com.example.myautoapplication.shared.IMyAidlInterface;

public class NativeLib {

    Binder aidlBinder = new IMyAidlInterface.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    };

    public IBinder binder() {
        return aidlBinder;
    }
    // Used to load the 'nativelib' library on application startup.
    static {
        System.loadLibrary("nativelib");
    }

    /**
     * A native method that is implemented by the 'nativelib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    //public native void queryByCommand();
}