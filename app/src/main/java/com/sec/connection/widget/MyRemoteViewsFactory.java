package com.sec.connection.widget;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.sec.connection.BaseListInfo;
import com.sec.connection.R;
import com.sec.connection.data.Audio;
import com.sec.connection.MainActivity;
import com.sec.connection.MainService;
import com.sec.connection.MusicApplication;

import java.util.List;

public class MyRemoteViewsFactory implements RemoteViewsFactory {

	Context mContext;
	List<Audio> list;
	Audio audio;
	public MyRemoteViewsFactory(Context context,Intent intent) {
		mContext = context;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		list = BaseListInfo.getInstance().getList();
	}

	@Override
	public void onDataSetChanged() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		list.clear();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public RemoteViews getViewAt(int position) {
		if (position < 0 || position >= list.size())  {
               return null;
		}
		
        // ????????????位????????View  
        final RemoteViews rv = new RemoteViews(mContext.getPackageName(),  
        		R.layout.widget_list_item);
        String title = list.get(position).getTitle();
        String artist = list.get(position).getArtist();
		// ??????????????  
        rv.setTextViewText(R.id.widget_title, title);  
        rv.setTextViewText(R.id.widget_artist, artist);  
        if(MainService.isPlay && position == MainActivity.getcurrentposition()){
        	rv.setTextColor(android.R.id.text1, mContext.getResources().getColor(R.color.playingcolor));
        }
        else{
        	rv.setTextColor(android.R.id.text1, mContext.getResources().getColor(R.color.widget_no_play ));
        }
        Intent fillintent = new Intent();
        fillintent.setAction(PlayWidget.COLLECTION_VIEW_ACTION);
        fillintent.putExtra("widget_list_item", position);
        Log.d("bin1111.yang", "widget_item : "+position);
        rv.setOnClickFillInIntent(R.layout.widget_list_item, fillintent);

        return rv;
	}

	@Override
	public RemoteViews getLoadingView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

}
