package com.sec.myonlinefm

import android.util.Log
import com.sec.myonlinefm.data.*
import java.util.*
import android.graphics.Bitmap
import org.json.JSONObject
import org.json.JSONException

import com.sec.myonlinefm.classificationprogram.data.DemandChannel.PodCasters
import com.sec.myonlinefm.classificationprogram.data.DemandChannel.AuthorsBroadcasters

import com.sec.myonlinefm.classificationprogram.data.WaPiData
import com.sec.myonlinefm.classificationprogram.data.DemandChannel

import com.sec.myonlinefm.classificationprogram.data.ClassifyRecommend
import com.sec.myonlinefm.classificationprogram.data.RequestProgramClassify
import com.sec.myonlinefm.classificationprogram.data.WaPiDataPattern
import com.sec.myonlinefm.classificationprogram.data.ChannelProgram
import com.sec.myonlinefm.classificationprogram.data.ChannelProgramPattern
import com.sec.myonlinefm.classificationprogram.data.ChannelProgram.ProgramMediaInfo.BitrateUrl
import com.sec.myonlinefm.classificationprogram.data.RecommendsDataPattern
import com.sec.myonlinefm.classificationprogram.dataimport.RecommendsData
import com.sec.myonlinefm.data.PropertyInfo
import com.sec.myonlinefm.data.SearchType
import com.sec.myonlinefm.data.StationProgram
import com.sec.myonlinefm.updataUIListener.ObserverUIListenerManager


/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/17.
 */
class JsonStringConvert {
    private val TAG: String = "JsonStringConvert"

    /**
     * token permission
     * @param result string
     * @param mHttpUtil HttpUtil
     */
    @Throws(JSONException::class)
    fun getOAuthPermission(result: String?, mHttpUtil: HttpUtil?) {
        if (result != null) {
            val mOAuth_Pre = JSONObject(result)
            mHttpUtil!!.access_token = mOAuth_Pre.getString("access_token")
            mHttpUtil.token_type = mOAuth_Pre.getString("token_type")
            mHttpUtil.expires_in = mOAuth_Pre.getInt("expires_in")
            Log.d(TAG, """
     token : ${mHttpUtil.access_token}
     ${mHttpUtil.token_type}
     ${mHttpUtil.expires_in}
     
     """.trimIndent())
        }
    }

    /**
     * local or other classify
     * @param category_id int
     * @param property_info String
     */
    @Throws(JSONException::class)
    fun getPropertyInfo(property_info: String?, category_id: Int): ClassificationAttributePattern? {
        var mPattern: ClassificationAttributePattern? = null
        val mInfo: MutableList<PropertyInfo?>
        val mInfoMap: HashMap<Int?, MutableList<PropertyInfo.values?>?>
        if (property_info != null) {
            mInfo = ArrayList()
            mInfoMap = LinkedHashMap()
            val mPropertyInfo = JSONObject(property_info)
            val jArr = mPropertyInfo.getJSONArray("data")
            for (i in 0 until jArr.length()) {
                val obj = jArr.getJSONObject(i)
                val info = PropertyInfo()
                info.setPropertyInfoId(obj.getInt("id"))
                info.setPropertyInfoname(obj.getString("name"))
                val mInfoValus: MutableList<PropertyInfo.values?> = ArrayList()
                val jArr_val = obj.getJSONArray("values")
                if (category_id != 5 && i == 0) {
                    val values = PropertyInfo.values()
                    values.setvaluesId(0)
                    values.setvaluesname(info.getPropertyInfoname())
                    values.setvaluessequence(null)
                    mInfoValus.add(values)
                }
                for (j in 0 until jArr_val.length()) {
                    val obj1 = jArr_val.getJSONObject(j)
                    val values = PropertyInfo.values()
                    values.setvaluesId(obj1.getInt("id"))
                    values.setvaluesname(obj1.getString("name"))
                    values.setvaluessequence(obj1.getString("sequence"))
                    mInfoValus.add(values)
                }
                mInfo.add(info)
                mInfoMap[info.getPropertyInfoId()] = mInfoValus
            }
            mPattern = ClassificationAttributePattern()
            mPattern.setInfo(mInfo)
            mPattern.setInfoMap(mInfoMap)
        }
        return mPattern
    }

