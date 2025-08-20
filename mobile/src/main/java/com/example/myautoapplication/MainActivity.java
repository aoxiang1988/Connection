// MainActivity.java
package com.example.myautoapplication;

import static com.example.myautoapplication.toolutils.ToolUtils.MY_PERMISSIONS_REQUEST_PERMISSION;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myautoapplication.datamodel.Audio;
import com.example.myautoapplication.media.SecondActivity;
import com.example.myautoapplication.toolutils.ToolUtils;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private MainViewModel viewModel;
    private ServiceManager serviceManager;
    private BroadcastReceiverManager broadcastManager;

    private TextView mTextView;
    private Button mChangeActivityBut;
    private Button mPlayBut;
    private Button mStopBut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化 ViewModel 和管理器
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        serviceManager = new ServiceManager(this, viewModel);
        broadcastManager = new BroadcastReceiverManager(this, viewModel);

        // 检查权限
        boolean hasPermission = ToolUtils.checkPermission(this);

        // 所有权限都已授予
        viewModel.setPermissionGranted(hasPermission, this);

        // 初始化 UI 组件
        initViews();

        // 启动服务
        serviceManager.startService();

        // 注册广播接收器
        broadcastManager.registerReceiver();
    }

    private void initViews() {
        mTextView = findViewById(R.id.infoView);
        mTextView.setTextColor(Color.BLACK);

        mChangeActivityBut = findViewById(R.id.butToActivity);
        mChangeActivityBut.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            startActivity(intent);
        });

        mPlayBut = findViewById(R.id.butPlay);
        mPlayBut.setOnClickListener(v -> viewModel.togglePlayPause());

        mStopBut = findViewById(R.id.butStop);
        mStopBut.setOnClickListener(v -> viewModel.stopMedia());

        // 观察数据变化
        observeData();
    }

    private void observeData() {
        viewModel.getServiceInfo().observe(this, info -> {
            mTextView.setText(info);
            mTextView.setTextColor(Color.RED);
        });

        viewModel.getPlayButtonText().observe(this, textResId ->
                mPlayBut.setText(textResId));

        viewModel.getAudioList().observe(this, audioList -> {
            if (audioList != null) {
                for (Audio audio : audioList) {
                    Log.d(TAG, "MusicInfo:" + audio.getTitle() + "--" + audio.getArtist());
                }
            }
        });

        viewModel.getPermissionGranted().observe(this, granted -> {
            if (!granted) {
                Log.d(TAG, "no permission!!!");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 绑定服务
        serviceManager.bindService();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        serviceManager.unbindService();
        broadcastManager.unregisterReceiver();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_PERMISSION) {
            // 处理权限申请结果
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {

            } else {
                // 有权限被拒绝
            }
        }
    }

}
