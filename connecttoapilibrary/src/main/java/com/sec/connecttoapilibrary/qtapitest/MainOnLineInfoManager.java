package com.sec.connecttoapilibrary.qtapitest;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sec.connecttoapilibrary.RequestCallBack;
import com.sec.connecttoapilibrary.qtapitest.OnLineFMPlayerListener.OberverOnLinePlayerManager;
import com.sec.connecttoapilibrary.qtapitest.liveRadioData.ClassifyConditionInfoPattern;
import com.sec.connecttoapilibrary.qtapitest.liveRadioData.PropertyInfo;
import com.sec.connecttoapilibrary.qtapitest.liveRadioData.Station;
import com.sec.connecttoapilibrary.qtapitest.liveRadioData.StationProgram;
import com.sec.connecttoapilibrary.qtapitest.defultonlineinfolistener.ObserverDefaultLiveRadioDefaultLiveRadioListenerManager;
import com.sec.connecttoapilibrary.qtapitest.updataLiveRadioUIListener.ObserverLiveRadioLiveRadioUIListenerManager;

import org.json.JSONException;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/3/8.
 * this is all the main work code of online fm ,we can just use this to replace the RadioPlayer' code...
 */

public class MainOnLineInfoManager {

    private Context mContext;

    boolean isConnectNet = true;
    private boolean mOnLineWork = false;
    private HttpUtil mHttpUtil;
    private JsonStringConvert mConvert;
    private OnLineWorkerThread mOnLineWorkerThread = null;
    private static String _TAG = "MainInfoCode";

    private String mGPS_Name = "天津";
    private List<Station> mStations = null;
    private List<Station> mCenterStations = null;
    private Map<Integer, List<StationProgram>> map = null;
    private Map<Integer, List<StationProgram>> centermap = null;
    private HashMap<Integer, List<PropertyInfo.values>> mInfoMap = null;

    private static int day_of_week = 1;
    private static String mTime;
    public static int mLocal_ID;

    private static final int UPDATE_LOCAL_LIST_INFO = 1;
    private static final int UPDATE_CENTER_LIST_INFO = 2;
    private static final int UPDATE_DIFFERENT_INFO = 3;
    private static final int START_ONLINE_PLAYER = 4;
    private static final int UPDATE_ONE_DAY_PROGREM = 5;

    private Station mStation;
    private int day_info;
    private List<StationProgram> mOneDayProgrems = null;
    private List<Station> mDifferentStations ;
    private Map<Integer, List<StationProgram>> mDifferent;
    private int mCurrentPage = 1;

    private int stationId;
    private int play_type;
    private String programStart_time;
    private String programEnd_time;
    private String mRequest_Replay_Url;
    private static MainOnLineInfoManager mMainInfoCode;
    private RequestCallBack mCallBack;

