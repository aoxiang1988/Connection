package com.example.myautoapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myautoapplication.datamodel.ActionMode;
import com.example.myautoapplication.datamodel.Audio;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";

    private MyMusicService mService = null;
    private boolean isBind = false;
    private AudioItemAdapter mAdapter;

    private FrameLayout mLoadLayout;
    private ListView mListView;
    private IntentFilter mFiler;
    private boolean mListViewVisible = false;

    private SeekBar mAudioPlayingBar;
    private ImageButton mNextAudioBut;
    private ImageButton mPreAudioBut;
    private ImageButton mPlayAudioBut;
    private ImageButton mPauseAudioBut;
    private ImageButton mVolumeBut;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ActionMode.AUDIO_LIST_READY:
                    if (!mListViewVisible) {
                        mLoadLayout.setVisibility(View.GONE);
                        mListView.setVisibility(View.VISIBLE);
                        mListViewVisible = true;
                    }
                    /*if (mService.getPlayState()) {
                        mAdapter.setCurrentPlayingItemId(true, mService.getCurrentAudio());
                    }*/
                    //mAdapter.notifyDataSetChanged();
                    break;
                case ActionMode.MEDIA_SOURCE_STOP_ACTION:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, MyMusicService.class);
        startService(intent);

        mListView = findViewById(R.id.list_view);
        mLoadLayout = findViewById(R.id.load_view);

        mAudioPlayingBar = findViewById(R.id.audio_seek_bar);
        mAudioPlayingBar.setOnSeekBarChangeListener(mSeekBarListener);

        mNextAudioBut = findViewById(R.id.next_button);
        mNextAudioBut.setOnClickListener(mClickListener);

        mPreAudioBut = findViewById(R.id.pre_button);
        mPreAudioBut.setOnClickListener(mClickListener);

        mPlayAudioBut = findViewById(R.id.play_button);
        mPlayAudioBut.setOnClickListener(mClickListener);

        mPauseAudioBut = findViewById(R.id.pause_button);
        mPauseAudioBut.setOnClickListener(mClickListener);

        mVolumeBut = findViewById(R.id.vol_button);
        mVolumeBut.setOnClickListener(mClickListener);


    }


    private SeekBar.OnSeekBarChangeListener mSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.next_button:
                    if (mService.getCurrentAudio() < mService.mAudioList.size()) {
                        mService.setCurrentAudio(mService.getCurrentAudio() + 1);
                        mService.playSource();
                    } else {
                        Toast.makeText(getBaseContext(), "no Music!!!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.pre_button:
                    if (mService.getCurrentAudio() > 0) {
                        mService.setCurrentAudio(mService.getCurrentAudio() - 1);
                        mService.playSource();
                    } else {
                        Toast.makeText(getBaseContext(), "no Music!!!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.play_button:
                    mService.playSource();
                    mPlayAudioBut.setVisibility(View.GONE);
                    mPauseAudioBut.setVisibility(View.VISIBLE);
                    break;
                case R.id.pause_button:
                    mService.pauseSource();
                    mPlayAudioBut.setVisibility(View.VISIBLE);
                    mPauseAudioBut.setVisibility(View.GONE);
                    break;
                case R.id.vol_button:
                    break;
            }
        }
    };

    private Audio mCurrentAudio;
    private static final int SERVICE_READY = 1;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == SERVICE_READY) {
                mAdapter = new AudioItemAdapter(getBaseContext());
                mListView.setAdapter(mAdapter);
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mService.setCurrentAudio(position);
                        mService.playSource();
                    }
                });
                if (!mService.mAudioListReady) {
                    mLoadLayout.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                    mListViewVisible = false;
                }
                if (mService.getPlayState()) {
                    mAdapter.setCurrentPlayingItemId(true, mService.getCurrentAudio());
                    mAdapter.notifyDataSetChanged();
                    mCurrentAudio = mService.mAudioList.get(mService.getCurrentAudio());

                    mPlayAudioBut.setVisibility(View.GONE);
                    mPauseAudioBut.setVisibility(View.VISIBLE);
                }
                return true;
            }
            return false;
        }
    });

    @Override
    protected void onResume() {
        super.onResume();

        bindService(new Intent(this, MyMusicService.class), mServiceConnection, BIND_ALLOW_OOM_MANAGEMENT);//绑定服务

        mFiler = new IntentFilter();
        mFiler.addAction(ActionMode.AUDIO_LIST_READY);
        registerReceiver(mReceiver, mFiler);


        Fragment f = new BlankFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.black_fragment, f);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        unregisterReceiver(mReceiver);
    }

    /* ***
     * 绑定服务 *
     * ***/
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG,"onServiceConnected");
            MyMusicService.MyServiceBinder binder = (MyMusicService.MyServiceBinder) service;
            mService = binder.getMyService();
            isBind = true;
            mHandler.sendEmptyMessage(SERVICE_READY);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = false;
        }
    };

    private class AudioItemAdapter extends BaseAdapter {

        private final Context context;
        private int mCurrentPlayingItemId = -1;
        private boolean isPlaying = false;

        AudioItemAdapter (Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            if (mService.mAudioList != null && mService.mAudioList.size() != 0)
                return mService.mAudioList.size();
            else
                return 0;
        }

        @Override
        public Audio getItem(int position) {
            return mService.mAudioList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void setCurrentPlayingItemId(boolean isPlaying, int position) {
            mCurrentPlayingItemId = position;
            this.isPlaying = isPlaying;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            VideoItemViewHolder holder;
            Audio audio = getItem(position);
            if(convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.video_item_layout, null);
                holder = new VideoItemViewHolder(convertView);
                convertView.setTag(holder);
            }
            else holder = (VideoItemViewHolder) convertView.getTag();

            holder.setTitleViewId(R.id.video_title);
            holder.setArtistViewId(R.id.video_info);
            holder.setImageViewId(R.id.video_imgView);
            holder.setTimeViewId(R.id.video_time);

            holder.getTitleView().setText(audio.getTitle());
            holder.getArtistView().setText(audio.getArtist());
            holder.getTimeView().setText(audio.getDuration());

            if (isPlaying && mCurrentPlayingItemId == position) {
                holder.itemPlayingDisplayOn();
            } else {
                holder.itemPlayingDisplayOff();
            }

            return convertView;
        }
    }
}