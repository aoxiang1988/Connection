package com.sec.myonlinefm.updataUIListener

import com.sec.myonlinefm.data.SearchType
import com.sec.myonlinefm.data.Station
import com.sec.myonlinefm.data.StationProgram

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/31.
 */
class ObserverUIListenerManager : SubjectUIListener {
    //观察者接口集合
    private val list: MutableList<ObserverUIListener?> = ArrayList()
    override fun add(observerUIListener: ObserverUIListener?) {
        list.add(observerUIListener)
    }

    override fun notifyLiveRadioLocalObserver(mStations: MutableList<Station?>?, map: MutableMap<Int?, MutableList<StationProgram?>?>?) {
        for (observerUIListener in list) {
            observerUIListener!!.observerLiveRadioLocalUIUpData(mStations, map)
        }
    }

    override fun notifyLiveRadioCenterObserver(mStations: MutableList<Station?>?, map: MutableMap<Int?, MutableList<StationProgram?>?>?) {
        for (observerUIListener in list) {
            observerUIListener!!.observerLiveRadioCenterUIUpData(mStations, map)
        }
    }

    override fun notifyDifferentInfoObserver(mStations: MutableList<Station?>?, map: MutableMap<Int?, MutableList<StationProgram?>?>?) {
        for (observerUIListener in list) {
            observerUIListener!!.observerDifferentInfoUIUpData(mStations, map)
        }
    }

    override fun notifyChannelLiveObserver(mSearchChannelLiveList: MutableList<SearchType.ChannelLive?>?) {
        for (observerUIListener in list) {
            observerUIListener!!.observerChannelLiveUpData(mSearchChannelLiveList)
        }
    }

    override fun notifyProgramLiveObserver(mSearchProgramLiveList: MutableList<SearchType.ProgramLive?>?) {
        for (observerUIListener in list) {
            observerUIListener!!.observerProgramLiveUpData(mSearchProgramLiveList)
        }
    }

    override fun notifyOneDayProgramUpData(mOneDayPrograms: MutableList<StationProgram?>?) {
        for (observerUIListener in list) {
            observerUIListener!!.observerOneDayProgramUpData(mOneDayPrograms)
        }
    }

    override fun notifyConnectStatus(isConnectNet: Boolean) {
        for (observerUIListener in list) {
            observerUIListener!!.observerConnectStatus(isConnectNet)
        }
    }

    override fun remove(observerUIListener: ObserverUIListener?) {
        if (list.contains(observerUIListener)) list.remove(observerUIListener)
    }

    companion object {
        private var observerUIManager: ObserverUIListenerManager? = null

        /**
         * 单例
         */
        fun getInstance(): ObserverUIListenerManager? {
            if (null == observerUIManager) {
                synchronized(ObserverUIListenerManager::class.java) {
                    if (null == observerUIManager) {
                        observerUIManager = ObserverUIListenerManager()
                    }
                }
            }
            return observerUIManager
        }
    }
}