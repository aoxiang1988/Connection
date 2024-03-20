package com.sec.myonlinefm

import com.sec.myonlinefm.OnLineFMConnectManager.Companion.mMainInfoCode
import org.json.JSONException
import com.sec.myonlinefm.updataUIListener.ObserverUIListenerManager
import org.json.JSONArray
import android.content.*

import com.sec.myonlinefm.data.Station
import com.sec.myonlinefm.data.StationProgram
import com.sec.myonlinefm.xmlcheck.Program
import com.sec.myonlinefm.xmlcheck.PullLocalInfoParser
import java.lang.Exception
import java.util.*

/**
 * Created by srct on 2018/1/11.
 */
class FMListData(private val mContext: Context?) {
    //    private ArrayList<HashMap<String, String>> mListInfo = null;
    private val mLocalName: String?
    private val getGPSInfo: GetGPSInfo?
    private var mStations: MutableList<Station?>? = null
    private var map: MutableMap<Int?, MutableList<StationProgram?>?>? = null
    private var mCenterStations: MutableList<Station?>? = null
    private var centermap: MutableMap<Int?, MutableList<StationProgram?>?>? = null
    fun makeLocationList(stations: MutableList<Station?>?, map: MutableMap<Int?, MutableList<StationProgram?>?>?, type: Int) {
        if (type == TYPE_LOCAL) {
            mStations = stations
            this.map = map
        }
        if (type == TYPE_CENTER) {
            mCenterStations = stations
            centermap = map
        }
        val mLocation = getGPSInfo!!.getLocation()
        //为空重新制表
        if (mLocalListInfo!!.size == 0 || mCenterListInfo!!.size == 0 || mLocalHashMap!!.size == 0) {
            if (mLocalName != null) {
                //diaLog 刷新列表
                //   displayDialog();
                makeList(mLocalName, type)
            } else {
                //dialog 重新定位重新做表
                //    displayDialog();
                makeList(getGPSInfo.getLocalName(mLocation), type)
            }
        }
    }

