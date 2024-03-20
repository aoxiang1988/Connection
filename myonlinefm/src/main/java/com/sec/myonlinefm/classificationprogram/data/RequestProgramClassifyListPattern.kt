package com.sec.myonlinefm.classificationprogram.data

import java.io.Serializable

import kotlin.jvm.Volatile

/**
 * Created by SRC-TJ-MM-BinYang on 2018/4/13.
 */
class RequestProgramClassifyListPattern private constructor() : Serializable {
    fun setRequestProgramClassifyList(requestProgramClassify_List: MutableList<RequestProgramClassify?>?) {
        requestProgramClassifyList = requestProgramClassify_List
    }

    fun getRequestProgramClassifyList(): MutableList<RequestProgramClassify?>? {
        return requestProgramClassifyList
    }

    companion object {
        @Volatile
        private var mInstance: RequestProgramClassifyListPattern? = null
        private var requestProgramClassifyList: MutableList<RequestProgramClassify?>? = null
        fun getInstance(): RequestProgramClassifyListPattern? {
            if (mInstance == null) {
                synchronized(RequestProgramClassifyListPattern::class.java) {
                    if (mInstance == null) {
                        mInstance = RequestProgramClassifyListPattern()
                    }
                }
            }
            return mInstance
        }
    }
}