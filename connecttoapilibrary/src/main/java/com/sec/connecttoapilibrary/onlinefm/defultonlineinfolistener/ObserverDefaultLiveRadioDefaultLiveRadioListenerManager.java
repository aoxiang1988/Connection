package com.sec.connecttoapilibrary.onlinefm.defultonlineinfolistener;

import com.sec.connecttoapilibrary.onlinefm.liveRadioData.Station;
import com.sec.connecttoapilibrary.onlinefm.liveRadioData.StationProgram;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/31.
 */

public class ObserverDefaultLiveRadioDefaultLiveRadioListenerManager implements SubjectDefaultLiveRadioListener {
    private static ObserverDefaultLiveRadioDefaultLiveRadioListenerManager observerManager;
    //观察者接口集合
    private List<ObserverDefaultLiveRadioListener> list = new ArrayList<>();

    /**
     * 单例
     */
    public static ObserverDefaultLiveRadioDefaultLiveRadioListenerManager getInstance(){
        if (null == observerManager){
            synchronized (ObserverDefaultLiveRadioDefaultLiveRadioListenerManager.class){
                if (null == observerManager){
                    observerManager = new ObserverDefaultLiveRadioDefaultLiveRadioListenerManager();
                }
            }
        }
        return observerManager;
    }
    /**
     * 加入监听队列
     */
    @Override
    public void add(ObserverDefaultLiveRadioListener observerDefaultLiveRadioListener) {
        list.add(observerDefaultLiveRadioListener);
    }

    @Override
    public void notifyObserverLocalList(List<Station> mStations, Map<Integer, List<StationProgram>> map) {
        for (ObserverDefaultLiveRadioListener observerDefaultLiveRadioListener : list) {
            observerDefaultLiveRadioListener.observerUpDataLocalList(mStations, map);
        }
    }

    @Override
    public void notifyObserverCenterList(List<Station> mCenterStations, Map<Integer, List<StationProgram>> center_map) {
        for (ObserverDefaultLiveRadioListener observerDefaultLiveRadioListener : list) {
            observerDefaultLiveRadioListener.observerUpDataCenterList(mCenterStations, center_map);
        }
    }

    /**
     * 通知观察者刷新数据
     */

    @Override
    public void notifyDifferentInfoObserver(List<Station> mStations, Map<Integer, List<StationProgram>> map) {
        for (ObserverDefaultLiveRadioListener observerDefaultLiveRadioListener : list){
            observerDefaultLiveRadioListener.observerUpDataDifferentInfo(mStations, map);
        }
    }

    /**
     * 监听队列中移除
     */
    @Override
    public void remove(ObserverDefaultLiveRadioListener observerDefaultLiveRadioListener) {
        if (list.contains(observerDefaultLiveRadioListener)){
            list.remove(observerDefaultLiveRadioListener);
        }
    }
}