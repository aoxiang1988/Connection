package com.sec.myonlinefm.abstructObserver

import com.sec.myonlinefm.data.SearchType
import com.sec.myonlinefm.data.StationProgram
import com.sec.myonlinefm.data.Station

import com.sec.myonlinefm.updataUIListener.ObserverUIListener

open class OnLineInfo : ObserverUIListener, ObserverListWork {
    override fun addToObserverList() {}
    override fun removeToObserverList() {}
    override fun observerLiveRadioLocalUIUpData(mStations: MutableList<Station?>?, map: MutableMap<Int?, MutableList<StationProgram?>?>?) {}
    override fun observerLiveRadioCenterUIUpData(mStations: MutableList<Station?>?, map: MutableMap<Int?, MutableList<StationProgram?>?>?) {}
    override fun observerDifferentInfoUIUpData(mStations: MutableList<Station?>?, map: MutableMap<Int?, MutableList<StationProgram?>?>?) {}
    override fun observerChannelLiveUpData(mSearchChannelLiveList: MutableList<SearchType.ChannelLive?>?) {}
    override fun observerProgramLiveUpData(mSearchProgramLiveList: MutableList<SearchType.ProgramLive?>?) {}
    override fun observerOneDayProgramUpData(mOneDayPrograms: MutableList<StationProgram?>?) {}
    override fun observerConnectStatus(isConnectNet: Boolean) {}
}