package com.sec.myonlinefm.classificationprogram.data;

import android.graphics.Bitmap;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/5/10.
 */

public class FavData {

    private int channelID;
    private int categoryID;
    private String channelName;
    private Bitmap channelIcon;
    private String podCasterName;

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getPodCasterName() {
        return podCasterName;
    }

    public void setPodCasterName(String podCasterName) {
        this.podCasterName = podCasterName;
    }

    public Bitmap getChannelIcon() {
        return channelIcon;
    }

    public int getChannelID() {
        return channelID;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelIcon(Bitmap channelIcon) {
        this.channelIcon = channelIcon;
    }

    public void setChannelID(int channelID) {
        this.channelID = channelID;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
