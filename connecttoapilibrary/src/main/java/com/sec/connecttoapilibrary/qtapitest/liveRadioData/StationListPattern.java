package com.sec.connecttoapilibrary.qtapitest.liveRadioData;

import java.io.Serializable;
import java.util.List;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/4/10.
 */

public class StationListPattern implements Serializable {
    private List<Station> stations;
    public void setCurrentStationList(List<Station> stations) {
        this.stations = stations;
    }

    public List<Station> getCurrentStationList () {
        return stations;
    }
}
