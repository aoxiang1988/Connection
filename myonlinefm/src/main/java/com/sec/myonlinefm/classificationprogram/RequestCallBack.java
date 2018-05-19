package com.sec.myonlinefm.classificationprogram;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/4/13.
 */

public interface RequestCallBack<T> {
    void onSuccess(T val);
    void onFail(String errorMessage);
}
