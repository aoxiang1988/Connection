package com.sec.myonlinefm.updataUIListener;

import com.sec.myonlinefm.data.SearchType;
import com.sec.myonlinefm.data.Station;
import com.sec.myonlinefm.data.StationProgram;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/31.
 */

public class ObserverUIListenerManager implements SubjectUIListener {

    private static ObserverUIListenerManager observerUIManager;
    //观察者接口集合
    private List<ObserverUIListener> list = new ArrayList<>();

    /**
     * 单例
     */
    public static ObserverUIListenerManager getInstance(){
        if (null == observerUIManager){
            synchronized (ObserverUIListenerManager.class){
                if (null == observerUIManager){
                    observerUIManager = new ObserverUIListenerManager();
                }
            }
        }
        return observerUIManager;
    }

    @Override
    public void add(ObserverUIListener observerUIListener) {
        list.add(observerUIListener);
    }

    @Override
    public void notifyLiveRadioLocalObserver(List<Station> mStations, Map<Integer, List<StationProgram>> map) {
        for (ObserverUIListener observerUIListener : list){
            observerUIListener.observerLiveRadioLocalUIUpData(mStations, map);
        }
    }

    @Override
    public void notifyLiveRadioCenterObserver(List<Station> mStations, Map<Integer, List<StationProgram>> map) {
        for (ObserverUIListener observerUIListener : list){
            observerUIListener.observerLiveRadioCenterUIUpData(mStations, map);
        }
    }

    @Override
    public void notifyDifferentInfoObserver(List<Station> mStations, Map<Integer, List<StationProgram>> map) {
        for (ObserverUIListener observerUIListener : list){
            observerUIListener.observerDifferentInfoUIUpData(mStations, map);
        }
    }

    @Override
    public void notifyChannelLiveObserver(List<SearchType.ChannelLive> mSearchChannelLiveList) {
        for (ObserverUIListener observerUIListener : list){
            observerUIListener.observerChannelLiveUpData(mSearchChannelLiveList);
        }
    }

    @Override
    public void notifyProgramLiveObserver(List<SearchType.ProgramLive> mSearchProgramLiveList) {
        for (ObserverUIListener observerUIListener : list){
            observerUIListener.observerProgramLiveUpData(mSearchProgramLiveList);
        }
    }

    @Override
    public void notifyOneDayProgramUpData(List<StationProgram> mOneDayPrograms) {
        for (ObserverUIListener observerUIListener : list){
            observerUIListener.observerOneDayProgramUpData(mOneDayPrograms);
        }
    }

    @Override
    public void notifyConnectStatus(boolean isConnectNet) {
        for (ObserverUIListener observerUIListener : list){
            observerUIListener.observerConnectStatus(isConnectNet);
        }
    }

    @Override
    public void remove(ObserverUIListener observerUIListener) {
        if(list.contains(observerUIListener))
            list.remove(observerUIListener);
    }
}
