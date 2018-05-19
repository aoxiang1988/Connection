package com.sec.myonlinefm.classificationprogram.data;

import android.graphics.Bitmap;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/5/7.
 */

public class WaPiData {
    private int category_id; //分类id
    private Bitmap cover; //专辑头像
    private int id; //专辑id
    private int rank; //排名
    private String update_time; //更新时间
    private String desc; //专辑简介
    private String name; //专辑名字

    public void setCategoryID(int category_id) {
        this.category_id = category_id;
    }

    public void setUpdateTime(String update_time) {
        this.update_time = update_time;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getId() {
        return id;
    }

    public int getCategoryID() {
        return category_id;
    }

    public int getRank() {
        return rank;
    }

    public Bitmap getCover() {
        return cover;
    }

    public String getDesc() {
        return desc;
    }

    public String getName() {
        return name;
    }

    public String getUpdateTime() {
        return update_time;
    }
}
