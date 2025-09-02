package com.sec.connection;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StartActivity extends AppCompatActivity {

	private static final String TAG = "StartActivity";
	private static final int MY_PERMISSIONS_REQUEST_PERMISSION = 101;
	ProgressBar progressBar;
	Handler mHandler ;
	ImageView imageView;

	private static final int MSG_CHECK_MUSIC = 0;
	private static final int MSG_NO_MUSIC = 1;
	private static final int MSG_START_MAIN_ACTIVITY = 2;
	private static final long DELAY_TIME = 2000;

	@SuppressLint("SuspiciousIndentation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		progressBar = findViewById(R.id.progressBar1);
		imageView = findViewById(R.id.imageView2);

		imageView.setBackgroundResource(R.drawable.start);
		AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
		animationDrawable.start();

		mHandler = new Handler(msg -> {
			switch (msg.what) {
				case MSG_CHECK_MUSIC:
					if (!BaseListInfo.getInstance().getList().isEmpty()) {
						Log.d(TAG, "has music");
						mHandler.postDelayed(() -> {
							Intent intent = new Intent();
							intent.setClass(getBaseContext(), MainActivity.class);
							startActivity(intent);
							finish();
						}, DELAY_TIME);
					} else {
						Log.d(TAG, "no music");
						mHandler.sendEmptyMessage(MSG_NO_MUSIC);
					}
					break;
				case MSG_NO_MUSIC:
					Toast.makeText(getBaseContext(), "no music", Toast.LENGTH_LONG).show();
					updateFace();
					return true;
				case MSG_START_MAIN_ACTIVITY:
					Intent intent = new Intent();
					intent.setClass(getBaseContext(), MainActivity.class);
					startActivity(intent);
					finish();
					return true;
			}
			return false;
		});

		String[] permissions = getStrings();

		boolean needRequestPermissions = false;

		for (String permission : permissions) {
			Log.d(TAG, "检查" + permission + "权限，in before ");
			if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
				Log.d(TAG, "没有" + permission + "权限，正在申请权限 in before");
				needRequestPermissions = true;
			} else {
				Log.d(TAG, "已经有" + permission + "权限， in before");
			}
		}

		if (needRequestPermissions) {
			Log.d(TAG, "needRequestPermissions " + needRequestPermissions);
			ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST_PERMISSION);
		} else {
			ExecutorService executorService = Executors.newSingleThreadExecutor();
			executorService.submit(() -> {
				BaseListInfo.getInstance().setList(MediaUtils.getAudioList(getApplicationContext()));
				startMainService();
				mHandler.sendEmptyMessageDelayed(MSG_CHECK_MUSIC, DELAY_TIME);
			});
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == MY_PERMISSIONS_REQUEST_PERMISSION) {
			boolean allPermissionsGranted = true;
			for (int result : grantResults) {
				if (result != PackageManager.PERMISSION_GRANTED) {
					allPermissionsGranted = false;
					break;
				}
			}
			if (allPermissionsGranted) {
				ExecutorService executorService = Executors.newSingleThreadExecutor();
				executorService.submit(() -> {
					BaseListInfo.getInstance().setList(MediaUtils.getAudioList(getApplicationContext()));
					startMainService();
					mHandler.sendEmptyMessageDelayed(MSG_CHECK_MUSIC, DELAY_TIME);
				});
			} else {
				// Handle the case where some permissions are denied
				Toast.makeText(this, "Some permissions are denied", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacksAndMessages(null);
	}


	private void updateFace() {
		progressBar.setVisibility(View.GONE);
		imageView.setVisibility(View.GONE);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	public void startMainService() {
		// TODO Auto-generated method stub
		Intent i = new Intent(this,MainService.class);
		startService(i);
	}

	private static String[] getStrings() {
		String[] permission = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			permission = new String[]{
					Manifest.permission.READ_MEDIA_AUDIO,
					Manifest.permission.READ_MEDIA_VIDEO,
					Manifest.permission.RECORD_AUDIO,
					Manifest.permission.POST_NOTIFICATIONS,
			};
		} else {
			permission = new String[]{
					Manifest.permission.WRITE_EXTERNAL_STORAGE,
					Manifest.permission.READ_EXTERNAL_STORAGE,
			};
		}
		return permission;
	}
}
