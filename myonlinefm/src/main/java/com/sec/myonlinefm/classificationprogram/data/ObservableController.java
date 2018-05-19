package com.sec.myonlinefm.classificationprogram.data;

import android.util.Log;

import java.util.Observable;

/**
 * Created by gaolin on 2018/4/20.
 */

public class ObservableController extends Observable {
    private static ObservableController mObservable = null;
    public static final int REFRESH     = 1;
    public static final int UPDATETITLE = 2;
    public static final int UPDATETHUMB = 3;
    public static final int UPDATEHEADER= 4;

    private ObservableController(){
        Log.d("gaolin", "ObservableController creator !!");
    }

    public static ObservableController getInstance() {
        if (mObservable == null) {
            mObservable = new ObservableController();
        }
        return mObservable;
    }

    @Override
    public void notifyObservers(Object arg) {
        setChanged();
        super.notifyObservers(arg);
    }
}
