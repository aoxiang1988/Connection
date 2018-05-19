package com.sec.myonlinefm.data;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/16.
 * this is the OnLine Station info, define the station's content
 */

public class Station implements Serializable {
    //private int total;//station num
    private int category_id; //classify id
    private String description ; //station description
    private int id; //station id
    private String playCount; //play num
    private String title; //station title
    private String type ;  //classify：channel_on_demand is album；channel_live is radio
    private String update_time; //update time
    private Bitmap thumbs; //thumbnail. if no set null，if has it likes ：small, medium, large，200，400，800(select the needed)
    private String freq; //station frequency
    private int audience_count; //audience count
    private String weBoUrl; //the URL for share

    private StationProgram currentProgram = null;
    private String currentProgramTime;
    private String nextProgram = null;

    private int item = 0;

    public void setWhichItem(int item){
        this.item = item;
    }
    public int getWhichItem(){
        return item;
    }

    public void setCurrentProgram(StationProgram currentProgram){
        this.currentProgram = currentProgram;
    }
    public StationProgram getCurrentProgram(){
        return currentProgram;
    }

    public void setCurrentProgramTime(String currentProgramTime){
        this.currentProgramTime = currentProgramTime;
    }
    public String getCurrentProgramTime(){
        return currentProgramTime;
    }
    public void setNextProgram(String nextProgram){
        this.nextProgram = nextProgram;
    }
    public String getNextProgram(){
        return nextProgram;
    }

    public void setStationThumbs(Bitmap thumbs){
        this.thumbs = thumbs;
    }
    public Bitmap getStationThumbs(){
        return thumbs;
    }

    public void setStationCategory_id(int category_id){
        this.category_id = category_id;
    }
    public int getStationCategory_id(){
        return category_id;
    }

    public void setStationDescription(String description){
        this.description = description;
    }
    public String getStationDescription(){
        return description;
    }

    public void setStationId(int id){
        this.id = id;
    }
    public int getStationId(){
        return id;
    }

    public void setStationPlayCount(String playCount){
        this.playCount = playCount;
    }
    public String getStationPlayCount(){
        return playCount;
    }

    public void setStationTitle(String title){
        this.title = title;
    }
    public String getStationTitle(){
        return title;
    }

    public void setStationType(String type){
        this.type = type;
    }
    public String getStationType(){
        return type;
    }

    public void setStationUpdate_time(String update_time){
        this.update_time = update_time;
    }
    public String getStationUpdate_time(){
        return update_time;
    }

    public void setStationFreq(String freq){
        this.freq = freq;
    }
    public String getStationFreq(){
        return freq;
    }

    public void setStationAudience_count(int audience_count){
        this.audience_count = audience_count;
    }
    public int getStationAudience_count(){
        return audience_count;
    }

    public void setStationWeBoUrl(String weBoUrl){
        this.weBoUrl = weBoUrl;
    }
    public String getStationWeBoUrl(){
        return weBoUrl;
    }

}
