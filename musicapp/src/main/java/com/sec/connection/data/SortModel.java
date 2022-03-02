package com.sec.connection.data;

/**
 * Created by SRC-TJ-MM-BinYang on 2017/9/14.
 */

public class SortModel {

    private String name;   //显示的数据
    private String sortLetters;  //显示数据拼音的首字母
    private Audio audio;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSortLetters() {
        return sortLetters;
    }
    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public void setAudio(Audio audio){
        this.audio = audio;
    }
    public Audio getAudio(){
        return audio;
    }
}
