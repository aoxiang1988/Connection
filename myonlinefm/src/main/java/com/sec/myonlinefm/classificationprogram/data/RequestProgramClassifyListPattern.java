package com.sec.myonlinefm.classificationprogram.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/4/13.
 */

public class RequestProgramClassifyListPattern implements Serializable {
    private static volatile RequestProgramClassifyListPattern mInstance = null;
    private static List<RequestProgramClassify> requestProgramClassifyList;

    private RequestProgramClassifyListPattern() {
        // It shouldn't delete this constructor for the factory method(Singleton)
    }

    public static RequestProgramClassifyListPattern getInstance() {
        if(mInstance == null) {
            synchronized (RequestProgramClassifyListPattern.class) {
                if (mInstance == null) {
                    mInstance = new RequestProgramClassifyListPattern();
                }
            }
        }
        return mInstance;
    }

    public void setRequestProgramClassifyList(List<RequestProgramClassify> requestProgramClassify_List) {
        requestProgramClassifyList = requestProgramClassify_List;
    }

    public List<RequestProgramClassify> getRequestProgramClassifyList() {
        return requestProgramClassifyList;
    }
}
