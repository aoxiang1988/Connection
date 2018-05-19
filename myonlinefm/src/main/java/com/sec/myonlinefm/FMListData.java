package com.sec.myonlinefm;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.sec.myonlinefm.data.Station;
import com.sec.myonlinefm.data.StationProgram;
import com.sec.myonlinefm.updataUIListener.ObserverUIListenerManager;
import com.sec.myonlinefm.xmlcheck.LocalInfo;
import com.sec.myonlinefm.xmlcheck.Program;
import com.sec.myonlinefm.xmlcheck.PullLocalInfoParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by srct on 2018/1/11.
 */

public class FMListData {

    static final public int TYPE_LOCAL = 1;

    static final public int TYPE_CENTER = 2;

//    private ArrayList<HashMap<String, String>> mListInfo = null;

    private String mLocalName;

    private Context mContext;

    private static ArrayList<HashMap<String, String>> mLocalListInfo = null;

    private static ArrayList<HashMap<String, String>> mCenterListInfo = null;

    private static HashMap<String, String> mLocalHashMap = null;

    private static HashMap<String, HashMap<String, String>> mChannelNetInfo = null;

    private static HashMap<String, List<Program>> mProgramNetInfo = null;

    private GetGPSInfo getGPSInfo;
    public FMListData(Context context) {
        mContext = context;
        getGPSInfo = new GetGPSInfo(mContext);
        mLocalName = getGPSInfo.getStringInfo(mContext, GetGPSInfo.KEY_LOCAL_NAME);

        final String KEY_CENTER_LIST = "centerList";
        final String KEY_HASH_MAP = "hash map";
        final String KEY_LOCAL_LIST = "localList";

        mLocalListInfo = getListInfo(mContext, KEY_LOCAL_LIST);
        mCenterListInfo = getListInfo(mContext, KEY_CENTER_LIST);
        mLocalHashMap = getHashMapData(mContext, KEY_HASH_MAP);

        mProgramNetInfo = new HashMap<>();
        mChannelNetInfo = new HashMap<>();
    }

    private List<Station> mStations;
    private Map<Integer, List<StationProgram>> map;

    private List<Station> mCenterStations;
    private Map<Integer, List<StationProgram>> centermap;

    public void makeLocationList(List<Station> stations, Map<Integer, List<StationProgram>> map, int type) {

        if (type == TYPE_LOCAL) {
            this.mStations = stations;
            this.map = map;
        }
        if (type == TYPE_CENTER) {
            this.mCenterStations = stations;
            this.centermap = map;
        }

        Location mLocation = getGPSInfo.getLocation();
        //为空重新制表
        if (mLocalListInfo.size() == 0 || mCenterListInfo.size() == 0 ||
                mLocalHashMap.size() == 0) {
            if (mLocalName != null) {
                //diaLog 刷新列表
                //   displayDialog();
                makeList(mLocalName, type);
            } else {
                //dialog 重新定位重新做表
                //    displayDialog();
                makeList(getGPSInfo.getLocalName(mLocation), type);
            }
        }
    }

