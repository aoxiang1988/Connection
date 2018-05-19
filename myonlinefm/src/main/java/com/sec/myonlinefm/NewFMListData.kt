package com.sec.myonlinefm

import android.content.Context

import com.sec.myonlinefm.data.Station
import com.sec.myonlinefm.data.StationProgram
import com.sec.myonlinefm.onlineinfolistener.ObserverListener
import com.sec.myonlinefm.onlineinfolistener.ObserverListenerManager
import com.sec.myonlinefm.updataUIListener.ObserverUIListenerManager

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/31.
 */

class NewFMListData (context: Context) : ObserverListener {

    var mContext : Context? = null
    var mStations : MutableList<Station>? = null
    private var mMap : MutableMap<Int, List<StationProgram>> ? = null
    private var mCenterStations : MutableList<Station>? = null
    private var mCenterMap : MutableMap<Int, List<StationProgram>> ? = null
    var mPlayer : OnLineFMConnectManager
    private var mFMListData : FMListData

    init {
        mContext = context
        mPlayer = OnLineFMConnectManager.mMainInfoCode!!
        ObserverListenerManager.getInstance().add(this)
        mFMListData = FMListData(mContext)
    }
    override fun observerLiveRadioLocalUpData(stations : MutableList<Station>, map : MutableMap<Int, List<StationProgram>>) {
        this.mStations = stations
        this.mMap = map
        mFMListData.makeLocationList(mStations,mMap, FMListData.TYPE_LOCAL)
    }

    override fun observerLiveRadioCenterUpData(mStations : MutableList<Station>, map : MutableMap<Int, List<StationProgram>>) {
        this.mCenterStations = mStations
        this.mCenterMap = map
        mFMListData.makeLocationList(mCenterStations,mCenterMap, FMListData.TYPE_CENTER)
    }

    override fun observerUpDataDifferentInfo(mStations : MutableList<Station>, map : MutableMap<Int, List<StationProgram>>) {
        mFMListData.addNetInfo(mStations,map)
        ObserverUIListenerManager.getInstance().notifyDifferentInfoObserver(mStations, map)
    }

//    fun doUpdateUI() {
//        mFMListData.makeLocationList(mStations, map,mCenterStations,centerMap)
//        mFMListData.makeLocationList(mStations, map,mCenterStations,centerMap);
//    }
    fun doConnectApi(){
        mPlayer.getOnLineFM()
    }
    fun stopConnectApi(){
//        mPlayer.StopOnLineFM();
    }
}
