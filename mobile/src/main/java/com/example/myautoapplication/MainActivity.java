package com.example.myautoapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myautoapplication.datamodel.Audio;
import com.example.myautoapplication.datamodel.MediaUtil;
import com.example.myautoapplication.media.SecondActivity;
import com.example.myautoapplication.toolutils.ToolUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private MyService mService = null;
    private boolean isBind = false;

    private String mInfo = "no info";

    private TextView mTextView;
    private Button mChangeActivityBut;
    private Button mPlayBut;
    private Button mStopBut;

    private IntentFilter mIntentFilter = new IntentFilter();
    private List<Audio> mAudioList = null;

    /* ***
     * 动态广播注册 *
     * ***/
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MyService.SERVICE_INTENT_ACTION)) {
                mInfo = intent.getStringExtra(MyService.SERVICE_INTENT_INFO);
                mTextView.setText(mInfo);
                mTextView.setTextColor(Color.RED);
                Log.d(TAG, "service broadcast come!!");
            } else if (intent.getAction().equals(MyService.MEDIA_SOURCE_STOP_ACTION)) {
                mPlayBut.setText(R.string.play_source);
            }
        }
    };

    /* ***
     * 绑定服务 *
     * ***/
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MyServiceBinder binder = (MyService.MyServiceBinder) service;
            mService = binder.getMyService();
            isBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = false;
        }
    };

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.butToActivity:
                    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                    startActivity(intent);
                    break;
                case R.id.butPlay:
                    if (mService != null) {
                        if (mService.getPlayState()) {
                            mPlayBut.setText(R.string.play_source);
                            mService.pauseSource();
                        } else {
                            mPlayBut.setText(R.string.pause_source);
                            Log.d(TAG, "MusicInfo:" + mAudioList.get(1).getTitle() + "--" + mAudioList.get(1).getArtist());
                            mService.setSourcePath(mAudioList.get(1).getPath());
                            mService.playSource();
                        }
                    }
                    break;
                case R.id.butStop:
                    if (mService != null) {
                        mService.stopSource();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToolUtils.checkPermission(this);

        mTextView = findViewById(R.id.infoView);
        mTextView.setText(mInfo);
        mTextView.setTextColor(Color.BLACK);

        mChangeActivityBut = findViewById(R.id.butToActivity);
        mChangeActivityBut.setOnClickListener(mClickListener);

        mPlayBut = findViewById(R.id.butPlay);
        mPlayBut.setOnClickListener(mClickListener);

        mStopBut = findViewById(R.id.butStop);
        mStopBut.setOnClickListener(mClickListener);

        startService(new Intent(MainActivity.this, MyService.class));//启动服务

        mIntentFilter.addAction(MyService.SERVICE_INTENT_ACTION);
        mIntentFilter.addAction(MyService.MEDIA_SOURCE_STOP_ACTION);
        registerReceiver(mReceiver, mIntentFilter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(this, MyService.class), mServiceConnection, BIND_ALLOW_OOM_MANAGEMENT);//绑定服务

        if (mService != null) {
            if (mService.getPlayState()) {
                mPlayBut.setText(R.string.pause_source);
            }
        }

        if (ToolUtils.checkPermission(this)) {
            mAudioList = MediaUtil.getAudioList(this);

            for (Audio audio : mAudioList) {
                Log.d(TAG, "MusicInfo:" + audio.getTitle() + "--" + audio.getArtist());
            }
        } else {
            Log.d(TAG, "no permission!!!");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        unregisterReceiver(mReceiver);
    }


}