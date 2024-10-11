package com.sec.connection.xmlcheck;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/8.
 */

public class Program {
    private String time;
    private int data;
    private String content;

    String[] mSplitPath;

    public void setTime(String time) {
        this.time = time;
        stringTo(time);
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    private void stringTo(String time){
        String new_path = time.replace(" - ", "@");
        mSplitPath = new_path.split("@");
    }

    public int getStartTime(){
        return Integer.parseInt(mSplitPath[0]);
    }

    public int getFinishTime(){
        return Integer.parseInt(mSplitPath[1]);
    }
}
