package com.sec.connecttoapilibrary.qtapitest;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/16.
 * ok-http connect to Qing Ting API.
 */

public class HttpUtil {
    private String TAG = "HttpUtil";

    public String access_token;
    public String token_type;
    public int expires_in;
    public String error = null;

    private ArrayList nameValuePairs;

    private String mToken_Url;
    private String mAll_Station_Url;
    private String mProperty_Url;
    private String mStation_Progrem_Url;

    private static final String mClient_ID = "***";
    private static final String mClient_Secret = "***";

    /**
     * ** OAuth2.0授权 ***
     */
    public String getAccess_Token() {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        mToken_Url = "http://api.open.qingting.fm/access?&grant_type=client_credentials";
        if (nameValuePairs != null)
            nameValuePairs.clear();
        nameValuePairs = new ArrayList(2);
        nameValuePairs.add(new BasicNameValuePair("client_id", mClient_ID));
        nameValuePairs.add(new BasicNameValuePair("client_secret", mClient_Secret));
        return httpPost(mToken_Url);
    }

    /**
     * ** 获取分类下的属性 ***
     */
    private String getProperty_Url(int category_id) {
        mProperty_Url = "http://api.open.qingting.fm/v6/media/categories/"
                + category_id
                + "?";
        return mProperty_Url;
    }

    public String getProperty(int category_id) {
        if (error != null) {
            Log.d(TAG, "error cause :" + getError(error));
            return null;
        }
        if (nameValuePairs != null)
            nameValuePairs.clear();
        nameValuePairs = new ArrayList(1);
        nameValuePairs.add(new BasicNameValuePair("access_token", access_token));
        return httpPost(getProperty_Url(category_id));
    }


    /* **********************
     * * *** 获取分类下的所有电台或直播电台 ***
     * *********************/
    private String getAll_Station_Url(int category_id, int curpage, int attr_id_1) {
        mAll_Station_Url = "http://api.open.qingting.fm/v6/media/categories/"
                + category_id
                + "/channels/order/0/attr/"
                + attr_id_1
                + "/curpage/"
                + curpage
                + "/pagesize/30?";
        return mAll_Station_Url;
    }

    public String getAll_Station(int category_id, int curpage, int attr_id_1) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        if (error != null) {
            Log.d(TAG, "error cause :" + getError(error));
            return null;
        }
        if (nameValuePairs != null)
            nameValuePairs.clear();
        nameValuePairs = new ArrayList(1);
        nameValuePairs.add(new BasicNameValuePair("access_token", access_token));
        return httpPost(getAll_Station_Url(category_id, curpage, attr_id_1));
    }

    /* *************
     * get replay url rule
     * *************/
    public String getReplayRule() {
        if (error != null) {
            Log.d(TAG, "error cause :" + getError(error));
            return null;
        }
        if (nameValuePairs != null)
            nameValuePairs.clear();
        nameValuePairs = new ArrayList(1);
        nameValuePairs.add(new BasicNameValuePair("access_token", access_token));
        return httpPost("http://api.open.qingting.fm/v6/media/mediacenterlist?");
    }

    /**
     * *** 获取直播电台节目单 ***
     */
    private String getStation_Progrem_Url(int channel_id, int day_of_week) {
        mStation_Progrem_Url = "http://api.open.qingting.fm/v6/media/channellives/"
                + channel_id
                + "/programs/day/"
                + day_of_week
                + "?";
        return mStation_Progrem_Url;
    }

    public String getStation_Progrem(int channel_id, int day_of_week) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        if (error != null) {
            Log.d(TAG, "error cause :" + getError(error));
            return null;
        }
        if (nameValuePairs != null)
            nameValuePairs.clear();
        nameValuePairs = new ArrayList(1);
        nameValuePairs.add(new BasicNameValuePair("access_token", access_token));
        return httpPost(getStation_Progrem_Url(channel_id, day_of_week));
    }

    /* **************
     * get search result
     * *************/
    public String getSearchResult(String keyword, String type, int mCurrentPage) {
        final String mSearch_URL = "http://api.open.qingting.fm/newsearch/"+keyword+"/type/"+type+"?";
        if (error != null) {
            Log.d(TAG, "error cause :" + getError(error));
            return null;
        }
        if (nameValuePairs != null)
            nameValuePairs.clear();
        nameValuePairs = new ArrayList(3);
        nameValuePairs.add(new BasicNameValuePair("access_token", access_token));
        nameValuePairs.add(new BasicNameValuePair("curpage", Integer.toString(mCurrentPage)));
        nameValuePairs.add(new BasicNameValuePair("pagesize", "20"));
        return httpPost(mSearch_URL);
    }

    public String getAccess_token(){
        return access_token;
    }

    private String getError(String error) {
        String error_cause = null;
        switch (error) {
            case "invalid_client":
                error_cause = "非法的用户。用户不存在或密码错误。";
                break;
            case "invalid_request":
                error_cause = "非法请求。只允许POST请求。";
                break;
            case "internal_server_error":
                error_cause = "服务器内部错误。";
                break;
            case "unsupported_grant_type":
                error_cause = "未支持的授权类型。";
                break;
            case "invalid_scope":
                error_cause = "非法域（由于目前没有支持scope，所以可以暂时不处理该错误）";
                break;
            default:
                error_cause = "未知错误。";
                break;
        }
        return error_cause;
    }

    /**
     * httpPost请求
     * @param httpUrl
     * @return
     */
    private String httpPost(String httpUrl){
        String result = "" ;
        HttpPost httpPost = new HttpPost( httpUrl );

        HttpResponse httpResponse = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpClient httpClient = new DefaultHttpClient() ;
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000 ) ;
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000 );

            httpResponse = httpClient.execute( httpPost ) ;
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                result  = EntityUtils.toString(httpResponse.getEntity());
            } else {
                try {
                    JSONObject error_obj = new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
                    error = error_obj.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                result  = null;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"result : "+result);
        return result ;
    }

    /**
     * get请求
     * @param httpUrl
     * @return
     * @throws
     */
    public String httpGet(String httpUrl ){
        String result = "" ;
        try {
            BufferedReader reader = null;
            StringBuffer sbf = new StringBuffer() ;

            URL url  = new URL( httpUrl ) ;
            HttpURLConnection connection = (HttpURLConnection) url.openConnection() ;
            //设置超时时间 10s
            connection.setConnectTimeout(10000);
            //设置请求方式
            connection.setRequestMethod( "GET" ) ;
            connection.connect();
            InputStream is = connection.getInputStream() ;
            reader = new BufferedReader(new InputStreamReader( is , "UTF-8" )) ;
            String strRead = null ;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
