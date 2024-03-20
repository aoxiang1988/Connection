package com.example.myautoapplication.media;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import com.example.myautoapplication.MyService;
import com.example.myautoapplication.R;
import com.example.myautoapplication.datamodel.MediaUtil;
import com.example.myautoapplication.datamodel.Video;
import com.example.myautoapplication.media.lintener.UpdateListenerManager;
import com.example.myautoapplication.toolutils.ToolUtils;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.IBinder;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myautoapplication.databinding.ActivitySecondBinding;

import java.util.List;

public class SecondActivity extends AppCompatActivity {

    private static final String TAG = "SecondActivity";

    private AppBarConfiguration appBarConfiguration;
    private ActivitySecondBinding binding;

    public Context mContext;
    private static List<Video> mVideoList;
    private UpdateListenerManager mManager;

    protected static MyService mService = null;
    private boolean isBind = false;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MyServiceBinder binder = (MyService.MyServiceBinder) service;
            Log.d(TAG, "onServiceConnected");
            mService = binder.getMyService();
            isBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mManager = UpdateListenerManager.getInstnse();
        mContext = SecondActivity.this;

        binding = ActivitySecondBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_second);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (ToolUtils.checkPermission(this)) {
            mVideoList = MediaUtil.getVideoList(this);
            mManager.doVideListUpdate(mVideoList);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(
                new Intent(mContext, MyService.class),
                mServiceConnection,
                BIND_ALLOW_OOM_MANAGEMENT
        );//绑定服务
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_second);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public static List<Video> getVideoList() {
        return mVideoList;
    }
}