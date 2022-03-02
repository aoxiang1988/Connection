package com.sec.myonlinefm.data

/**
 * Created by SRC-TJ-MM-BinYang on 2018/4/19.
 */
class ClassificationAttributePattern {
    private var mInfo: MutableList<PropertyInfo?>? = null
    private var mInfoMap: HashMap<Int?, MutableList<PropertyInfo.values?>?>? = null
    fun setInfo(mInfo: MutableList<PropertyInfo?>?) {
        this.mInfo = mInfo
    }

    fun getInfo(): MutableList<PropertyInfo?>? {
        return mInfo
    }

    fun setInfoMap(mInfoMap: HashMap<Int?, MutableList<PropertyInfo.values?>?>?) {
        this.mInfoMap = mInfoMap
    }

    fun getInfoMap(): HashMap<Int?, MutableList<PropertyInfo.values?>?>? {
        return mInfoMap
    }
}