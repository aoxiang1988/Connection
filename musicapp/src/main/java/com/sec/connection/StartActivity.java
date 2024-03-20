package com.sec.connection;

import android.Manifest;
import android.app.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sec.connection.data.MediaUtile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StartActivity extends Activity {

	private static final String TAG = "StartActivity";
	private static final int MY_PERMISSIONS_REQUEST_PERMISSION = 101;
	ProgressBar progressBar;
	Handler mHandler ;
	ImageView imageView;
	Thread t;

	Message mStartMainActivity = new Message();

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
		progressBar = findViewById(R.id.progressBar1);
		imageView = findViewById(R.id.imageView2);

		imageView.setBackgroundResource(R.drawable.start);
		AnimationDrawable animationDrawable = (AnimationDrawable)imageView.getBackground();
		animationDrawable.start();

		mHandler = new Handler(msg -> {
			if(msg.what == 1){
				Toast.makeText(getBaseContext(),"no music",Toast.LENGTH_LONG).show();
				updateFace();
				return true;
			}
			if (msg.what == 2) {
				Intent intent = new Intent();
				intent.setClass(getBaseContext(), MainActivity.class);
				startActivity(intent);
				finish();
				return true;
			}
			return false;
		});
		String[] permession = {
				Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.READ_EXTERNAL_STORAGE,
		};

		boolean needRequestPermissions = false;
		List<String> stringList = new ArrayList<>(Arrays.asList(permession));

		for (int i = 0; i < permession.length; i++) {
			Log.d(TAG, "检查" + permession[i] + "权限，in before ");
			if (ContextCompat.checkSelfPermission(this,
					permession[i])
					!= PackageManager.PERMISSION_GRANTED) {
				Log.d(TAG, "没有" + permession[i] + "权限，正在申请权限 in before");
				needRequestPermissions = true;
			} else {
				Log.d(TAG, "已经有" + permession[i] + "权限， in before");
				stringList.remove(permession[i]);//不用申请这个权限，移除掉
			}
		}
		if (needRequestPermissions) {
			String[] needToRequestPermission = new String[stringList.size()];
			ActivityCompat.requestPermissions(this,
					stringList.toArray(needToRequestPermission),
					MY_PERMISSIONS_REQUEST_PERMISSION);
		} else {
			t = new Thread(() -> {
				BaseListInfo.getInstance().setList(MediaUtile.getAudioList(getApplicationContext()));
				startMainService();
				if(!BaseListInfo.getInstance().getList().isEmpty()) {
					mStartMainActivity.what = 2;
					mHandler.sendMessageDelayed(mStartMainActivity, 2000);
				} else {
					mHandler.sendEmptyMessage(1);
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void startMainService() {
		// TODO Auto-generated method stub
		Intent i = new Intent(this,MainService.class);
		startService(i);
	}
}
