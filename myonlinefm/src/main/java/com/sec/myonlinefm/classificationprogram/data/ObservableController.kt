package com.sec.myonlinefm.classificationprogram.data

import android.util.Log
import java.util.*


/**
 * Created by gaolin on 2018/4/20.
 */
class ObservableController private constructor() : Observable() {
    override fun notifyObservers(arg: Any?) {
        setChanged()
        super.notifyObservers(arg)
    }

    companion object {
        private var mObservable: ObservableController? = null
        const val REFRESH = 1
        const val UPDATETITLE = 2
        const val UPDATETHUMB = 3
        const val UPDATEHEADER = 4
        fun getInstance(): ObservableController? {
            if (mObservable == null) {
                mObservable = ObservableController()
            }
            return mObservable
        }
    }

    init {
        Log.d("gaolin", "ObservableController creator !!")
    }
}