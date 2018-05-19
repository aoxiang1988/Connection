package com.sec.myonlinefm.classificationprogram.data

import android.graphics.Bitmap
import java.io.Serializable
import java.util.*

/**
 * Created by gaolin on 2018/4/20.
 */

class ClassifyRecommend : Serializable{
    private var categoryID : Int = -1
    private var ids : MutableList<Int> = ArrayList()
    private var titles : MutableList<String> = ArrayList()
    private var thumbUrls : MutableList<String> = ArrayList()
    private var thumbs : MutableList<Bitmap> = ArrayList()

    fun setCategoryID(categoryID : Int) {
        this.categoryID = categoryID
    }

    fun getCategoryID() : Int {
        return categoryID
    }

    fun setId(id : Int) {
        ids.add(id)
    }

    fun getId (i : Int) : Int {
        if (ids.isEmpty())
            return -1
        return ids[i]
    }

    fun setTitle(title : String) {
        titles.add(title)
    }

    fun getTitle(i : Int) : String? {
        if(titles.isEmpty())
            return null
        return titles.get(i)
    }

    fun setThumbUrl(url : String) {
        thumbUrls.add(url)
    }

    fun getThumbUrl(i : Int) : String? {
        return if(!thumbUrls.isEmpty())
            thumbUrls.get(i)
        else null
    }

    fun setThumb(i : Int, thumb : Bitmap) {
        thumbs[i] = thumb
    }

    fun getThumb(i : Int) : Bitmap? {
        if (thumbs.isEmpty())
            return null
        return thumbs[i]
    }
}
