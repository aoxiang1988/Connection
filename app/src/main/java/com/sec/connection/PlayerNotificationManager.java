package com.sec.connection;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.RemoteViews;

import com.sec.connection.data.Audio;

public class PlayerNotificationManager {

	private static String TAG = "PlayerNotificationManager";
	public static final String NOTIFY_CHANNEL_ID = "channel_1";
	public static final String NOTIFY_CHANNEL_NAME = "channel_name_1";
	private Context mContext = null;
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

	public void removenotification(Context context){
		mContext = context.getApplicationContext();
		((NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(0);
		isNotify = false;
	}

	public void initialize(Context context) {
		// TODO Auto-generated method stub
		mContext = context.getApplicationContext();
		NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		views = new RemoteViews(mContext.getPackageName(), R.layout.notification_bar);
		Intent intent = new Intent(mContext, MainActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
		stackBuilder.addNextIntent(intent);
		PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);

		assert notificationManager != null;
		if (android.os.Build.VERSION.SDK_INT >= 26) {
			NotificationChannel mNotificationChannel = new NotificationChannel(NOTIFY_CHANNEL_ID, NOTIFY_CHANNEL_NAME,
					NotificationManager.IMPORTANCE_HIGH);
			notificationManager.createNotificationChannel(mNotificationChannel);
			mNotification = new Notification.Builder(mContext, NOTIFY_CHANNEL_ID).
					setOnlyAlertOnce(true).
					setSmallIcon(R.mipmap.ic_launcher_round).
					setContentIntent(pendingIntent).
					setOngoing(false).
					setCustomBigContentView(views).
					setOnlyAlertOnce(true).
					build();
		} else {
			mNotification = new Notification.Builder(mContext).
					setSmallIcon(R.drawable.music_library_add_playlist_now_play).
					setContentIntent(pendingIntent).
					build();
			mNotification.bigContentView = views;
		}
		notificationManager.notify(0, mNotification);
	}
	public void shownotification(Audio audio) {
		// TODO Auto-generated method stub
		isNotify = true;
		updatecurrentname(audio);

		mNotification.flags = Notification.FLAG_NO_CLEAR;
		views.setOnClickPendingIntent(R.id.ic_media_next,
				PendingIntent.getBroadcast(mContext, 1, new Intent(MainService.NOTIFY_NEXT), 0));
		views.setOnClickPendingIntent(R.id.ic_media_previous,
				PendingIntent.getBroadcast(mContext, 1, new Intent(MainService.NOTIFY_PRE), 0));
		views.setOnClickPendingIntent(R.id.finish,
				PendingIntent.getBroadcast(mContext, 1, new Intent(MainService.NOTIFY_REMOVE), 0));//?no action need check
		assert ((NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE)) != null;
		((NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE)).notify(0, mNotification);
	}
	public void updatecontureUI(){
		if(!MainService.isPlay){
			views.setOnClickPendingIntent(R.id.ic_media_play,
					PendingIntent.getBroadcast(mContext, 1, new Intent(MainService.NOTIFY_PLAY), 0));
			views.setViewVisibility(R.id.ic_media_stop, View.GONE);
			views.setViewVisibility(R.id.ic_media_play, View.VISIBLE);
		} else {
			views.setOnClickPendingIntent(R.id.ic_media_stop,
					PendingIntent.getBroadcast(mContext, 1, new Intent(MainService.NOTIFY_STOP), 0));
			views.setViewVisibility(R.id.ic_media_play, View.GONE);
			views.setViewVisibility(R.id.ic_media_stop, View.VISIBLE);
		}
		((NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE)).notify(0, mNotification);
	}
//	public void updatecurrenttime(int time){
//		if(isNotify){
//			int min = (time/1000)/60;
//			int sec = (time/1000)%60;
//			views.setTextViewText(R.id.notif_current_time,text(min)+":"+text(sec));
//			((NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE)).notify(0, mNotification);
//		}
//	}
	public void updatecurrentname(Audio audio){
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
			((NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE)).notify(0, mNotification);
		}
		updatecontureUI();
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