    /**
     * local Station info
     * @param station_info String
     */
    @Throws(JSONException::class)
    fun getAll_Station(station_info: String?): MutableList<Station?>? {
        val mStations: MutableList<Station?> = ArrayList()
        if (station_info != null) {
            val mAllStation = JSONObject(station_info)
            val jArr = mAllStation.getJSONArray("data")
            for (i in 0 until jArr.length()) {
                val obj = jArr.getJSONObject(i)
                val station = Station()
                station.setStationAudience_count(obj.getInt("audience_count"))
                station.setStationCategory_id(obj.getInt("category_id"))
                station.setStationDescription(obj.getString("description"))
                station.setStationFreq(obj.getString("freq").trim { it <= ' ' })
                station.setStationId(obj.getInt("id"))
                station.setStationThumbs(getThumb(obj))
                station.setStationPlayCount(obj.getString("playcount"))
                station.setStationTitle(obj.getString("title"))
                station.setStationType(obj.getString("type"))
                station.setStationUpdate_time(obj.getString("update_time"))
                station.setStationWeBoUrl(obj.getString("weburl"))
                mStations.add(station)
            }
        }
        return mStations
    }

    /**
     * get thumb url
     * @param obj JSONObject
     */
    private fun getThumb(obj: JSONObject?): String? {
        val mThumb_200: String?
        mThumb_200 = try {
            obj!!.getJSONObject("thumbs").getString("200_thumb")
        } catch (e: JSONException) {
            e.printStackTrace()
            null
        }
        val mThumb_400: String?
        mThumb_400 = try {
            obj!!.getJSONObject("thumbs").getString("400_thumb")
        } catch (e: JSONException) {
            e.printStackTrace()
            null
        }
        val mThumb_800: String?
        mThumb_800 = try {
            obj!!.getJSONObject("thumbs").getString("800_thumb")
        } catch (e: JSONException) {
            e.printStackTrace()
            null
        }
        val mThumb_Large: String?
        mThumb_Large = try {
            obj!!.getJSONObject("thumbs").getString("large_thumb")
        } catch (e: JSONException) {
            e.printStackTrace()
            null
        }
        var mThumb_Medium: String?
        mThumb_Medium = try {
            obj!!.getJSONObject("thumbs").getString("medium_thumb")
        } catch (e: JSONException) {
            e.printStackTrace()
            null
        }
        var mThumb_Small: String? = ""
        try {
            mThumb_Small = obj!!.getJSONObject("thumbs").getString("small_thumb")
        } catch (e: JSONException) {
            e.printStackTrace()
            mThumb_Medium = null
        }
        if (mThumb_200 != null && mThumb_200 != "") return mThumb_200
        if (mThumb_400 != null && mThumb_400 != "") return mThumb_400
        if (mThumb_Small != null && mThumb_Small != "") return mThumb_Small
        if (mThumb_Medium != null && mThumb_Medium != "") return mThumb_Medium
        if (mThumb_800 != null && mThumb_800 != "") return mThumb_800
        return if (mThumb_Large != null && mThumb_Large != "") mThumb_Large else null
    }

    /**
     * Station program info
     * @param day_of_week int
     * @param program_info String
     */
    @Throws(JSONException::class)
    fun getStationProgram(program_info: String?, day_of_week: Int): MutableList<StationProgram?>? {
        val mPrograms: MutableList<StationProgram?> = ArrayList()
        if (program_info != null) {
            val obj = JSONObject(program_info)
            val obj1 = obj.getJSONObject("data")
            val jArr = obj1.getJSONArray(day_of_week.toString())
            for (i in 0 until jArr.length()) {
                val obj2 = jArr.getJSONObject(i)
                val program = StationProgram()
                program.setProgramId(obj2.getInt("id"))
                program.setProgramStart_time(obj2.getString("start_time"))
                program.setProgramEnd_Time(obj2.getString("end_time"))
                program.setProgramDuration(obj2.getInt("duration"))
                program.setProgramTitle(obj2.getString("title"))
                program.setProgramType(obj2.getString("type"))
                program.setProgramChannel_Id(obj2.getInt("channel_id"))
                program.setProgramSelf_Id(obj2.getInt("program_id"))
                val jArr_val = obj2.getJSONObject("detail").getJSONArray("broadcasters")
                if (jArr_val.length() > 0) {
                    program.newBroadcasters(jArr_val.length())
                    for (j in 0 until jArr_val.length()) {
                        val obj_user = jArr_val.getJSONObject(j)
                        val broadcasters = StationProgram.Broadcasters()
                        broadcasters.setBroadcastersName(obj_user.getString("username"))
                        program.getBroadcaster()!![j] = broadcasters
                    }
                }
                mPrograms.add(program)
            }
        }
        return mPrograms
    }

