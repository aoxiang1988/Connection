package com.sec.myonlinefm.OnLineFMPlayerListener


/**
 * Created by SRC-TJ-MM-BinYang on 2018/2/7.
 */
interface SubPlayerListener {
    open fun add(observerListener: ObserverPlayerListener?)
    open fun notifyObserver(mPlayer_Url: String?, play_type: Int)
    open fun notifyObserverDNS(isStart: Boolean)
    open fun remove(observerListener: ObserverPlayerListener?)
}