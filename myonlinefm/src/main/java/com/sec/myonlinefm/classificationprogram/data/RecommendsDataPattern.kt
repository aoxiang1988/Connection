package com.sec.myonlinefm.classificationprogram.data

import com.sec.myonlinefm.classificationprogram.dataimport.RecommendsData

/**
 * Created by SRC-TJ-MM-BinYang on 2018/5/7.
 */
class RecommendsDataPattern {
    internal var recommendsDataList: MutableList<RecommendsData?>? = null
    fun setRecommendsDataList(demandChannelsList: MutableList<RecommendsData?>?) {
        recommendsDataList = recommendsDataList
    }

    fun getRecommendsDataList(): MutableList<RecommendsData?>? {
        return recommendsDataList
    }
}