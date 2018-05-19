package com.sec.myonlinefm.updataUIListener;

import com.sec.myonlinefm.data.SearchType;
import com.sec.myonlinefm.data.Station;
import com.sec.myonlinefm.data.StationProgram;

import java.util.List;
import java.util.Map;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/31.
 */

public interface SubjectUIListener {
    void add(ObserverUIListener observerUIListener);
    void notifyLiveRadioLocalObserver(List<Station> mStations, Map<Integer, List<StationProgram>> map);
    void notifyLiveRadioCenterObserver(List<Station> mStations, Map<Integer, List<StationProgram>> map);
    void notifyDifferentInfoObserver(List<Station> mStations, Map<Integer, List<StationProgram>> map);
    void notifyChannelLiveObserver(List<SearchType.ChannelLive> mSearchChannelLiveList);
    void notifyProgramLiveObserver(List<SearchType.ProgramLive> mSearchProgramLiveList);
    void notifyOneDayProgramUpData(List<StationProgram> mOneDayPrograms);
    void notifyConnectStatus(boolean isConnectNet);
    void remove(ObserverUIListener observerUIListener);
}
