package com.sec.myonlinefm.xmlcheck;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/3.
 */

public class LocalInfo {
    private String postion;
    private int channel;
    private String name;
    private int tag;



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
    public String getstationname() {
        return name;
    }
    public int gettag() {
        return tag;
    }
}
