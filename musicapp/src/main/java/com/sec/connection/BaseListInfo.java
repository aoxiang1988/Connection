package com.sec.connection;

import com.sec.connection.data.Audio;

import java.util.List;

/**
 * Created by Administrator on 2018/4/21.
 */

public class BaseListInfo {

    private List<Audio> list;
    private static BaseListInfo mBaseListInfo;

    public BaseListInfo() {
        mBaseListInfo = this;
    }

    public static BaseListInfo getInstance() {
        return mBaseListInfo;
    }

    public void setList(List<Audio> list) {
        this.list = list;
    }

    public List<Audio> getList() {
        return list;
    }
}
