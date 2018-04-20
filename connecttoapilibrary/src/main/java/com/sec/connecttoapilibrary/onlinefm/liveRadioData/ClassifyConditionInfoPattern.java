package com.sec.connecttoapilibrary.onlinefm.liveRadioData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/4/19.
 */

public class ClassifyConditionInfoPattern {
    private List<PropertyInfo> info = new ArrayList<>();
    private HashMap<Integer, List<PropertyInfo.values>> infoMap = new LinkedHashMap<>();

    public void setInfo(List<PropertyInfo> info) {
        this.info = info;
    }

    public void setInfoMap(HashMap<Integer, List<PropertyInfo.values>> infoMap) {
        this.infoMap = infoMap;
    }

    public List<PropertyInfo> getInfo() {
        return info;
    }

    public HashMap<Integer, List<PropertyInfo.values>> getInfoMap() {
        return infoMap;
    }
}
