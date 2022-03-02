package com.sec.myonlinefm.classificationprogram.data

/**
 * Created by SRC-TJ-MM-BinYang on 2018/5/7.
 */
class WaPiDataPattern {
    internal var waPiDataList: MutableList<WaPiData?>? = null
    fun getWaPiDataList(): MutableList<WaPiData?>? {
        return waPiDataList
    }

    fun setWaPiDataList(waPiDataList: MutableList<WaPiData?>?) {
        this.waPiDataList = waPiDataList
    }
}