package com.sec.myonlinefm.updataUIListener

import com.sec.myonlinefm.data.SearchType
import com.sec.myonlinefm.data.Station
import com.sec.myonlinefm.data.StationProgram


/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/31.
 */
interface ObserverUIListener {
    fun observerLiveRadioLocalUIUpData(mStations: MutableList<Station?>?, map: MutableMap<Int?, MutableList<StationProgram?>?>?)
    fun observerLiveRadioCenterUIUpData(mStations: MutableList<Station?>?, map: MutableMap<Int?, MutableList<StationProgram?>?>?)
    fun observerDifferentInfoUIUpData(mStations: MutableList<Station?>?, map: MutableMap<Int?, MutableList<StationProgram?>?>?)
    fun observerChannelLiveUpData(mSearchChannelLiveList: MutableList<SearchType.ChannelLive?>?)
    fun observerProgramLiveUpData(mSearchProgramLiveList: MutableList<SearchType.ProgramLive?>?)
    fun observerOneDayProgramUpData(mOneDayPrograms: MutableList<StationProgram?>?)
    fun observerConnectStatus(isConnectNet: Boolean)
}