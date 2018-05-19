package com.sec.myonlinefm.data;

import java.util.HashMap;
import java.util.List;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/4/19.
 */

public class ClassificationAttributePattern {
    private List<PropertyInfo> mInfo = null;
    private HashMap<Integer, List<PropertyInfo.values>> mInfoMap = null;

    public void setInfo(List<PropertyInfo> mInfo) {
        this.mInfo = mInfo;
    }
    public List<PropertyInfo> getInfo() {
        return mInfo;
    }

    public void setInfoMap(HashMap<Integer, List<PropertyInfo.values>> mInfoMap) {
        this.mInfoMap = mInfoMap;
    }
    public HashMap<Integer, List<PropertyInfo.values>> getInfoMap() {
        return mInfoMap;
    }
}
