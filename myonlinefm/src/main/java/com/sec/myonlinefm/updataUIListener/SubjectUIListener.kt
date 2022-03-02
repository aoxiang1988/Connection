package com.sec.myonlinefm.updataUIListener

import com.sec.myonlinefm.data.SearchType
import com.sec.myonlinefm.data.Station
import com.sec.myonlinefm.data.StationProgram

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/31.
 */
interface SubjectUIListener {
    fun add(observerUIListener: ObserverUIListener?)
    fun notifyLiveRadioLocalObserver(mStations: MutableList<Station?>?, map: MutableMap<Int?, MutableList<StationProgram?>?>?)
    fun notifyLiveRadioCenterObserver(mStations: MutableList<Station?>?, map: MutableMap<Int?, MutableList<StationProgram?>?>?)
    fun notifyDifferentInfoObserver(mStations: MutableList<Station?>?, map: MutableMap<Int?, MutableList<StationProgram?>?>?)
    fun notifyChannelLiveObserver(mSearchChannelLiveList: MutableList<SearchType.ChannelLive?>?)
    fun notifyProgramLiveObserver(mSearchProgramLiveList: MutableList<SearchType.ProgramLive?>?)
    fun notifyOneDayProgramUpData(mOneDayPrograms: MutableList<StationProgram?>?)
    fun notifyConnectStatus(isConnectNet: Boolean)
    fun remove(observerUIListener: ObserverUIListener?)
}