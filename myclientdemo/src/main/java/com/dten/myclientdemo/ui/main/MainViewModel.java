package com.dten.myclientdemo.ui.main;

import android.os.RemoteException;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.dten.myservicedemo.IMyService;

public class MainViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private static final String TAG = "MainViewModel";
    private String mSendText;
    private String mGetFromServiceText;



    public void setSendText(String mSendText) {
        this.mSendText = mSendText;
        Log.d(TAG, "name : " + mSendText);
    }

    public void sendTextToService(IMyService iMyService) {
        try {
            iMyService.setName(mSendText);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public String getGetFromServiceText(IMyService iMyService) {
        try {
            mGetFromServiceText = iMyService.getName();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        return mGetFromServiceText;
    }
}