    /**
     * local or other classify
     * @param request_program_result String
     */
    @Throws(JSONException::class)
    fun getRequestProgramClassifiesList(request_program_result: String?): MutableList<RequestProgramClassify?>? {
        var requestProgramClassifiesList: MutableList<RequestProgramClassify?>? = null
        if (request_program_result != null) {
            requestProgramClassifiesList = ArrayList()
            val mRequestProgramListInfo = JSONObject(request_program_result)
            val jArr = mRequestProgramListInfo.getJSONArray("data")
            for (i in 0 until jArr.length()) {
                val obj = jArr.getJSONObject(i)
                val info = RequestProgramClassify()
                info.setId(obj.getInt("id"))
                info.setName(obj.getString("name"))
                info.setSectionId(obj.getInt("section_id"))
                requestProgramClassifiesList.add(info)
            }
        }
        return requestProgramClassifiesList
    }
    // add by gaolin 4/20
    /**
     * recommend header
     * @param recommend_program_item String
     * @param section_id int
     */
    @Throws(JSONException::class)
    fun getClassifyRecommendheader(recommend_program_item: String?, section_id: Int): ClassifyRecommend? {
        var item: ClassifyRecommend? = null
        if (recommend_program_item != null) {
            item = ClassifyRecommend()
            item.setCategoryID(section_id)
            val mRecommedProgramItem = JSONObject(recommend_program_item)
            val array = mRecommedProgramItem.getJSONArray("data")
            var cnt = 0
            for (i in 0 until array.length()) {
                val objData = array.getJSONObject(i)
                val dataArr = objData.getJSONArray("recommends")
                for (j in 0 until dataArr.length()) {
                    val itemObj = dataArr.getJSONObject(j)
                    item.setId(itemObj.getInt("id"))
                    item.setTitle(itemObj.getString("title"))
                    val thumbUrl = getThumb(itemObj)
                    Log.d(TAG, "gaolin cnt : $cnt thumbUrl : $thumbUrl")
                    assert(thumbUrl != null)
                    item.setThumbUrl(thumbUrl!!)
                    cnt++
                    if (cnt == 1) {
                        break
                    }
                }
                if (cnt == 1) {
                    break
                }
            }
        }
        return item
    }

    /**
     * recommend program
     * @param recommend_program_item String
     * @param category_id int
     */
    @Throws(JSONException::class)
    fun getClassifyRecommendItem(recommend_program_item: String?, category_id: Int): ClassifyRecommend? {
        var item: ClassifyRecommend? = null
        if (recommend_program_item != null) {
            item = ClassifyRecommend()
            item.setCategoryID(category_id)
            val mRecommedProgramItem = JSONObject(recommend_program_item)
            if (mRecommedProgramItem.isNull("data")) {
                val array = mRecommedProgramItem.getJSONArray("data")
                var size = 3
                if (array.length() < 3) size = array.length()
                for (i in 0 until size) {
                    val objData = array.getJSONObject(i)
                    item.setId(objData.getInt("id"))
                    item.setTitle(objData.getString("title"))
                    val thumbUrl = getThumb(objData)!!
                    item.setThumbUrl(thumbUrl)
                }
            }
        }
        return item
    }

    /**
     * recommend thumb
     * @param url String
     */
    fun getThumb(url: String?): Bitmap? {
        return OnLineFMConnectManager.mMainInfoCode!!.getBitmap(url, 128, 128)
    }

    //end
    @Throws(JSONException::class)
    fun getDemandChannelContextList(demand_channel_context_result: String?): MutableList<DemandChannel?>? {
        var demandChannelContextList: MutableList<DemandChannel?>? = null
        if (demand_channel_context_result != null) {
            demandChannelContextList = ArrayList()
            val mAllStation = JSONObject(demand_channel_context_result)
            val jArr = mAllStation.getJSONArray("data")
            for (i in 0 until jArr.length()) {
                val obj = jArr.getJSONObject(i)
                val demand_channel = DemandChannel()
                demand_channel.setCategoryId(obj.getInt("category_id"))
                demand_channel.setDescription(obj.getString("description"))
                demand_channel.setId(obj.getInt("id"))
                demand_channel.setThumbs(getThumb(obj))
                demand_channel.setProgramCount(obj.getInt("program_count"))
                demand_channel.setPlayCount(obj.getString("playcount"))
                demand_channel.setTitle(obj.getString("title"))
                demand_channel.setType(obj.getString("type"))
                demand_channel.setUpdateTime(obj.getString("update_time"))
                demand_channel.setSaleType(obj.getInt("sale_type"))
                demandChannelContextList.add(demand_channel)
            }
            if (!demandChannelContextList.isEmpty()) demandChannelContextList[0]!!.setTotal(mAllStation.getInt("total"))
        }
        return demandChannelContextList
    }

