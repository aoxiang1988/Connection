package com.sec.myonlinefm.classificationprogram.data

import android.graphics.Bitmap
import java.io.Serializable

/**
 * Created by gaolin on 2018/4/20.
 */
class ClassifyRecommendPattern private constructor() : Serializable {
    fun addRecommendMap(sectionId: Int, classifyRecommend: ClassifyRecommend?) {
        classifyRecommendMap?.put(sectionId, classifyRecommend)
    }

    fun getClassifyRecommendItem(sectionId: Int): ClassifyRecommend? {
        return classifyRecommendMap?.get(sectionId)
    }

    companion object {
        @Volatile
        private var mInstance: ClassifyRecommendPattern? = null
        var classifyRecommendMap: HashMap<Int?, ClassifyRecommend?>? = HashMap()
        var scrollBitmap: Array<Bitmap?>? = arrayOfNulls<Bitmap?>(5)
        fun getInstance(): ClassifyRecommendPattern? {
            if (mInstance == null) {
                synchronized(ClassifyRecommendPattern::class.java) {
                    if (mInstance == null) {
                        mInstance = ClassifyRecommendPattern()
                    }
                }
            }
            return mInstance
        }
    }
}