package com.sec.connection;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sec.connection.data.Audio;
import com.sec.connection.setting.FilterSettings;
import com.sec.connection.setting.MediaPlayerTest;
import com.sec.connection.setting.MusicInformationActivity;
import com.sec.connection.view.FlingView;
import com.sec.connection.view.LrcView;
import com.sec.connection.vpview.TestViewPagerActivity;
import com.sec.connection.xmlcheck.LocalInfo;
import com.sec.connection.xmlcheck.Program;
import com.sec.connection.xmlcheck.PullLocalInfoParser;

import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = "MainActivity";
	private static final String UPDATE_ACTION = "com.example.action.UPDATE_ACTION";
	private static final String UPDATE_LIST_ACTIVITY_ACTION = "com.example.action.UPDATE_LIST_ACTIVITY_ACTION";
	//	public static final String DELETE_ITEM = "com.example.action.DELETE_ITEM";
	private static final String PLAY_STATUE = "com.example.action.PLAY_STATUE";
	public static final String CTL_ACTION = "com.example.action.CTL_ACTION";
	private static final String MUSIC_CURRENT = "com.example.action.MUSIC_CURRENT";
	private static final String MUSIC_DURATION = "com.example.action.MUSIC_DURATION";
	private static final String CURRENT_ID = "com.example.action.CURRENT_ID";

	private static final int MY_PERMISSIONS_REQUEST_PERMISSION = 1;

	public static MainService mService = null;
	@SuppressLint("StaticFieldLeak")
	public static MainActivity _inActivity;
	public static LrcView mLrcView;

	private View mActionBar;
	private SeekBar mPlayBar;
	private Button mPreBut;
	private Button mNextBut;
	private Button mStartBut;
	private Button mStopBut;
	private Button mPauseBut;
	private ImageView mLrcOnOff;
	private Button mTestButton;
	private FloatingActionButton mFloatBut;
	private TextView mCurrentTimeView;
	private TextView mCurrentStatusView;
	private TextView mAllTimeView;
	private TextView mMusicNameView;
	private List<Audio> mList = null;
	private ListView mListView;
	private HomeReceiver mHomeReceiver;

//	private ImageView imageView;

	private FlingView mFlingView;
	private LinearLayout mFlingViewBack;
//	private Context mContext = null;

	public boolean isActivity = true; // 閿熸枻鎷烽敓鑺傝鎷烽敓鏂ゆ嫹
	public boolean isFirstTime = true;
	public boolean isPlaying; // 閿熸枻鎷烽敓鑺傝鎷烽敓鏂ゆ嫹
	public static int mListPosition = 0;   //閿熸枻鎷疯瘑閿熷彨鎲嬫嫹浣嶉敓鏂ゆ嫹


	private int mRepeatState = 3;        //寰敓鏂ゆ嫹閿熸枻鎷疯瘑
	private String mTitle;

	private int mCurrentTime;
	private long mCurrentMusicID;
	private int mDuration;
	private UserAdapter mUserAdapter;
	private PlayerNotificationManager mNotificationManager;

	private static final int SHOW_NOTIFICATION = 1;
	private static final int REMOVE_NOTIFICATION = 2;
	private static final int QUICK_RIGHT = 3;
	private static final int QUICK_LEFT = 4;
	private static final int PLAY_NEXT_VIEW = 5;

	private static final int MOVE_TO_SETTING_PERMISSION_VIEW = 10;


	private ActionMode mActionMode = null;
	boolean mIsActionMode = false;
	private RotateAnimation mRotateAnimation = null;

	private boolean isBind = false;

	ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MainService.ServiceBinder binder = (MainService.ServiceBinder) service;
			mService = binder.getService();
			isBind = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			isBind = false;
		}
	};

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(@NonNull Message msg) {
			switch (msg.what) {
				case MOVE_TO_SETTING_PERMISSION_VIEW:
					Intent intent = new Intent(
							Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
							Uri.fromParts("package", getPackageName(), null)
					);
					startActivity(intent);
					break;
				case PLAY_NEXT_VIEW:
					ItemPlay(mListPosition);
					break;
				case SHOW_NOTIFICATION:
					mNotificationManager.showNotification(mList.get(mListPosition));
					break;
				case REMOVE_NOTIFICATION:
					mNotificationManager.reMoveNotification(mService.getBaseContext());
					break;
				case QUICK_RIGHT:
					if (mCurrentTime < mList.get(mListPosition).getDuration()) {
						mCurrentTime = mCurrentTime + 5000;
						mService.progress(mCurrentTime);
					}
					if (mNextBut.isPressed()) {
						mHandler.sendMessageDelayed(Message.obtain(mHandler, QUICK_RIGHT), 200);
					} else {
						if (mListPosition + 1 == mList.size()) {
							mNextBut.setBackground(getResources().getDrawable(R.drawable.disable_next, null));
						} else {
							mNextBut.setBackground(getResources().getDrawable(R.drawable.next_button_ripple, null));
						}
					}
					break;
				case QUICK_LEFT:
					if (mCurrentTime >= 0) {
						mCurrentTime = mCurrentTime - 5000;
						mService.progress(mCurrentTime);
					}//has a problem : the left just get to 0, if still press it just at 0.....
					if (mPreBut.isPressed()) {
						mHandler.sendMessageDelayed(Message.obtain(mHandler, QUICK_LEFT), 200);
					} else {
						if (mListPosition == 0) {
							mPreBut.setBackground(getResources().getDrawable(R.drawable.disable_pre, null));
						} else {
							mPreBut.setBackground(getResources().getDrawable(R.drawable.previous_button_ripple, null));
						}
					}
					break;
			}
			return false;
		}
	});

	public static void initService(MainService service) {
		mService = service;
	}

	public static int getCurrentPosition() {
		return mListPosition;
	}


	Callback actionmode_callback;
	private final OnItemLongClickListener mLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