    private fun makeList(localName: String?, type: Int) {
        try {
            mLocalListInfo = ArrayList()
            mCenterListInfo = ArrayList()
            mLocalHashMap = HashMap()
            val `is` = mContext!!.getAssets().open("$localName.xml")
            val parser = PullLocalInfoParser()
            val localInfos = parser.parse(`is`)
            //            DBManager dbmanager = new DBManager(mContext);
            //            dbmanager.insertProgram(parser.getMap(), localName);
            //            dbmanager.close();
            for (i in localInfos!!.indices) {
                if (type == TYPE_LOCAL) {
                    for (j in mStations!!.indices) {
                        if (mStations!!.get(j)!!.getStationTitle() == localInfos[i]!!.getstationname()
                                && mStations!!.get(j)!!.getStationFreq() == "") {
                            mStations!!.get(j)!!.setStationFreq(Objects.requireNonNull(mMainInfoCode)!!.convertToMhz(localInfos[i]!!.getchannel()))
                        }
                    }
                }
                if (type == TYPE_CENTER) {
                    for (j in mCenterStations?.indices!!) {
                        if (mCenterStations!!.get(j)!!.getStationTitle() == localInfos[i]!!.getstationname()) {
                            mCenterStations!!.get(j)!!.setStationFreq(Objects.requireNonNull(mMainInfoCode)!!.convertToMhz(localInfos[i]!!.getchannel()))
                        }
                    }
                }
            }
            if (type == TYPE_LOCAL) {
                addNetInfo(mStations, map)
                ObserverUIListenerManager.Companion.getInstance()!!.notifyLiveRadioLocalObserver(mStations, map)
            }
            if (type == TYPE_CENTER) {
                addNetInfo(mCenterStations, centermap)
                ObserverUIListenerManager.Companion.getInstance()!!.notifyLiveRadioCenterObserver(mCenterStations, centermap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addNetInfo(mStations: MutableList<Station?>?, map: MutableMap<Int?, MutableList<StationProgram?>?>?) {
        val UNKNOWN_PROGRAM = "暂无节目信息"
        Objects.requireNonNull(mMainInfoCode)!!.getTime()
        val mTime = mMainInfoCode!!.getCurrentTime()
        for (i in mStations!!.indices) {
            val mList = map!!.get(mStations.get(i)!!.getStationId())
            //            String mCurrentPro = UNKNOWN_PROGRAM;
            var mNextPro = UNKNOWN_PROGRAM
            var mNextProTime: String? = "23:59"
            var endTimeTemp: String?
            var starTimeTemp: String?
            if (mList != null) {
                for (j in mList.indices) {
                    starTimeTemp = mList[j]!!.getProgramStart_time()
                    endTimeTemp = mList[j]!!.getProgramEnd_Time()
                    if (endTimeTemp == "00:00") endTimeTemp = "24:00"
                    if (endTimeTemp != null) {
                        if (starTimeTemp!!.compareTo(mTime) <= 0 &&
                                endTimeTemp.compareTo(mTime) > 0) {
            //                        mCurrentPro = mList.get(j).getProgramTitle();
                            mStations.get(i)!!.setCurrentProgram(mList[j])
                            mStations.get(i)!!.setCurrentProgramTime(mList[j]!!.getProgramStart_time())
                            mStations.get(i)!!.setWhichItem(j)
                        }
                    }
                    if (starTimeTemp!!.compareTo(mTime) > 0 &&
                            endTimeTemp!!.compareTo(mNextProTime!!) <= 0) {
                        mNextProTime = starTimeTemp
                        mNextPro = mList[j]!!.getProgramTitle().toString()
                        mStations.get(i)!!.setNextProgram(mNextPro)
                    }
                }
            }
        }
    }

    companion object {
        const val TYPE_LOCAL = 1
        const val TYPE_CENTER = 2
        private var mLocalListInfo: ArrayList<HashMap<String?, String?>?>? = null
        private var mCenterListInfo: ArrayList<HashMap<String?, String?>?>? = null
        private var mLocalHashMap: HashMap<String?, String?>? = null
        private var mChannelNetInfo: HashMap<String?, HashMap<String?, String?>?>? = null
        private var mProgramNetInfo: HashMap<String?, MutableList<Program?>?>? = null
        private fun getListInfo(context: Context?, key: String?): ArrayList<HashMap<String?, String?>?>? {
            val datas = ArrayList<HashMap<String?, String?>?>()
            val sp = context!!.getSharedPreferences("list_demo", Context.MODE_PRIVATE)
            val result = sp.getString(key, "")
            try {
                val array = JSONArray(result)
                for (i in 0 until array.length()) {
                    val itemObject = array.getJSONObject(i)
                    val itemMap = HashMap<String?, String?>()
                    val names = itemObject.names()
                    if (names != null) {
                        for (j in 0 until names.length()) {
                            val name = names.getString(j)
                            val value = itemObject.getString(name)
                            itemMap[name] = value
                        }
                    }
                    datas.add(itemMap)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return datas
        }

        private fun getHashMapData(context: Context?, key: String?): HashMap<String?, String?> {
            val datas = HashMap<String?, String?>()
            val sp = context!!.getSharedPreferences("config", Context.MODE_PRIVATE)
            val result = sp.getString(key, "")
            try {
                val array = JSONArray(result)
                for (i in 0 until array.length()) {
                    val itemObject = array.getJSONObject(i)
                    val names = itemObject.names()
                    if (names != null) {
                        for (j in 0 until names.length()) {
                            val name = names.getString(j)
                            val value = itemObject.getString(name)
                            datas[name] = value
                        }
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return datas
        }

        fun getLocalHashMap(): HashMap<String?, String?>? {
            return mLocalHashMap
        }

        fun getChannelNetInfo(): HashMap<String?, HashMap<String?, String?>?>? {
            return mChannelNetInfo
        }

        fun getProgramNetInfo(): HashMap<String?, MutableList<Program?>?>? {
            return mProgramNetInfo
        }
    }

    init {
        getGPSInfo = GetGPSInfo(mContext)
        mLocalName = getGPSInfo.getStringInfo(mContext, GetGPSInfo.Companion.KEY_LOCAL_NAME)
        val KEY_CENTER_LIST = "centerList"
        val KEY_HASH_MAP = "hash map"
        val KEY_LOCAL_LIST = "localList"
        mLocalListInfo = getListInfo(mContext, KEY_LOCAL_LIST)
        mCenterListInfo = getListInfo(mContext, KEY_CENTER_LIST)
        mLocalHashMap = getHashMapData(mContext, KEY_HASH_MAP)
        mProgramNetInfo = HashMap()
        mChannelNetInfo = HashMap()
    }
}