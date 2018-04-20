package com.sec.connecttoapilibrary.onlinefm.liveRadioData;

import java.io.Serializable;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/29.
 */

public class PropertyInfo implements Serializable {

    private int id;
    private String name;

    public void setPropertyInfoId(int id){
        this.id = id;
    }
    public void setPropertyInfoname(String name){
        this.name = name;
    }
    public int getPropertyInfoId(){
        return id;
    }
    public String getPropertyInfoname(){
        return name;
    }

    public static class values {
        private int id;
        private String name;
        private String sequence;
        public void setvaluesId(int id){
            this.id = id;
        }
        public void setvaluesname(String name){
            this.name = name;
        }
        public void setvaluessequence(String sequence){
            this.sequence = sequence;
        }
        public int getvaluesId(){
            return id;
        }
        public String getvaluesname(){
            return name;
        }
        public String getvaluessequence(){
            return sequence;
        }
    }
}


