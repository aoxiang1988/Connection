package com.sec.myonlinefm;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.sec.myonlinefm.classificationprogram.data.ChannelProgram;
import com.sec.myonlinefm.classificationprogram.data.ChannelProgramPattern;
import com.sec.myonlinefm.classificationprogram.data.ClassifyRecommend;
import com.sec.myonlinefm.classificationprogram.data.DemandChannel;
import com.sec.myonlinefm.classificationprogram.data.RecommendsData;
import com.sec.myonlinefm.classificationprogram.data.RecommendsDataPattern;
import com.sec.myonlinefm.classificationprogram.data.RequestProgramClassify;
import com.sec.myonlinefm.classificationprogram.data.WaPiData;
import com.sec.myonlinefm.classificationprogram.data.WaPiDataPattern;
import com.sec.myonlinefm.data.ClassificationAttributePattern;
import com.sec.myonlinefm.data.PropertyInfo;
import com.sec.myonlinefm.data.SearchType;
import com.sec.myonlinefm.data.Station;
import com.sec.myonlinefm.data.StationProgram;
import com.sec.myonlinefm.updataUIListener.ObserverUIListenerManager;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/17.
 */

public class JsonStringConvert {
    private String TAG = "JsonStringConvert";
    /**
     * token permission
     * @param result string
     * @param mHttpUtil HttpUtil
     */
    public void getOAuthPermission(String result, HttpUtil mHttpUtil) throws JSONException {
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

    /**
     * local or other classify
     * @param category_id int
     * @param property_info String
     */

    public ClassificationAttributePattern getPropertyInfo(String property_info, int category_id) throws JSONException {
        ClassificationAttributePattern mPattern = null;
        List<PropertyInfo> mInfo;
        HashMap<Integer, List<PropertyInfo.values>> mInfoMap;
        if (property_info != null) {
            mInfo = new ArrayList<>();
            mInfoMap = new LinkedHashMap<>();
            JSONObject mPropertyInfo = new JSONObject(property_info);
            JSONArray jArr = mPropertyInfo.getJSONArray("data");
            for (int i = 0; i < jArr.length(); i++) {
                JSONObject obj = jArr.getJSONObject(i);
                PropertyInfo info = new PropertyInfo();
                info.setPropertyInfoId(obj.getInt("id"));
                info.setPropertyInfoname(obj.getString("name"));
                List<PropertyInfo.values> mInfoValus = new ArrayList<>();
                JSONArray jArr_val = obj.getJSONArray("values");
                if(category_id != 5 && i == 0) {
                    PropertyInfo.values values = new PropertyInfo.values();
                    values.setvaluesId(0);
                    values.setvaluesname(info.getPropertyInfoname());
                    values.setvaluessequence(null);
                    mInfoValus.add(values);
                }
                for (int j = 0; j < jArr_val.length(); j++) {
                    JSONObject obj1 = jArr_val.getJSONObject(j);
                    PropertyInfo.values values = new PropertyInfo.values();
                    values.setvaluesId(obj1.getInt("id"));
                    values.setvaluesname(obj1.getString("name"));
                    values.setvaluessequence(obj1.getString("sequence"));
                    mInfoValus.add(values);
                }
                mInfo.add(info);
                mInfoMap.put(info.getPropertyInfoId(), mInfoValus);
            }
            mPattern = new ClassificationAttributePattern();
            mPattern.setInfo(mInfo);
            mPattern.setInfoMap(mInfoMap);
        }
        return mPattern;
    }

    /**
     * local Station info
     * @param station_info String
     */
    public List<Station> getAll_Station(String station_info) throws JSONException {
        List<Station> mStations = new ArrayList<>();
        if (station_info != null) {
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
                station.setStationPlayCount(obj.getString("playcount"));
                station.setStationTitle(obj.getString("title"));
                station.setStationType(obj.getString("type"));
                station.setStationUpdate_time(obj.getString("update_time"));
                station.setStationWeBoUrl(obj.getString("weburl"));
                mStations.add(station);
            }
        }
        return mStations;
    }
    /**
     * get thumb url
     * @param obj JSONObject
     */
    private String getThumb(JSONObject obj) {
        String mThumb_200;
        try {
            mThumb_200 = obj.getJSONObject("thumbs").getString("200_thumb");
        } catch (JSONException e) {
            e.printStackTrace();
            mThumb_200 = null;
        }
        String mThumb_400;
        try {
            mThumb_400 = obj.getJSONObject("thumbs").getString("400_thumb");
        } catch (JSONException e) {
            e.printStackTrace();
            mThumb_400 = null;
        }
        String mThumb_800;
        try {
            mThumb_800 = obj.getJSONObject("thumbs").getString("800_thumb");
        } catch (JSONException e) {
            e.printStackTrace();
            mThumb_800 = null;
        }
        String mThumb_Large;
        try {
            mThumb_Large = obj.getJSONObject("thumbs").getString("large_thumb");
        } catch (JSONException e) {
            e.printStackTrace();
            mThumb_Large = null;
        }
        String mThumb_Medium;
        try {
            mThumb_Medium = obj.getJSONObject("thumbs").getString("medium_thumb");
        } catch (JSONException e) {
            e.printStackTrace();
            mThumb_Medium = null;
        }
        String mThumb_Small = "";
        try {
            mThumb_Small = obj.getJSONObject("thumbs").getString("small_thumb");
        } catch (JSONException e) {
            e.printStackTrace();
            mThumb_Medium = null;
        }
        if(mThumb_200 != null && !Objects.equals(mThumb_200, ""))
            return mThumb_200;
        if(mThumb_400 != null && !Objects.equals(mThumb_400, ""))
            return mThumb_400;
        if(mThumb_Small != null && !Objects.equals(mThumb_Small, ""))
            return mThumb_Small;
        if(mThumb_Medium != null && !Objects.equals(mThumb_Medium, ""))
            return mThumb_Medium;
        if(mThumb_800 != null && !Objects.equals(mThumb_800, ""))
            return mThumb_800;
        if(mThumb_Large != null && !Objects.equals(mThumb_Large, ""))
            return mThumb_Large;
        return null;
    }

