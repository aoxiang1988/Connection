package com.sec.connecttoapilibrary.qtapitest.xmlcheck;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/8.
 */

public class Program {
    private String time;
    private int data;
    private String content;
    private String end;

    public void settime(String time) {
        this.time = time;
    }
    public void setend(String end) {
        this.end = end;
    }
    public void setdata(int data) {
        this.data = data;
    }
    public void setcontent(String content) {
        this.content = content;
    }

    public String getend() {
        return end;
    }
    public String gettime() {
        return time;
    }
    public int getdata() {
        return data;
    }
    public String getcontent() {
        return content;
    }
}
