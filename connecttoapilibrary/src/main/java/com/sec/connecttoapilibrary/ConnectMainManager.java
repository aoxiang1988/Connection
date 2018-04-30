package com.sec.connecttoapilibrary;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/4/10.
 */
import android.content.Context;
import android.util.Log;

import com.sec.connecttoapilibrary.qtapitest.FMListData;
import com.sec.connecttoapilibrary.qtapitest.MainOnLineInfoManager;
import com.sec.connecttoapilibrary.qtapitest.NewFMListData;
import com.sec.connecttoapilibrary.qtapitest.abstructObserver.AbsTractOnLineLiveRadioInfo;
import com.sec.connecttoapilibrary.qtapitest.liveRadioData.OnLineRadioPattern;
import com.sec.connecttoapilibrary.qtapitest.liveRadioData.Station;
import com.sec.connecttoapilibrary.qtapitest.liveRadioData.StationProgram;

import java.util.List;
import java.util.Map;

public class ConnectMainManager {

    private Context mContext;
    private static ConnectMainManager mMainManager = null;
    private MainOnLineInfoManager mConnectManager = null;
    private NewFMListData mFMListData = null;
    private RequestCallBack<OnLineRadioPattern> mCallBack;

    public ConnectMainManager(Context context) {
        mContext = context;
        mMainManager = this;
        mConnectManager = new MainOnLineInfoManager(context);
        mFMListData = new NewFMListData(context);
        UpdateLocalList mUpdateLocalList = new UpdateLocalList();
    }

    public static ConnectMainManager getInstance() {
        return mMainManager;
    }

    public void getLocalList(final RequestCallBack<OnLineRadioPattern> callBack) {
        mConnectManager.GetOnLineFM(FMListData.TYPE_LOCAL_LIST);
        mCallBack = callBack;
    }
    public void getCenterList(final RequestCallBack<OnLineRadioPattern> callBack){
        Log.d("bin1111.yang","getCenterList");
        mConnectManager.GetOnLineFM(FMListData.TYPE_CENTER_LIST);
        mCallBack = callBack;
    }
    public void getDifferentLocalList(int local_ID, final RequestCallBack callBack) {
        mConnectManager.getDifferentLocalOnlineInfo(local_ID);
    }
    public void getOneDayProgramList(Station station, int day_info, final RequestCallBack callBack) {
        mConnectManager.getOneDayProgram(station, day_info);
    }

    private void prepareOnLinePlay(StationProgram stationProgram, int play_type, final RequestCallBack callBack) {
        mConnectManager.getReplayUrl(stationProgram, play_type);
    }

    private int mCurrentPage = 1;

    public void changeToNextPage() {
        mConnectManager.setCurrentPage(mCurrentPage+1);
    }

    public void searchByUser(String keyword, String type, final RequestCallBack callBack) {
        mConnectManager.getSearchResult(keyword, type, mCurrentPage);
    }

    private class UpdateLocalList extends AbsTractOnLineLiveRadioInfo {
        @Override
        public void observerUIUpDataLocalList(List<Station> stations,
                                              Map<Integer, List<StationProgram>> map) {
            //TODO update local stations information
            if(stations.isEmpty() || map.isEmpty())
                mCallBack.onFail("empty");
            else {
                OnLineRadioPattern radioPattern = new OnLineRadioPattern();
                radioPattern.setCurrentStationList(stations);
                radioPattern.setCurrentStationProgramMap(map);
                mCallBack.onSuccess(radioPattern);
            }
        }

        @Override
        public void observerUIUpDataCenterList(List<Station> center_stations,
                                               Map<Integer, List<StationProgram>> center_map) {
            //TODO update local stations information
            Log.d("bin1111.yang","observerUIUpDataCenterList");
            if(center_stations.isEmpty() || center_map.isEmpty())
                mCallBack.onFail("empty");
            else {
                OnLineRadioPattern radioPattern = new OnLineRadioPattern();
                radioPattern.setCurrentStationList(center_stations);
                radioPattern.setCurrentStationProgramMap(center_map);
                mCallBack.onSuccess(radioPattern);
            }
        }

        @Override
        public void observerDifferentInfoUIUpData(List<Station> mStations,
                                                  Map<Integer, List<StationProgram>> map) {
            //TODO update different local stations information
            if(mStations.isEmpty() || map.isEmpty())
                mCallBack.onFail("empty");
            else {
                OnLineRadioPattern radioPattern = new OnLineRadioPattern();
                radioPattern.setCurrentStationList(mStations);
                radioPattern.setCurrentStationProgramMap(map);
                mCallBack.onSuccess(radioPattern);
            }
        }
    }
}