    /**
     * Station program info
     * @param day_of_week int
     * @param program_info String
     */
    public List<StationProgram> getStationProgram(String program_info, int day_of_week) throws JSONException {
        List<StationProgram> mPrograms = new ArrayList<>();
        if (program_info != null) {
            JSONObject obj = new JSONObject(program_info);
            JSONObject obj1 = obj.getJSONObject("data");
            JSONArray jArr = obj1.getJSONArray(String.valueOf(day_of_week));
            for (int i = 0; i < jArr.length(); i++) {
                JSONObject obj2 = jArr.getJSONObject(i);
                StationProgram program = new StationProgram();
                program.setProgramId(obj2.getInt("id"));
                program.setProgramStart_time(obj2.getString("start_time"));
                program.setProgramEnd_Time(obj2.getString("end_time"));
                program.setProgramDuration(obj2.getInt("duration"));
                program.setProgramTitle(obj2.getString("title"));
                program.setProgramType(obj2.getString("type"));
                program.setProgramChannel_Id(obj2.getInt("channel_id"));
                program.setProgramSelf_Id(obj2.getInt("program_id"));
                JSONArray jArr_val = obj2.getJSONObject("detail").getJSONArray("broadcasters");

                if (jArr_val.length() > 0) {
                    program.newBroadcasters(jArr_val.length());
                    for (int j = 0; j < jArr_val.length(); j++) {
                        JSONObject obj_user = jArr_val.getJSONObject(j);
                        StationProgram.Broadcasters broadcasters = new StationProgram.Broadcasters();
                        broadcasters.setBroadcastersName(obj_user.getString("username"));
                        program.getBroadcaster()[j] = broadcasters;
                    }
                }

                mPrograms.add(program);
            }
        }
        return mPrograms;
    }

