package com.example.myautoapplication.media.lintener;

import com.example.myautoapplication.datamodel.Video;

import java.util.ArrayList;
import java.util.List;

public class UpdateListenerManager implements Update{

    private static UpdateListenerManager mManager;
    private static List<UpdateListener> mVideoListListenerList = null;

    public static UpdateListenerManager getInstnse() {
        if (mManager == null) {
            mManager = new UpdateListenerManager();
        }
        if (mVideoListListenerList == null) {
            mVideoListListenerList = new ArrayList<>();
        }
        return mManager;
    }

    @Override
    public void addVideoListUpdateListener(UpdateListener listener) {
        mVideoListListenerList.add(listener);
    }

    @Override
    public void doVideListUpdate(List<Video> list) {
        for (UpdateListener listener : mVideoListListenerList) {
            listener.onVideoListUpdateListener(list);
        }
    }

    @Override
    public void removeVideoListUpdateListener(UpdateListener listener) {
        mVideoListListenerList.remove(listener);
    }
}
