package com.sec.myonlinefm

import android.util.Log
import java.io.IOException
import java.util.HashMap

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/16.
 * ok-http connect to Qing Ting API. (
 * 允许连接到同一个主机地址的所有请求,提高请求效率 [3]
 * 共享Socket,减少对服务器的请求次数 [3]
 * 通过连接池,减少了请求延迟 [3]
 * 缓存响应数据来减少重复的网络请求 [4]
 * 减少了对数据流量的消耗 [3]
 * 自动处理GZip压缩 [3] )
 */
class OkHttpUtil (private val mClient_ID : String = "***",
                  private val mClient_Secret : String = "***") {

    private fun getTAG() :String {return "HttpUtil"}

    var access_token : String? = null
    var token_type : String? = null
    var expires_in : Int? = null
    var error : String? = null

    /**
     * ** OAuth2.0授权 ***
     */
    fun getAccess_Token(): String? {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        val mToken_Url = "http://api.open.qingting.fm/access?&grant_type=client_credentials"

        val mCondition : MutableMap<String, String> =HashMap()
        mCondition.put("client_id", mClient_ID)
        mCondition.put("client_secret", mClient_Secret)
        return httpPost(mToken_Url, mCondition)
    }

    /**
     * ** 获取分类下的属性 ***
     */
    fun getProperty_Url(category_id : Int) : String {
        return "http://api.open.qingting.fm/v6/media/categories/"+ category_id.toString() + "?"
    }

    fun getProperty(category_id : Int): String? {
        if (error != null) {
            Log.d(getTAG(), "error cause :" + getError(error!!))
            return null
        }
        val mCondition : MutableMap<String, String> =HashMap()
        mCondition.put("access_token", access_token!!)

        return httpPost(getProperty_Url(category_id), mCondition)
    }


    /* **********************
     * * *** 获取分类下的所有电台或直播电台 ***
     * *********************/
    fun getAll_Station_Url(category_id : Int, current_page : Int, attr_id_1 : Int, attr_id : Array<Int>) : String {
        val attr : StringBuilder = StringBuilder()
        val mAll_Station_Url : String
        if(attr_id_1 == 0) {
            for (anAttr_id in attr_id) {
                if(anAttr_id > 0)
                    attr.append(anAttr_id).append("/")
            }
            mAll_Station_Url = "http://api.open.qingting.fm/v6/media/categories/"+
                    category_id.toString()+
                    "/channels/order/0/attr/"+
                    attr.toString()+
                    "curpage/"+
                    current_page.toString()+
                    "/pagesize/30?"
        }
        else
            mAll_Station_Url = "http://api.open.qingting.fm/v6/media/categories/"+
                    category_id.toString()+
                    "/channels/order/0/attr/"+
                    attr_id_1.toString()+
                    "/curpage/"+
                    current_page.toString()+
                    "/pagesize/30?"
        Log.d(getTAG(), mAll_Station_Url)
        return mAll_Station_Url
    }

    fun getAll_Station(category_id : Int, current_page : Int, attr_id_1 : Int, attr_id : Array<Int>) : String? {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        if (error != null) {
            Log.d(getTAG(), "error cause :" + getError(error!!))
            return null
        }
        val mCondition : MutableMap<String, String> = HashMap()
        mCondition.put("access_token", access_token!!)
        return httpPost(getAll_Station_Url(category_id, current_page, attr_id_1, attr_id), mCondition)
    }

    /* *************
     * get replay url rule
     * *************/
    fun getReplayRule() : String? {
        if (error != null) {
            Log.d(getTAG(), "error cause :" + getError(error!!))
            return null
        }
        val mCondition : MutableMap<String, String> = HashMap()
        mCondition.put("access_token", access_token!!)
        return httpPost("http://api.open.qingting.fm/v6/media/mediacenterlist?", mCondition)
    }

    /**
     * *** 获取直播电台节目单 ***
     */
    private fun getStationProgramUrl(channel_id : Int, day_of_week : Int) : String {
        return "http://api.open.qingting.fm/v6/media/channellives/" +
                channel_id.toString() +
                "/programs/day/" +
                day_of_week.toString() +
                "?"
    }

    fun getStationProgram(channel_id : Int, day_of_week : Int) : String? {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        if (error != null) {
            Log.d(getTAG(), "error cause :" + getError(error!!))
            return null
        }
        val mCondition : MutableMap<String, String> = HashMap()
        mCondition.put("access_token", access_token!!)
        return httpPost(getStationProgramUrl(channel_id, day_of_week), mCondition)
    }

    /* **************
     * get search result
     * *************/
    fun getSearchResult(keyword : String, type : String, mCurrentPage : Int) : String ? {
        val mSearchURL = "http://api.open.qingting.fm/newsearch/$keyword/type/$type?"
        if (error != null) {
            Log.d(getTAG(), "error cause :" + getError(error!!))
            return null
        }
        val mCondition : MutableMap<String, String> = HashMap()
        mCondition.put("access_token", access_token!!)
        mCondition.put("curpage", Integer.toString(mCurrentPage))
        mCondition.put("pagesize", "20")
        return httpPost(mSearchURL, mCondition)
    }

    /* *
     * get request program
     * */
    fun getRequestProgram() : String? {
        val mRequestUrl = "http://api.open.qingting.fm/v6/media/categories?"
        if (error != null) {
            Log.d(getTAG(), "error cause :" + getError(error!!))
            return null
        }
        val mCondition : MutableMap<String, String> = HashMap()
        mCondition.put("access_token", access_token!!)
        return httpPost(mRequestUrl, mCondition)
    }

    /*
     * Current Demand Channel */
    fun getCurrentDemandChannel(channelID : Int) : String? {
        val mCurrentDemandChannelUrl = "http://api.open.qingting.fm/v6/media/channelondemands/"+channelID+"?"
        if (error != null) {
            Log.d(getTAG(), "error cause :" + getError(error!!))
            return null
        }
        val mCondition : MutableMap<String, String> = HashMap()
        mCondition.put("access_token", access_token!!)
        return httpPost(mCurrentDemandChannelUrl, mCondition)
    }

    fun getCurrentDemandChannelPrograms(channelID :Int, currentPage : Int, order : Int) : String? {
        if (error != null) {
            Log.d(getTAG(), "error cause :" + getError(error!!))
            return null
        }
        val mCurrentDemandProgramsUrl : String = "http://api.open.qingting.fm/v6/media/channelondemands/" +
                channelID.toString() +
                "/programs/order/" +
                order.toString() +
                "/curpage/" +
                currentPage.toString() +
                "/pagesize/30?"
        val mCondition : MutableMap<String, String> = HashMap()
        mCondition.put("access_token", access_token!!)
        return httpPost(mCurrentDemandProgramsUrl, mCondition)
    }

    fun getAccessToken() : String? {
        return access_token
    }

    private fun getError(error : String) : String {
        var errorCause : String ? = ""
        when (error) {
            "invalid_client" -> errorCause = "非法的用户。用户不存在或密码错误。"
            "invalid_request" -> errorCause = "非法请求。只允许POST请求。"
            "internal_server_error" -> errorCause = "服务器内部错误。"
            "unsupported_grant_type" -> errorCause = "未支持的授权类型。"
            "invalid_scope" -> errorCause = "非法域（由于目前没有支持scope，所以可以暂时不处理该错误）"
        }
        return errorCause!!
    }

    /**
     * httpPost请求
     * @param httpUrl
     * @param nameValuePairs
     * @return result
     */
    private fun httpPost(httpUrl : String, mCondition : MutableMap<String, String>) : String? {

        var result: String
        val client = OkHttpClient()
        val builder : FormBody.Builder = FormBody.Builder()
        for (key : String in mCondition.keys) {
            builder.add(key, mCondition[key])
        }
        val body : RequestBody = builder.build()
        val request : Request = Request.Builder().url(httpUrl).post(body).build()
        val response: Response?
        try {
            response = client.newCall(request).execute()
            result = response.body()!!.string()
        } catch (e : IOException) {
            e.printStackTrace()
            result = null.toString()
        }
        Log.d(getTAG(), "result : $result")
        return result
    }

    /**
     * get请求
     * @param httpUrl
     * @return
     * @throws
     */
    fun httpGet(httpUrl : String) : String {
        var result = ""
        try {
            val reader: BufferedReader?
            val sbf = StringBuilder()

            val url = URL( httpUrl )
            val connection : HttpURLConnection = url.openConnection() as HttpURLConnection
            //设置超时时间 10s
            connection.setConnectTimeout(10000)
            //设置请求方式
            connection.setRequestMethod( "GET" )
            connection.connect();
            val inputStream : InputStream = connection.getInputStream()
            reader = BufferedReader(InputStreamReader( inputStream , "UTF-8" ))
            var strRead : String ? = null
            strRead = reader.readLine()
            while (strRead != null) {
                sbf.append(strRead)
                sbf.append("\r\n")
                strRead = reader.readLine()
            }
            reader.close()
            result = sbf.toString()
        } catch (e : Exception) {
            e.printStackTrace()
        }
        return result
    }

}
