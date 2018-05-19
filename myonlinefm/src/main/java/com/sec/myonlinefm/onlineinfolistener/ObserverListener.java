package com.sec.myonlinefm.onlineinfolistener;

import com.sec.myonlinefm.data.Station;
import com.sec.myonlinefm.data.StationProgram;

import java.util.List;
import java.util.Map;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/31.
 */

public interface ObserverListener {
    void observerLiveRadioLocalUpData(List<Station> mStations, Map<Integer, List<StationProgram>> map);
    void observerLiveRadioCenterUpData(List<Station> mStations, Map<Integer, List<StationProgram>> map);
    void observerUpDataDifferentInfo(List<Station> mStations, Map<Integer, List<StationProgram>> map);
}
