package com.sec.connecttoapilibrary.qtapitest.abstructObserver;

import com.sec.connecttoapilibrary.qtapitest.liveRadioData.SearchType;
import com.sec.connecttoapilibrary.qtapitest.liveRadioData.Station;
import com.sec.connecttoapilibrary.qtapitest.liveRadioData.StationProgram;
import com.sec.connecttoapilibrary.qtapitest.updataLiveRadioUIListener.ObserverLiveRadioUIListener;
import com.sec.connecttoapilibrary.qtapitest.updataLiveRadioUIListener.ObserverLiveRadioLiveRadioUIListenerManager;

import java.util.List;
import java.util.Map;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/3/15.
 * Template Pattern, Decorator Pattern
 */

public abstract class AbsTractOnLineLiveRadioInfo implements ObserverLiveRadioUIListener, ObserverListWork {

    @Override
    public void addToObserverList() {
        ObserverLiveRadioLiveRadioUIListenerManager.getInstance().add(this);
    }

    @Override
    public void removeToObserverList() {
        ObserverLiveRadioLiveRadioUIListenerManager.getInstance().remove(this);
    }

    @Override
    public void observerUIUpDataLocalList(List<Station> stations,
                                          Map<Integer, List<StationProgram>> map) {
        //TODO update local stations information
    }

    @Override
    public void observerUIUpDataCenterList(List<Station> center_stations,
                                           Map<Integer, List<StationProgram>> center_map) {
        //TODO update local stations information
    }

    @Override
    public void observerDifferentInfoUIUpData(List<Station> mStations,
                                              Map<Integer, List<StationProgram>> map) {
        //TODO update different local stations information
    }

    @Override
    public void observerChannelLiveUpData(List<SearchType.ChannelLive> mSearchChannelLiveList) {
        //TODO get the search channels information
    }

    @Override
    public void observerProgramLiveUpData(List<SearchType.ProgramLive> mSearchProgramLiveList) {
        //TODO get the search programs information
    }

    @Override
    public void observerOneDayProgramUpData(List<StationProgram> mOneDayPrograms) {
        //TODO get one day user want to get programs information
    }

    @Override
    public void observerConnectStatus(boolean is_connect_net) {
        //TODO get the network connection status
    }
}
