package com.sec.myonlinefm.classificationprogram.data

import android.graphics.Bitmap


/**
 * Created by SRC-TJ-MM-BinYang on 2018/5/10.
 */
class FavData {
    internal var channelID = 0
    internal var categoryID = 0
    internal var channelName: String? = null
    internal var channelIcon: Bitmap? = null
    internal var podCasterName: String? = null
    fun getCategoryID(): Int {
        return categoryID
    }

    fun setCategoryID(categoryID: Int) {
        this.categoryID = categoryID
    }

    fun getPodCasterName(): String? {
        return podCasterName
    }

    fun setPodCasterName(podCasterName: String?) {
        this.podCasterName = podCasterName
    }

    fun getChannelIcon(): Bitmap? {
        return channelIcon
    }

    fun getChannelID(): Int {
        return channelID
    }

    fun getChannelName(): String? {
        return channelName
    }

    fun setChannelIcon(channelIcon: Bitmap?) {
        this.channelIcon = channelIcon
    }

    fun setChannelID(channelID: Int) {
        this.channelID = channelID
    }

    fun setChannelName(channelName: String?) {
        this.channelName = channelName
    }
}