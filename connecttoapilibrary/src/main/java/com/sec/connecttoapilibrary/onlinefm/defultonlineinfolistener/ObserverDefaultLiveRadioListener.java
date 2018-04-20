package com.sec.connecttoapilibrary.onlinefm.defultonlineinfolistener;

import com.sec.connecttoapilibrary.onlinefm.liveRadioData.Station;
import com.sec.connecttoapilibrary.onlinefm.liveRadioData.StationProgram;

import java.util.List;
import java.util.Map;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/31.
 */

public interface ObserverDefaultLiveRadioListener {
    void observerUpDataLocalList(List<Station> mStations, Map<Integer, List<StationProgram>> map);
    void observerUpDataCenterList(List<Station> mCenterStations, Map<Integer, List<StationProgram>> center_map);
    void observerUpDataDifferentInfo(List<Station> mStations, Map<Integer, List<StationProgram>> map);
}
