package com.sec.myonlinefm.onlineinfolistener;

import com.sec.myonlinefm.data.Station;
import com.sec.myonlinefm.data.StationProgram;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/31.
 */

public class ObserverListenerManager implements SubjectListener {
    private static ObserverListenerManager observerManager;
    //观察者接口集合
    private List<ObserverListener> list = new ArrayList<>();

    /**
     * 单例
     */
    public static ObserverListenerManager getInstance(){
        if (null == observerManager){
            synchronized (ObserverListenerManager.class){
                if (null == observerManager){
                    observerManager = new ObserverListenerManager();
                }
            }
        }
        return observerManager;
    }
    /**
     * 加入监听队列
     */
    @Override
    public void add(ObserverListener observerListener) {
        list.add(observerListener);
    }
    /**
     * 通知观察者刷新数据
     */
    @Override
    public void notifyLiveRadioLocalObserver(List<Station> mStations, Map<Integer, List<StationProgram>> map) {
        if(mStations == null || map == null) {
            for (ObserverListener observerListener : list) {
                remove(observerListener);
                observerManager = null;
            }
        } else {
            for (ObserverListener observerListener : list) {
                observerListener.observerLiveRadioLocalUpData(mStations, map);
            }
        }
    }

    @Override
    public void notifyLiveRadioCenterObserver(List<Station> mStations, Map<Integer, List<StationProgram>> map) {
        if(mStations == null || map == null) {
            for (ObserverListener observerListener : list) {
                remove(observerListener);
                observerManager = null;
            }
        } else {
            for (ObserverListener observerListener : list) {
                observerListener.observerLiveRadioCenterUpData(mStations, map);
            }
        }
    }

    @Override
    public void notifyDifferentInfoObserver(List<Station> mStations, Map<Integer, List<StationProgram>> map) {
        if(mStations == null || map == null) {
            for (ObserverListener observerListener : list) {
                remove(observerListener);
                observerManager = null;
            }
        } else {
            for (ObserverListener observerListener : list) {
                observerListener.observerUpDataDifferentInfo(mStations, map);
            }
        }
    }

    /**
     * 监听队列中移除
     */
    @Override
    public void remove(ObserverListener observerListener) {
        if (list.contains(observerListener)){
            list.remove(observerListener);
        }
    }
}