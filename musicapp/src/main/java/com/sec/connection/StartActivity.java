package com.sec.connection;

import android.Manifest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sec.connection.data.MediaUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StartActivity extends AppCompatActivity {

	private static final String TAG = "StartActivity";
	private static final int MY_PERMISSIONS_REQUEST_PERMISSION = 101;
	ProgressBar progressBar;
	Handler mHandler ;
	ImageView imageView;
	Thread t;

	Message mStartMainActivity = new Message();

	@SuppressLint("SuspiciousIndentation")
	@Override
	protected void onCreate(Bundle savedInstanceState)  {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		/*if (Build.VERSION.SDK_INT >= 23) {
			if (!Settings.canDrawOverlays(this)) {
				Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivityForResult(intent, 1);
			}
		}*/
		progressBar = findViewById(R.id.progressBar1);
		imageView = findViewById(R.id.imageView2);

		imageView.setBackgroundResource(R.drawable.start);
		AnimationDrawable animationDrawable = (AnimationDrawable)imageView.getBackground();
		animationDrawable.start();

		mHandler = new Handler(msg -> {
			if (msg.what == 0) {
				if(!BaseListInfo.getInstance().getList().isEmpty()) {
					Log.d(TAG, "has music");
					mStartMainActivity.what = 2;
					mHandler.sendMessageDelayed(mStartMainActivity, 2000);
				} else {
					Log.d(TAG, "no music");
					mHandler.sendEmptyMessage(1);
				}
			}
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
		String[] permission = {
				Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.READ_EXTERNAL_STORAGE,
		};

		boolean needRequestPermissions = false;
		List<String> stringList = new ArrayList<>(Arrays.asList(permission));

        for (String s : permission) {
            Log.d(TAG, "检查" + s + "权限，in before ");
            if (ContextCompat.checkSelfPermission(this,
                    s)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "没有" + s + "权限，正在申请权限 in before");
                needRequestPermissions = true;
            } else {
                Log.d(TAG, "已经有" + s + "权限， in before");
                stringList.remove(s);//不用申请这个权限，移除掉
            }
        }
		if (needRequestPermissions) {
			Log.d(TAG, "needRequestPermissions " + needRequestPermissions);
			String[] needToRequestPermission = new String[stringList.size()];
			ActivityCompat.requestPermissions(this,
					stringList.toArray(needToRequestPermission),
					MY_PERMISSIONS_REQUEST_PERMISSION);
		} else {
			t = new Thread(() -> {
				BaseListInfo.getInstance().setList(MediaUtils.getAudioList(getApplicationContext()));
				startMainService();
				mHandler.sendEmptyMessageDelayed(0, 2000);
			});
			t.start();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == MY_PERMISSIONS_REQUEST_PERMISSION) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Log.d(TAG, "has got permission of window manager");
				t = new Thread(() -> {
					BaseListInfo.getInstance().setList(MediaUtils.getAudioList(getApplicationContext()));
					startMainService();
					if(!BaseListInfo.getInstance().getList().isEmpty()) {
						mStartMainActivity.what = 2;
						mHandler.sendMessageDelayed(mStartMainActivity, 2000);
					} else {
						mHandler.sendEmptyMessage(1);
					}
				});
				t.start();
			} else {
				if (t != null && t.isAlive()) {
					t.interrupt();
				}
				Intent intent = new Intent(
						Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
						Uri.fromParts("package", getPackageName(), null)
				);
				startActivity(intent);
			}
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