    /**
     * local or other classify
     * @param request_program_result String
     */
    public List<RequestProgramClassify> getRequestProgramClassifiesList(String request_program_result) throws JSONException {
        List<RequestProgramClassify> requestProgramClassifiesList = null;
        if (request_program_result != null) {
            requestProgramClassifiesList = new ArrayList<>();
            JSONObject mRequestProgramListInfo = new JSONObject(request_program_result);
            JSONArray jArr = mRequestProgramListInfo.getJSONArray("data");
            for (int i = 0; i < jArr.length(); i++) {
                JSONObject obj = jArr.getJSONObject(i);
                RequestProgramClassify info = new RequestProgramClassify();
                info.setId(obj.getInt("id"));
                info.setName(obj.getString("name"));
                info.setSectionId(obj.getInt("section_id"));
                requestProgramClassifiesList.add(info);
            }
        }
        return requestProgramClassifiesList;
    }

    // add by gaolin 4/20
    /**
     * recommend header
     * @param recommend_program_item String
     * @param section_id int
     */
    public ClassifyRecommend getClassifyRecommendheader(String recommend_program_item, int section_id) throws JSONException {
        ClassifyRecommend item = null;
        if(recommend_program_item != null) {
            item = new ClassifyRecommend();
            item.setCategoryID(section_id);
            JSONObject mRecommedProgramItem = new JSONObject(recommend_program_item);
            JSONArray array = mRecommedProgramItem.getJSONArray("data");
            int cnt = 0;
            for (int i = 0; i < array.length(); i++) {
                JSONObject objData = array.getJSONObject(i);
                JSONArray dataArr = objData.getJSONArray("recommends");
                for (int j = 0; j < dataArr.length(); j++) {
                    JSONObject itemObj = dataArr.getJSONObject(j);
                    item.setId(itemObj.getInt("id"));
                    item.setTitle(itemObj.getString("title"));
                    String thumbUrl = getThumb(itemObj);
                    Log.d(TAG, "gaolin cnt : " + cnt + " thumbUrl : " + thumbUrl);
                    assert thumbUrl != null;
                    item.setThumbUrl(thumbUrl);
                    cnt++;
                    if (cnt == 1) {
                        break;
                    }
                }
                if (cnt == 1) {
                    break;
                }
            }
        }
        return item;
    }

    /**
     * recommend program
     * @param recommend_program_item String
     * @param category_id int
     */
    public ClassifyRecommend getClassifyRecommendItem(String recommend_program_item, int category_id) throws JSONException {
        ClassifyRecommend item = null;
        if(recommend_program_item != null) {
            item = new ClassifyRecommend();
            item.setCategoryID(category_id);
            JSONObject mRecommedProgramItem = new JSONObject(recommend_program_item);
            if (mRecommedProgramItem.isNull("data")) {
                JSONArray array = mRecommedProgramItem.getJSONArray("data");
                int size = 3;
                if(array.length() < 3) size = array.length();
                for (int i = 0; i < size; i++) {
                    JSONObject objData = array.getJSONObject(i);
                    item.setId(objData.getInt("id"));
                    item.setTitle(objData.getString("title"));
                    String thumbUrl = getThumb(objData);
                    assert thumbUrl != null;
                    item.setThumbUrl(thumbUrl);
                }
            }
        }
        return item;
    }

    /**
     * recommend thumb
     * @param url String
     */
    public Bitmap getThumb(String url) {
        return OnLineFMConnectManager.Companion.getMMainInfoCode().getBitmap(url, 128, 128);
    }
    //end

