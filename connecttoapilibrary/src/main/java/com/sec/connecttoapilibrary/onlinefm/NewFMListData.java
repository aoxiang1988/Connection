package com.sec.connecttoapilibrary.onlinefm;

import android.content.Context;

import com.sec.connecttoapilibrary.onlinefm.liveRadioData.Station;
import com.sec.connecttoapilibrary.onlinefm.liveRadioData.StationProgram;
import com.sec.connecttoapilibrary.onlinefm.defultonlineinfolistener.ObserverDefaultLiveRadioListener;
import com.sec.connecttoapilibrary.onlinefm.defultonlineinfolistener.ObserverDefaultLiveRadioDefaultLiveRadioListenerManager;
import com.sec.connecttoapilibrary.onlinefm.updataLiveRadioUIListener.ObserverLiveRadioLiveRadioUIListenerManager;

import java.util.List;
import java.util.Map;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/31.
 */

public class NewFMListData implements ObserverDefaultLiveRadioListener {

    private Context mContext = null;
    private List<Station> mStations = null;
    private Map<Integer, List<StationProgram>> map = null;
    private List<Station> mCenterStations;
    private Map<Integer, List<StationProgram>> centermap;
    private FMListData mFMlistData;

    public NewFMListData(Context context){
        mContext = context;
        ObserverDefaultLiveRadioDefaultLiveRadioListenerManager.getInstance().add(this);
        mFMlistData = new FMListData(mContext);
    }

    @Override
    public void observerUpDataLocalList(List<Station> mStations, Map<Integer, List<StationProgram>> map) {
        this.mStations = mStations;
        this.map = map;
        doUpdateUILocalList();
    }

    @Override
    public void observerUpDataCenterList(List<Station> mCenterStations, Map<Integer, List<StationProgram>> center_map) {
        this.mCenterStations = mCenterStations;
        this.centermap = center_map;
        doUpdateUICenterList();
    }

    @Override
    public void observerUpDataDifferentInfo(List<Station> mStations, Map<Integer, List<StationProgram>> map) {
        mFMlistData.addNetInfo(mStations,map);
        ObserverLiveRadioLiveRadioUIListenerManager.getInstance().notifyDifferentInfoObserver(mStations, map);
    }

    public void doUpdateUILocalList() {
        mFMlistData.makeLocationList(FMListData.TYPE_LOCAL_LIST, mStations, map);
    }
    public void doUpdateUICenterList() {
        mFMlistData.makeLocationList(FMListData.TYPE_CENTER_LIST, mStations, map);
    }
}
