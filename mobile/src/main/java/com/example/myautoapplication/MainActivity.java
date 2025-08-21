// MainActivity.java
package com.example.myautoapplication;

import static com.example.myautoapplication.toolutils.ToolUtils.MY_PERMISSIONS_REQUEST_PERMISSION;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myautoapplication.datamodel.Audio;
import com.example.myautoapplication.media.SecondActivity;
import com.example.myautoapplication.media.VideoItemViewHolder;
import com.example.myautoapplication.toolutils.ToolUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private MainViewModel viewModel;
    private ServiceManager serviceManager;
    private BroadcastReceiverManager broadcastManager;

    private TextView mTextView;
    private Button mChangeActivityBut;
    private Button mPlayBut;
    private Button mStopBut;
    private ListView mListView;

    private AudioListAdapter mAudioListAdapter;

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
        mPlayBut.setOnClickListener(v -> viewModel.togglePlayPause(0));

        mStopBut = findViewById(R.id.butStop);
        mStopBut.setOnClickListener(v -> viewModel.stopMedia());

        mListView = findViewById(R.id.audioList);

        mAudioListAdapter = new AudioListAdapter(this, null);
        mListView.setAdapter(mAudioListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "music info" + mAudioListAdapter.getItem(position).getTitle());
                viewModel.togglePlayPause(position);
                observeData();
            }
        });

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
                mAudioListAdapter.setAudioList(audioList);
                mAudioListAdapter.notifyDataSetChanged();
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
                viewModel.setPermissionGranted(allGranted, this);
                observeData();
            } else {
                Toast.makeText(this, "请到设置中打开权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
