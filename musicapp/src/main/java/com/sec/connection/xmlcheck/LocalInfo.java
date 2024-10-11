package com.sec.connection.xmlcheck;

import android.content.Context;
import android.os.Bundle;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/3.
 */

public class LocalInfo {
    private String position;
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

    public void setPosition(String postion) {
        this.position = postion;
    }
    public void setChannel(int channel) {
        this.channel = channel;
    }

    public void setStationName(String name) {
        this.name = name;
    }
    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getPosition() {
        return position;
    }
    public int getChannel() {
        return channel;
    }

    public String getRadioID() {
        return radio_ID;
    }

    public String getSrc() {
        return src;
    }

    public String getStationName() {
        return name;
    }
    public int getTag() {
        return tag;
    }
}
