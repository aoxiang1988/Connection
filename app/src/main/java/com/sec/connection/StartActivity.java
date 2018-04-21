package com.sec.connection;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sec.connection.data.MediaUtile;
import com.sec.connection.widget.RemoteViewService;

public class StartActivity extends Activity implements ServiceConnection {

	ProgressBar progressBar;
	Handler mHandler ;
	ImageView imageView;
	Thread t;

	@Override
	protected void onCreate(Bundle savedInstanceState)  {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		if (Build.VERSION.SDK_INT >= 23) {
			if (!Settings.canDrawOverlays(this)) {
				Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivityForResult(intent, 1);
			}
		}
		progressBar = (ProgressBar)findViewById(R.id.progressBar1);
		imageView = (ImageView)findViewById(R.id.imageView2);

		imageView.setBackgroundResource(R.drawable.start);
		AnimationDrawable animationDrawable = (AnimationDrawable)imageView.getBackground();
		animationDrawable.start();

		mHandler = new Handler(){
			private int progress = 0;

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what == 1){
					Toast.makeText(getBaseContext(),"no music",Toast.LENGTH_LONG).show();
					updateFace();
				}
			}
		};
		if(ActivityCompat.checkSelfPermission(getApplicationContext(),
				Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission(getApplicationContext(),
				Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(StartActivity.this,
					new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
							Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
		} else {
			t = new Thread(new Runnable() {
				@Override
				public void run() {
					MusicApplication.list = MediaUtile.getAudioList(getApplicationContext());
					startMainService();
					bindMainService();
					Log.d("bin1111.yang", "MusicApplication.list " + MusicApplication.list.size());
					if(!MusicApplication.list.isEmpty()) {
						Intent intent = new Intent();
						intent.setClass(getBaseContext(), MainActivity.class);
						startActivity(intent);
						finish();
					} else {
						mHandler.sendEmptyMessage(1);
					}
				}
			});
			t.start();
		}
	}

	private void updateFace() {
		progressBar.setVisibility(View.GONE);
		imageView.setVisibility(View.GONE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if(t.isAlive())
			t.interrupt();
	}


	public void startMainService() {
		// TODO Auto-generated method stub
		Intent i = new Intent(this,MainService.class);
		startService(i);
	}
	public void startRemoteViewService() {
		// TODO Auto-generated method stub
		Intent i = new Intent(this,RemoteViewService.class);
		startService(i);
	}
	public void stopMainService() {
		// TODO Auto-generated method stub
		Intent i = new Intent(this,MainService.class);
		stopService(i);
	}
	private void bindMainService() {
		// TODO Auto-generated method stub
		Intent i = new Intent(this,MainService.class);
		this.bindService(i, this, Service.BIND_AUTO_CREATE);
	}
	public void unbindservice(){
		this.unbindService(this);
	}
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		// TODO Auto-generated method stub
		if(service instanceof MainService.ServiceBinder){
			MainService.ServiceBinder binder = (MainService.ServiceBinder)service;
			binder.getService();
		}
	}
	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "onServiceDisconnected name=" + name, Toast.LENGTH_LONG).show();
	}
}
