package com.sec.connecttoapilibrary.onlinefm.updataLiveRadioUIListener;

import com.sec.connecttoapilibrary.onlinefm.liveRadioData.SearchType;
import com.sec.connecttoapilibrary.onlinefm.liveRadioData.Station;
import com.sec.connecttoapilibrary.onlinefm.liveRadioData.StationProgram;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/31.
 */

public class ObserverLiveRadioLiveRadioUIListenerManager implements SubjectLiveRadioUIListener {

    private static ObserverLiveRadioLiveRadioUIListenerManager observerUIManager;
    //观察者接口集合
    private List<ObserverLiveRadioUIListener> list = new ArrayList<>();

    /**
     * 单例
     */
    public static ObserverLiveRadioLiveRadioUIListenerManager getInstance(){
        if (null == observerUIManager){
            synchronized (ObserverLiveRadioLiveRadioUIListenerManager.class){
                if (null == observerUIManager){
                    observerUIManager = new ObserverLiveRadioLiveRadioUIListenerManager();
                }
            }
        }
        return observerUIManager;
    }

    @Override
    public void add(ObserverLiveRadioUIListener observerLiveRadioUIListener) {
        list.add(observerLiveRadioUIListener);
    }

    @Override
    public void notifyObserverLocalList(List<Station> mStations, Map<Integer, List<StationProgram>> map) {
        for(ObserverLiveRadioUIListener observerLiveRadioUIListener : list) {
            observerLiveRadioUIListener.observerUIUpDataLocalList(mStations, map);
        }
    }

    @Override
    public void notifyObserverCenterList(List<Station> mCenterStations, Map<Integer, List<StationProgram>> centermap) {
        for(ObserverLiveRadioUIListener observerLiveRadioUIListener : list) {
            observerLiveRadioUIListener.observerUIUpDataCenterList(mCenterStations, centermap);
        }
    }

    @Override
    public void notifyDifferentInfoObserver(List<Station> mStations, Map<Integer, List<StationProgram>> map) {
        for (ObserverLiveRadioUIListener observerLiveRadioUIListener : list){
            observerLiveRadioUIListener.observerDifferentInfoUIUpData(mStations, map);
        }
    }

    @Override
    public void notifyChannelLiveObserver(List<SearchType.ChannelLive> mSearchChannelLiveList) {
        for (ObserverLiveRadioUIListener observerLiveRadioUIListener : list){
            observerLiveRadioUIListener.observerChannelLiveUpData(mSearchChannelLiveList);
        }
    }

    @Override
    public void notifyProgramLiveObserver(List<SearchType.ProgramLive> mSearchProgramLiveList) {
        for (ObserverLiveRadioUIListener observerLiveRadioUIListener : list){
            observerLiveRadioUIListener.observerProgramLiveUpData(mSearchProgramLiveList);
        }
    }

    @Override
    public void notifyOneDayProgramUpData(List<StationProgram> mOneDayPrograms) {
        for (ObserverLiveRadioUIListener observerLiveRadioUIListener : list){
            observerLiveRadioUIListener.observerOneDayProgramUpData(mOneDayPrograms);
        }
    }

    @Override
    public void notifyConnectStatus(boolean isConnectNet) {
        for (ObserverLiveRadioUIListener observerLiveRadioUIListener : list){
            observerLiveRadioUIListener.observerConnectStatus(isConnectNet);
        }
    }

    @Override
    public void remove(ObserverLiveRadioUIListener observerLiveRadioUIListener) {
        if(list.contains(observerLiveRadioUIListener))
            list.remove(observerLiveRadioUIListener);
    }
}
