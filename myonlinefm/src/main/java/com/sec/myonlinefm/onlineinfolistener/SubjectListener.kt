package com.sec.myonlinefm.onlineinfolistener

import com.sec.myonlinefm.data.StationProgram
import com.sec.myonlinefm.data.Station


/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/31.
 */
interface SubjectListener {
    open fun add(observerListener: ObserverListener?)
    open fun notifyLiveRadioLocalObserver(mStations: MutableList<Station?>?, map: MutableMap<Int?, MutableList<StationProgram?>?>?)
    open fun notifyLiveRadioCenterObserver(mStations: MutableList<Station?>?, map: MutableMap<Int?, MutableList<StationProgram?>?>?)
    open fun notifyDifferentInfoObserver(mStations: MutableList<Station?>?, map: MutableMap<Int?, MutableList<StationProgram?>?>?)
    open fun remove(observerListener: ObserverListener?)
}