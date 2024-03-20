package com.sec.myonlinefm

import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.params.CoreConnectionPNames
import org.apache.http.util.EntityUtils
import org.json.JSONObject
import org.json.JSONException

import android.util.Log
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/16.
 * ok-http connect to Qing Ting API.
 */
class HttpUtil {
    private val TAG: String? = "HttpUtil"
    var access_token: String? = null
    var token_type: String? = null
    var expires_in = 0
    private var error: String? = null

    /**
     * ** OAuth2.0授权 ***
     */
    fun getAccess_Token(): String? {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        val mToken_Url = "http://api.open.qingting.fm/access?&grant_type=client_credentials"
        val nameValuePairs: ArrayList<BasicNameValuePair?>
        nameValuePairs = ArrayList(2)
        nameValuePairs.add(BasicNameValuePair("client_id", mClient_ID))
        nameValuePairs.add(BasicNameValuePair("client_secret", mClient_Secret))
        return httpPost(mToken_Url, nameValuePairs)
    }

    /**
     * ** 获取分类下的属性 ***
     */
    private fun getProperty_Url(category_id: Int): String? {
        return ("http://api.open.qingting.fm/v6/media/categories/"
                + category_id
                + "?")
    }

    fun getProperty(category_id: Int): String? {
        if (error != null) {
            Log.d(TAG, "error cause :" + getError(error))
            return null
        }
        val nameValuePairs: ArrayList<BasicNameValuePair?>
        nameValuePairs = ArrayList(1)
        nameValuePairs.add(BasicNameValuePair("access_token", access_token))
        return httpPost(getProperty_Url(category_id), nameValuePairs)
    }

    /* **********************
     * * *** 获取分类下的所有电台或直播电台 ***
     * *********************/
    private fun getAll_Station_Url(category_id: Int, curpage: Int, attr_id_1: Int, attr_id: Array<Int?>?): String? {
        val attr = StringBuilder()
        val mAll_Station_Url: String
        mAll_Station_Url = if (attr_id_1 == 0) {
            if (attr_id == null) {
                ("http://api.open.qingting.fm/v6/media/categories/"
                        + category_id
                        + "/channels/order/0/curpage/"
                        + curpage
                        + "/pagesize/30?")
            } else {
                for (anAttr_id in attr_id) {
                    if (anAttr_id!! > 0) attr.append(anAttr_id).append("/")
                }
                ("http://api.open.qingting.fm/v6/media/categories/"
                        + category_id
                        + "/channels/order/0/attr/"
                        + attr.toString()
                        + "curpage/"
                        + curpage
                        + "/pagesize/30?")
            }
        } else ("http://api.open.qingting.fm/v6/media/categories/"
                + category_id
                + "/channels/order/0/attr/"
                + attr_id_1
                + "/curpage/"
                + curpage
                + "/pagesize/30?")
        Log.d(TAG, mAll_Station_Url)
        return mAll_Station_Url
    }

    fun getAll_Station(category_id: Int, curpage: Int, attr_id_1: Int, attr_id: Array<Int?>?): String? {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        if (error != null) {
            Log.d(TAG, "error cause :" + getError(error))
            return null
        }
        val nameValuePairs: ArrayList<BasicNameValuePair?>
        nameValuePairs = ArrayList(1)
        nameValuePairs.add(BasicNameValuePair("access_token", access_token))
        return httpPost(getAll_Station_Url(category_id, curpage, attr_id_1, attr_id), nameValuePairs)
    }

    /* *************
     * get replay url rule
     * *************/
    fun getReplayRule(): String? {
        if (error != null) {
            Log.d(TAG, "error cause :" + getError(error))
            return null
        }
        val nameValuePairs: ArrayList<BasicNameValuePair?>
        nameValuePairs = ArrayList(1)
        nameValuePairs.add(BasicNameValuePair("access_token", access_token))
        return httpPost("http://api.open.qingting.fm/v6/media/mediacenterlist?", nameValuePairs)
    }

