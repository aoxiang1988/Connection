package com.sec.myonlinefm.abstructObserver

import com.sec.myonlinefm.data.SearchType
import com.sec.myonlinefm.data.Station
import com.sec.myonlinefm.data.StationProgram
import com.sec.myonlinefm.updataUIListener.ObserverUIListener

/**
 * Created by SRC-TJ-MM-BinYang on 2018/3/15.
 * Template Pattern, Decorator Pattern
 */

abstract class OnLineInfo : ObserverUIListener, ObserverListWork {

    override fun observerLiveRadioLocalUIUpData(mStations : MutableList<Station>,
                                                map : MutableMap<Int, MutableList<StationProgram>>){
        //TODO update local stations information
    }

    override fun observerLiveRadioCenterUIUpData(mStations : MutableList<Station>,
                                                 map : MutableMap<Int, MutableList<StationProgram>>){
        //TODO update local stations information
    }

    override fun observerDifferentInfoUIUpData(mStations : MutableList<Station>,
                                               map : MutableMap<Int, MutableList<StationProgram>>) {
        //TODO update different local stations information
    }

    override fun observerChannelLiveUpData(mSearchChannelLiveList : MutableList<SearchType.ChannelLive>) {
        //TODO get the search channels information
    }

    override fun observerProgramLiveUpData(mSearchProgramLiveList : MutableList<SearchType.ProgramLive>) {
        //TODO get the search programs information
    }

    override fun observerOneDayProgramUpData(mOneDayPrograms : MutableList<StationProgram>) {
        //TODO get one day user want to get programs information
    }

    override fun observerConnectStatus(is_connect_net : Boolean) {
        //TODO get the network connection status
    }
}