    /**
     * get the current demand channel info which has been opened
     * @param current_demand_channel_result String
     */
    @Throws(JSONException::class)
    fun getCurrentDemandChannelInfo(current_demand_channel_result: String?): DemandChannel? {
        var currentDemandChannel: DemandChannel? = null
        if (current_demand_channel_result != null) {
            currentDemandChannel = DemandChannel()
            val current_channel_json = JSONObject(current_demand_channel_result)
            val data = current_channel_json.getJSONObject("data")
            currentDemandChannel.setCategoryId(data.getInt("category_id"))
            currentDemandChannel.setDescription(data.getString("description"))
            currentDemandChannel.setId(data.getInt("id"))
            currentDemandChannel.setIsFinished(data.getInt("is_finished"))
            currentDemandChannel.setLatestProgram(data.getString("latest_program"))
            currentDemandChannel.setOrdered(data.getInt("ordered"))
            currentDemandChannel.setSaleType(data.getInt("sale_type"))
            currentDemandChannel.setTags(data.getString("tags"))
            currentDemandChannel.setThumbs(getThumb(data))
            currentDemandChannel.setThumbsUrl(getThumb(data))
            currentDemandChannel.setTitle(data.getString("title"))
            currentDemandChannel.setType(data.getString("type"))
            currentDemandChannel.setUpdateTime(data.getString("update_time"))
            if (!data.isNull("detail")) {
                val detail_json = data.getJSONObject("detail")
                currentDemandChannel.setDetail()
                currentDemandChannel.detail!!.setFavCount(detail_json.getString("favcount"))
                currentDemandChannel.detail!!.setPlayCount(detail_json.getString("playcount"))
                currentDemandChannel.detail!!.setProgramCount(detail_json.getInt("program_count"))
                if (!detail_json.isNull("authors")) {
                    currentDemandChannel.detail!!.setAuthors()
                    val jArr = detail_json.getJSONArray("authors")
                    for (i in 0 until jArr.length()) {
                        val obj = jArr.getJSONObject(i)
                        val author = AuthorsBroadcasters()
                        author.setId(obj.getInt("id"))
                        author.setQQId(obj.getString("qq_id"))
                        author.setQQName(obj.getString("qq_name"))
                        //                        author.setThumb(getBitmap(getThumb(current_channel_json)));
                        author.setUserName(obj.getString("username"))
                        author.setWeiboId(obj.getString("weibo_id"))
                        author.setWeiboName(obj.getString("weibo_name"))
                        currentDemandChannel.detail!!.authors!!.add(author)
                    }
                }
                if (!detail_json.isNull("podcasters")) {
                    currentDemandChannel.detail!!.setPodCasters()
                    val jArr = detail_json.getJSONArray("podcasters")
                    for (i in 0 until jArr.length()) {
                        val obj = jArr.getJSONObject(i)
                        val podCaster = PodCasters()
                        podCaster.setNickName(obj.getString("nickname"))
                        podCaster.setDesc(obj.getString("description"))
                        podCaster.setImgUrl(obj.getString("avatar"))
                        podCaster.setId(obj.getInt("id"))
                        podCaster.setFanNum(obj.getInt("fan_num"))
                        currentDemandChannel.detail!!.podCasters!!.add(podCaster)
                    }
                }
                if (!detail_json.isNull("broadcasters")) {
                    currentDemandChannel.detail!!.setBroadcasters()
                    val jArr = detail_json.getJSONArray("broadcasters")
                    for (i in 0 until jArr.length()) {
                        val obj = jArr.getJSONObject(i)
                        val broadcaster = AuthorsBroadcasters()
                        broadcaster.setId(obj.getInt("id"))
                        broadcaster.setQQId(obj.getString("qq_id"))
                        broadcaster.setQQName(obj.getString("qq_name"))
                        //                        broadcaster.setThumb(getBitmap(getThumb(current_channel_json)));
                        broadcaster.setUserName(obj.getString("username"))
                        broadcaster.setWeiboId(obj.getString("weibo_id"))
                        broadcaster.setWeiboName(obj.getString("weibo_name"))
                        currentDemandChannel.detail!!.broadcasters!!.add(broadcaster)
                    }
                }
            }
        }
        return currentDemandChannel
    }

