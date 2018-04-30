package com.sec.connecttoapilibrary.qtapitest.liveRadioData;

import java.io.Serializable;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/16.
 */

public class StationProgram implements Serializable {
    private int id ;//节目id
    private String start_time ;//播放开始时间
    private String end_time ;//播放结束时间
    private int duration ;//播放时长，单位秒
    private String title ;//标题
    private String type ;//节目类型
    private int channel_id ;//所属电台id
    private int program_id ;//节目唯一标示符
    private Broadcasters[] broadcasters;

    private int broadcasters_num = 0;

    public void newBroadcasters(int num){
        broadcasters_num = num;
        broadcasters = new Broadcasters[num];
    }

    public Broadcasters[] getBroadcaster(){
        return broadcasters;
    }

    public int getBroadcastersNum(){
        return broadcasters_num;
    }

    public void setProgramId(int id){
        this.id = id;
    }
    public int getProgramId(){
        return id;
    }

    public void setProgramStart_time(String start_time){
        this.start_time = start_time;
    }
    public String getProgramStart_time(){
        return start_time;
    }

    public void setProgramEnd_Time(String end_time){
        this.end_time = end_time;
    }
    public String getProgramEnd_Time(){
        return end_time;
    }

    public void setProgramDuration(int duration){
        this.duration = duration;
    }
    public int getProgramDuration(){
        return duration;
    }

    public void setProgramTitle(String title){
        this.title = title;
    }
    public String getProgramTitle(){
        return title;
    }

    public void setProgramType(String type){
        this.type = type;
    }
    public String getProgramType(){
        return type;
    }

    public void setProgramChannel_Id(int channel_id){
        this.channel_id = channel_id;
    }
    public int getProgramChannel_Id(){
        return channel_id;
    }

    public void setProgramProgram_Id(int program_id){
        this.program_id = program_id;
    }
    public int getProgramProgram_Id(){
        return program_id;
    }

    public static class Broadcasters {
        /*also can get like wei bo or QQ id if has the info*/
        public int id;
        public String username;
        public void setBroadcastersName(String username){
            this.username = username;
        }
        public String getBroadcastersName(){
            return username;
        }
    }
}