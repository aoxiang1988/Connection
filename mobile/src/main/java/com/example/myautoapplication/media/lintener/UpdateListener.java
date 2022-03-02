package com.example.myautoapplication.media.lintener;

import com.example.myautoapplication.datamodel.Video;

import java.util.List;

public interface UpdateListener {
    void onVideoListUpdateListener(List<Video> list);
}