    /**
     * *** 获取直播电台节目单 ***
     */
    private fun getStationProgramUrl(channel_id: Int, day_of_week: Int): String? {
        return ("http://api.open.qingting.fm/v6/media/channellives/"
                + channel_id
                + "/programs/day/"
                + day_of_week
                + "?")
    }

    fun getStationProgram(channel_id: Int, day_of_week: Int): String? {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        if (error != null) {
            Log.d(TAG, "error cause :" + getError(error))
            return null
        }
        val nameValuePairs: ArrayList<BasicNameValuePair?>
        nameValuePairs = ArrayList(1)
        nameValuePairs.add(BasicNameValuePair("access_token", access_token))
        return httpPost(getStationProgramUrl(channel_id, day_of_week), nameValuePairs)
    }

    /* **************
     * get search result
     * *************/
    fun getSearchResult(keyword: String?, type: String?, mCurrentPage: Int): String? {
        val mSearch_URL = "http://api.open.qingting.fm/newsearch/$keyword/type/$type?"
        if (error != null) {
            Log.d(TAG, "error cause :" + getError(error))
            return null
        }
        val nameValuePairs: ArrayList<BasicNameValuePair?>
        nameValuePairs = ArrayList(3)
        nameValuePairs.add(BasicNameValuePair("access_token", access_token))
        nameValuePairs.add(BasicNameValuePair("curpage", Integer.toString(mCurrentPage)))
        nameValuePairs.add(BasicNameValuePair("pagesize", "20"))
        return httpPost(mSearch_URL, nameValuePairs)
    }

    /* *
     * get request program
     * */
    fun getRequestProgram(): String? {
        val mRequestUrl = "http://api.open.qingting.fm/v6/media/categories?"
        if (error != null) {
            Log.d(TAG, "error cause :" + getError(error))
            return null
        }
        val nameValuePairs: ArrayList<BasicNameValuePair?>
        nameValuePairs = ArrayList(1)
        nameValuePairs.add(BasicNameValuePair("access_token", access_token))
        return httpPost(mRequestUrl, nameValuePairs)
    }

    /*
     * Current Demand Channel */
    fun getCurrentDemandChannel(channelID: Int): String? {
        val mCurrentDemandChannelUrl = "http://api.open.qingting.fm/v6/media/channelondemands/$channelID?"
        if (error != null) {
            Log.d(TAG, "error cause :" + getError(error))
            return null
        }
        val nameValuePairs: ArrayList<BasicNameValuePair?>
        nameValuePairs = ArrayList(1)
        nameValuePairs.add(BasicNameValuePair("access_token", access_token))
        return httpPost(mCurrentDemandChannelUrl, nameValuePairs)
    }

    fun getCurrentDemandChannelPrograms(channelID: Int, currentPage: Int, order: Int): String? {
        if (error != null) {
            Log.d(TAG, "error cause :" + getError(error))
            return null
        }
        val mCurrentDemandProgramsUrl = ("http://api.open.qingting.fm/v6/media/channelondemands/"
                + channelID
                + "/programs/order/"
                + order
                + "/curpage/"
                + currentPage
                + "/pagesize/30?")
        val nameValuePairs: ArrayList<BasicNameValuePair?>
        nameValuePairs = ArrayList(1)
        nameValuePairs.add(BasicNameValuePair("access_token", access_token))
        return httpPost(mCurrentDemandProgramsUrl, nameValuePairs)
    }

    @JvmName("getAccess_token1")
    fun getAccess_token(): String? {
        return access_token
    }

    private fun getError(error: String?): String? {
        val error_cause: String
        error_cause = when (error) {
            "invalid_client" -> "非法的用户。用户不存在或密码错误。"
            "invalid_request" -> "非法请求。只允许POST请求。"
            "internal_server_error" -> "服务器内部错误。"
            "unsupported_grant_type" -> "未支持的授权类型。"
            "invalid_scope" -> "非法域（由于目前没有支持scope，所以可以暂时不处理该错误）"
            else -> "未知错误。"
        }
        return error_cause
    }