    @Throws(JSONException::class)
    fun getCurrentDemandChannelPrograms(mCurrentDemandChannelProgramsResult: String?): ChannelProgramPattern? {
        var channelProgramPattern: ChannelProgramPattern? = null
        if (mCurrentDemandChannelProgramsResult != null) {
            channelProgramPattern = ChannelProgramPattern()
            val programs_json = JSONObject(mCurrentDemandChannelProgramsResult)
            channelProgramPattern.setTotal(programs_json.getInt("total"))
            val jArr = programs_json.getJSONArray("data")
            val channelProgramList: MutableList<ChannelProgram?> = ArrayList()
            for (i in 0 until jArr.length()) {
                val channelProgram = ChannelProgram()
                val obj = jArr.getJSONObject(i)
                channelProgram.setDescription(obj.getString("description"))
                //                channelProgram.setChannelID(obj.getString("channel_id"));
                channelProgram.setDuration(obj.getLong("duration").toFloat())
                channelProgram.setId(obj.getInt("id"))
                channelProgram.setTitle(obj.getString("title"))
                channelProgram.setType(obj.getString("type"))
                channelProgram.setUpdateTime(obj.getString("update_time"))
                channelProgram.setMediaInfo()
                val media_obj = JSONObject(obj.getString("mediainfo"))
                val mediaInfo = channelProgram.getMediaInfo()!!
                mediaInfo.setId(media_obj.getInt("id"))
                mediaInfo.setDuration(media_obj.getLong("duration").toFloat())
                mediaInfo.setBitrateUrl()
                val media_jArr = media_obj.getJSONArray("bitrates_url")
                for (j in 0 until media_jArr.length()) {
                    val media_jArr_obj = media_jArr.getJSONObject(j)
                    val bitrateUrl = BitrateUrl()
                    bitrateUrl.setBitrate(media_jArr_obj.getInt("bitrate"))
                    bitrateUrl.setFilePath(media_jArr_obj.getString("file_path"))
                    bitrateUrl.setQetag(media_jArr_obj.getString("qetag"))
                    Objects.requireNonNull(mediaInfo.getBitrateUrlList())!!.add(bitrateUrl)
                }
                channelProgramList.add(channelProgram)
            }
            channelProgramPattern.setChannelProgramList(channelProgramList)
        }
        return channelProgramPattern
    }

