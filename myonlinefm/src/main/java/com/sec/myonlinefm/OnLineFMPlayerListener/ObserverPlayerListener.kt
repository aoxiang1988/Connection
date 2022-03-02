package com.sec.myonlinefm.OnLineFMPlayerListener

/**
 * Created by SRC-TJ-MM-BinYang on 2018/2/7.
 */
interface ObserverPlayerListener {
    open fun observerStartPlayer(mPlayer_Url: String?, play_type: Int)
    open fun observerStartDNS(isStart: Boolean)
}