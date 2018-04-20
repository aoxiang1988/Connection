package com.sec.connecttoapilibrary.onlinefm;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.sec.connecttoapilibrary.onlinefm.liveRadioData.Station;
import com.sec.connecttoapilibrary.onlinefm.liveRadioData.StationProgram;
import com.sec.connecttoapilibrary.onlinefm.updataLiveRadioUIListener.ObserverLiveRadioLiveRadioUIListenerManager;
import com.sec.connecttoapilibrary.onlinefm.xmlcheck.LocalInfo;
import com.sec.connecttoapilibrary.onlinefm.xmlcheck.Program;
import com.sec.connecttoapilibrary.onlinefm.xmlcheck.PullLocalInfoParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

    final private String KEY_LOCAL_LIST = "localList";

    final private String KEY_CENTER_LIST = "centerList";

    final private String KEY_HASH_MAP = "hash map";

    private ArrayList<HashMap<String, String>> mListInfo = null;

    private String mLocalName = null;

    private Location mLocation = null;

    private Context mContext = null;

    private List<LocalInfo> localInfos = null;

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

        mLocalListInfo = getListInfo(mContext, KEY_LOCAL_LIST);
        mCenterListInfo = getListInfo(mContext, KEY_CENTER_LIST);
        mLocalHashMap = getHashMapData(mContext, KEY_HASH_MAP);
        mProgramNetInfo = new HashMap<String, List<Program>>();
        mChannelNetInfo = new HashMap<String, HashMap<String, String>>();
    }

    public static final int TYPE_LOCAL_LIST = 1;
    public static final int TYPE_CENTER_LIST = 2;
    private List<Station> mStations;
    private Map<Integer, List<StationProgram>> map;

    private List<Station> mCenterStations;
    private Map<Integer, List<StationProgram>> centermap;

    public void makeLocationList(int list_type, List<Station> mStations, Map<Integer, List<StationProgram>> map) {

        if(list_type == TYPE_LOCAL_LIST) {
            this.mStations = mStations;
            this.map = map;
        }
        if(list_type == TYPE_CENTER_LIST) {
            this.mCenterStations = mCenterStations;
            this.centermap = centermap;
        }

        mLocation = getGPSInfo.getLocation();
        //为空重新制表
        if (mLocalListInfo.size() == 0 || mCenterListInfo.size() == 0 ||
                mLocalHashMap.size() == 0) {
            if (mLocalName != null) {
                //diaLog 刷新列表
                //   displayDialog();
                makeList(list_type, mLocalName);
            } else {
                //dialog 重新定位重新做表
                //    displayDialog();
                makeList(list_type, getGPSInfo.getLocalName(mLocation));
            }
        }
    }

    private void makeList(int list_type, String localName) {
        try {
            mLocalListInfo = new ArrayList<HashMap<String, String>>();
            mCenterListInfo = new ArrayList<HashMap<String, String>>();
            mLocalHashMap = new HashMap<String, String>();
            InputStream is = mContext.getAssets().open(localName + ".xml");
            PullLocalInfoParser parser = new PullLocalInfoParser();
            localInfos = parser.parse(is);
            //            DBManager dbmanager = new DBManager(mContext);
            //            dbmanager.insertProgram(parser.getMap(), localName);
            //            dbmanager.close();
            for (int i = 0; i < localInfos.size(); i++) {
                if(list_type == TYPE_LOCAL_LIST) {
                    for (int j = 0; j < mStations.size(); j++) {
                        if (Objects.equals(mStations.get(j).getStationTitle(), localInfos.get(i).getstationname())
                                && Objects.equals(mStations.get(j).getStationFreq(), "")) {
                            mStations.get(j).setStationFreq(MainOnLineInfoManager.convertToMhz(localInfos.get(i).getchannel()));
                        }
                    }
                }
                if(list_type == TYPE_CENTER_LIST) {
                    for (int j = 0; j < mCenterStations.size(); j++) {
                        if (Objects.equals(mCenterStations.get(j).getStationTitle(), localInfos.get(i).getstationname())) {
                            mCenterStations.get(j).setStationFreq(MainOnLineInfoManager.convertToMhz(localInfos.get(i).getchannel()));
                        }
                    }
                }
            }
            if(list_type == TYPE_LOCAL_LIST) {
                addNetInfo(mStations, map);
                ObserverLiveRadioLiveRadioUIListenerManager.getInstance().notifyObserverLocalList(mStations, map);
            }
            if(list_type == TYPE_CENTER_LIST) {
                addNetInfo(mCenterStations, centermap);
                ObserverLiveRadioLiveRadioUIListenerManager.getInstance().notifyObserverCenterList(mCenterStations, centermap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<HashMap<String, String>> getListInfo(Context context, String key) {
        ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
        SharedPreferences sp = context.getSharedPreferences("list_demo", Context.MODE_PRIVATE);
        String result = sp.getString(key, "");
        try {
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                JSONObject itemObject = array.getJSONObject(i);
                HashMap<String, String> itemMap = new HashMap<String, String>();
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

        }

        return datas;
    }


    public static HashMap<String, String> getHashMapData(Context context, String key) {

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

    /*
    * current progrem and next progrem*
     * **/
    private static String mTime = null;

    public void addNetInfo(List<Station> mStations, Map<Integer, List<StationProgram>> map) {
        final String UNKNOWN_PROGRAM = "暂无节目信息";
        MainOnLineInfoManager.getTime();
        mTime = MainOnLineInfoManager.getCurrentTime();
        for(int i = 0; i < mStations.size(); i++){
            List<StationProgram> mList = map.get(mStations.get(i).getStationId());
            String mCurrentPro = UNKNOWN_PROGRAM;
            String mNextPro = UNKNOWN_PROGRAM;
            String mNextProTime = "23:59";
            String endTimeTemp = null;
            String starTimeTemp = null;
            if(mList != null) {
                for(int j = 0; j < mList.size(); j++) {
                    starTimeTemp = mList.get(j).getProgramStart_time();
                    endTimeTemp = mList.get(j).getProgramEnd_Time();
                    if(endTimeTemp.equals("00:00"))
                        endTimeTemp = "24:00";
                    if (starTimeTemp.compareTo(mTime) <= 0 &&
                            endTimeTemp.compareTo(mTime) > 0) {
                        mCurrentPro = mList.get(j).getProgramTitle();
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
