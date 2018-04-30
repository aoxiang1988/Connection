package com.sec.connecttoapilibrary.qtapitest.updataLiveRadioUIListener;

import com.sec.connecttoapilibrary.qtapitest.liveRadioData.SearchType;
import com.sec.connecttoapilibrary.qtapitest.liveRadioData.Station;
import com.sec.connecttoapilibrary.qtapitest.liveRadioData.StationProgram;

import java.util.List;
import java.util.Map;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/31.
 */

public interface ObserverLiveRadioUIListener {
    void observerUIUpDataLocalList(List<Station> mStations, Map<Integer, List<StationProgram>> map);
    void observerUIUpDataCenterList(List<Station> mCenterStations, Map<Integer, List<StationProgram>> centermap);
    void observerDifferentInfoUIUpData(List<Station> mStations, Map<Integer, List<StationProgram>> map);

    void observerChannelLiveUpData(List<SearchType.ChannelLive> mSearchChannelLiveList);
    void observerProgramLiveUpData(List<SearchType.ProgramLive> mSearchProgramLiveList);
    void observerOneDayProgramUpData(List<StationProgram> mOneDayPrograms);
    void observerConnectStatus(boolean isConnectNet);
}
