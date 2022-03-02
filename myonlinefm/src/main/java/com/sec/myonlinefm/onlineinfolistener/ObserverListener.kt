package com.sec.myonlinefm.onlineinfolistener

import com.sec.myonlinefm.data.StationProgram
import com.sec.myonlinefm.data.Station


/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/31.
 */
interface ObserverListener {
    open fun observerLiveRadioLocalUpData(mStations: MutableList<Station?>?, map: MutableMap<Int?, MutableList<StationProgram?>?>?)
    open fun observerLiveRadioCenterUpData(mStations: MutableList<Station?>?, map: MutableMap<Int?, MutableList<StationProgram?>?>?)
    open fun observerUpDataDifferentInfo(mStations: MutableList<Station?>?, map: MutableMap<Int?, MutableList<StationProgram?>?>?)
}