    public List<DemandChannel> getDemandChannelContextList(String demand_channel_context_result) throws JSONException {
        List<DemandChannel> demandChannelContextList = null;
        if (demand_channel_context_result != null) {
            demandChannelContextList = new ArrayList<>();
            JSONObject mAllStation = new JSONObject(demand_channel_context_result);
            JSONArray jArr = mAllStation.getJSONArray("data");
            for (int i = 0; i < jArr.length(); i++) {
                JSONObject obj = jArr.getJSONObject(i);
                DemandChannel demand_channel = new DemandChannel();
                demand_channel.setCategoryId(obj.getInt("category_id"));
                demand_channel.setDescription(obj.getString("description"));
                demand_channel.setId(obj.getInt("id"));
                demand_channel.setThumbs(getThumb(obj));
                demand_channel.setProgramCount(obj.getInt("program_count"));
                demand_channel.setPlayCount(obj.getString("playcount"));
                demand_channel.setTitle(obj.getString("title"));
                demand_channel.setType(obj.getString("type"));
                demand_channel.setUpdateTime(obj.getString("update_time"));
                demand_channel.setSaleType(obj.getInt("sale_type"));
                demandChannelContextList.add(demand_channel);
            }
            if(!demandChannelContextList.isEmpty())
                demandChannelContextList.get(0).setTotal(mAllStation.getInt("total"));
        }
        return demandChannelContextList;
    }

    /**
     * get the current demand channel info which has been opened
     * @param current_demand_channel_result String
     */

    public DemandChannel getCurrentDemandChannelInfo(String current_demand_channel_result) throws JSONException {
        DemandChannel currentDemandChannel = null;
        if (current_demand_channel_result != null) {
            currentDemandChannel = new DemandChannel();
            JSONObject current_channel_json = new JSONObject(current_demand_channel_result);
            JSONObject data = current_channel_json.getJSONObject("data");
            currentDemandChannel.setCategoryId(data.getInt("category_id"));
            currentDemandChannel.setDescription(data.getString("description"));
            currentDemandChannel.setId(data.getInt("id"));
            currentDemandChannel.setIsFinished(data.getInt("is_finished"));
            currentDemandChannel.setLatestProgram(data.getString("latest_program"));
            currentDemandChannel.setOrdered(data.getInt("ordered"));
            currentDemandChannel.setSaleType(data.getInt("sale_type"));
            currentDemandChannel.setTags(data.getString("tags"));
            currentDemandChannel.setThumbs(getThumb(data));
            currentDemandChannel.setThumbsUrl(getThumb(data));
            currentDemandChannel.setTitle(data.getString("title"));
            currentDemandChannel.setType(data.getString("type"));
            currentDemandChannel.setUpdateTime(data.getString("update_time"));
            if(!data.isNull("detail")) {
                JSONObject detail_json = data.getJSONObject("detail");
                currentDemandChannel.setDetail();
                currentDemandChannel.getDetail().setFavCount(detail_json.getString("favcount"));
                currentDemandChannel.getDetail().setPlayCount(detail_json.getString("playcount"));
                currentDemandChannel.getDetail().setProgramCount(detail_json.getInt("program_count"));
                if(!detail_json.isNull("authors")) {
                    currentDemandChannel.getDetail().setAuthors();
                    JSONArray jArr = detail_json.getJSONArray("authors");
                    for(int i = 0; i < jArr.length(); i++) {
                        JSONObject obj = jArr.getJSONObject(i);
                        DemandChannel.AuthorsBroadcasters author = new DemandChannel.AuthorsBroadcasters();
                        author.setId(obj.getInt("id"));
                        author.setQQId(obj.getString("qq_id"));
                        author.setQQName(obj.getString("qq_name"));
//                        author.setThumb(getBitmap(getThumb(current_channel_json)));
                        author.setUserName(obj.getString("username"));
                        author.setWeiboId(obj.getString("weibo_id"));
                        author.setWeiboName(obj.getString("weibo_name"));
                        currentDemandChannel.getDetail().getAuthors().add(author);
                    }
                }
                if(!detail_json.isNull("podcasters")){
                    currentDemandChannel.getDetail().setPodCasters();
                    JSONArray jArr = detail_json.getJSONArray("podcasters");
                    for(int i = 0; i < jArr.length(); i++) {
                        JSONObject obj = jArr.getJSONObject(i);
                        DemandChannel.PodCasters podCaster = new DemandChannel.PodCasters();
                        podCaster.setNickName(obj.getString("nickname"));
                        podCaster.setDesc(obj.getString("description"));
                        podCaster.setImgUrl(obj.getString("avatar"));
                        podCaster.setId(obj.getInt("id"));
                        podCaster.setFanNum(obj.getInt("fan_num"));
                        currentDemandChannel.getDetail().getPodCasters().add(podCaster);
                    }
                }
                if(!detail_json.isNull("broadcasters")) {
                    currentDemandChannel.getDetail().setBroadcasters();
                    JSONArray jArr = detail_json.getJSONArray("broadcasters");
                    for(int i = 0; i < jArr.length(); i++) {
                        JSONObject obj = jArr.getJSONObject(i);
                        DemandChannel.AuthorsBroadcasters broadcaster = new DemandChannel.AuthorsBroadcasters();
                        broadcaster.setId(obj.getInt("id"));
                        broadcaster.setQQId(obj.getString("qq_id"));
                        broadcaster.setQQName(obj.getString("qq_name"));
//                        broadcaster.setThumb(getBitmap(getThumb(current_channel_json)));
                        broadcaster.setUserName(obj.getString("username"));
                        broadcaster.setWeiboId(obj.getString("weibo_id"));
                        broadcaster.setWeiboName(obj.getString("weibo_name"));
                        currentDemandChannel.getDetail().getBroadcasters().add(broadcaster);
                    }
                }
            }
        }
        return currentDemandChannel;
    }

