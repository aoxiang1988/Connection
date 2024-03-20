package com.example.myautoapplication.media;


import static android.content.Context.BIND_ALLOW_OOM_MANAGEMENT;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myautoapplication.R;
import com.example.myautoapplication.databinding.FragmentFirstBinding;
import com.example.myautoapplication.datamodel.Video;
import com.example.myautoapplication.media.lintener.UpdateListener;
import com.example.myautoapplication.media.lintener.UpdateListenerManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment implements UpdateListener {

    private static final String TAG = "FirstFragment";

    private FragmentFirstBinding binding;
    private List<Video> mList = new ArrayList<>();

    private VideoItemAdapter mVideosAdapter;

    private UpdateListenerManager mManager;

    private boolean mButVis = false;
    private int mCurrentPosition = 0;

    private AlarmManager manager;
    private final boolean mPlayingFast = false;

    private long mCurDuration = 0;

    private PendingIntent mOptionIntent;

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    private IntentFilter mFilter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mManager = UpdateListenerManager.getInstnse();
        mManager.addVideoListUpdateListener(this);
        mVideosAdapter = new VideoItemAdapter(getContext());
        manager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        mOptionIntent = PendingIntent.getBroadcast(
                getContext(),
                1,
                new Intent("bbb"),
                PendingIntent.FLAG_CANCEL_CURRENT
        );

    }

    @Override
    public void onResume() {
        super.onResume();
        mFilter = new IntentFilter();
        mFilter.addAction("aaa");
        mFilter.addAction("bbb");
        requireContext().registerReceiver(mReceiver, mFilter);

        if (SecondActivity.mService != null && SecondActivity.mService.getIsVideoPlaying()) {
            mCurrentPosition = SecondActivity.mService.getCurVideoID();
            mCurDuration = SecondActivity.mService.getCurDuration();
            playVideo(mCurrentPosition);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        requireContext().unregisterReceiver(mReceiver);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        viewNeedGone();
        return binding.getRoot();

    }

    private final View.OnClickListener mButClickListener = new View.OnClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button:
                    if (mCurrentPosition > 0) {
                        playVideo(mCurrentPosition - 1);
                    } else {
                        Toast.makeText(getContext(), R.string.first_video, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.button2:
                    playVideo(mCurrentPosition);
                    break;
                case R.id.button3:
                    stopPlayVideo();
                    break;
                case R.id.button4:
                    if (mCurrentPosition < mList.size() - 1) {
                        playVideo(mCurrentPosition + 1);
                    } else {
                        Toast.makeText(getContext(), R.string.last_video, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.big_video_view:
                    NavHostFragment.findNavController(FirstFragment.this)
                            .navigate(R.id.action_FirstFragment_to_SecondFragment);
                    SecondActivity.mService.setCurVideoInfo(
                            binding.firstFragmentVideoVideoView.isPlaying(),
                            mCurrentPosition,
                            mCurDuration
                            );
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + v.getId());
            }
        }
    };

    private static final int UPDATE_CURRENT_DURATION = 1;
    private static final int UPDATE_CURRENT_VIDEO_IMG = 2;

    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == UPDATE_CURRENT_DURATION) {
                mCurDuration = mCurDuration + 500;
            }
            if (msg.what == UPDATE_CURRENT_VIDEO_IMG) {
                for (Video video : mList) {
                    String imgPath = Environment.getExternalStorageDirectory() + File.separator + video.getTitle() + ".jpg";
                    video.setVideoBitmap(new BitmapDrawable(null, imgPath));
                }
                mVideosAdapter.notifyDataSetChanged();
            }
            return true;
        }
    });
    private final Runnable mCurDurationRunnable = new Runnable() {
        @Override
        public void run() {
            while (binding.firstFragmentVideoVideoView.isPlaying()) {
                mHandler.sendEmptyMessageDelayed(UPDATE_CURRENT_DURATION, 500);
            }
        }
    };

    private void playVideo(int newPosition) {
        Video newVideo = mVideosAdapter.getItem(newPosition);
        if (newVideo.getIsNormalVideo()) {
            viewNeedVisible();
            binding.firstFragmentVideoVideoView.setVideoPath(newVideo.getPath());
            binding.firstFragmentVideoVideoView.start();
            mCurrentPosition = newPosition;
            mVideosAdapter.setCurrentPlayingItemId(true, mCurrentPosition);
            mVideosAdapter.notifyDataSetChanged();
            Thread thread = new Thread(mCurDurationRunnable);
            thread.start();
        } else {
            if (binding.videoView.getVisibility() == View.VISIBLE
                    && !binding.firstFragmentVideoVideoView.isPlaying()) {
                viewNeedGone();
            }
            Toast.makeText(getContext(),
                    R.string.play_error_info,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void stopPlayVideo() {
        if (binding.firstFragmentVideoVideoView.isPlaying()) {
            binding.firstFragmentVideoVideoView.pause();

            viewNeedGone();
            mButVis = false;
            mVideosAdapter.setCurrentPlayingItemId(false, mCurrentPosition);
            mVideosAdapter.notifyDataSetChanged();
            mCurDuration = 0;
            SecondActivity.mService.setCurVideoInfo(false, -1, 0);
        }
    }

    private void playFastCheck() {
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent intent = PendingIntent.getBroadcast(
                getContext(),
                1,
                new Intent("aaa"),
                PendingIntent.FLAG_CANCEL_CURRENT
        );
        manager.setAlarmClock(new AlarmManager.AlarmClockInfo(3000, intent), mOptionIntent);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.videosList.setAdapter(mVideosAdapter);
        binding.videosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Video currentVideo = mVideosAdapter.getItem(position);
                binding.firstFragmentVideoVideoView.setVisibility(View.VISIBLE);
                playVideo(position);
            }
        });

        binding.firstFragmentVideoVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (binding.firstFragmentVideoVideoView.isPlaying() && !mButVis) {
                            viewNeedVisible();
                            mButVis = true;
                        } else if (binding.firstFragmentVideoVideoView.isPlaying() && mButVis) {
                            viewNeedGone();
                            mButVis = false;
                        }
                        playFastCheck();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if (manager.getNextAlarmClock().getTriggerTime() != 0) {
                            manager.cancel(mOptionIntent);
                            manager.notifyAll();
                        }
                        break;
                }
                return true;
            }
        });
        binding.button.setOnClickListener(mButClickListener);
        binding.button2.setOnClickListener(mButClickListener);
        binding.button3.setOnClickListener(mButClickListener);
        binding.button4.setOnClickListener(mButClickListener);
        binding.bigVideoView.setOnClickListener(mButClickListener);

        if (!binding.firstFragmentVideoVideoView.isPlaying()) {
            viewNeedGone();
        }
    }

    private void viewNeedVisible() {
        binding.firstFragmentVideoVideoView.setVisibility(View.VISIBLE);
        binding.videoView.setVisibility(View.VISIBLE);
        binding.controllerView.setVisibility(View.VISIBLE);
        binding.bigVideoView.setVisibility(View.VISIBLE);
    }

    private void viewNeedGone() {
        if (!binding.firstFragmentVideoVideoView.isPlaying()) {
            binding.firstFragmentVideoVideoView.setVisibility(View.GONE);
        }
        binding.controllerView.setVisibility(View.GONE);
        binding.bigVideoView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        mManager.removeVideoListUpdateListener(this);
    }

    @Override
    public void onVideoListUpdateListener(List<Video> list) {
        mList.clear();
        mList.addAll(list);
        mVideosAdapter.notifyDataSetChanged();
        updateVideoImg(list);
    }

    private void updateVideoImg(List<Video> list) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (Video video : list) {
                    String path = video.getPath();
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(path);
                    //String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    //int sec = Integer.parseInt(time)/1000;
                    Bitmap bitmap = retriever.getFrameAtTime(
                            1000 * 1000,
                            MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                    /*video.setVideoBitmap(
                            retriever.getFrameAtTime(
                                    1000 * 1000,
                                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC));*/
                    String imgPath = Environment.getExternalStorageDirectory() + File.separator + video.getTitle() + ".jpg";
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(imgPath);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                //mVideosAdapter.notifyDataSetChanged();

                mHandler.sendEmptyMessage(UPDATE_CURRENT_VIDEO_IMG);
            }
        });

        t.start();
    }

    private class VideoItemAdapter extends BaseAdapter {

        private final Context context;
        private int mCurrentPlayingItemId = -1;
        private boolean isPlaying = false;

        VideoItemAdapter (Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            if (mList != null && mList.size() != 0)
                return mList.size();
            else
                return 0;
        }

        @Override
        public Video getItem(int position) {
            return mList.get(position);
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
            Video video = getItem(position);
            if(convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.video_item_layout, null);
                holder = new VideoItemViewHolder(convertView);
                convertView.setTag(holder);
            }
            else holder = (VideoItemViewHolder) convertView.getTag();

            holder.setTitleViewId(R.id.video_title);
            holder.setArtistViewId(R.id.video_info);
            holder.setImageViewId(R.id.video_imgView);
            holder.getImageView().findViewById(R.id.video_imgView).setBackground(video.getVideoDrawable());
            holder.setTimeViewId(R.id.video_time);

            holder.getTitleView().setText(video.getTitle());
            holder.getArtistView().setText(video.getArtist());
            holder.getTimeView().setText(video.getDuration());

            if (isPlaying && mCurrentPlayingItemId == position) {
                holder.itemPlayingDisplayOn();
            } else {
                holder.itemPlayingDisplayOff();
            }

            return convertView;
        }
    }

}