//			mListPosition = arg2;
			if (mActionMode == null) {
				mIsActionMode = true;
				mActionMode = startActionMode(actionmode_callback);
				mUserAdapter.notifyDataSetChanged();
			}
			return true;
		}
	};

	@SuppressLint({"NewApi", "UnspecifiedRegisterReceiverFlag"})
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG,"onCreate");
		bindService(
				new Intent(this, MainService.class), mServiceConnection,
				BIND_ALLOW_OOM_MANAGEMENT
		);//绑定服务
		isActivity = true;
		onMyCreate("onCreate");
		mNotificationManager = PlayerNotificationManager.instance();
		mHomeReceiver = new HomeReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(UPDATE_ACTION);
		intentFilter.addAction(PLAY_STATUE);
		intentFilter.addAction(CTL_ACTION);
		intentFilter.addAction(MUSIC_DURATION);
		intentFilter.addAction(MUSIC_CURRENT);
		intentFilter.addAction(CURRENT_ID);
		intentFilter.addAction(UPDATE_LIST_ACTIVITY_ACTION);
		registerReceiver(mHomeReceiver, intentFilter);
		_inActivity = this;
		actionmode_callback = new ActionMode_CallBack();
		mRepeatState = restorePreferences();
		setStatus(mRepeatState);
		mActionBar = findViewById(R.id.action_bar);
		mActionBar.setBackgroundColor(getResources().getColor(R.color.playingcolor));
