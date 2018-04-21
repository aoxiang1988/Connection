package com.sec.connection.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.sec.connection.BaseListInfo;
import com.sec.connection.R;
import com.sec.connection.StartActivity;
import com.sec.connection.data.Audio;
import com.sec.connection.MainService;
import com.sec.connection.MusicApplication;

import java.util.List;

/*****http://johnsonxu.iteye.com/blog/1763042******/
public class PlayWidget extends AppWidgetProvider {
	public static final String PLAY_STATUE = "com.example.action.PLAY_STATUE";
	public static final String COLLECTION_VIEW_ACTION = "com.example.play.COLLECTION_VIEW_ACTION";
	public static final String UPDATE_ACTION = "com.example.action.UPDATE_ACTION";
	private RemoteViews widget_view;
	private Bitmap bitmap = null;
	private int listitem = 0;
	private String TAG = "PlayWidget";
	List<Audio> list = BaseListInfo.getInstance().getList();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub	
		AppWidgetManager appWidgetManger = AppWidgetManager.getInstance(context);  
		int[] appIds = appWidgetManger.getAppWidgetIds(new ComponentName(context, PlayWidget.class));
		listitem = intent.getIntExtra("current_music", 0);
		Log.d(TAG,"listitem : "+listitem);
		String action = intent.getAction();
		if(action.equals(PLAY_STATUE)){
			intent.getBooleanExtra("isplay", false);

			onUpdate(context,appWidgetManger,appIds);
		}
		if(action.equals(COLLECTION_VIEW_ACTION)) {
			int item = intent.getIntExtra("widget_item", 0);
			Log.d(TAG,"listitem : "+item);
		}
		if(action.equals(UPDATE_ACTION)){
//			listitem = intent.getIntExtra("current_music", 0);
			onUpdate(context,appWidgetManger,appIds);
		}
		
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		Intent intent_mainactivity = new Intent(context, StartActivity.class);
		PendingIntent pendingintent_mainactivity = PendingIntent.getActivity(context, 0, intent_mainactivity, 0);
		
		ComponentName componentName = new ComponentName(context, PlayWidget.class);
		widget_view = new RemoteViews(context.getPackageName(), R.layout.widget);
		widget_view.setOnClickPendingIntent(R.id.widget_imageView, pendingintent_mainactivity);
		
		
		if(!MainService.isPlay){
			widget_view.setOnClickPendingIntent(R.id.ic_media_play, 
					PendingIntent.getBroadcast(context, 1, new Intent(MainService.NOTIFY_PLAY), 0));
			widget_view.setViewVisibility(R.id.ic_media_stop, View.GONE);
			widget_view.setViewVisibility(R.id.ic_media_play, View.VISIBLE);
		} else {
			widget_view.setOnClickPendingIntent(R.id.ic_media_stop, 
					PendingIntent.getBroadcast(context, 1, new Intent(MainService.NOTIFY_STOP), 0));
			widget_view.setViewVisibility(R.id.ic_media_play, View.GONE);
			widget_view.setViewVisibility(R.id.ic_media_stop, View.VISIBLE);
		}
		
		widget_view.setOnClickPendingIntent(R.id.ic_media_next, 
				PendingIntent.getBroadcast(context, 1, new Intent(MainService.NOTIFY_NEXT), 0));
		widget_view.setOnClickPendingIntent(R.id.ic_media_previous, 
				PendingIntent.getBroadcast(context, 1, new Intent(MainService.NOTIFY_PRE), 0));

		Intent intent = new Intent(context, RemoteViewService.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds);
		widget_view.setRemoteAdapter(R.id.widget_listview, intent);

		intent.setAction(COLLECTION_VIEW_ACTION);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 设置intent模板
        widget_view.setPendingIntentTemplate(R.id.widget_listview, pendingIntent);

		update(list.get(listitem));

		appWidgetManager.updateAppWidget(componentName, widget_view);
		
	}

	public void update(Audio audio){
		bitmap = audio.getBitmap();
		if(bitmap != null){
			widget_view.setImageViewBitmap(R.id.widget_imageView, bitmap);
		} else {
			widget_view.setImageViewResource(R.id.widget_imageView, R.drawable.defult);
		}
		int min = ((audio.getDuration())/1000)/60;
		int sec = ((audio.getDuration())/1000)%60;
		widget_view.setTextViewText(R.id.widget_musicname, audio.getTitle());
		widget_view.setTextViewText(R.id.widget_artist, audio.getArtist());
		widget_view.setTextViewText(R.id.widget_all_time, text(min)+":"+text(sec));
	}
	@Override
	public void onAppWidgetOptionsChanged(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId,
			Bundle newOptions) {
		// TODO Auto-generated method stub
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,
				newOptions);
	}
	private String text(int set_time){
		String get_time = null;
		if(set_time<10){
			get_time = String.format("0%s",set_time);
		} else {
			get_time = String.format("%s",set_time);
		}
		if(set_time<10){
			get_time = String.format("0%s",set_time);
		} else {
			get_time = String.format("%s",set_time);
		}
		return get_time;
	}

}