    public ChannelProgramPattern getCurrentDemandChannelPrograms(String mCurrentDemandChannelProgramsResult) throws JSONException {
        ChannelProgramPattern channelProgramPattern = null;
        if(mCurrentDemandChannelProgramsResult != null) {
            channelProgramPattern = new ChannelProgramPattern();
            JSONObject programs_json = new JSONObject(mCurrentDemandChannelProgramsResult);
            channelProgramPattern.setTotal(programs_json.getInt("total"));
            JSONArray jArr = programs_json.getJSONArray("data");
            List<ChannelProgram> channelProgramList = new ArrayList<>();
            for (int i = 0; i < jArr.length(); i++) {
                ChannelProgram channelProgram = new ChannelProgram();
                JSONObject obj = jArr.getJSONObject(i);
                channelProgram.setDescription(obj.getString("description"));
//                channelProgram.setChannelID(obj.getString("channel_id"));
                channelProgram.setDuration(obj.getLong("duration"));
                channelProgram.setId(obj.getInt("id"));
                channelProgram.setTitle(obj.getString("title"));
                channelProgram.setType(obj.getString("type"));
                channelProgram.setUpdateTime(obj.getString("update_time"));
                channelProgram.setMediaInfo();
                JSONObject media_obj = new JSONObject(obj.getString("mediainfo"));
                ChannelProgram.ProgramMediaInfo mediaInfo = channelProgram.getMediaInfo();
                assert mediaInfo != null;
                mediaInfo.setId(media_obj.getInt("id"));
                mediaInfo.setDuration(media_obj.getLong("duration"));
                mediaInfo.setBitrateUrl();
                JSONArray media_jArr = media_obj.getJSONArray("bitrates_url");
                for (int j = 0; j < media_jArr.length(); j++) {
                    JSONObject media_jArr_obj = media_jArr.getJSONObject(j);
                    ChannelProgram.ProgramMediaInfo.BitrateUrl bitrateUrl = new ChannelProgram.ProgramMediaInfo.BitrateUrl();
                    bitrateUrl.setBitrate(media_jArr_obj.getInt("bitrate"));
                    bitrateUrl.setFilePath(media_jArr_obj.getString("file_path"));
                    bitrateUrl.setQetag(media_jArr_obj.getString("qetag"));
                    Objects.requireNonNull(mediaInfo.getBitrateUrlList()).add(bitrateUrl);
                }
                channelProgramList.add(channelProgram);
            }
            channelProgramPattern.setChannelProgramList(channelProgramList);
        }
        return channelProgramPattern;
    }