    public MainOnLineInfoManager(Context context){
        mContext = context;
        mMainInfoCode = this;
        refreshOnLineWorkerThread();
        BroadcastReceiver mNetReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                assert connectivityManager != null;
                NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
                    isConnectNet = false;
                } else {
                    isConnectNet = true;

                    mOnLineWorkerThread.SendNewRunnable(OnLineWorkerThread.OPERATION_GET_ONLINE_LOCAL_LIST_INFO);
//                if (mOnLineWork)
//                    mWorkerThread.doOperation(WorkerThread.OPERATION_GET_ONLINE_INFO, 1);
                }
            }
        };
        context.registerReceiver(mNetReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    protected static MainOnLineInfoManager getIntense(){
        return mMainInfoCode;
    }

    /* **********
     *online thread
     * ********/

    protected boolean checkNetWorkStatus(){
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        isConnectNet = !(!mobNetInfo.isConnected() && !wifiNetInfo.isConnected());
        Log.d(_TAG,"isConnectNet "+isConnectNet);
        return isConnectNet;
    }

    private void refreshOnLineWorkerThread() {
        if(mOnLineWorkerThread != null) mOnLineWorkerThread.shutdown();
        mOnLineWorkerThread = new OnLineWorkerThread(mContext);
    }

    /* *
     * for get info api
     * */

    public void GetOnLineFM(int list_type) {
        if(!checkNetWorkStatus())
            return;
        mOnLineWork = checkNetWorkStatus();
        GetGPSInfo getGPSInfo = new GetGPSInfo(mContext);
        getGPSInfo.getLocalName(getGPSInfo.getLocation());
        mGPS_Name = getGPSInfo.getStringInfo(mContext, GetGPSInfo.KEY_LOCAL_NAME_FOR_THREAD);
        mHttpUtil = new HttpUtil();
        mConvert = new JsonStringConvert();
        map = new HashMap<>();
        centermap = new HashMap<>();

        if(list_type == FMListData.TYPE_LOCAL_LIST) {
            mOnLineWorkerThread.SendNewRunnable(OnLineWorkerThread.OPERATION_GET_ONLINE_LOCAL_LIST_INFO);
        }
        if(list_type == FMListData.TYPE_CENTER_LIST)
            mOnLineWorkerThread.SendNewRunnable(OnLineWorkerThread.OPERATION_GET_ONLINE_CENTER_LIST_INFO);
    }

    public void getDifferentLocalOnlineInfo(int mLocal_ID) {
        if (MainOnLineInfoManager.mLocal_ID != mLocal_ID)
            mOnLineWorkerThread.SendNewRunnable(OnLineWorkerThread.OPERATION_GET_OTHER_ONLINE_INFO, mLocal_ID);
    }

    public void getOneDayProgram(Station mStation, int day_info){
        this.mStation = mStation;
        this.day_info = day_info;
        mOneDayProgrems = null;

        mOnLineWorkerThread.SendNewRunnable(OnLineWorkerThread.OPERATION_GET_ONE_DAY_PROGRAM);
    }

    public void getReplayUrl(StationProgram stationProgram, int play_type) {
        this.stationId = stationProgram.getProgramChannel_Id();
        this.programStart_time = stationProgram.getProgramStart_time();
        this.programEnd_time = stationProgram.getProgramEnd_Time();
        this.play_type = play_type;

        mOnLineWorkerThread.SendNewRunnable(OnLineWorkerThread.OPERATION_GET_REPLAY_URL, mLocal_ID);
    }

    public void setCurrentPage(int mCurrentPage){
        this.mCurrentPage = mCurrentPage;
    }

    public void getSearchResult(String keyword, String type, int current_page) {
        mOnLineWorkerThread.SendNewRunnable(OnLineWorkerThread.OPERATION_GET_SEARCH_RESULT, keyword, type ,current_page);
    }

    /* *
     * thread work....
     * */
    private int getStationsLocalId(String mGPS_Name) {
        int id = 0;
        List<PropertyInfo.values> mLocal = getClassifyConditionInfo(5).getInfoMap().get(20);
        for (int i = 0; i < mLocal.size(); i++) {
            if (Objects.equals(mLocal.get(i).getvaluesname(), mGPS_Name)) {
                id = mLocal.get(i).getvaluesId();
                mLocal_ID = id;
                break;
            }
        }
        return id;
    }

    private void ConnectPrepare() {
        String mTokenResult = null;
        try {
            mTokenResult = mHttpUtil.getAccess_Token();
            Log.d(_TAG, "result :" + mTokenResult);
        } catch (NullPointerException e) {
            if(mTokenResult == null)
                return;
        }
        try {
            mConvert.getOAuth_Permission(mTokenResult, mHttpUtil);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public ClassifyConditionInfoPattern getClassifyConditionInfo(int category_id) {
        getTime();
        try {
            String property_info = mHttpUtil.getProperty(category_id);
            return mConvert.getPropertyInfo(property_info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        mInfoMap = mConvert.getmInfoMap();
        return null;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATE_LOCAL_LIST_INFO)
                ObserverDefaultLiveRadioDefaultLiveRadioListenerManager.getInstance().notifyObserverLocalList(mStations,map);
            else if (msg.what == UPDATE_CENTER_LIST_INFO)
                ObserverDefaultLiveRadioDefaultLiveRadioListenerManager.getInstance().notifyObserverCenterList(mCenterStations, centermap);
            else if (msg.what == UPDATE_DIFFERENT_INFO)
                ObserverDefaultLiveRadioDefaultLiveRadioListenerManager.getInstance().notifyDifferentInfoObserver(mDifferentStations, mDifferent);
            else if (msg.what == START_ONLINE_PLAYER)
                OberverOnLinePlayerManager.getInstance().notifyObserver(mRequest_Replay_Url, play_type);
            else if(msg.what == UPDATE_ONE_DAY_PROGREM){
                ObserverLiveRadioLiveRadioUIListenerManager.getInstance().notifyOneDayProgramUpData(mOneDayProgrems);
            }
        }
    };

    public void getOneDayProgramAsyncEx() {//0:yesterday; 1:today; 2:tomorrow
        int day_of_week = MainOnLineInfoManager.day_of_week;
        if(day_info == 0) {
            if(day_of_week == 1) day_of_week = 6;
            else day_of_week = day_of_week -1;
        }
        if(day_info == 2) {
            if(day_of_week == 6) day_of_week = 1;
            else day_of_week = day_of_week +1;
        }
        boolean find = false;
        try {
            String program_info = mHttpUtil.getStation_Progrem(mStation.getStationId(), day_of_week);
            mOneDayProgrems = mConvert.getStation_Program(program_info, day_of_week);
            find = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (find) {
            handler.sendEmptyMessage(UPDATE_ONE_DAY_PROGREM);
        }
    }

    public void getOnlineLocalListInfoAsyncEx() {
        boolean find = false;
        ConnectPrepare();
        String station_info = null;
        try {
            station_info = mHttpUtil.getAll_Station(5, 1, getStationsLocalId(mGPS_Name));
        } catch (NullPointerException e) {
            return;
        }
        try {
            mStations = mConvert.getAll_Station(station_info);
            for (int i = 0; i < mStations.size(); i++) {
                String program_info = mHttpUtil.getStation_Progrem(mStations.get(i).getStationId(), day_of_week);
                List<StationProgram> mPrograms = mConvert.getStation_Program(program_info, day_of_week);
                map.put(mStations.get(i).getStationId(), mPrograms);
            }
            find = true;
        } catch (JSONException e) {
            find = false;
            e.printStackTrace();
        }
        if (find) {
            handler.sendEmptyMessage(UPDATE_LOCAL_LIST_INFO);
        }
    }

    public void getOnlineCenterListInfoAsyncEx() {
        boolean find = false;
        ConnectPrepare();
        String center_station_info = null;
        try {
            center_station_info = mHttpUtil.getAll_Station(5, 1, 1242);
        } catch (NullPointerException e) {
            return;
        }
        try {
            mCenterStations = mConvert.getAll_Station(center_station_info);
            for (int i = 0; i < mCenterStations.size(); i++) {
                String program_info = mHttpUtil.getStation_Progrem(mCenterStations.get(i).getStationId(), day_of_week);
                List<StationProgram> mPrograms = mConvert.getStation_Program(program_info, day_of_week);
                centermap.put(mCenterStations.get(i).getStationId(), mPrograms);
            }
            find = true;
        } catch (JSONException e) {
            find = false;
            e.printStackTrace();
        }
        if (find) {
            handler.sendEmptyMessage(UPDATE_CENTER_LIST_INFO);
        }
    }

    @SuppressLint("UseSparseArrays")
    public void getDifferentLocalOnlineInfoAsyncEx(int mLocal_ID) {
        boolean find = false;
        String station_info = null;
        try {
            station_info = mHttpUtil.getAll_Station(5, 1, mLocal_ID);
        } catch (NullPointerException e) {
            return;
        }
        try {
            if(mDifferent !=  null) mDifferent =  null;
            mDifferent = new HashMap<>();
            mDifferentStations = mConvert.getAll_Station(station_info);
            for (int i = 0; i < mDifferentStations.size(); i++) {
                String program_info = mHttpUtil.getStation_Progrem(mDifferentStations.get(i).getStationId(), day_of_week);
                List<StationProgram> mPrograms = mConvert.getStation_Program(program_info, day_of_week);
                mDifferent.put(mDifferentStations.get(i).getStationId(), mPrograms);
            }
            find = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (find) {
            handler.sendEmptyMessage(UPDATE_DIFFERENT_INFO);
        }
    }

    /* ********************
     * *replay on line **
     * *******************/

    public void getReplayUrlAsyncEx() {
        try {
            JsonStringConvert.Rule mRule = mConvert.getReplay_URL_Rule(mHttpUtil.getReplayRule());

            if (play_type == 2) {
                String mData = getData();
                String mStartTime = programStart_time.replace(":", "");
                String mEndTime = programEnd_time.replace(":", "");
                mRequest_Replay_Url = "https://" + mRule.mReplay_Domain + "/cache/" + mData + "/" + stationId + "/"
                        + stationId + "_" + mData + "_" + mStartTime + "_" + mEndTime + "_" + 24 + "_" + 0 + ".aac";
            } else {
                //play station
                mRequest_Replay_Url = "https://" + mRule.mDomain + "/live/" + stationId + "/" + 24 + "k.m3u8";
            }
            handler.sendEmptyMessage(START_ONLINE_PLAYER);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getSearchResultAsyncEx(String keyword, String type, int mCurrentPage) {
        String mSearch_result = null;
        try{
            mSearch_result = mHttpUtil.getSearchResult(keyword, type, mCurrentPage);
        } catch (NullPointerException e) {
            return;
        }
        try {
            mConvert.getSearchResult(mSearch_result,type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* *
     * data get function..
     * */

    protected static String convertToMhz(int freq) {
        if(freq == 0) {
            return "000.0";
        } else {
            return String.format(Locale.US, "%.1f", freq / 100f);
        }
    }

    private String getData() {
        Calendar c = Calendar.getInstance();//
        String mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        int mMonth = c.get(Calendar.MONTH) + 1;// 获取当前月份
        int mDay = c.get(Calendar.DAY_OF_MONTH);// 获取当日期
        String mSMonth = null;
        String mSDay = null;

        if (mMonth < 10)
            mSMonth = String.format("0%s", mMonth);
        else
            mSMonth = String.valueOf(mMonth);
        if (mDay < 10)
            mSDay = String.format("0%s", mDay);
        else
            mSDay = String.valueOf(mDay);
        return mYear + mSMonth + mSDay;
    }

    public static void getTime() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int week = c.get(Calendar.DAY_OF_WEEK);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        day_of_week = week;
        String mh = String.valueOf(hour);
        if (hour < 10) {
            mh = "0" + mh;
        }
        String mM = String.valueOf(minute);
        if (minute < 10) {
            mM = "0" + mM;
        }
        mTime = mh + ":" + mM;
    }

    public static String getCurrentTime() {
        return mTime;
    }
}
