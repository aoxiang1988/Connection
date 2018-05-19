@file:Suppress("DEPRECATION")

package com.sec.myonlinefm

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Handler
import android.os.Message
import android.support.annotation.RequiresApi
import android.util.Log

import com.sec.myonlinefm.OnLineFMPlayerListener.OberverOnLinePlayerManager
import com.sec.myonlinefm.classificationprogram.RequestCallBack
import com.sec.myonlinefm.classificationprogram.data.ChannelProgramPattern
import com.sec.myonlinefm.classificationprogram.data.ClassifyRecommend
import com.sec.myonlinefm.classificationprogram.data.DemandChannel
import com.sec.myonlinefm.classificationprogram.data.DemandChannelPattern
import com.sec.myonlinefm.classificationprogram.data.RecommendsDataPattern
import com.sec.myonlinefm.classificationprogram.data.RequestProgramClassify
import com.sec.myonlinefm.classificationprogram.data.RequestProgramClassifyListPattern
import com.sec.myonlinefm.classificationprogram.data.WaPiDataPattern
import com.sec.myonlinefm.data.ClassificationAttributePattern
import com.sec.myonlinefm.data.PropertyInfo
import com.sec.myonlinefm.data.Station
import com.sec.myonlinefm.data.StationProgram
import com.sec.myonlinefm.onlineinfolistener.ObserverListenerManager
import com.sec.myonlinefm.updataUIListener.ObserverUIListenerManager

import org.json.JSONException
import java.util.*

