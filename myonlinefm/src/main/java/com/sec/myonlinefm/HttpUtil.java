package com.sec.myonlinefm;

import android.util.Log;

import org.apache.http.HttpResponse;
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

    private static final String mClient_ID = "***";
    private static final String mClient_Secret = "***";

    /**
     * ** OAuth2.0授权 ***
     */
    public String getAccess_Token() {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        String mToken_Url = "http://api.open.qingting.fm/access?&grant_type=client_credentials";
        ArrayList<BasicNameValuePair> nameValuePairs;
        nameValuePairs = new ArrayList<>(2);
        nameValuePairs.add(new BasicNameValuePair("client_id", mClient_ID));
        nameValuePairs.add(new BasicNameValuePair("client_secret", mClient_Secret));
        return httpPost(mToken_Url, nameValuePairs);
    }

    /**
     * ** 获取分类下的属性 ***
     */
    private String getProperty_Url(int category_id) {
        return "http://api.open.qingting.fm/v6/media/categories/"
                + category_id
                + "?";
    }

    public String getProperty(int category_id) {
        if (error != null) {
            Log.d(TAG, "error cause :" + getError(error));
            return null;
        }
        ArrayList<BasicNameValuePair> nameValuePairs;
        nameValuePairs = new ArrayList<>(1);
        nameValuePairs.add(new BasicNameValuePair("access_token", access_token));
        return httpPost(getProperty_Url(category_id), nameValuePairs);
    }


    /* **********************
     * * *** 获取分类下的所有电台或直播电台 ***
     * *********************/
    private String getAll_Station_Url(int category_id, int curpage, int attr_id_1, Integer[] attr_id) {
        StringBuilder attr = new StringBuilder();
        String mAll_Station_Url;
        if(attr_id_1 == 0) {
            if(attr_id == null) {
                mAll_Station_Url = "http://api.open.qingting.fm/v6/media/categories/"
                        + category_id
                        + "/channels/order/0/curpage/"
                        + curpage
                        + "/pagesize/30?";
            } else {
                for (int anAttr_id : attr_id) {
                    if(anAttr_id > 0)
                        attr.append(anAttr_id).append("/");
                }
                mAll_Station_Url = "http://api.open.qingting.fm/v6/media/categories/"
                        + category_id
                        + "/channels/order/0/attr/"
                        + attr.toString()
                        + "curpage/"
                        + curpage
                        + "/pagesize/30?";
            }
        }
        else
            mAll_Station_Url = "http://api.open.qingting.fm/v6/media/categories/"
                    + category_id
                    + "/channels/order/0/attr/"
                    + attr_id_1
                    + "/curpage/"
                    + curpage
                    + "/pagesize/30?";
        Log.d(TAG, mAll_Station_Url);
        return mAll_Station_Url;
    }

    public String getAll_Station(int category_id, int curpage, int attr_id_1, Integer[] attr_id) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        if (error != null) {
            Log.d(TAG, "error cause :" + getError(error));
            return null;
        }
        ArrayList<BasicNameValuePair> nameValuePairs;
        nameValuePairs = new ArrayList<>(1);
        nameValuePairs.add(new BasicNameValuePair("access_token", access_token));
        return httpPost(getAll_Station_Url(category_id, curpage, attr_id_1, attr_id), nameValuePairs);
    }

    /* *************
     * get replay url rule
     * *************/
    public String getReplayRule() {
        if (error != null) {
            Log.d(TAG, "error cause :" + getError(error));
            return null;
        }
        ArrayList<BasicNameValuePair> nameValuePairs;
        nameValuePairs = new ArrayList<>(1);
        nameValuePairs.add(new BasicNameValuePair("access_token", access_token));
        return httpPost("http://api.open.qingting.fm/v6/media/mediacenterlist?", nameValuePairs);
    }

    /**
     * *** 获取直播电台节目单 ***
     */
    private String getStationProgramUrl(int channel_id, int day_of_week) {
        return "http://api.open.qingting.fm/v6/media/channellives/"
                + channel_id
                + "/programs/day/"
                + day_of_week
                + "?";
    }

    public String getStationProgram(int channel_id, int day_of_week) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        if (error != null) {
            Log.d(TAG, "error cause :" + getError(error));
            return null;
        }
        ArrayList<BasicNameValuePair> nameValuePairs;
        nameValuePairs = new ArrayList<>(1);
        nameValuePairs.add(new BasicNameValuePair("access_token", access_token));
        return httpPost(getStationProgramUrl(channel_id, day_of_week), nameValuePairs);
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
        ArrayList<BasicNameValuePair> nameValuePairs;
        nameValuePairs = new ArrayList<>(3);
        nameValuePairs.add(new BasicNameValuePair("access_token", access_token));
        nameValuePairs.add(new BasicNameValuePair("curpage", Integer.toString(mCurrentPage)));
        nameValuePairs.add(new BasicNameValuePair("pagesize", "20"));
        return httpPost(mSearch_URL, nameValuePairs);
    }

    /* *
     * get request program
     * */
    public String getRequestProgram() {
        final String mRequestUrl = "http://api.open.qingting.fm/v6/media/categories?";
        if (error != null) {
            Log.d(TAG, "error cause :" + getError(error));
            return null;
        }
        ArrayList<BasicNameValuePair> nameValuePairs;
        nameValuePairs = new ArrayList<>(1);
        nameValuePairs.add(new BasicNameValuePair("access_token", access_token));
        return httpPost(mRequestUrl, nameValuePairs);
    }



    /*
     * Current Demand Channel */
    public String getCurrentDemandChannel(int channelID) {
        final String mCurrentDemandChannelUrl = "http://api.open.qingting.fm/v6/media/channelondemands/"+channelID+"?";
        if (error != null) {
            Log.d(TAG, "error cause :" + getError(error));
            return null;
        }
        ArrayList<BasicNameValuePair> nameValuePairs;
        nameValuePairs = new ArrayList<>(1);
        nameValuePairs.add(new BasicNameValuePair("access_token", access_token));
        return httpPost(mCurrentDemandChannelUrl, nameValuePairs);
    }

    public String getCurrentDemandChannelPrograms(int channelID, int currentPage, int order) {
        if (error != null) {
            Log.d(TAG, "error cause :" + getError(error));
            return null;
        }
        final String mCurrentDemandProgramsUrl = "http://api.open.qingting.fm/v6/media/channelondemands/"
                +channelID
                +"/programs/order/"
                +order
                +"/curpage/"
                +currentPage
                +"/pagesize/30?";
        ArrayList<BasicNameValuePair> nameValuePairs;
        nameValuePairs = new ArrayList<>(1);
        nameValuePairs.add(new BasicNameValuePair("access_token", access_token));
        return httpPost(mCurrentDemandProgramsUrl, nameValuePairs);
    }

    public String getAccess_token(){
        return access_token;
    }

    private String getError(String error) {
        String error_cause;
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
     * @param nameValuePairs
     * @return result
     */
    private String httpPost(String httpUrl, ArrayList<BasicNameValuePair> nameValuePairs){
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
            StringBuilder sbf = new StringBuilder() ;

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

    //add by gaolin 4/19
    /**
     * *** 获取分类的推荐电台 ***
     */
    public String getFiveRecommend(int section_id){
        if (error != null) {
            Log.d(TAG, "error cause :" + getError(error));
            return null;
        }
        final String mSectionProgramUrl;
        mSectionProgramUrl = "http://api.open.qingting.fm/v6/media/recommends/guides/section/"
                + section_id
                + "?";
        ArrayList<BasicNameValuePair> nameValuePairs;
        nameValuePairs = new ArrayList<>(1);
        nameValuePairs.add(new BasicNameValuePair("access_token", access_token));
        return httpPost(mSectionProgramUrl, nameValuePairs);
    }

    public String getRecommendProgram(int category_id){
        if (error != null) {
            Log.d(TAG, "error cause :" + getError(error));
            return null;
        }
        final String mSectionProgramUrl;
        mSectionProgramUrl = "http://api.open.qingting.fm/v6/media/categories/"
                + category_id
                + "/channels/order/0/curpage/1/pagesize/30?";
        ArrayList<BasicNameValuePair> nameValuePairs;
        nameValuePairs = new ArrayList<>(1);
        nameValuePairs.add(new BasicNameValuePair("access_token", access_token));
        return httpPost(mSectionProgramUrl, nameValuePairs);
    }

    public String getWapiDataResult(int category_id) {
        if (error != null) {
            Log.d(TAG, "error cause :" + getError(error));
            return null;
        }
        final String mWapi_Data_Url = "http://api.open.qingting.fm/wapi/categories/"+category_id+"/channels/billboard?";
        ArrayList<BasicNameValuePair> nameValuePairs;
        nameValuePairs = new ArrayList<>(1);
        nameValuePairs.add(new BasicNameValuePair("access_token", access_token));
        return httpPost(mWapi_Data_Url, nameValuePairs);
    }

    //end by gaolin 4/19

}
