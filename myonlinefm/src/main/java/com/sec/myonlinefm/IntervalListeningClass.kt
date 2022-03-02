package com.sec.myonlinefm

import android.content.*
import android.os.*
import android.util.Log
import com.sec.myonlinefm.OnLineFMPlayerListener.OberverOnLinePlayerManager

/**
 * Created by SRC-TJ-MM-BinYang on 2018/3/1.
 * 30s FM signal (by FM parameter data) checker...
 * when device use different, need check which search parameter is good for DNS function
 */
class IntervalListeningClass(context: Context?) {
    private var mCurrentRSSI: LongArray? = null
    private var mCurrentSNR: LongArray? = null
    private val TAG: String? = "IntervalListeningClass"

    //    private int mRSSIth;
    //    private int mSNRth;
    private var mHandler: Handler? = null
    private val mPlayer: OnLineFMConnectManager?
    private val mContext: Context?
    private var index = 0
    private var isRunning = false
    private val mCheck_By_Parameter = 1 //Rssi : 1, Snr : 2
    fun startListener() {
        Log.d(TAG, "startListener")
        isRunning = true
        index = 0
        mHandler!!.postDelayed(runnable!!, 500)
    }

    fun stopListener() {
        Log.d(TAG, "stopListener")
        mHandler!!.removeCallbacks(runnable!!)
        isRunning = false
    }

    fun isListenerRunning(): Boolean {
        return isRunning
    }

    private val runnable: Runnable? = object : Runnable {
        override fun run() {
            // TODO Auto-generated method stub
//            mCurrentRSSI[index++] = mPlayer.getCurrentRssi();
//            Log.d(TAG,"Rssi : "+mPlayer.getCurrentRssi());
//            mCurrentSNR[index++] = mPlayer.getCurrentSnr();
            if (index == 50) { // get data..
                var fSumSNR = 0.0
                var fSumRSSI = 0.0
                for (aMCurrentSNR in mCurrentSNR!!) fSumSNR += (aMCurrentSNR * aMCurrentSNR).toDouble()
                for (aMCurrentRSSI in mCurrentRSSI!!) fSumRSSI += (aMCurrentRSSI * aMCurrentRSSI).toDouble()
                fSumSNR = Math.sqrt(fSumSNR / mCurrentSNR!!.size)
                fSumRSSI = Math.sqrt(fSumRSSI / mCurrentSNR!!.size)
                if (mCheck_By_Parameter == 2) {
                    if (fSumSNR < 6) {
                        OberverOnLinePlayerManager.Companion.getInstance()!!.notifyObserverDNS(true)
                        Log.d(TAG, "bed : " + String.format("%.2s", fSumSNR))
                    } else Log.d(TAG, "good : " + String.format("%.2s", fSumSNR))
                } else if (mCheck_By_Parameter == 1) {
                    if (fSumRSSI < 20) {
                        OberverOnLinePlayerManager.Companion.getInstance()!!.notifyObserverDNS(true)
                        Log.d(TAG, "bed : " + String.format("%.2s", fSumRSSI))
                    } else Log.d(TAG, "good : " + String.format("%.2s", fSumRSSI))
                }
                index = 0
            }
            mHandler!!.postDelayed(this, 500)
        }
    }

    companion object {
        private var mThisClass: IntervalListeningClass? = null
        fun getIntense(): IntervalListeningClass? {
            return mThisClass
        }
    }

    init {
        mThisClass = this
        mContext = context
        mHandler = Handler()
        mPlayer = OnLineFMConnectManager.mMainInfoCode
        //        if (FMRadioFeature.CHIP_VENDOR.equals(FMRadioFeature.CHIP_QCOM)){
//            mCheck_By_Parameter = 2;
//        } else if(FMRadioFeature.CHIP_VENDOR.equals(FMRadioFeature.CHIP_RICHWAVE)) {
//            mCheck_By_Parameter = 1;
//        }
        index = 0
        mCurrentRSSI = LongArray(50)
        mCurrentSNR = LongArray(50)
    }
}