    public RecommendsDataPattern getRecommendsData(String mRecommendResult) throws JSONException {
        RecommendsDataPattern recommendsDataPattern = null;
        if(mRecommendResult != null) {
            recommendsDataPattern = new RecommendsDataPattern();
            List<RecommendsData> recommendsDataList = new ArrayList<>();
            JSONObject programs_json = new JSONObject(mRecommendResult);
            JSONArray jArr = programs_json.getJSONArray("data");
            for (int i = 0; i < jArr.length(); i++) {
                RecommendsData recommendsData = new RecommendsData();
                JSONObject obj = jArr.getJSONObject(i);
                recommendsData.setBriefName(obj.getString("brief_name"));
                recommendsData.setName(obj.getString("name"));
                JSONArray recommends_jArr = obj.getJSONArray("recommends");
                recommendsData.setRecommendsList();
                for (int j = 0; j < recommends_jArr.length(); j++) {
                    RecommendsData.Recommends recommend = new RecommendsData.Recommends();
                    JSONObject obj_r = recommends_jArr.getJSONObject(j);
                    recommend.setId(obj_r.getInt("id"));
                    recommend.setObjectID(obj_r.getInt("object_id"));
                    recommend.setSubTitle(obj_r.getString("sub_title"));
                    recommend.setThumb(obj_r.getString("thumb"));
                    recommend.setThumbs(getThumb(obj_r));
                    recommend.setTitle(obj_r.getString("title"));
                    recommend.setUpdateTime(obj_r.getString("update_time"));
                    if(!obj_r.isNull("detail")) {
                        JSONObject detail_obj = obj_r.getJSONObject("detail");
                        recommend.setDetail();
                        RecommendsData.Recommends.Detail detail = recommend.getDetail();

                        if(!detail_obj.isNull("channel_star"))
                            detail.setChannelStar(detail_obj.getInt("channel_star"));

                        if(!detail_obj.isNull("chatgroup_id"))
                            detail.setChatGroupID(detail_obj.getInt("chatgroup_id"));

                        if(!detail_obj.isNull("description"))
                            detail.setDescription(detail_obj.getString("description"));

                        if(!detail_obj.isNull("duration"))
                            detail.setDuration(detail_obj.getInt("duration"));

                        if(!detail_obj.isNull("id"))
                            detail.setId(detail_obj.getInt("id"));

                        if(!detail_obj.isNull("original_fee"))
                            detail.setOriginalFee(detail_obj.getInt("original_fee"));

                        if(!detail_obj.isNull("price"))
                            detail.setPrice(detail_obj.getInt("price"));

                        if(!detail_obj.isNull("redirect_url"))
                            detail.setRedirectUrl(detail_obj.getString("redirect_url"));

                        if(!detail_obj.isNull("sale_status"))
                            detail.setSaleStatus(detail_obj.getString("sale_status"));

                        if(!detail_obj.isNull("sequence"))
                            detail.setSequence(detail_obj.getInt("sequence"));

                        if(!detail_obj.isNull("title"))
                            detail.setTitle(detail_obj.getString("title"));

                        if(!detail_obj.isNull("type"))
                            detail.setType(detail_obj.getString("type"));

                        if(!detail_obj.isNull("update_time"))
                            detail.setUpdateTime(detail_obj.getString("update_time"));

                    }
                    if(!obj_r.isNull("parent_info")) {
                        JSONObject parent_obj = obj_r.getJSONObject("parent_info");
                        recommend.setParentInfo();
                        RecommendsData.Recommends.ParentInfo parentInfo = recommend.getParentInfo();
                        if(!parent_obj.isNull("parent_extra")) {
                            parentInfo.setParentExtra();
                            parentInfo.getParentExtra().setCategoryID(
                                    parent_obj.getJSONObject("parent_extra").getInt("category_id")
                            );
                            parentInfo.getParentExtra().setTag(
                                    parent_obj.getJSONObject("parent_extra").getString("tag")
                            );
                        }
                        parentInfo.setParentID(parent_obj.getInt("parent_id"));
                        parentInfo.setParentName(parent_obj.getString("parent_name"));
                    }
                    recommendsData.getRecommendsList().add(recommend);
                }
                recommendsDataList.add(recommendsData);
            }
            recommendsDataPattern.setRecommendsDataList(recommendsDataList);
        }
        return recommendsDataPattern;
    }

