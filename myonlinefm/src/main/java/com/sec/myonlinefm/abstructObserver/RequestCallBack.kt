package com.sec.myonlinefm.abstructObserver

/**
 * Created by SRC-TJ-MM-BinYang on 2018/4/13.
 */
interface RequestCallBack<T> {
    fun onSuccess(`val`: T?)
    fun onFail(errorMessage: String?)
}