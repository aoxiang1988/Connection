package com.dten.videoplayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;

import androidx.fragment.app.FragmentActivity;

import com.dten.videoplayer.data.MediaUtils;

import java.util.ArrayList;
import java.util.List;

import cn.cbg.exoplayer.BaseFactory;
import cn.cbg.exoplayer.DownloadTracker;
import cn.cbg.exoplayer.PlayerExo;
import cn.cbg.exoplayer.utils.ExoDownloadService;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<Movie> list = MediaUtils.getMediaList(this);
        if (list == null) {
            Log.d(TAG, "List is null");
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_browse_fragment, new MainFragment())
                .commitNow();
        }
    }
}