    @Throws(JSONException::class)
    fun getRecommendsData(mRecommendResult: String?): RecommendsDataPattern? {
        var recommendsDataPattern: RecommendsDataPattern? = null
        if (mRecommendResult != null) {
            recommendsDataPattern = RecommendsDataPattern()
            val recommendsDataList: MutableList<RecommendsData?> = ArrayList()
            val programs_json = JSONObject(mRecommendResult)
            val jArr = programs_json.getJSONArray("data")
            for (i in 0 until jArr.length()) {
                val recommendsData = RecommendsData()
                val obj = jArr.getJSONObject(i)
                recommendsData.setBriefName(obj.getString("brief_name"))
                recommendsData.setName(obj.getString("name"))
                val recommends_jArr = obj.getJSONArray("recommends")
                recommendsData.setRecommendsList()
                for (j in 0 until recommends_jArr.length()) {
                    val recommend = RecommendsData.Recommends()
                    val obj_r = recommends_jArr.getJSONObject(j)
                    recommend.setId(obj_r.getInt("id"))
                    recommend.setObjectID(obj_r.getInt("object_id"))
                    recommend.setSubTitle(obj_r.getString("sub_title"))
                    recommend.setThumb(obj_r.getString("thumb"))
                    recommend.setThumbs(getThumb(obj_r))
                    recommend.setTitle(obj_r.getString("title"))
                    recommend.setUpdateTime(obj_r.getString("update_time"))
                    if (!obj_r.isNull("detail")) {
                        val detail_obj = obj_r.getJSONObject("detail")
                        recommend.setDetail()
                        val detail = recommend.detail
                        if (!detail_obj.isNull("channel_star"))
                            detail!!.setChannelStar(detail_obj.getInt("channel_star"))
                        if (!detail_obj.isNull("chatgroup_id"))
                            detail!!.setChatGroupID(detail_obj.getInt("chatgroup_id"))
                        if (!detail_obj.isNull("description"))
                            detail!!.setDescription(detail_obj.getString("description"))
                        if (!detail_obj.isNull("duration"))
                            detail!!.setDuration(detail_obj.getInt("duration"))
                        if (!detail_obj.isNull("id"))
                            detail!!.setId(detail_obj.getInt("id"))
                        if (!detail_obj.isNull("original_fee"))
                            detail!!.setOriginalFee(detail_obj.getInt("original_fee"))
                        if (!detail_obj.isNull("price"))
                            detail!!.setPrice(detail_obj.getInt("price"))
                        if (!detail_obj.isNull("redirect_url"))
                            detail!!.setRedirectUrl(detail_obj.getString("redirect_url"))
                        if (!detail_obj.isNull("sale_status"))
                            detail!!.setSaleStatus(detail_obj.getString("sale_status"))
                        if (!detail_obj.isNull("sequence"))
                            detail!!.setSequence(detail_obj.getInt("sequence"))
                        if (!detail_obj.isNull("title"))
                            detail!!.setTitle(detail_obj.getString("title"))
                        if (!detail_obj.isNull("type"))
                            detail!!.setType(detail_obj.getString("type"))
                        if (!detail_obj.isNull("update_time"))
                            detail!!.setUpdateTime(detail_obj.getString("update_time"))
                    }
                    if (!obj_r.isNull("parent_info")) {
                        val parent_obj = obj_r.getJSONObject("parent_info")
                        recommend.setParentInfo()
                        val parentInfo = recommend.getParentInfo()
                        if (!parent_obj.isNull("parent_extra")) {
                            parentInfo!!.setParentExtra()
                            parentInfo.parentExtra!!.setCategoryID(parent_obj.getJSONObject("parent_extra").getInt("category_id"))
                            parentInfo.parentExtra!!.setTag(parent_obj.getJSONObject("parent_extra").getString("tag"))
                        }
                        parentInfo!!.setParentID(parent_obj.getInt("parent_id"))
                        parentInfo.setParentName(parent_obj.getString("parent_name"))
                    }
                    recommendsData.recommendsList!!.add(recommend)
                }
                recommendsDataList.add(recommendsData)
            }
            recommendsDataPattern.recommendsDataList = recommendsDataList
        }
        return recommendsDataPattern
    }

    @Throws(JSONException::class)
    fun getWapiData(mWapiDataResult: String?): WaPiDataPattern? {
        var waPiDataPattern: WaPiDataPattern? = null
        if (mWapiDataResult != null) {
            waPiDataPattern = WaPiDataPattern()
            val waPiDataList: MutableList<WaPiData?> = ArrayList()
            val programs_json = JSONObject(mWapiDataResult)
            val jArr = programs_json.getJSONArray("data")
            for (i in 0 until jArr.length()) {
                val data = WaPiData()
                val obj = jArr.getJSONObject(i)
                data.setCategoryID(obj.getInt("category_id"))
                data.setCover(obj.getString("cover"))
                data.setId(obj.getInt("id"))
                data.setRank(obj.getInt("rank"))
                data.setDesc(obj.getString("desc"))
                data.setName(obj.getString("name"))
                data.setUpdateTime(obj.getString("update_time"))
                waPiDataList.add(data)
            }
            waPiDataPattern.waPiDataList = waPiDataList
        }
        return waPiDataPattern
    }

    /* *******
     * play rule info , with this function we can get the Qing ting's play resources URL
     * so that can play by this URL
     * as QingTing replay ,they have the play sdk ,so we can just set the sdk to replace this function
     * ******/
    class Rule {
        var mAccess // online play need
                : String? = null
        var mResult: String? = null
        var mTest_Path: String? = null
        var mBitrate: Array<String?>? = null
        var mReplay // replay need
                : String? = null
        var mDomain // online play need
                : String? = null
        var mReplay_Domain // replay need
                : String? = null
    }

    private var mRule: Rule? = null

