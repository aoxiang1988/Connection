package com.sec.connecttoapilibrary.qtapitest.updataLiveRadioUIListener;

import com.sec.connecttoapilibrary.qtapitest.liveRadioData.SearchType;
import com.sec.connecttoapilibrary.qtapitest.liveRadioData.Station;
import com.sec.connecttoapilibrary.qtapitest.liveRadioData.StationProgram;

import java.util.List;
import java.util.Map;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/31.
 */

public interface SubjectLiveRadioUIListener {
    void add(ObserverLiveRadioUIListener observerLiveRadioUIListener);
    void notifyObserverLocalList(List<Station> mStations, Map<Integer, List<StationProgram>> map);
    void notifyObserverCenterList(List<Station> mCenterStations, Map<Integer, List<StationProgram>> centermap);
    void notifyDifferentInfoObserver(List<Station> mStations, Map<Integer, List<StationProgram>> map);
    void notifyChannelLiveObserver(List<SearchType.ChannelLive> mSearchChannelLiveList);
    void notifyProgramLiveObserver(List<SearchType.ProgramLive> mSearchProgramLiveList);
    void notifyOneDayProgramUpData(List<StationProgram> mOneDayPrograms);
    void notifyConnectStatus(boolean isConnectNet);
    void remove(ObserverLiveRadioUIListener observerLiveRadioUIListener);
}
