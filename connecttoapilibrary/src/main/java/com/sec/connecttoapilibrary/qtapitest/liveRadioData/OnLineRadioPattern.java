package com.sec.connecttoapilibrary.qtapitest.liveRadioData;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/4/10.
 */

public class OnLineRadioPattern implements Serializable {
    private List<Station> stations;
    private Map<Integer, List<StationProgram>> station_map;

    public void setCurrentStationList(List<Station> stations) {
        this.stations = stations;
    }

    public List<Station> getCurrentStationList () {
        return stations;
    }

    public void setCurrentStationProgramMap(Map<Integer, List<StationProgram>> station_map) {
        this.station_map = station_map;
    }

    public Map<Integer, List<StationProgram>> getCurrentStationProgramMap () {
        return station_map;
    }

}
