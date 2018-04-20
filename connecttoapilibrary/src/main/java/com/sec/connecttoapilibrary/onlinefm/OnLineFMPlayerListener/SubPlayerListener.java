package com.sec.connecttoapilibrary.onlinefm.OnLineFMPlayerListener;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/2/7.
 */

public interface SubPlayerListener {
    void add(ObserverPlayerListener observerListener);
    void notifyObserver(String mPlayer_Url, int play_type);
    void notifyObserverDNS(boolean isStart);
    void remove(ObserverPlayerListener observerListener);
}
