package com.sec.myonlinefm.updataUIListener;

import com.sec.myonlinefm.data.SearchType;
import com.sec.myonlinefm.data.Station;
import com.sec.myonlinefm.data.StationProgram;

import java.util.List;
import java.util.Map;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/31.
 */

public interface ObserverUIListener {
    void observerLiveRadioLocalUIUpData(List<Station> mStations, Map<Integer, List<StationProgram>> map);
    void observerLiveRadioCenterUIUpData(List<Station> mStations, Map<Integer, List<StationProgram>> map);
    void observerDifferentInfoUIUpData(List<Station> mStations, Map<Integer, List<StationProgram>> map);

    void observerChannelLiveUpData(List<SearchType.ChannelLive> mSearchChannelLiveList);
    void observerProgramLiveUpData(List<SearchType.ProgramLive> mSearchProgramLiveList);
    void observerOneDayProgramUpData(List<StationProgram> mOneDayPrograms);
    void observerConnectStatus(boolean isConnectNet);
}
