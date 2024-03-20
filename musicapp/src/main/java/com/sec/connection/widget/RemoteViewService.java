package com.sec.connection.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class RemoteViewService extends RemoteViewsService {
	MyRemoteViewsFactory factory;
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		// TODO Auto-generated method stub
		factory = new MyRemoteViewsFactory(this.getApplicationContext(), intent);
		return factory;
	}

	public long getpostion(int pos){
		return factory.getItemId(pos);
	}
}
