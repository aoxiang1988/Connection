package com.sec.connecttoapilibrary.qtapitest.defultonlineinfolistener;

import com.sec.connecttoapilibrary.qtapitest.liveRadioData.Station;
import com.sec.connecttoapilibrary.qtapitest.liveRadioData.StationProgram;

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
