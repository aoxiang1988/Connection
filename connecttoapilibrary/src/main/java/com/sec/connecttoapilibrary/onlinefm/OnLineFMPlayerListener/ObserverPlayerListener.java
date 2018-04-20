package com.sec.connecttoapilibrary.onlinefm.OnLineFMPlayerListener;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/2/7.
 */

public interface ObserverPlayerListener {
    void observerStartPlayer(String mPlayer_Url, int play_type);
    void observerStartDNS(boolean isStart);
}
