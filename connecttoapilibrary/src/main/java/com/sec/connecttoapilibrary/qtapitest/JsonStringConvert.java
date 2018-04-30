package com.sec.connecttoapilibrary.qtapitest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.sec.connecttoapilibrary.qtapitest.liveRadioData.ClassifyConditionInfoPattern;
import com.sec.connecttoapilibrary.qtapitest.liveRadioData.PropertyInfo;
import com.sec.connecttoapilibrary.qtapitest.liveRadioData.SearchType;
import com.sec.connecttoapilibrary.qtapitest.liveRadioData.Station;
import com.sec.connecttoapilibrary.qtapitest.liveRadioData.StationProgram;
import com.sec.connecttoapilibrary.qtapitest.updataLiveRadioUIListener.ObserverLiveRadioLiveRadioUIListenerManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/17.
 */

public class JsonStringConvert {
    private String TAG = "JsonStringConvert";

    /* ***
     * token permission*
     * ***/
    public void getOAuth_Permission(String result, HttpUtil mHttpUtil) throws JSONException {
        if (result != null) {
            JSONObject mOAuth_Pre = new JSONObject(result);

            mHttpUtil.access_token = mOAuth_Pre.getString("access_token");
            mHttpUtil.token_type = mOAuth_Pre.getString("token_type");
            mHttpUtil.expires_in = mOAuth_Pre.getInt("expires_in");
            Log.d(TAG, "token : " + mHttpUtil.access_token + "\n"
                    + mHttpUtil.token_type + "\n"
                    + mHttpUtil.expires_in + "\n");
        }
    }

    /* ***
     * local or other classify *
     * ***/
    public ClassifyConditionInfoPattern getPropertyInfo(String property_info) throws JSONException {
        ClassifyConditionInfoPattern mConditionInfoPattern = null;
        if (property_info != null) {
            mConditionInfoPattern = new ClassifyConditionInfoPattern();
            List<PropertyInfo> mInfo = new ArrayList<>();
            HashMap<Integer, List<PropertyInfo.values>> mInfoMap = new HashMap<>();
            JSONObject mPropertyInfo = new JSONObject(property_info);
            JSONArray jArr = mPropertyInfo.getJSONArray("data");
            for (int i = 0; i < jArr.length(); i++) {
                JSONObject obj = jArr.getJSONObject(i);
                PropertyInfo info = new PropertyInfo();
                info.setPropertyInfoId(obj.getInt("id"));
                info.setPropertyInfoname(obj.getString("name"));
                List<PropertyInfo.values> mInfoVales = new ArrayList<>();
                JSONArray jArr_val = obj.getJSONArray("values");
                for (int j = 0; j < jArr_val.length(); j++) {
                    JSONObject obj1 = jArr_val.getJSONObject(j);
                    PropertyInfo.values values = new PropertyInfo.values();
                    values.setvaluesId(obj1.getInt("id"));
                    values.setvaluesname(obj1.getString("name"));
                    values.setvaluessequence(obj1.getString("sequence"));
                    mInfoVales.add(values);
                }
                mInfoMap.put(info.getPropertyInfoId(), mInfoVales);
                mInfo.add(info);

                mConditionInfoPattern.setInfo(mInfo);
                mConditionInfoPattern.setInfoMap(mInfoMap);
            }
        }
        return mConditionInfoPattern;
    }

    /* ***
     * local Station info *
     * ***/
    public List<Station> getAll_Station(String station_info) throws JSONException {
        List<Station> mStations = null;
        if (station_info != null) {
            mStations = new ArrayList<>();
            JSONObject mAllStation = new JSONObject(station_info);
            JSONArray jArr = mAllStation.getJSONArray("data");
            for (int i = 0; i < jArr.length(); i++) {
                JSONObject obj = jArr.getJSONObject(i);
                Station station = new Station();
                station.setStationAudience_count(obj.getInt("audience_count"));
                station.setStationCategory_id(obj.getInt("category_id"));
                station.setStationDescription(obj.getString("description"));
                station.setStationFreq(obj.getString("freq").trim());
                station.setStationId(obj.getInt("id"));
                station.setStationThumbs(getThumb(obj));
                station.setStationPlaycount(obj.getString("playcount"));
                station.setStationTitle(obj.getString("title"));
                station.setStationType(obj.getString("type"));
                station.setStationUpdate_time(obj.getString("update_time"));
                station.setStationWebUrl(obj.getString("weburl"));
                mStations.add(station);
            }
        }
        return mStations;
    }

