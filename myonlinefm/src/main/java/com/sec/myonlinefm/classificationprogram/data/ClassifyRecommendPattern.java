package com.sec.myonlinefm.classificationprogram.data;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gaolin on 2018/4/20.
 */

public class ClassifyRecommendPattern implements Serializable{
    private static volatile ClassifyRecommendPattern mInstance = null;

    public static HashMap<Integer, ClassifyRecommend> classifyRecommendMap = new HashMap<>();

    public static Bitmap[] scrollBitmap = new Bitmap[5];

    private ClassifyRecommendPattern() {
        // It shouldn't delete this constructor for the factory method(Singleton)
    }

    public static ClassifyRecommendPattern getInstance() {
        if(mInstance == null) {
            synchronized (ClassifyRecommendPattern.class){
                if (mInstance == null) {
                    mInstance = new ClassifyRecommendPattern();
                }
            }
        }
        return mInstance;
    }

    public void addRecommendMap(int section_id, ClassifyRecommend classifyRecommend) {
        classifyRecommendMap.put(section_id, classifyRecommend);
    }

    public ClassifyRecommend getClassifyRecommendItem(int section_id) {
        return classifyRecommendMap.get(section_id);
    }
}