    private void makeList(String localName, int type) {
        try {
            mLocalListInfo = new ArrayList<>();
            mCenterListInfo = new ArrayList<>();
            mLocalHashMap = new HashMap<>();
            InputStream is = mContext.getAssets().open(localName + ".xml");
            PullLocalInfoParser parser = new PullLocalInfoParser();
            List<LocalInfo> localInfos = parser.parse(is);
            //            DBManager dbmanager = new DBManager(mContext);
            //            dbmanager.insertProgram(parser.getMap(), localName);
            //            dbmanager.close();
            for (int i = 0; i < localInfos.size(); i++) {
                if (type == TYPE_LOCAL) {
                    for (int j = 0; j < mStations.size(); j++) {
                        if (Objects.equals(mStations.get(j).getStationTitle(), localInfos.get(i).getstationname())
                                && Objects.equals(mStations.get(j).getStationFreq(), "")) {
                            mStations.get(j).setStationFreq(Objects.requireNonNull(OnLineFMConnectManager.Companion.getMMainInfoCode()).convertToMhz(localInfos.get(i).getchannel()));
                        }
                    }
                }
                if (type == TYPE_CENTER) {
                    for (int j = 0; j < mCenterStations.size(); j++) {
                        if (Objects.equals(mCenterStations.get(j).getStationTitle(), localInfos.get(i).getstationname())) {
                            mCenterStations.get(j).setStationFreq(Objects.requireNonNull(OnLineFMConnectManager.Companion.getMMainInfoCode()).convertToMhz(localInfos.get(i).getchannel()));
                        }
                    }
                }
            }
            if (type == TYPE_LOCAL) {
                addNetInfo(mStations, map);
                ObserverUIListenerManager.getInstance().notifyLiveRadioLocalObserver(mStations, map);
            }
            if (type == TYPE_CENTER) {
                addNetInfo(mCenterStations, centermap);
                ObserverUIListenerManager.getInstance().notifyLiveRadioCenterObserver(mCenterStations, centermap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<HashMap<String, String>> getListInfo(Context context, String key) {
        ArrayList<HashMap<String, String>> datas = new ArrayList<>();
        SharedPreferences sp = context.getSharedPreferences("list_demo", Context.MODE_PRIVATE);
        String result = sp.getString(key, "");
        try {
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                JSONObject itemObject = array.getJSONObject(i);
                HashMap<String, String> itemMap = new HashMap<>();
                JSONArray names = itemObject.names();
                if (names != null) {
                    for (int j = 0; j < names.length(); j++) {
                        String name = names.getString(j);
                        String value = itemObject.getString(name);
                        itemMap.put(name, value);
                    }
                }
                datas.add(itemMap);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return datas;
    }


    private static HashMap<String, String> getHashMapData(Context context, String key) {

        HashMap<String, String> datas = new HashMap<>();
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        String result = sp.getString(key, "");
        try {
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                JSONObject itemObject = array.getJSONObject(i);
                JSONArray names = itemObject.names();
                if (names != null) {
                    for (int j = 0; j < names.length(); j++) {
                        String name = names.getString(j);
                        String value = itemObject.getString(name);
                        datas.put(name, value);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return datas;
    }

    public static HashMap<String, String> getLocalHashMap() {
        return mLocalHashMap;
    }

    public static HashMap<String, HashMap<String, String>> getChannelNetInfo() {
        return mChannelNetInfo;
    }

    public static HashMap<String, List<Program>> getProgramNetInfo() {
        return mProgramNetInfo;
    }

    public void addNetInfo(List<Station> mStations, Map<Integer, List<StationProgram>> map) {
        final String UNKNOWN_PROGRAM = "暂无节目信息";
        Objects.requireNonNull(OnLineFMConnectManager.Companion.getMMainInfoCode()).getTime();
        String mTime = OnLineFMConnectManager.Companion.getMMainInfoCode().getCurrentTime();
        for(int i = 0; i < mStations.size(); i++){
            List<StationProgram> mList = map.get(mStations.get(i).getStationId());
//            String mCurrentPro = UNKNOWN_PROGRAM;
            String mNextPro = UNKNOWN_PROGRAM;
            String mNextProTime = "23:59";
            String endTimeTemp;
            String starTimeTemp;
            if(mList != null) {
                for(int j = 0; j < mList.size(); j++) {
                    starTimeTemp = mList.get(j).getProgramStart_time();
                    endTimeTemp = mList.get(j).getProgramEnd_Time();
                    if(endTimeTemp.equals("00:00"))
                        endTimeTemp = "24:00";
                    if (starTimeTemp.compareTo(mTime) <= 0 &&
                            endTimeTemp.compareTo(mTime) > 0) {
//                        mCurrentPro = mList.get(j).getProgramTitle();
                        mStations.get(i).setCurrentProgram(mList.get(j));
                        mStations.get(i).setCurrentProgramTime(mList.get(j).getProgramStart_time());
                        mStations.get(i).setWhichItem(j);
                    }
                    if (starTimeTemp.compareTo(mTime) > 0 &&
                            endTimeTemp.compareTo(mNextProTime) <= 0) {
                        mNextProTime = starTimeTemp;
                        mNextPro = mList.get(j).getProgramTitle();
                        mStations.get(i).setNextProgram(mNextPro);
                    }
                }
            }
        }
    }
}