//		mActionBar.setVisibility(View.GONE);
    }

	private void onMyCreate(String who) {
		setContentView(R.layout.activity_main);
		try {
			
			InputStream is = getAssets().open("TianJin.xml");
			Log.d(TAG,"info : "+is);
			PullLocalInfoParser parser = new PullLocalInfoParser();
			List<LocalInfo> localAllInfo = parser.parse(is);
			Map<Integer,List<Program>> map = parser.getmap();
			Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			Log.d(TAG,"time : "+hour+":"+minute);
			for (int i=0;i<localAllInfo.size();i++) {
				Log.d(TAG,"info : "
						+localAllInfo.get(i).getPosition()+" "
						+localAllInfo.get(i).getTag()+" "
						+localAllInfo.get(i).getStationName()+" "
						+localAllInfo.get(i).getChannel() +" "
						+localAllInfo.get(i).getRadioID());
				for(int j = 0; j< Objects.requireNonNull(
						map.get(localAllInfo.get(i).getChannel())
				).size(); j++){
					Log.d(TAG,"progrem : "
							+ localAllInfo.get(i).getChannel()+" "
							+ map.get(localAllInfo.get(i).getChannel()).get(j).getContent()+" "

					);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*try {
			ConnectMainManager.getInstance().getCenterList(new RequestCallBack<OnLineRadioPattern>() {
				@Override
				public void onSuccess(OnLineRadioPattern val) {
					String a = val.getCurrentStationList().get(0).getStationTitle();
					Log.d(TAG,"mTitle : "+a);
				}

				@Override
				public void onFail(String errorMessage) {
					Log.d(TAG,"error : "+errorMessage);
				}
			});
		} catch (Exception e) {
			Log.e(TAG,"ConnectMainManager start failed");
		}*/
		mFlingView = new FlingView(getBaseContext());
		if (mService != null) {
			mList = mService.getList();
		}
		Log.d(TAG, who);
		mFlingView = findViewById(R.id.fling_view);
		mFlingViewBack = findViewById(R.id.fling_view_back);
		FindViewById();
		SetOnClickListener();
		SetOnLongClickListener();
		mLrcView = findViewById(R.id.textView1);
		mPlayBar.setOnSeekBarChangeListener(new SeekBarListener());
		mUserAdapter = new UserAdapter(getBaseContext(), R.layout.listitem, mList);
		mListView.setAdapter(mUserAdapter);
		mListView.setOnItemClickListener(new ItemClickListener());
		mListView.setOnItemLongClickListener(mLongClickListener);
		mFlingView.setOnScrollToScreenListener((currentScreen, screenCount) -> {
			stop();
			mListPosition = currentScreen;
			mHandler.sendEmptyMessage(PLAY_NEXT_VIEW);
		});
		mHandler.sendEmptyMessage(SHOW_NOTIFICATION);
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			openDialog(PlayerDialog.EXIT);
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			onMyCreate("onConfigurationChanged ORIENTATION_LANDSCAPE");
			onResume();
		}
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			onMyCreate("onConfigurationChanged ORIENTATION_PORTRAIT");
			onResume();
		}
	}

	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mDuration = mList.get(mListPosition).getDuration();
		int min = (mDuration / 1000) / 60;
		int sec = (mDuration / 1000) % 60;
		mAllTimeView.setText(String.format("%s:%s", text(min), text(sec)));
		mPlayBar.setMax(mDuration);
		if (MainService.isPlay) {
			mStartBut.setVisibility(View.GONE);
			mStopBut.setVisibility(View.VISIBLE);
			mPlayBar.setEnabled(true);
		}
		mRepeatState = restorePreferences();
		setStatus(mRepeatState);
		mMusicNameView.setText(mList.get(mListPosition).getTitle());
		mFlingView.setToScreen(mListPosition, true);
		setCurrentMusic(mListPosition);

		if(!mService.isAdded){
			try {
				if(ContextCompat.checkSelfPermission(getBaseContext(),
						Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED ) {
					Log.d(TAG, "no permission of window manager");
					ActivityCompat.requestPermissions(this,
							new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, MY_PERMISSIONS_REQUEST_PERMISSION);
				}
				mService.createFloatView();
				mLrcOnOff.setBackground(getResources().getDrawable(R.drawable.lrc_on));
			}catch (SecurityException s){
				Toast.makeText(this,"check the permission",Toast.LENGTH_LONG).show();
			}
		}
		mService.initLrc(mList.get(mListPosition).getPath());
		mFlingView.setToScreen(mListPosition, false);
		if (MainService.isPlay)
			startPlayAnim(mListPosition);
		mHandler.sendEmptyMessage(REMOVE_NOTIFICATION);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (requestCode == MY_PERMISSIONS_REQUEST_PERMISSION
				&& grantResults.length > 0
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			Log.d(TAG, "has got permission of window manager");
		} else {
			mHandler.sendEmptyMessageDelayed(MOVE_TO_SETTING_PERMISSION_VIEW, 1000);
		}
	}

	private SharedPreferences preferences;

	private int restorePreferences() {
		preferences = getSharedPreferences(MusicApplication.PREF_NAME, MODE_PRIVATE);
		return preferences.getInt("status", 3);
	}

	private void savePreferences(int status) {
		preferences = getSharedPreferences(MusicApplication.PREF_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("status", status);
		editor.apply();
	}
	public int getStatus(){
		return mRepeatState;
	}

	public void setStatus(int status) {
		if (status == 1) {
			mRepeatState = 1;
			mCurrentStatusView.setText(R.string.signal_round);
		} else if (status == 2) {
			mRepeatState = 2;
			mCurrentStatusView.setText(R.string.all_round);
		} else if (status == 3) {
			mRepeatState = 3;
			mCurrentStatusView.setText(R.string.order_play);
		} else if (status == 4) {
			mRepeatState = 4;
			mCurrentStatusView.setText(R.string.random);
		}
		savePreferences(mRepeatState);
		Intent intent = new Intent(CTL_ACTION);
		intent.putExtra("control", status);
		sendBroadcast(intent);
	}

	protected void onStop() {
		// TODO Auto-generated method stub
		isActivity = false;
		super.onStop();
	}

	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mHomeReceiver);
		mHandler.sendEmptyMessage(REMOVE_NOTIFICATION);
//		unbindservice();
//		stopMainService();
	}

	private String text(int set_time) {
		String get_time;
		if (set_time < 10) {
			get_time = String.format("0%s", set_time);
		} else {
			get_time = String.format("%s", set_time);
		}
		return get_time;
	}

	public void setCurrentMusicID(long currentMusicID) {
		this.mCurrentMusicID = currentMusicID;

	}

	@Override
	public void onStart() {
		super.onStart();
	}

	private class HomeReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(PLAY_STATUE)) {
				isPlaying = intent.getBooleanExtra("isplay", false);
				if (!isPlaying) {
					if (mService.getPlayerStatus()) {
						stop();
					}
				}
			}
			if (action.equals(UPDATE_LIST_ACTIVITY_ACTION)) {
				mList = MainService.list;
				mListPosition = intent.getIntExtra("current_music", 0);
				mUserAdapter.addItems(mList);
				mFlingView.setToScreen(mListPosition,false);
				PlayMusicUIUpdate(mListPosition);
			}
			if (action.equals(UPDATE_ACTION)) {
				mListPosition = intent.getIntExtra("current_music", -1);
				mTitle = mList.get(mListPosition).getTitle();
				mMusicNameView.setText(mTitle);
				mDuration = intent.getIntExtra("mDuration", -1);
				int min = (mDuration / 1000) / 60;
				int sec = (mDuration / 1000) % 60;
				mAllTimeView.setText(String.format("%s:%s", text(min), text(sec)));
				mPlayBar.setMax(mDuration);
				updateSingleRow(mUserAdapter.getItemId(mListPosition));
				PlayMusicUIUpdate(mListPosition);
			}
			if (action.equals(MUSIC_DURATION)) {
				mDuration = intent.getIntExtra("mDuration", -1);
				int min = (mDuration / 1000) / 60;
				int sec = (mDuration / 1000) % 60;
				mAllTimeView.setText(String.format("%s:%s", text(min), text(sec)));
				mPlayBar.setMax(mDuration);
			}
			if (action.equals(MUSIC_CURRENT)) {
				mCurrentTime = intent.getIntExtra("current_time", -1);
				int min = (mCurrentTime / 1000) / 60;
				int sec = (mCurrentTime / 1000) % 60;
				mCurrentTimeView.setText(String.format("%s:%s", text(min), text(sec)));
				mPlayBar.setProgress(mCurrentTime);
//				roate(mCurrentTime);
			}
			if (action.equals(CURRENT_ID)) {
				mListPosition = intent.getIntExtra("current_id", -1);
				mFlingView.snapToScreen(mListPosition, true);
			}
		}
	}

	private class ItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
			if(!mIsActionMode) {
				mListPosition = arg2;
				ItemPlay(mListPosition);
			} else {
				CheckBox box = arg1.findViewById(R.id.checkBox1);
				if(mList.get(arg2).getSelected()) {
					mList.get(arg2).setSelected(false);
					box.setChecked(false);
				} else {
					mList.get(arg2).setSelected(true);
					box.setChecked(true);
				}
			}
		}
	}

	public void ItemPlay(final int pos){
		mService.isPause = false;
		isFirstTime = false;
		isPlaying = true;
//		stopplayanim();
//		mService.mStopBut();
//		Intent intent = new Intent(UPDATE_ACTION);
//		intent.putExtra("current_music", pos);
//		intent.putExtra("mDuration", mDuration);
//		sendBroadcast(intent);
		Thread t  = new Thread(() -> mService.play(0, pos));
		t.start();
		PlayMusicUIUpdate(pos);
	}

	private void route(int progress) {
		mFlingView.getChildAt(mListPosition).setPivotX(
				(float) mFlingView.getChildAt(mListPosition).getWidth() / 2);
		mFlingView.getChildAt(mListPosition).setPivotY(
				(float) mFlingView.getChildAt(mListPosition).getHeight() / 2);
		mFlingView.getChildAt(mListPosition).setRotation((float) progress / 360);
//		mFlingView.getChildAt(mListPosition).getRotation();
	}

	private class SeekBarListener implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
									  boolean fromUser) {
			// TODO Auto-generated method stub
			if (fromUser) {
				mService.progress(progress);
				route(progress);
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

	}

	private void FindViewById() {
		// TODO Auto-generated method stub
		mPlayBar = findViewById(R.id.playBar);
		mPreBut = findViewById(R.id.pre);
		mNextBut = findViewById(R.id.next);
		mStartBut = findViewById(R.id.start);
		mStopBut = findViewById(R.id.stop);
		mPauseBut = findViewById(R.id.pause);
		mTestButton = findViewById(R.id.test_button);
		mFloatBut = findViewById(R.id.fab);
		mCurrentTimeView = findViewById(R.id.currenttime);
		mAllTimeView = findViewById(R.id.alltime);
		mMusicNameView = findViewById(R.id.musicname);
        mLrcOnOff = findViewById(R.id.lrc_on_off);
		if (mMusicNameView != null) {
			mMusicNameView.setFocusable(true);
		}
		mListView = findViewById(R.id.list);
		mPlayBar = findViewById(R.id.playBar);
		if (mPlayBar != null) {
			mPlayBar.setEnabled(false);
		}
		mCurrentStatusView = findViewById(R.id.current_status);
	}

	public void setCurrentMusic(int position) {
		mListView.setSelection(position);
	}

	private void updateSingleRow(long id) {
		if (mListView != null) {
			int start = mListView.getFirstVisiblePosition();
			for (int i = start, j = mListView.getLastVisiblePosition(); i <= j; i++)
				if (id == mUserAdapter.getItemId(i)) {
					//View view = mListView.getChildAt(i - mStartBut);
					mUserAdapter.notifyDataSetChanged();
					setCurrentMusic(i);
					break;
				}
		}
	}

	public void startPlayAnim(final int pos) {
		if(mRotateAnimation == null) {
			mRotateAnimation = new RotateAnimation(0, 360,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
		}
		mRotateAnimation.setDuration(10000);
		mRotateAnimation.setRepeatCount(Animation.INFINITE);
		LinearInterpolator interpolator = new LinearInterpolator();
		mRotateAnimation.setInterpolator(interpolator);
		mRotateAnimation.setFillBefore(true);
		mFlingView.getChildAt(pos).startAnimation(mRotateAnimation);
	}

	public void stopPlayAnim() {
		if (mRotateAnimation != null) {
			mFlingView.getChildAt(mListPosition).setRotation(0);
			mRotateAnimation.cancel();
			mRotateAnimation.reset();
			mRotateAnimation = null;
		}
	}

	private void PlayMusicUIUpdate(int position) {
		// TODO Auto-generated method stub
		mFlingView.setToScreen(position, false);
		mFlingView.snapToScreen(position, true);
		mStartBut.setVisibility(View.GONE);
		mStopBut.setVisibility(View.VISIBLE);
		mPlayBar.setEnabled(true);
		mTitle = mList.get(position).getTitle();
		mMusicNameView.setText(mTitle);
		updateSingleRow(mUserAdapter.getItemId(position));
//		mService.play(0, position);
		if(position == 0) {
			mPreBut.setBackground(getResources().getDrawable(R.drawable.disable_pre, null));
		}
		else{
			mPreBut.setBackground(getResources().getDrawable(R.drawable.previous_button_ripple, null));
		}
		if(position+1 == mList.size()) {
			mNextBut.setBackground(getResources().getDrawable(R.drawable.disable_next, null));
		}
		else{
			mNextBut.setBackground(getResources().getDrawable(R.drawable.next_button_ripple, null));
		}
		startPlayAnim(position);
	}

	private void SetOnLongClickListener() {
		// TODO Auto-generated method stub
		OnLongClickListener longClickListener = v -> {
			// TODO Auto-generated method stub
			if (v.getId() == R.id.pre) {
				if(isPlaying){
					quick_left();
					v.setBackground(getResources().getDrawable(R.drawable.fast_rewind_button_ripple, null));
				}
			}
			if (v.getId() == R.id.next) {
				if(isPlaying) {
					quick_right();
					v.setBackground(getResources().getDrawable(R.drawable.fast_forward_button_ripple, null));
				}
			}
			return true;
		};
		mPreBut.setOnLongClickListener(longClickListener);
		mNextBut.setOnLongClickListener(longClickListener);
	}

	protected void quick_right() {
		// TODO Auto-generated method stub
//		Log.d(TAG, "quick_right()");
		mHandler.sendEmptyMessage(QUICK_RIGHT);
	}

	protected void quick_left() {
		// TODO Auto-generated method stub
		mHandler.sendEmptyMessage(QUICK_LEFT);
	}
//	private boolean is record = false;
	private void SetOnClickListener() {
		// TODO Auto-generated method stub
		OnClickListener clickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
                if(v.getId() == R.id.lrc_on_off){
                    if(!mService.isAdded) {
                        mService.createFloatView();
                        mService.initLrc(mList.get(mListPosition).getPath());
                        mLrcOnOff.setBackground(getResources().getDrawable(R.drawable.lrc_on));
                    }
                    else {
                        mService.dismissFloatView();
                        mLrcOnOff.setBackground(getResources().getDrawable(R.drawable.lrc_off));
                    }
                }
				if (v.getId() == R.id.pre) {
					if(mListPosition != 0)
						previous();
				}
				if (v.getId() == R.id.next) {
					if(mListPosition + 1 != mList.size())
						next();
				}
				if (v.getId() == R.id.stop) {
					stop();
				}
				if (v.getId() == R.id.pause) {
					pause();
				}
				if(v.getId() == R.id.test_button){
					Intent intent = new Intent();
					intent.setClass(MainActivity.this,MediaPlayerTest.class);
					startActivity(intent);
				}

				if (v.getId() == R.id.start) {
					if (isFirstTime) {
						PlayMusicUIUpdate(mListPosition);
						mService.play(0, mListPosition);
						isFirstTime = false;
					} else {
						PlayMusicUIUpdate(mListPosition);
						mService.play(0, mListPosition);
					}
				}
				if(v.getId() == R.id.fab){
					Intent intent = new Intent();
					intent.putExtra("which music", mListPosition);
					intent.setClass(MainActivity.this, MusicInformationActivity.class);
					startActivity(intent);
				}
			}
		};
		if(mListPosition == 0) {
			mPreBut.setBackground(getResources().getDrawable(R.drawable.disable_pre, null));
		}
		else{
			mPreBut.setBackground(getResources().getDrawable(R.drawable.previous_button_ripple, null));
		}
		if(mList != null && mListPosition + 1 == mList.size()) {
			mNextBut.setBackground(getResources().getDrawable(R.drawable.disable_next, null));
		}
		else{
			mNextBut.setBackground(getResources().getDrawable(R.drawable.next_button_ripple,null));
		}
		mPreBut.setOnClickListener(clickListener);
		mNextBut.setOnClickListener(clickListener);
		mStartBut.setOnClickListener(clickListener);
		mStopBut.setOnClickListener(clickListener);
		mPauseBut.setOnClickListener(clickListener);
		mTestButton.setOnClickListener(clickListener);
		mFloatBut.setOnClickListener(clickListener);
        mLrcOnOff.setOnClickListener(clickListener);
	}

	public void previous() {
		mService.isPause = false;
		if(mListPosition - 1>= 0)
			stopPlayAnim();
		mListPosition = mListPosition - 1;
		if (mListPosition >= 0) {
			mService.playPre();
			PlayMusicUIUpdate(mListPosition);
		} else {
			Toast.makeText(this, "fist one", Toast.LENGTH_SHORT).show();
		}
	}

	public void next() {
		mService.isPause = false;
		if(mListPosition +1 != mList.size())
			stopPlayAnim();
		mListPosition = mListPosition + 1;
		if (mListPosition < mList.size()) {
			mService.playNext();
			PlayMusicUIUpdate(mListPosition);
		} else {
			Toast.makeText(this, "last one", Toast.LENGTH_SHORT).show();
		}
	}

	public void pause() {
		mStartBut.setVisibility(View.VISIBLE);
		mStopBut.setVisibility(View.GONE);
		mPauseBut.setVisibility(View.VISIBLE);
		mService.pause();
		stopPlayAnim();
	}

	public void stop() {
		//mHandler.removeCallbacks(doupdatecurrenttime);
		mPlayBar.setEnabled(false);
		stopPlayAnim();
		mService.stop();
		mStartBut.setVisibility(View.VISIBLE);
		mStopBut.setVisibility(View.GONE);
//        mPauseBut.setVisibility(View.VISIBLE);
		mPlayBar.setProgress(0);
//        roate(0);
		mCurrentTimeView.setText(R.string._00_00);
		updateSingleRow(mCurrentMusicID);
		mUserAdapter.notifyDataSetChanged();
//		MusicApplication.musicApplation.unbindservice();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	void openDialog(int type) {
		PlayerDialog playerDialog;
		playerDialog = PlayerDialog.newInstance(this, type);
		playerDialog.setStyle(R.style.ActionBar ,0);
        playerDialog.show(getFragmentManager(), String.valueOf(type));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			openDialog(PlayerDialog.STATUE);
			return true;
		}
		if (id == R.id.Media_seting) {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this,MediaPlayerTest.class);
			startActivity(intent);
		}

		if (id == R.id.searchmusic) {
			openDialog(PlayerDialog.SEARCH);
		}
		if(id == R.id.filter_settings){
			Intent intent = new Intent();
			intent.setClass(MainActivity.this,FilterSettings.class);
			startActivity(intent);
		}
		if(id == R.id.test){

			ComponentName componentName = new ComponentName("com.sec.myonlinefm", "com.sec.myonlinefm.MainActivity");
			Intent i = new Intent();
			i.setComponent(componentName);
			startActivity(i);

			try {
				InputStream is = getAssets().open("TianJin.xml");
				PullLocalInfoParser parser = new PullLocalInfoParser();
				List<LocalInfo> localInfos = parser.parse(is);
				for (LocalInfo local : localInfos) {
					Log.d(TAG,"info : "
							+local.getPosition()+" "
							+local.getTag()+" "
							+local.getStationName()+" "
							+local.getChannel());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (id == R.id.search_music) {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this,TestViewPagerActivity.class);
			startActivity(intent);
		}
		if (id == R.id.list_show) {
			if (!mListView.isShown()) {
				mListView.setVisibility(View.VISIBLE);
				mFlingView.setVisibility(View.GONE);
				mFlingViewBack.setVisibility(View.GONE);
				item.setIcon(R.drawable.music_library_add_playlist_now_play);
			} else {
				mListView.setVisibility(View.GONE);
				mFlingView.setVisibility(View.VISIBLE);
				mFlingViewBack.setVisibility(View.VISIBLE);
				item.setIcon(R.drawable.tw_ic_ab_drawer_mtrl);
			}
		}
		return super.onOptionsItemSelected(item);
	}

	public void delete(int postion) {
		// TODO Auto-generated method stub
		for (int i = mList.size(); i > -1; i--) {
			if (i == postion) {
				mList.remove(i);
				mUserAdapter.notifyDataSetChanged();
			}
		}
	}

	private class ActionMode_CallBack implements Callback {

		public boolean onCreateActionMode(ActionMode mode,
										  Menu menu) {
			// TODO Auto-generated method stub
			mActionMode = mode;
			MenuItem menuItem = menu.add(R.string.delete);

			mActionBar.setVisibility(View.GONE);
			menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode,
										   Menu menu) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode,
										   MenuItem item) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			// TODO Auto-generated method stub
			mIsActionMode = false;
			mActionMode = null;
			for(int i = 0; i< mList.size(); i++){
				mList.get(i).setSelected(false);
			}
			mActionBar.setVisibility(View.VISIBLE);
			mUserAdapter.notifyDataSetChanged();
		}
	}
}