    /**
     * httpPost&#x8bf7;&#x6c42;
     * @param httpUrl
     * @param nameValuePairs
     * @return result
     */
    private fun httpPost(httpUrl: String?, nameValuePairs: ArrayList<BasicNameValuePair?>?): String? {
        var result: String? = ""
        val httpPost = HttpPost(httpUrl)
        val httpResponse: HttpResponse
        try {
            httpPost.entity = UrlEncodedFormEntity(nameValuePairs)
            val httpClient: HttpClient = DefaultHttpClient()
            httpClient.params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000)
            httpClient.params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000)
            httpResponse = httpClient.execute(httpPost)
            if (httpResponse.statusLine.statusCode == 200) {
                result = EntityUtils.toString(httpResponse.entity)
            } else {
                try {
                    val error_obj = JSONObject(EntityUtils.toString(httpResponse.entity))
                    error = error_obj.getString("error")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                result = null
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Log.d(TAG, "result : $result")
        return result
    }

    /**
     * get请求
     * @param httpUrl
     * @return
     * @throws
     */
    fun httpGet(httpUrl: String?): String? {
        var result = ""
        try {
            val reader: BufferedReader
            val sbf = StringBuilder()
            val url = URL(httpUrl)
            val connection = url.openConnection() as HttpURLConnection
            //设置超时时间 10s
            connection.connectTimeout = 10000
            //设置请求方式
            connection.requestMethod = "GET"
            connection.connect()
            val `is` = connection.inputStream
            reader = BufferedReader(InputStreamReader(`is`, "UTF-8"))
            var strRead: String?
            while (reader.readLine().also { strRead = it } != null) {
                sbf.append(strRead)
                sbf.append("\r\n")
            }
            reader.close()
            result = sbf.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
    //add by gaolin 4/19
    /**
     * *** 获取分类的推荐电台 ***
     */
    fun getFiveRecommend(section_id: Int): String? {
        if (error != null) {
            Log.d(TAG, "error cause :" + getError(error))
            return null
        }
        val mSectionProgramUrl: String
        mSectionProgramUrl = ("http://api.open.qingting.fm/v6/media/recommends/guides/section/"
                + section_id
                + "?")
        val nameValuePairs: ArrayList<BasicNameValuePair?>
        nameValuePairs = ArrayList(1)
        nameValuePairs.add(BasicNameValuePair("access_token", access_token))
        return httpPost(mSectionProgramUrl, nameValuePairs)
    }

    fun getRecommendProgram(category_id: Int): String? {
        if (error != null) {
            Log.d(TAG, "error cause :" + getError(error))
            return null
        }
        val mSectionProgramUrl: String
        mSectionProgramUrl = ("http://api.open.qingting.fm/v6/media/categories/"
                + category_id
                + "/channels/order/0/curpage/1/pagesize/30?")
        val nameValuePairs: ArrayList<BasicNameValuePair?>
        nameValuePairs = ArrayList(1)
        nameValuePairs.add(BasicNameValuePair("access_token", access_token))
        return httpPost(mSectionProgramUrl, nameValuePairs)
    }

    fun getWapiDataResult(category_id: Int): String? {
        if (error != null) {
            Log.d(TAG, "error cause :" + getError(error))
            return null
        }
        val mWapi_Data_Url = "http://api.open.qingting.fm/wapi/categories/$category_id/channels/billboard?"
        val nameValuePairs: ArrayList<BasicNameValuePair?>
        nameValuePairs = ArrayList(1)
        nameValuePairs.add(BasicNameValuePair("access_token", access_token))
        return httpPost(mWapi_Data_Url, nameValuePairs)
    } //end by gaolin 4/19

    companion object {
        private val mClient_ID: String? = "MjM2NDI4MzYtZmE3Mi0xMWU3LTkyM2YtMDAxNjNlMDAyMGFk"
        private val mClient_Secret: String? = "YzhjNTljOWEtZTRlMS0zNDU1LTlhOGUtMTgyZTJjYzE3OGM5"
    }
}