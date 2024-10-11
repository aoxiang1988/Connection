package com.sec.connection;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.sec.connection.data.Audio;

public class PlayerNotificationManager {

	private static final String TAG = "PlayerNotificationManager";
	public static final String NOTIFY_CHANNEL_ID = "channel_1";
	public static final String NOTIFY_CHANNEL_NAME = "channel_name_1";
	private Context mContext = null;
	@SuppressLint("StaticFieldLeak")
	private static PlayerNotificationManager mInstance = null;
	private Notification mNotification;
	private RemoteViews views ;
	private boolean isNotify = false;

	public static PlayerNotificationManager instance(){
		if(mInstance == null) {
			mInstance = new PlayerNotificationManager();
		}
		return mInstance;
	}

	public void reMoveNotification(Context context){
		mContext = context.getApplicationContext();
		((NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(0);
		isNotify = false;
	}

	private static NotificationManager mNotificationManager = null;
	@SuppressLint("LongLogTag")
	public void initialize(Context context) {
		// TODO Auto-generated method stub
		mContext = context.getApplicationContext();
		mNotificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		views = new RemoteViews(mContext.getPackageName(), R.layout.notification_bar);
		Intent intent = new Intent(mContext, MainActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
		stackBuilder.addNextIntent(intent);
		PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_MUTABLE);

		assert mNotificationManager != null;
		Notification.Builder builder = null;
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			Log.d(TAG, "build notification");
			NotificationChannel mNotificationChannel = new NotificationChannel(NOTIFY_CHANNEL_ID, NOTIFY_CHANNEL_NAME,
					NotificationManager.IMPORTANCE_HIGH);
			mNotificationChannel.setSound(null, null);
			mNotificationManager.createNotificationChannel(mNotificationChannel);
			builder = new Notification.Builder(mContext, NOTIFY_CHANNEL_ID).
					setOnlyAlertOnce(true).
					setSmallIcon(R.mipmap.ic_launcher_round).
					setContentIntent(pendingIntent).
					setOngoing(false).
					setCustomBigContentView(views);
		} else {
			Log.d(TAG, "build notification2");
			builder = new Notification.Builder(mContext).
					setSmallIcon(R.drawable.music_library_add_playlist_now_play).
					setContentIntent(pendingIntent);
			mNotification.bigContentView = views;
		}
		mNotification = builder.build();
		mNotificationManager.notify(0, mNotification);
	}
	public void showNotification(Audio audio) {
		// TODO Auto-generated method stub
		isNotify = true;
		upDateCurrentName(audio);

		mNotification.flags = Notification.FLAG_NO_CLEAR;
		views.setOnClickPendingIntent(R.id.ic_media_next,
				PendingIntent.getBroadcast(mContext, 1, new Intent(MainService.NOTIFY_NEXT), PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_NO_CREATE));
		views.setOnClickPendingIntent(R.id.ic_media_previous,
				PendingIntent.getBroadcast(mContext, 1, new Intent(MainService.NOTIFY_PRE), PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_NO_CREATE));
		views.setOnClickPendingIntent(R.id.finish,
				PendingIntent.getBroadcast(mContext, 1, new Intent(MainService.NOTIFY_REMOVE), PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_NO_CREATE));//?no action need check
		assert mNotificationManager != null;
		mNotificationManager.notify(0, mNotification);
	}
	public void upDateControlUI(){
		if(!MainService.isPlay){
			views.setOnClickPendingIntent(R.id.ic_media_play,
					PendingIntent.getBroadcast(mContext, 1, new Intent(MainService.NOTIFY_PLAY), PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_NO_CREATE));
			views.setViewVisibility(R.id.ic_media_stop, View.GONE);
			views.setViewVisibility(R.id.ic_media_play, View.VISIBLE);
		} else {
			views.setOnClickPendingIntent(R.id.ic_media_stop,
					PendingIntent.getBroadcast(mContext, 1, new Intent(MainService.NOTIFY_STOP), PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_NO_CREATE));
			views.setViewVisibility(R.id.ic_media_play, View.GONE);
			views.setViewVisibility(R.id.ic_media_stop, View.VISIBLE);
		}
		assert mNotificationManager != null;
		mNotificationManager.notify(0, mNotification);
	}
//	public void updatecurrenttime(int time){
//		if(isNotify){
//			int min = (time/1000)/60;
//			int sec = (time/1000)%60;
//			views.setTextViewText(R.id.notif_current_time,text(min)+":"+text(sec));
//			((NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE)).notify(0, mNotification);
//		}
//	}
	public void upDateCurrentName(Audio audio){
		if(isNotify){
			int min = ((audio.getDuration())/1000)/60;
			int sec = ((audio.getDuration())/1000)%60;
			views.setTextViewText(R.id.notif_all_time, String.format("%s:%s",text(min),text(sec)));
			views.setTextViewText(R.id.notif_musicname, audio.getTitle());
			views.setTextViewText(R.id.notif_artist, audio.getArtist());
			Bitmap bitmap = audio.getBitmap();
			if(bitmap != null){
				views.setImageViewBitmap(R.id.notif_imageView, bitmap);
			} else {
				views.setImageViewResource(R.id.notif_imageView, R.drawable.defult);
			}
			assert mNotificationManager != null;
			mNotificationManager.notify(0, mNotification);
		}
		upDateControlUI();
	}
	private String text(int set_time){
		String get_time;
		if(set_time<10){
			get_time = String.format("0%s",set_time);
		} else {
			get_time = String.format("%s",set_time);
		}
		return get_time;
	}
	
}
