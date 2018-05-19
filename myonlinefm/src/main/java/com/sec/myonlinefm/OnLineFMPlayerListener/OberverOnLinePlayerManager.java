package com.sec.myonlinefm.OnLineFMPlayerListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/2/7.
 */

public class OberverOnLinePlayerManager implements SubPlayerListener {
    private static OberverOnLinePlayerManager observerManager;
    //观察者接口集合
    private List<ObserverPlayerListener> list = new ArrayList<>();

    /**
     * 单例
     */
    public static OberverOnLinePlayerManager getInstance(){
        if (null == observerManager){
            synchronized (OberverOnLinePlayerManager.class){
                if (null == observerManager){
                    observerManager = new OberverOnLinePlayerManager();
                }
            }
        }
        return observerManager;
    }

    @Override
    public void add(ObserverPlayerListener observerListener) {
        list.add(observerListener);
    }

    @Override
    public void notifyObserver(String mPlayer_Url, int play_type) {
        for (ObserverPlayerListener observerListener : list){
            observerListener.observerStartPlayer(mPlayer_Url, play_type);
        }
    }

    @Override
    public void notifyObserverDNS(boolean isStart) {
        for (ObserverPlayerListener observerListener : list){
            observerListener.observerStartDNS(isStart);
        }
    }

    @Override
    public void remove(ObserverPlayerListener observerListener) {
        if (list.contains(observerListener)){
            list.remove(observerListener);
        }
    }
}