    private String getThumb(JSONObject obj) throws JSONException {
        String mThumb_200 = "";
        try {
            mThumb_200 = obj.getJSONObject("thumbs").getString("200_thumb");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String mThumb_400 = "";
        try {
            mThumb_400 = obj.getJSONObject("thumbs").getString("400_thumb");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String mThumb_800 = "";
        try {
            mThumb_800 = obj.getJSONObject("thumbs").getString("800_thumb");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String mThumb_Large = "";
        try {
            mThumb_Large = obj.getJSONObject("thumbs").getString("large_thumb");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String mThumb_Medium = "";
        try {
            mThumb_Medium = obj.getJSONObject("thumbs").getString("medium_thumb");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String mThumb_Small = "";
        try {
            mThumb_Small = obj.getJSONObject("thumbs").getString("small_thumb");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(!Objects.equals(mThumb_200, ""))
            return mThumb_200;
        if(!Objects.equals(mThumb_400, ""))
            return mThumb_400;
        if(!Objects.equals(mThumb_Small, ""))
            return mThumb_Small;
        if(!Objects.equals(mThumb_Medium, ""))
            return mThumb_Medium;
        if(!Objects.equals(mThumb_800, ""))
            return mThumb_800;
        if(!Objects.equals(mThumb_Large, ""))
            return mThumb_Large;
        return null;
    }

    /* ***
     * Station progrem info *
     * ***/
    public List<StationProgram> getStation_Program(String program_info, int day_of_week) throws JSONException {
        List<StationProgram> mPrograms = null;
        if (program_info != null) {
            mPrograms = new ArrayList<>();
            JSONObject obj = new JSONObject(program_info);
            JSONObject obj1 = obj.getJSONObject("data");
            JSONArray jArr = obj1.getJSONArray(String.valueOf(day_of_week));
            for (int i = 0; i < jArr.length(); i++) {
                JSONObject obj2 = jArr.getJSONObject(i);
                StationProgram progrem = new StationProgram();
                progrem.setProgramId(obj2.getInt("id"));
                progrem.setProgramStart_time(obj2.getString("start_time"));
                progrem.setProgramEnd_Time(obj2.getString("end_time"));
                progrem.setProgramDuration(obj2.getInt("duration"));
                progrem.setProgramTitle(obj2.getString("title"));
                progrem.setProgramType(obj2.getString("type"));
                progrem.setProgramChannel_Id(obj2.getInt("channel_id"));
                progrem.setProgramProgram_Id(obj2.getInt("program_id"));
                JSONArray jArr_val = obj2.getJSONObject("detail").getJSONArray("broadcasters");

                if (jArr_val.length() > 0) {
                    progrem.newBroadcasters(jArr_val.length());
                    for (int j = 0; j < jArr_val.length(); j++) {
                        JSONObject obj_user = jArr_val.getJSONObject(j);
                        StationProgram.Broadcasters broadcasters = new StationProgram.Broadcasters();
                        broadcasters.setBroadcastersName(obj_user.getString("username"));
                        progrem.getBroadcaster()[j] = broadcasters;
                    }
                }

                mPrograms.add(progrem);
            }
        }
        return mPrograms;
    }

    /* *******
     * play rule info , with this function we can get the Qing ting's play resources URL
     * so that can play by this URL
     * as QingTing replay ,they have the play sdk ,so we can just set the sdk to replace this function
     * ******/
    public class Rule {
        String mAccess;// online play need
        String mResult;
        String mTest_Path;
        String[] mBitrate;
        String mReplay; // replay need
        public String mDomain;// online play need
        public String mReplay_Domain;// replay need
    }

    private Rule mRule = null;

    public Rule getReplay_URL_Rule(String mResult) throws JSONException {
        if (mRule != null)
            mRule = null;
        mRule = new Rule();
        if (mResult != null) {
            JSONObject obj = new JSONObject(mResult);
            JSONObject obj1 = obj.getJSONObject("data");
            JSONArray jArr_val = obj1.getJSONObject("radiostations_hls_https").getJSONArray("mediacenters");
            for (int j = 0; j < jArr_val.length(); j++) {
                JSONObject obj_user = jArr_val.getJSONObject(j);
                mRule.mAccess = obj_user.getString("access");
                mRule.mDomain = obj_user.getString("domain");
                mRule.mReplay = obj_user.getString("replay");
                mRule.mBitrate = new String[obj_user.getJSONArray("bitrate").length()];
                for (int i = 0; i < obj_user.getJSONArray("bitrate").length(); i++)
                    mRule.mBitrate[i] = obj_user.getJSONArray("bitrate").getString(i);
                mRule.mReplay_Domain = obj_user.getString("replaydomain");
                mRule.mTest_Path = obj_user.getString("test_path");
                mRule.mResult = obj_user.getString("result");
            }
        }
        return mRule;
    }

    /* ****************
     * get search result
     * ****************/
    private List<SearchType.ChannelLive> mSearchChannelLiveList = null;
    private List<SearchType.ProgramLive> mSearchProgramLiveList = null;

    public List<SearchType.ChannelLive> getChannelLiveList() {
        return mSearchChannelLiveList;
    }
    public List<SearchType.ProgramLive> getProgremLiveList() {
        return mSearchProgramLiveList;
    }

    private int mSelectedType;
    public void getSearchResult(String mSearch_result, String mType) throws JSONException {
        if(Objects.equals(mType, "channel_live")) {
            if(mSearchChannelLiveList != null) {
                mSearchChannelLiveList.clear();
                mSearchChannelLiveList = null;
                mSelectedType = 0;
            }
            mSearchChannelLiveList = new ArrayList<>();
        } else if(Objects.equals(mType, "program_live")) {
            if(mSearchProgramLiveList != null) {
                mSearchProgramLiveList.clear();
                mSearchProgramLiveList = null;
                mSelectedType = 1;
            }
            mSearchProgramLiveList = new ArrayList<>();
        } else if(Objects.equals(mType, "all")) {
            if(mSearchProgramLiveList != null) {
                mSearchProgramLiveList.clear();
                mSearchProgramLiveList = null;
                mSelectedType = 2;
            }
            mSearchProgramLiveList = new ArrayList<>();
        }
        if(mSearch_result != null) {
            JSONObject mAllStation = new JSONObject(mSearch_result);
            JSONArray jArr = mAllStation.getJSONArray("data");
            for(int i=0;i<jArr.length();i++) {
                JSONObject obj_user = jArr.getJSONObject(i);
                JSONArray doc_list = obj_user.getJSONObject("doclist").getJSONArray("docs");
                for(int j=0;j<doc_list.length();j++) {
                    JSONObject list_data = doc_list.getJSONObject(j);
                    switch (mSelectedType) {
                        case 0: //do Channel Live List
                            SearchType.ChannelLive mChannelLive = new SearchType.ChannelLive();
                            mChannelLive.setChannelLiveId(list_data.getInt("id"));
                            mChannelLive.setChannelLiveCategoryId(list_data.getInt("category_id"));
                            mChannelLive.setChannelLiveCategoryName(list_data.getString("category_name"));
                            mChannelLive.setChannelLiveCover(getBitmap(list_data.getString("cover")));
                            mChannelLive.setChannelLiveFreq(list_data.getString("freqs"));
                            mChannelLive.setChannelLiveKeywords(list_data.getString("keywords"));
                            mChannelLive.setChannelLiveTitle(list_data.getString("title"));
                            mChannelLive.setChannelLiveType(list_data.getString("type"));
                            mSearchChannelLiveList.add(mChannelLive);
                            break;
                        case 1: //do Progrem Live List
                            SearchType.ProgramLive mProgramLive = new SearchType.ProgramLive();
                            mProgramLive.setProgramLiveId(list_data.getInt("id"));
                            mProgramLive.setProgramLiveCategoryId(list_data.getInt("category_id"));
                            mProgramLive.setProgramLiveCategoryName(list_data.getString("category_name"));
                            mProgramLive.setProgramLiveCover(getBitmap(list_data.getString("cover")));
                            mProgramLive.setProgramLiveParentId(list_data.getInt("parent_id"));
                            mProgramLive.setProgramLiveParentType(list_data.getString("parent_type"));
                            mProgramLive.setProgramLiveTitle(list_data.getString("title"));
                            mProgramLive.setProgramLiveType(list_data.getString("type"));
                            mSearchProgramLiveList.add(mProgramLive);
                            break;
                        default:
                            break;
                    }
                }
            }
            //json finish,update UI
            switch (mSelectedType) {
                case 0:
                    ObserverLiveRadioLiveRadioUIListenerManager.getInstance().notifyChannelLiveObserver(mSearchChannelLiveList);
                    break;
                case 1:
                    ObserverLiveRadioLiveRadioUIListenerManager.getInstance().notifyProgramLiveObserver(mSearchProgramLiveList);
                    break;
                default:
                    break;
            }
        }
    }

    private Bitmap getBitmap(String url) {
        Bitmap bm = null;
        try {
            URL iconUrl = new URL(url);
            URLConnection conn = iconUrl.openConnection();
            HttpURLConnection http = (HttpURLConnection) conn;
            int length = http.getContentLength();
            conn.connect();
            // 获得图像的字符流
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is, length);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();// 关闭流
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }

}
