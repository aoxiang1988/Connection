package com.sec.myonlinefm.OnLineFMPlayerListener

/**
 * Created by SRC-TJ-MM-BinYang on 2018/2/7.
 */
class OberverOnLinePlayerManager : SubPlayerListener {
    //观察者接口集合
    private val list: MutableList<ObserverPlayerListener?>? = ArrayList()
    override fun add(observerListener: ObserverPlayerListener?) {
        list!!.add(observerListener)
    }

    override fun notifyObserver(mPlayer_Url: String?, play_type: Int) {
        for (observerListener in list!!) {
            observerListener!!.observerStartPlayer(mPlayer_Url, play_type)
        }
    }

    override fun notifyObserverDNS(isStart: Boolean) {
        for (observerListener in list!!) {
            observerListener!!.observerStartDNS(isStart)
        }
    }

    override fun remove(observerListener: ObserverPlayerListener?) {
        if (list!!.contains(observerListener)) {
            list.remove(observerListener)
        }
    }

    companion object {
        private var observerManager: OberverOnLinePlayerManager? = null

        /**
         * 单例
         */
        fun getInstance(): OberverOnLinePlayerManager? {
            if (null == observerManager) {
                synchronized(OberverOnLinePlayerManager::class.java) {
                    if (null == observerManager) {
                        observerManager = OberverOnLinePlayerManager()
                    }
                }
            }
            return observerManager
        }
    }
}