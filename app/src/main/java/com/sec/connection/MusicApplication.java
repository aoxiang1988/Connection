package com.sec.connection;

import android.app.Application;

import com.sec.connection.data.Audio;
import com.sec.connecttoapilibrary.ConnectMainManager;

import java.util.List;

public class MusicApplication extends Application {
	public static List<Audio> list ;
	public static String PREF_NAME = "CURRENT_MUSIC"; // .xml
	private ConnectMainManager mMainManager;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		mMainManager = new ConnectMainManager(getApplicationContext());
		PlayerNotificationManager mNotifyManger = PlayerNotificationManager.instance();
		mNotifyManger.initialize(this);
		super.onCreate();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		mMainManager = null;
	}
}