    /**
     * get regroup http url
     * @param mResult String
     */
    @Throws(JSONException::class)
    fun getReplay_URL_Rule(mResult: String?): Rule? {
        if (mRule != null) mRule = null
        mRule = Rule()
        if (mResult != null) {
            val obj = JSONObject(mResult)
            val obj1 = obj.getJSONObject("data")
            val jArr_val = obj1.getJSONObject("radiostations_hls_https").getJSONArray("mediacenters")
            for (j in 0 until jArr_val.length()) {
                val obj_user = jArr_val.getJSONObject(j)
                mRule!!.mAccess = obj_user.getString("access")
                mRule!!.mDomain = obj_user.getString("domain")
                mRule!!.mReplay = obj_user.getString("replay")
                mRule!!.mBitrate = arrayOfNulls(obj_user.getJSONArray("bitrate").length())
                for (i in 0 until obj_user.getJSONArray("bitrate").length())
                    mRule!!.mBitrate!![i] = obj_user.getJSONArray("bitrate").getString(i)
                mRule!!.mReplay_Domain = obj_user.getString("replaydomain")
                mRule!!.mTest_Path = obj_user.getString("test_path")
                mRule!!.mResult = obj_user.getString("result")
            }
        }
        return mRule
    }

    /**
     * get search result
     * @param mSearch_result String
     * @param mType String
     */
    @Throws(JSONException::class)
    fun getSearchResult(mSearch_result: String?, mType: String?) {
        var mSearchChannelLiveList: MutableList<SearchType.ChannelLive?>? = null
        var mSearchProgramLiveList: MutableList<SearchType.ProgramLive?>? = null
        var mSelectedType = 0
        if (mType == "channel_live") {
            mSelectedType = 0
            mSearchChannelLiveList = ArrayList()
        } else if (mType == "program_live") {
            mSelectedType = 1
            mSearchProgramLiveList = ArrayList()
        } else if (mType == "all") {
            mSelectedType = 2
            mSearchProgramLiveList = ArrayList()
        }
        if (mSearch_result != null) {
            val mAllStation = JSONObject(mSearch_result)
            val jArr = mAllStation.getJSONArray("data")
            for (i in 0 until jArr.length()) {
                val obj_user = jArr.getJSONObject(i)
                val doc_list = obj_user.getJSONObject("doclist").getJSONArray("docs")
                for (j in 0 until doc_list.length()) {
                    val list_data = doc_list.getJSONObject(j)
                    when (mSelectedType) {
                        0 -> {
                            val mChannelLive = SearchType.ChannelLive()
                            mChannelLive.setChannelLiveId(list_data.getInt("id"))
                            mChannelLive.setChannelLiveCategoryId(list_data.getInt("category_id"))
                            mChannelLive.setChannelLiveCategoryName(list_data.getString("category_name"))
                            mChannelLive.setChannelLiveCover(list_data.getString("cover"))
                            mChannelLive.setChannelLiveFreqs(list_data.getString("freqs"))
                            mChannelLive.setChannelLiveKeywords(list_data.getString("keywords"))
                            mChannelLive.setChannelLiveTitle(list_data.getString("title"))
                            mChannelLive.setChannelLiveType(list_data.getString("type"))
                            assert(mSearchChannelLiveList != null)
                            mSearchChannelLiveList!!.add(mChannelLive)
                        }
                        1 -> {
                            val mProgramLive = SearchType.ProgramLive()
                            mProgramLive.setProgramLiveId(list_data.getInt("id"))
                            mProgramLive.setProgramLiveCategoryId(list_data.getInt("category_id"))
                            mProgramLive.setProgramLiveCategoryName(list_data.getString("category_name"))
                            mProgramLive.setProgramLiveCover(list_data.getString("cover"))
                            mProgramLive.setProgramLiveParentId(list_data.getInt("parent_id"))
                            mProgramLive.setProgramLiveParentType(list_data.getString("parent_type"))
                            mProgramLive.setProgramLiveTitle(list_data.getString("title"))
                            mProgramLive.setProgramLiveType(list_data.getString("type"))
                            mSearchProgramLiveList!!.add(mProgramLive)
                        }
                        else -> {
                        }
                    }
                }
            }
            when (mSelectedType) {
                0 -> ObserverUIListenerManager.getInstance()!!.notifyChannelLiveObserver(mSearchChannelLiveList)
                1 -> ObserverUIListenerManager.getInstance()!!.notifyProgramLiveObserver(mSearchProgramLiveList)
                else -> {
                }
            }
        }
    }
}