package com.sec.connecttoapilibrary;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/4/10.
 */

public interface RequestCallBack<T> {
    public void onSuccess(T val);
    public void onFail(String errorMessage);
}
