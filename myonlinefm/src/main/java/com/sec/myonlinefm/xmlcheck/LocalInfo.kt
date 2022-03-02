package com.sec.myonlinefm.xmlcheck

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/3.
 */
class LocalInfo {
    private var postion: String? = null
    private var channel = 0
    private var name: String? = null
    private var tag = 0
    fun setpostion(postion: String?) {
        this.postion = postion
    }

    fun setchannel(channel: Int) {
        this.channel = channel
    }

    fun setstationname(name: String?) {
        this.name = name
    }

    fun settag(tag: Int) {
        this.tag = tag
    }

    fun getpostion(): String? {
        return postion
    }

    fun getchannel(): Int {
        return channel
    }

    fun getstationname(): String? {
        return name
    }

    fun gettag(): Int {
        return tag
    }
}