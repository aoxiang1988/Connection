package com.example.myautoapplication.media.lintener;

import com.example.myautoapplication.datamodel.Video;

import java.util.List;

public interface Update {
    void addVideoListUpdateListener(UpdateListener listener);
    void doVideListUpdate(List<Video> list);
    void removeVideoListUpdateListener(UpdateListener listener);
}
