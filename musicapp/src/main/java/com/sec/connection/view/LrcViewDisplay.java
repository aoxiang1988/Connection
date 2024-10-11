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
    private final Handler handler;
    private final MediaPlayer mediaPlayer;
    private int c_duration;
    private int duration;
    private final LrcView lrcView;
    private final LrcProcess lrcProcess;

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
        lrcView.setLrcList(lrcContents);
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
                    if (c_duration < lrcContents.get(i).getLrcTime() && i == 0) {
                        index = i;
                    }
                    if (c_duration > lrcContents.get(i).getLrcTime()
                            && c_duration < lrcContents.get(i + 1).getLrcTime()) {
                        index = i;
                    }
                }
                if (i == lrcContents.size() - 1
                        && c_duration > lrcContents.get(i).getLrcTime()) {
                    index = i;
                }
            }
        }
        return index;
    }
    /************************************************************/
}
