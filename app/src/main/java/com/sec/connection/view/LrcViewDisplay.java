package com.sec.connection.view;

import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.Toast;

import com.sec.connection.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SRC-TJ-MM-BinYang on 2017/8/11.
 */

public class LrcViewDisplay {

    private List<LrcContent> lrcContents = new ArrayList<>();
    private int index = 0;
    private Handler handler;
    private MediaPlayer mediaPlayer;
    private int c_duration;
    private int duration;
    private LrcView lrcView;
    private LrcProcess lrcProcess;

    public LrcViewDisplay (int c_duration, MediaPlayer mediaPlayer,LrcView lrcView,Handler handler) {
        this.c_duration = c_duration;
        this.mediaPlayer = mediaPlayer;
        this.lrcView = lrcView;
        this.handler = handler;
        lrcProcess = new LrcProcess();
    }
    /************************Lrc*********************************/
    //<span style="white-space:pre">	</span>/**
    // * ????????????
    // /storage/emulated/0/Samsung/Music/Over*/
    public void initLrc(String path){
        //?????????
        lrcProcess.readLRC(path);
        if(lrcProcess.readLRC(path) == null){
            Toast.makeText(MainActivity._inActivity, "no LRC", Toast.LENGTH_SHORT).show();
            return;
        }
        //??????????????
        lrcContents = lrcProcess.getLrcList();
        lrcView.setmLrcList(lrcContents);
        handler.post(mRunnable);
    }
    Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            lrcView.setIndex(lrcIndex());
            lrcView.invalidate();
            handler.postDelayed(mRunnable, 500);
        }
    };

    public int lrcIndex() {
        if(mediaPlayer.isPlaying()) {
            c_duration = mediaPlayer.getCurrentPosition();
            duration = mediaPlayer.getDuration();
        }
        if(c_duration < duration) {
            for (int i = 0; i < lrcContents.size(); i++) {
                if (i < lrcContents.size() - 1) {
                    if (c_duration < lrcContents.get(i).getlrctime() && i == 0) {
                        index = i;
                    }
                    if (c_duration > lrcContents.get(i).getlrctime()
                            && c_duration < lrcContents.get(i + 1).getlrctime()) {
                        index = i;
                    }
                }
                if (i == lrcContents.size() - 1
                        && c_duration > lrcContents.get(i).getlrctime()) {
                    index = i;
                }
            }
        }
        return index;
    }
    /************************************************************/
}
