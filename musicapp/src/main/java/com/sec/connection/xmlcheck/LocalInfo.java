package com.sec.connection.xmlcheck;

import android.content.Context;
import android.os.Bundle;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/3.
 */

public class LocalInfo {
    private String postion;
    private int channel;
    private String name;
    private int tag;
    private String radio_ID;
    private String src;

    public LocalInfo(){

    }

    public LocalInfo(Bundle bundle, Context context) {
        name = bundle.getString("TITLE");
        src = bundle.getString("PICTURE");
        radio_ID = bundle.getString("NETURL");
    }

    public void setpostion(String postion) {
        this.postion = postion;
    }
    public void setchannel(int channel) {
        this.channel = channel;
    }

    public void setstationname(String name) {
        this.name = name;
    }
    public void settag(int tag) {
        this.tag = tag;
    }

    public String getpostion() {
        return postion;
    }
    public int getchannel() {
        return channel;
    }

    public String getradio_ID() {
        return radio_ID;
    }

    public String getsrc() {
        return src;
    }

    public String getstationname() {
        return name;
    }
    public int gettag() {
        return tag;
    }
}