/**
 * Created by SRC-TJ-MM-BinYang on 2018/3/8.
 * this is all the main work code of online fm
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
class OnLineFMConnectManager constructor(context: Context) {

    private fun getTAG(): String = "OnLineFMConnectManager"
    var mContext : Context? = context

    private var mOnLineWork : Boolean = false
    private var mHttpUtil : HttpUtil? = null
    private var mConvert : JsonStringConvert ? = null
    private var mOnLineWorkerThread : OnLineWorkerThread<Objects> ? = null

    private var mInfoMap : MutableMap<Int, List<PropertyInfo.values>> ? = null
    var mStations : List<Station>? = null
    var mCenterStations : List<Station> ? = null
    var map : MutableMap<Int, List<StationProgram>>? = null
    var centerMap : MutableMap<Int, List<StationProgram>>? = null
    var mDifferentStations : MutableList<Station>? = null
    var mDifferent : MutableMap<Int, List<StationProgram>>? = null
    var mOneDayPrograms : List<StationProgram> ? = null

    private var mDayOfWeek : Int = 1
    private lateinit var mTime : String

    private var stationId : Int = -1
    var mPlayType : Int = 0
    private var mProgramStartTime : java.lang.String? = null
    private var mProgramEndTime : java.lang.String ? = null
    lateinit var mRequestReplayUrl : String
    private lateinit var mStation : Station
    private var mDayInfo : Int = 0
    private var mOrder : Int = 0
    private var mDCAttrId : Array<Int>? = null
    private var getGPSInfo : GetGPSInfo? = null

    companion object {

        @SuppressLint("StaticFieldLeak")
        var mMainInfoCode : OnLineFMConnectManager? = null
        var isConnectNet : Boolean = true
        var mGPS_Name = "北京"
        var mLocal_ID : Int = 0
        private const val UPDATE_INFO: Int = 1
        private const val UPDATE_DIFFERENT_INFO: Int = 2
        private const val START_ONLINE_PLAYER: Int = 3
        private const val UPDATE_ONE_DAY_PROGRAM: Int = 4
    }

    @SuppressLint("HandlerLeak")
    private var handler : Handler = object : Handler() {
        override fun handleMessage(msg : Message?) {
            super.handleMessage(msg)
            when {
                msg!!.what == UPDATE_INFO -> {
                    ObserverListenerManager.getInstance().notifyLiveRadioLocalObserver(mStations, map)
                    ObserverListenerManager.getInstance().notifyLiveRadioCenterObserver(mCenterStations, centerMap)
                }
                msg.what == UPDATE_DIFFERENT_INFO -> ObserverListenerManager.getInstance().notifyDifferentInfoObserver(mDifferentStations, mDifferent)
                msg.what == START_ONLINE_PLAYER -> OberverOnLinePlayerManager.getInstance().notifyObserver(mRequestReplayUrl, mPlayType)
                msg.what == UPDATE_ONE_DAY_PROGRAM -> ObserverUIListenerManager.getInstance().notifyOneDayProgramUpData(mOneDayPrograms)
            }
        }
    }

    private fun checkNetWorkStatus() : Boolean {
        val connectivityManager : ConnectivityManager = mContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mobNetInfo : NetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        val wifiNetInfo : NetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)

        OnLineFMConnectManager.isConnectNet = !(!mobNetInfo.isConnected && !wifiNetInfo.isConnected)
        Log.d(getTAG(),"isConnectNet "+ OnLineFMConnectManager.isConnectNet)
        return OnLineFMConnectManager.isConnectNet
    }

    private var mNetReceiver : BroadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val connectivityManager : ConnectivityManager ? = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            val mobNetInfo : NetworkInfo = connectivityManager!!.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            val wifiNetInfo : NetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)

            if (!mobNetInfo.isConnected && !wifiNetInfo.isConnected) { isConnectNet = false
            } else {
                isConnectNet = true
                getOnLineFM()
            }
            ObserverUIListenerManager.getInstance().notifyConnectStatus(isConnectNet)
        }
    }

    private fun refreshOnLineWorkerThread() {
        if(mOnLineWorkerThread != null) mOnLineWorkerThread!!.shutdown()
        mOnLineWorkerThread = OnLineWorkerThread(mContext)
    }

    init {
        if(mContext == null)
            mContext = context
        mMainInfoCode = this
        refreshOnLineWorkerThread()
        context.registerReceiver(mNetReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    fun destroyManager() {
        handler.removeMessages(UPDATE_INFO)
        handler.removeMessages(UPDATE_DIFFERENT_INFO)
        handler.removeMessages(START_ONLINE_PLAYER)
        handler.removeMessages(UPDATE_ONE_DAY_PROGRAM)
        if(mOnLineWorkerThread != null) mOnLineWorkerThread!!.shutdown()
        mOnLineWorkerThread = null
        mHttpUtil = null
        mConvert = null
        getGPSInfo = null
        mMainInfoCode = null
        mInfoMap = null
        mStations = null
        mCenterStations = null
        map = null
        centerMap = null
        mDifferentStations = null
        mDifferent = null
        mOneDayPrograms = null
        mContext!!.unregisterReceiver(mNetReceiver)
        mContext = null
    }

    /* **********
     *online thread
     * ********/

    fun getOnLineStations() : List<Station>? {
        return mStations
    }

    fun getOnLineCenterStations() : List<Station>? {
        return mCenterStations
    }

    fun getOnLineStationMap(mStations : List<Station> ) :  Map<Int, Station> {
        val map : MutableMap<Int, Station> = HashMap()
        for (i in mStations) {
            map.put(i.stationId, i)
        }
        return map
    }

    fun getOnLineStationProgramMap() : MutableMap<Int, List<StationProgram>>? {
        return map
    }

    fun getOnLineStationProgramCentermap() : Map<Int, List<StationProgram>>? {
        return centerMap
    }

    fun getOnLineFM() {
        if(!checkNetWorkStatus())
            return
        mOnLineWork = true
        mHttpUtil = HttpUtil()
        mConvert = JsonStringConvert()
        map = HashMap()
        centerMap = HashMap()
        getGPSInfo = GetGPSInfo(mContext)
        getGPSInfo!!.getLocalName(getGPSInfo!!.location)
    }

    fun updateOnLineInfo() {
        mGPS_Name = getGPSInfo!!.getStringInfo(mContext, GetGPSInfo.KEY_LOCAL_NAME_FOR_THREAD)
        Log.d(getTAG(), "mGPS_Name : $mGPS_Name")
        mOnLineWorkerThread!!.SendNewRunnable(OnLineWorkerThread.OPERATION_GET_ONLINE_INFO)
    }

    fun getTime() {
        val c : Calendar = Calendar.getInstance()
        c.timeZone = TimeZone.getTimeZone("GMT+8:00")
        val week : Int = c.get(Calendar.DAY_OF_WEEK)
        val hour : Int = c.get(Calendar.HOUR_OF_DAY)
        val minute : Int = c.get(Calendar.MINUTE)
        mDayOfWeek = week
        var mh : String = hour.toString()
        if (hour < 10) {
            mh = "${'0'}" + mh
        }
        var mM : String = minute.toString()
        if (minute < 10) {
            mM = "${'0'}" + mM
        }
        mTime = mh + "${':'}" + mM
    }

    fun getLocalInfo() : List<PropertyInfo.values>? {
        return mInfoMap!![20]
    }

    private fun getStationsLocalId(mGPS_Name : String) : Int {
        var id = 0
        val mLocal : List<PropertyInfo.values> = mInfoMap!![20]!!
        for (i in mLocal) {
            if (Objects.equals(i.getvaluesname(), mGPS_Name)) {
                id = i.getvaluesId()
                mLocal_ID = id
                break
            }
        }
        return id
    }

    fun getCurrentTime() : String {
        return mTime
    }

    private fun connectPrepare() {
        var mTokenResult : String? = null
        try {
            mTokenResult = mHttpUtil!!.access_Token
            Log.d(getTAG(), "result :$mTokenResult")
        } catch (e : NullPointerException) {
            if(mTokenResult == null)
                return
        }
        try {
            mConvert!!.getOAuthPermission(mTokenResult, mHttpUtil)
        } catch (e : JSONException) {
            e.printStackTrace()
        }
    }
    fun getClassificationAttributeAsyncEx(mCategoryID : Int) : ClassificationAttributePattern? {
        getTime()
        connectPrepare()
        try {
            val mPropertyInfo : String? = mHttpUtil!!.getProperty(mCategoryID)
            return mConvert!!.getPropertyInfo(mPropertyInfo, mCategoryID)
        } catch (e : JSONException) {
            e.printStackTrace()
        }
        return null
    }

    fun getOneDayProgram(mStation : Station, day_info : Int){
        this.mStation = mStation
        this.mDayInfo = day_info
        mOneDayPrograms = null
        mOnLineWorkerThread!!.SendNewRunnable(OnLineWorkerThread.OPERATION_GET_ONE_DAY_PROGRAM)
    }

    fun getOneDayProgramAsyncEx() {//0:yesterday; 1:today; 2:tomorrow
        var dayOfWeek : Int = this.mDayOfWeek
        if(mDayInfo == 0) {
            if(dayOfWeek == 1) dayOfWeek = 6
            else dayOfWeek -= 1
        }
        if(mDayInfo == 2) {
            if(dayOfWeek == 6) dayOfWeek = 1
            else dayOfWeek += 1
        }
        var find = false
        try {
            val programInfo : String = mHttpUtil!!.getStationProgram(mStation.stationId, dayOfWeek)
            mOneDayPrograms = mConvert!!.getStationProgram(programInfo, dayOfWeek)
            find = true
        } catch (e : JSONException) {
            e.printStackTrace()
        }
        if (find) {
            handler.sendEmptyMessage(UPDATE_ONE_DAY_PROGRAM)
        }
    }

    fun getOnlineInfoAsyncEx() {
        var find: Boolean
        mInfoMap = getClassificationAttributeAsyncEx(5)!!.infoMap
        val stationInfo: String?
        val centerStationInfo : String?
        try {
            stationInfo = mHttpUtil!!.getAll_Station(5, 1, getStationsLocalId(mGPS_Name), null)
            centerStationInfo = mHttpUtil!!.getAll_Station(5, 1, 1242, null)
        } catch (e : NullPointerException) {
            return
        }

        try {
            mStations = mConvert!!.getAll_Station(stationInfo)
            mCenterStations = mConvert!!.getAll_Station(centerStationInfo)
            for (i in (mCenterStations as MutableList<Station>?)!!) {
                val programInfo : String = mHttpUtil!!.getStationProgram(i.stationId, mDayOfWeek)
                val programs : List<StationProgram> = mConvert!!.getStationProgram(programInfo, mDayOfWeek)
                centerMap!!.put(i.stationId, programs)
            }
            for (i in (mStations as MutableList<Station>?)!!) {
                val programInfo : String = mHttpUtil!!.getStationProgram(i.stationId, mDayOfWeek)
                val programs : List<StationProgram> = mConvert!!.getStationProgram(programInfo, mDayOfWeek)
                map!!.put(i.stationId, programs)
            }
            find = true
        } catch (e : JSONException) {
            find = false
            e.printStackTrace()
        }
        if (find) {
            handler.sendEmptyMessage(UPDATE_INFO)
        }
    }

    fun setDifferentLocalID(mLocal_ID : Int, mCurrentPage : Int) {
        if (mLocal_ID != mLocal_ID)
            mOnLineWorkerThread!!.SendNewRunnable(OnLineWorkerThread.OPERATION_GET_OTHER_ONLINE_INFO, mLocal_ID, mCurrentPage)
    }
    @SuppressLint("UseSparseArrays")
    fun getDifferentLocalOnlineInfoAsyncEx (mLocal_ID : Int, mCurrentPage : Int) {
        var find = false
        val stationInfo: String?
        try {
            stationInfo = mHttpUtil!!.getAll_Station(5, mCurrentPage, mLocal_ID, null)
        } catch (e : NullPointerException) {
            return
        }

        try {
            mDifferent = HashMap()
            mDifferentStations = mConvert!!.getAll_Station(stationInfo)
            for (i in mDifferentStations!!) {
                val programInfo : String = mHttpUtil!!.getStationProgram(i.stationId, mDayOfWeek)
                val mPrograms : List<StationProgram> = mConvert!!.getStationProgram(programInfo, mDayOfWeek)
                mDifferent!!.put(i.stationId, mPrograms)
            }
            find = true
        } catch (e : JSONException) {
            e.printStackTrace()
        }
        if (find) {
            handler.sendEmptyMessage(UPDATE_DIFFERENT_INFO)
        }
    }

    /* ********************
     * *replay on line **
     * *******************/
    fun getReplayUrl(stationId: Int, programStartTime: java.lang.String?, programEndTime: java.lang.String?, play_type: Int) {
        this.stationId = stationId
        this.mProgramStartTime = programStartTime
        this.mProgramEndTime = programEndTime
        this.mPlayType = play_type
        mOnLineWorkerThread!!.SendNewRunnable(OnLineWorkerThread.OPERATION_GET_REPLAY_URL, mLocal_ID, -1)
    }

    private fun getData() : String {
        val c : Calendar = Calendar.getInstance()
        val mYear : String = c.get(Calendar.YEAR).toString() // 获取当前年份
        val mMonth : Int = c.get(Calendar.MONTH) + 1// 获取当前月份
        val mDay = c.get(Calendar.DAY_OF_MONTH)// 获取当日期
        val mSMonth: String?
        val mSDay : String?

        mSMonth = if (mMonth < 10)
            "${'0'}"+mMonth.toString()
        else
            mMonth.toString()
        mSDay = if (mDay < 10)
            "${'0'}"+mDay.toString()
        else
            mDay.toString()
        return mYear + mSMonth + mSDay
    }

    fun getReplayUrlAsyncEx() {
        try {
            val mRule : JsonStringConvert.Rule = mConvert!!.getReplay_URL_Rule(mHttpUtil!!.replayRule)

            mRequestReplayUrl = if (mPlayType == 2) {
                val mData : String = getData()
                val mStartTime : String = mProgramStartTime!!.replace(":", "")
                val mEndTime : String = mProgramEndTime!!.replace(":", "")
                "https://" + mRule.mReplay_Domain + "/cache/" + mData + "/" + stationId + "/" + stationId.toString() + "_" + mData + "_" + mStartTime + "_" + mEndTime + "_" + 24 + "_" + 0 + ".aac"
            } else {
                //play station
                "https://" + mRule.mDomain + "/live/" + stationId + "/" + 24 + "k.m3u8"
            }
            handler.sendEmptyMessage(START_ONLINE_PLAYER)
        } catch (e : JSONException) {
            e.printStackTrace()
        }
    }

    fun getSearchResult(keyword : String, type : String, current_page : Int) {
        mOnLineWorkerThread!!.SendNewRunnable(OnLineWorkerThread.OPERATION_GET_SEARCH_RESULT, keyword, type, current_page)
    }

    fun getSearchResultAsyncEx (keyword : String, type : String, mCurrentPage : Int) {
        val mSearchResult: String?
        try{
            mSearchResult = mHttpUtil!!.getSearchResult(keyword, type, mCurrentPage)
        } catch (e : NullPointerException) {
            return
        }
        try {
            mConvert!!.getSearchResult(mSearchResult,type)
        } catch (e : JSONException) {
            e.printStackTrace()
        }
    }

    fun getClassificationAttribute (mCategoryID : Int,
                                           requestCallBack : RequestCallBack<ClassificationAttributePattern>) {
        mOnLineWorkerThread!!.SendNewRunnable(OnLineWorkerThread.OPERATION_GET_CLASSIFY_ATTRIBUTE,
                requestCallBack,
                mCategoryID,
                -1)
    }

    fun getRequestProgramClassify(callBack : RequestCallBack<RequestProgramClassifyListPattern>) {
        mOnLineWorkerThread!!.SendNewRunnable(OnLineWorkerThread.OPERATION_GET_REQUEST_PROGRAM_CLASSIFY_PROGRAM,
                callBack)
    }

    fun getCurrentDemandChannel(channelID : Int,
                                       requestCallBack : RequestCallBack<DemandChannelPattern>) {
        mOnLineWorkerThread!!.SendNewRunnable(OnLineWorkerThread.OPERATION_GET_CURRENT_DEMAND_CHANNEL,
                requestCallBack,
                channelID,
                -1)
    }

    fun getCurrentDemandChannelPrograms(channelID : Int,
                                               current_page : Int,
                                               order : Int,
                                               requestCallBack : RequestCallBack<ChannelProgramPattern>) {
        mOrder = order
        mOnLineWorkerThread!!.SendNewRunnable(OnLineWorkerThread.OPERATION_GET_CURRENT_DEMAND_CHANNEL_PROGRAMS,
                requestCallBack, channelID, current_page)
    }

    fun getClassifyInfoContext(mCurrentPage : Int,
                                      mChannelID : Int,
                                      mAttrId : Array<Int>?,
                                      requestCallBack : RequestCallBack<DemandChannelPattern>) {
        mOnLineWorkerThread!!.SendNewRunnable(OnLineWorkerThread.OPERATION_GET_CLASSIFY_CONTEXT,
                requestCallBack,
                mChannelID,
                mCurrentPage)
        if(mAttrId != null) mDCAttrId = mAttrId
    }

    fun getRequestProgramClassifyAsyncEx() : List<RequestProgramClassify> ? {
        connectPrepare()
        val mRequestProgramResult: String?
        return try {
            mRequestProgramResult = mHttpUtil!!.requestProgram
            mConvert!!.getRequestProgramClassifiesList(mRequestProgramResult)
        } catch (e : JSONException) {
            e.printStackTrace()
            null
        }
    }

    fun getDemandChannelContextAsyncEx(categoryID : Int, currentPage : Int) : List<DemandChannel> ? {

        if(mHttpUtil!!.getAccess_token() == null)
            connectPrepare()
        val mDemandChannelContextResult: String?
        return try {
            mDemandChannelContextResult = mHttpUtil!!.getAll_Station(categoryID, currentPage,0, mDCAttrId)
            mConvert!!.getDemandChannelContextList(mDemandChannelContextResult)
        } catch (e : JSONException) {
            e.printStackTrace()
            null
        }
    }
    fun getCurrentDemandChannelAsyncEx(channelID : Int) : DemandChannel ? {
        if(mHttpUtil!!.getAccess_token() == null)
            connectPrepare()
        val mCurrentDemandChannelResult: String?
        return try {
            mCurrentDemandChannelResult = mHttpUtil!!.getCurrentDemandChannel(channelID)
            mConvert!!.getCurrentDemandChannelInfo(mCurrentDemandChannelResult)
        } catch (e : JSONException) {
            e.printStackTrace()
            null
        }
    }

    fun getCurrentDemandChannelProgramsAsyncEx(channelID : Int, current_page : Int) : ChannelProgramPattern ? {
        if(mHttpUtil!!.getAccess_token() == null)
            connectPrepare()
        val mCurrentDemandChannelProgramsResult: String?
        return try {
            mCurrentDemandChannelProgramsResult = mHttpUtil!!.getCurrentDemandChannelPrograms(channelID,
                    current_page,
                    mOrder)
            mConvert!!.getCurrentDemandChannelPrograms(mCurrentDemandChannelProgramsResult)
        } catch (e : JSONException) {
            e.printStackTrace()
            null
        }
    }

    /* *****
       add by gaolin 4/20
     */
    fun getFiveRecmThumb(callBack : RequestCallBack<ClassifyRecommend>, sectionId : Int) {
        mOnLineWorkerThread!!.SendNewRunnable(OnLineWorkerThread.OPERATION_GET_FIVE_RECOMMEND_THUMB, callBack, sectionId)
    }

    fun getFiveRecmAsyncEx(sectionId : Int) : ClassifyRecommend ? {
        val mRecommendResult : String
        return try {
            mRecommendResult = mHttpUtil!!.getFiveRecommend(sectionId)
            mConvert!!.getClassifyRecommendheader(mRecommendResult, sectionId)
        }catch (e : JSONException) {
            e.printStackTrace()
            null
        }
    }

    fun getRequestRecmmendProgram(callBack : RequestCallBack<ClassifyRecommend>, category_id : Int) {
        mOnLineWorkerThread!!.SendNewRunnable(OnLineWorkerThread.OPERATION_GET_REQUEST_PROGRAM_RECOMMEND, callBack, category_id)
    }

    fun getRecommendAsyncEx(category_id : Int) : ClassifyRecommend ? {
        val mRecommendResult : String
        return try {
            mRecommendResult = mHttpUtil!!.getRecommendProgram(category_id)
            mConvert!!.getClassifyRecommendItem(mRecommendResult, category_id)
        }catch (e : JSONException) {
            e.printStackTrace()
            null
        }
    }

    fun getRecommendThumb( callBack : RequestCallBack<Bitmap>,  url : String) {
        mOnLineWorkerThread!!.SendNewRunnable(OnLineWorkerThread.OPERATION_GET_REQUEST_RECOMMEND_THUMB, callBack, url)
    }

    fun getThumbAsyncEx(url : String) : Bitmap ? {
        return try {
            mConvert!!.getThumb(url)
        }catch (e : Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getRecommendsDataList(section_id : Int, requestCallBack : RequestCallBack<RecommendsDataPattern>) {
        mOnLineWorkerThread!!.SendNewRunnableRecommends(OnLineWorkerThread.OPERATION_GET_REQUEST_RECOMMEND_LIST, requestCallBack, section_id)
    }

    fun getRecommendsDataListAsyncEx(section_id : Int) : RecommendsDataPattern ? {
        val mRecommendResult : String
        return try {
            mRecommendResult = mHttpUtil!!.getFiveRecommend(section_id)
            mConvert!!.getRecommendsData(mRecommendResult)
        }catch (e : JSONException) {
            e.printStackTrace()
            null
        }
    }

    fun getWaPiDataList(category_id : Int, requestCallBack : RequestCallBack<WaPiDataPattern>) {
        mOnLineWorkerThread!!.SendNewRunnableWaPi(OnLineWorkerThread.OPERATION_GET_WAPI_DATA_LIST, requestCallBack, category_id)
    }

    fun getWaPiDataListAsyncEx(category_id : Int) : WaPiDataPattern ? {
        val mWaPiDataResult : String
        return try {
            mWaPiDataResult = mHttpUtil!!.getWapiDataResult(category_id)
            mConvert!!.getWapiData(mWaPiDataResult)
        }catch (e : JSONException) {
            e.printStackTrace()
            null
        }
    }

    fun convertToMhz(freq : Int) : String ? {
        return if (freq == 0) {
            "000.0"
        } else {
            String.format(Locale.US, "%.1f", freq / 100f)
        }
    }
}

