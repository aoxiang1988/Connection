package com.example.myautoapplication.media;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myautoapplication.R;
import com.example.myautoapplication.databinding.FragmentSecondBinding;
import com.example.myautoapplication.datamodel.Video;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private static String TAG = "SecondFragment";
    private int mCurrentPosition = 0;
    private long mCurDuration = 0;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCurrentPosition = SecondActivity.mService.getCurVideoID();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        playVideo(mCurrentPosition);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
                    if (mCurrentPosition < SecondActivity.getVideoList().size() - 1) {
                        playVideo(mCurrentPosition + 1);
                    } else {
                        Toast.makeText(getContext(), R.string.last_video, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.big_video_view:
                    NavHostFragment.findNavController(SecondFragment.this)
                            .navigate(R.id.action_SecondFragment_to_FirstFragment);
                    SecondActivity.mService.setCurVideoInfo(
                            binding.secondFragmentVideoVideoView.isPlaying(),
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
    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == UPDATE_CURRENT_DURATION) {
                mCurDuration = mCurDuration + 500;
            }
            return true;
        }
    });

    private final Runnable mCurDurationRunnable = new Runnable() {
        @Override
        public void run() {
            while (binding.secondFragmentVideoVideoView.isPlaying()) {
                mHandler.sendEmptyMessageDelayed(UPDATE_CURRENT_DURATION, 500);
            }
        }
    };

    private void playVideo(int newPosition) {
        Video newVideo = SecondActivity.getVideoList().get(newPosition);
        if (newVideo.getIsNormalVideo()) {
            binding.secondFragmentVideoVideoView.setVideoPath(newVideo.getPath());
            binding.secondFragmentVideoVideoView.start();
            Thread thread = new Thread(mCurDurationRunnable);
            thread.start();
        } else {
            Toast.makeText(getContext(),
                    R.string.play_error_info,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void stopPlayVideo() {
        if (binding.secondFragmentVideoVideoView.isPlaying()) {
            binding.secondFragmentVideoVideoView.pause();

            SecondActivity.mService.setCurVideoInfo(false, -1, 0);
        }
    }

    private void playFastCheck() {
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent intent = PendingIntent.getBroadcast(
                getContext(),
                1,
                new Intent("aaa"),
                PendingIntent.FLAG_CANCEL_CURRENT
        );
    }

}