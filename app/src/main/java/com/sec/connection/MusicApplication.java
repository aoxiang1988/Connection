package com.sec.connection;

import android.app.Application;

import com.sec.connection.data.Audio;
import com.sec.connecttoapilibrary.ConnectMainManager;

import java.util.List;

public class MusicApplication extends Application {
	public static String PREF_NAME = "CURRENT_MUSIC"; // .xml
	private ConnectMainManager mMainManager;
	private BaseListInfo mInfo;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		mMainManager = new ConnectMainManager(getApplicationContext());
		PlayerNotificationManager mNotifyManger = PlayerNotificationManager.instance();
		mInfo = new BaseListInfo();
		mNotifyManger.initialize(this);
		super.onCreate();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		mMainManager = null;
		mInfo = null;
	}
}
