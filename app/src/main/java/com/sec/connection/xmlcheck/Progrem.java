package com.sec.connection.xmlcheck;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/8.
 */

public class Progrem {
    private String time;
    private int data;
    private String content;

    String splitpath[];
    int systemtime[];

    public void settime(String time) {
        this.time = time;
        stringto(time);
    }
    public void setdata(int data) {
        this.data = data;
    }
    public void setcontent(String content) {
        this.content = content;
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

    private void stringto(String time){
        String new_path = time.replace(" - ", "@");
        splitpath = new_path.split("@");
    }

    public int getstarttime(){
        return Integer.valueOf(splitpath[0]);
    }

    public int getfinishtime(){
        return Integer.valueOf(splitpath[1]);
    }
}
