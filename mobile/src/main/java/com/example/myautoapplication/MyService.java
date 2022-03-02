package com.example.myautoapplication;

import android.app.Service;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myautoapplication.datamodel.Audio;
import com.example.myautoapplication.datamodel.MediaUtil;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class MyService extends Service {

    private static final String TAG = "MyService";

    public static final String SERVICE_INTENT_ACTION = "service_action";
    public static final String MEDIA_SOURCE_STOP_ACTION = "media_source_stop_action";
    public static final String SERVICE_INTENT_INFO = "info";

    private final MyServiceBinder mBinder = new MyServiceBinder();

    private String mSourcePath = null;
    private MediaPlayer mPlayer = null;
    private boolean mSourceEnableRestart = false;
    private boolean mIsVideoPlaying = false;
    private int mCurVideoID = -1;
    private long mCurDuration = 0;

    public class MyServiceBinder extends Binder {
        public MyService getMyService() {
            return MyService.this;
        }
    }

    private Intent mServiceIntent = new Intent();

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mServiceIntent.setAction(SERVICE_INTENT_ACTION);
        mServiceIntent.putExtra(SERVICE_INTENT_INFO, "service start");
        sendBroadcast(mServiceIntent);
    }

    @Override
    public void onDestroy() {
        if (mPlayer != null) {
            stopSource();
            mPlayer = null;
        }
        super.onDestroy();
    }

    /*
     * play music
     * */

    public void setSourcePath(String path) {
        mSourcePath = path;
    }

    public boolean getPlayState() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            return true;
        }
        return false;
    }

    public void playSource() {
        Thread thread = new Thread(() -> {
            try {
                synchronized (this) {
                    if (mPlayer == null) {
                        mPlayer = new MediaPlayer();
                    }
                    if (!reStartSource()) {
                        mPlayer.setDataSource(mSourcePath);
                        mPlayer.prepare();
                        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mPlayer.start();
                            }
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public void pauseSource() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            mSourceEnableRestart = true;
        }
    }

    public boolean reStartSource() {
        if (mPlayer != null
                && mSourceEnableRestart
                && !mPlayer.isPlaying()) {
            mPlayer.start();
            return true;
        }
        return false;
    }

    public void stopSource() {
        if (mPlayer != null
                && mSourcePath != null) {
            mPlayer.stop();
            mPlayer.reset();

            mSourceEnableRestart = false;

            mServiceIntent.setAction(MEDIA_SOURCE_STOP_ACTION);
            sendBroadcast(mServiceIntent);
        }
    }

    public void setCurVideoInfo(boolean isVideoPlaying, int curVideoID, long curDuration) {
        mIsVideoPlaying = isVideoPlaying;
        mCurVideoID = curVideoID;
        mCurDuration = curDuration;
    }

    public long getCurDuration() {
        return mCurDuration;
    }

    public int getCurVideoID() {
        return mCurVideoID;
    }

    public boolean getIsVideoPlaying () {
        return mIsVideoPlaying;
    }
}