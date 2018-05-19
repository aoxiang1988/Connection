package com.sec.myonlinefm.onlineinfolistener;

import com.sec.myonlinefm.data.Station;
import com.sec.myonlinefm.data.StationProgram;

import java.util.List;
import java.util.Map;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/31.
 */

public interface SubjectListener {
    void add(ObserverListener observerListener);
    void notifyLiveRadioLocalObserver(List<Station> mStations, Map<Integer, List<StationProgram>> map);
    void notifyLiveRadioCenterObserver(List<Station> mStations, Map<Integer, List<StationProgram>> map);
    void notifyDifferentInfoObserver(List<Station> mStations, Map<Integer, List<StationProgram>> map);
    void remove(ObserverListener observerListener);
}
