package com.sec.myonlinefm.onlineinfolistener

import com.sec.myonlinefm.data.StationProgram
import com.sec.myonlinefm.data.Station

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/31.
 */
class ObserverListenerManager : SubjectListener {
    //观察者接口集合
    private val list: MutableList<ObserverListener?> = ArrayList()

    /**
     * 加入监听队列
     */
    override fun add(observerListener: ObserverListener?) {
        list!!.add(observerListener)
    }

    /**
     * 通知观察者刷新数据
     */
    override fun notifyLiveRadioLocalObserver(mStations: MutableList<Station?>?, map: MutableMap<Int?, MutableList<StationProgram?>?>?) {
        if (mStations == null || map == null) {
            for (observerListener in list!!) {
                remove(observerListener)
                ObserverListenerManager.Companion.observerManager = null
            }
        } else {
            for (observerListener in list!!) {
                observerListener!!.observerLiveRadioLocalUpData(mStations, map)
            }
        }
    }


    override fun notifyLiveRadioCenterObserver(mStations: MutableList<Station?>?, map: MutableMap<Int?, MutableList<StationProgram?>?>?) {
        if (mStations == null || map == null) {
            for (observerListener in list!!) {
                remove(observerListener)
                ObserverListenerManager.Companion.observerManager = null
            }
        } else {
            for (observerListener in list!!) {
                observerListener!!.observerLiveRadioCenterUpData(mStations, map)
            }
        }
    }

    override fun notifyDifferentInfoObserver(mStations: MutableList<Station?>?, map: MutableMap<Int?, MutableList<StationProgram?>?>?) {
        if (mStations == null || map == null) {
            for (observerListener in list!!) {
                remove(observerListener)
                ObserverListenerManager.Companion.observerManager = null
            }
        } else {
            for (observerListener in list!!) {
                observerListener!!.observerUpDataDifferentInfo(mStations, map)
            }
        }
    }

    /**
     * 监听队列中移除
     */
    override fun remove(observerListener: ObserverListener?) {
        if (list!!.contains(observerListener)) {
            list.remove(observerListener)
        }
    }

    companion object {
        private var observerManager: ObserverListenerManager? = null

        /**
         * 单例
         */
        fun getInstance(): ObserverListenerManager? {
            if (null == ObserverListenerManager.Companion.observerManager) {
                synchronized(ObserverListenerManager::class.java) {
                    if (null == ObserverListenerManager.Companion.observerManager) {
                        ObserverListenerManager.Companion.observerManager = ObserverListenerManager()
                    }
                }
            }
            return ObserverListenerManager.Companion.observerManager
        }
    }
}