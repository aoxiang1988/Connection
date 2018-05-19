package com.sec.myonlinefm;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.sec.myonlinefm.OnLineFMPlayerListener.OberverOnLinePlayerManager;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/3/1.
 * 30s FM signal (by FM parameter data) checker...
 * when device use different, need check which search parameter is good for DNS function
 */

public class IntervalListeningClass {

    private static IntervalListeningClass mThisClass;
    private long[] mCurrentRSSI;
    private long[] mCurrentSNR;
    private String TAG = "IntervalListeningClass";
//    private int mRSSIth;
//    private int mSNRth;
    private Handler mHandler;
    private OnLineFMConnectManager mPlayer;
    private Context mContext;
    private int index = 0;
    private boolean isRunning = false;
    private int mCheck_By_Parameter = 1; //Rssi : 1, Snr : 2

    public static IntervalListeningClass getIntense() {
        return mThisClass;
    }

    public IntervalListeningClass(Context context) {
        mThisClass = this;
        mContext = context;
        mHandler = new Handler();
        mPlayer = OnLineFMConnectManager.Companion.getMMainInfoCode();
//        if (FMRadioFeature.CHIP_VENDOR.equals(FMRadioFeature.CHIP_QCOM)){
//            mCheck_By_Parameter = 2;
//        } else if(FMRadioFeature.CHIP_VENDOR.equals(FMRadioFeature.CHIP_RICHWAVE)) {
//            mCheck_By_Parameter = 1;
//        }
        index = 0;
        mCurrentRSSI = new long[50];
        mCurrentSNR = new long[50];
    }
    public void startListener(){
        Log.d(TAG, "startListener");
        isRunning = true;
        index = 0;
        mHandler.postDelayed(runnable, 500);
    }

    public void stopListener(){
        Log.d(TAG, "stopListener");
        mHandler.removeCallbacks(runnable);
        isRunning = false;
    }

    public boolean isListenerRunning(){
        return isRunning;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
//            mCurrentRSSI[index++] = mPlayer.getCurrentRssi();
//            Log.d(TAG,"Rssi : "+mPlayer.getCurrentRssi());
//            mCurrentSNR[index++] = mPlayer.getCurrentSnr();
            if(index == 50) { // get data..
                double fSumSNR = 0;
                double fSumRSSI = 0;
                for (long aMCurrentSNR : mCurrentSNR) fSumSNR += aMCurrentSNR * aMCurrentSNR;
                for (long aMCurrentRSSI : mCurrentRSSI) fSumRSSI += aMCurrentRSSI * aMCurrentRSSI;
                fSumSNR = Math.sqrt(fSumSNR/mCurrentSNR.length);
                fSumRSSI = Math.sqrt(fSumRSSI/mCurrentSNR.length);
                if(mCheck_By_Parameter == 2) {
                    if (fSumSNR < 6) {
                        OberverOnLinePlayerManager.getInstance().notifyObserverDNS(true);
                        Log.d(TAG, "bed : " + String.format("%.2s", fSumSNR));
                    } else
                        Log.d(TAG, "good : " + String.format("%.2s", fSumSNR));
                } else if(mCheck_By_Parameter == 1) {
                    if (fSumRSSI < 20) {
                        OberverOnLinePlayerManager.getInstance().notifyObserverDNS(true);
                        Log.d(TAG, "bed : " + String.format("%.2s", fSumRSSI));
                    } else
                        Log.d(TAG, "good : " + String.format("%.2s", fSumRSSI));
                }
                index = 0;
            }
            mHandler.postDelayed(this, 500);
        }
    };
}