    public WaPiDataPattern getWapiData(String mWapiDataResult) throws JSONException {
        WaPiDataPattern waPiDataPattern = null;
        if(mWapiDataResult != null) {
            waPiDataPattern = new WaPiDataPattern();
            List<WaPiData> waPiDataList = new ArrayList<>();
            JSONObject programs_json = new JSONObject(mWapiDataResult);
            JSONArray jArr = programs_json.getJSONArray("data");
            for(int i = 0; i < jArr.length(); i++) {
                WaPiData data = new WaPiData();
                JSONObject obj = jArr.getJSONObject(i);
                data.setCategoryID(obj.getInt("category_id"));
                data.setCover(obj.getString("cover"));
                data.setId(obj.getInt("id"));
                data.setRank(obj.getInt("rank"));
                data.setDesc(obj.getString("desc"));
                data.setName(obj.getString("name"));
                data.setUpdateTime(obj.getString("update_time"));
                waPiDataList.add(data);
            }
            waPiDataPattern.setWaPiDataList(waPiDataList);
        }
        return waPiDataPattern;
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

    /**
     * get regroup http url
     * @param mResult String
     */
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

    /**
     * get search result
     * @param mSearch_result String
     * @param mType String
     */

    public void getSearchResult(String mSearch_result, String mType) throws JSONException {
        
        List<SearchType.ChannelLive> mSearchChannelLiveList = null;
        List<SearchType.ProgramLive> mSearchProgramLiveList = null;
        int mSelectedType = 0;
        
        if(Objects.equals(mType, "channel_live")) {
            mSelectedType = 0;
            mSearchChannelLiveList = new ArrayList<>();
        } else if(Objects.equals(mType, "program_live")) {
            mSelectedType = 1;
            mSearchProgramLiveList = new ArrayList<>();
        } else if(Objects.equals(mType, "all")) {
            mSelectedType = 2;
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
                            mChannelLive.setChannelLiveCover(list_data.getString("cover"));
                            mChannelLive.setChannelLiveFreqs(list_data.getString("freqs"));
                            mChannelLive.setChannelLiveKeywords(list_data.getString("keywords"));
                            mChannelLive.setChannelLiveTitle(list_data.getString("title"));
                            mChannelLive.setChannelLiveType(list_data.getString("type"));
                            assert mSearchChannelLiveList != null;
                            mSearchChannelLiveList.add(mChannelLive);
                            break;
                        case 1: //do Program Live List
                            SearchType.ProgramLive mProgramLive = new SearchType.ProgramLive();
                            mProgramLive.setProgramLiveId(list_data.getInt("id"));
                            mProgramLive.setProgramLiveCategoryId(list_data.getInt("category_id"));
                            mProgramLive.setProgramLiveCategoryName(list_data.getString("category_name"));
                            mProgramLive.setProgramLiveCover(list_data.getString("cover"));
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
                    ObserverUIListenerManager.getInstance().notifyChannelLiveObserver(mSearchChannelLiveList);
                    break;
                case 1:
                    ObserverUIListenerManager.getInstance().notifyProgramLiveObserver(mSearchProgramLiveList);
                    break;
                default:
                    break;
            }
        }
    }
}
