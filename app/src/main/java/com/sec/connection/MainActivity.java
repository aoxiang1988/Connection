package com.sec.connection;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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

import com.sec.connection.vpview.TestViewPagerActivity;
import com.sec.connection.data.Audio;
import com.sec.connection.setting.FilterSettings;
import com.sec.connection.setting.MediaPlayerTest;
import com.sec.connection.setting.MusicInformationActivity;
import com.sec.connection.view.FlingView;
import com.sec.connection.view.LrcView;
import com.sec.connection.xmlcheck.LocalInfo;
import com.sec.connection.xmlcheck.Progrem;
import com.sec.connection.xmlcheck.PullLocalInfoParser;
import com.sec.connecttoapilibrary.ConnectMainManager;
import com.sec.connecttoapilibrary.RequestCallBack;
import com.sec.connecttoapilibrary.onlinefm.liveRadioData.OnLineRadioPattern;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

	private static final String UPDATE_ACTION = "com.example.action.UPDATE_ACTION";
	private static final String UPDATE_LIST_ACTIVITY_ACTION = "com.example.action.UPDATE_LIST_ACTIVITY_ACTION";
	//	public static final String DELETE_ITEM = "com.example.action.DELETE_ITEM";
	private static final String PLAY_STATUE = "com.example.action.PLAY_STATUE";
	public static final String CTL_ACTION = "com.example.action.CTL_ACTION";
	private static final String MUSIC_CURRENT = "com.example.action.MUSIC_CURRENT";
	private static final String MUSIC_DURATION = "com.example.action.MUSIC_DURATION";
	private static final String CURRENT_ID = "com.example.action.CURRENT_ID";

	public static MainService mService = null;
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

	private ActionMode mActionMode = null;
	boolean mIsActionMode = false;
	private RotateAnimation mRotateAnimation = null;

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
				case PLAY_NEXT_VIEW:
					ItemPlay(mListPosition);
					break;
				case SHOW_NOTIFICATION:
					mNotificationManager.shownotification(mList.get(mListPosition));
					break;
				case REMOVE_NOTIFICATION:
					mNotificationManager.removenotification(getBaseContext());
					break;
				case QUICK_RIGHT:
					if (mCurrentTime < mList.get(mListPosition).getDuration()) {
						mCurrentTime = mCurrentTime + 5000;
						mService.progress(mCurrentTime);
					}
					if (mNextBut.isPressed()) {
						sendMessageDelayed(Message.obtain(mHandler, QUICK_RIGHT), 200);
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
						sendMessageDelayed(Message.obtain(mHandler, QUICK_LEFT), 200);
					} else {
						if (mListPosition == 0) {
							mPreBut.setBackground(getResources().getDrawable(R.drawable.disable_pre, null));
						} else {
							mPreBut.setBackground(getResources().getDrawable(R.drawable.previous_button_ripple, null));
						}
					}
					break;
			}
		}
	};

	public static void initservice(MainService service) {
		mService = service;
	}

	public static int getcurrentposition() {
		return mListPosition;
	}


	Callback actionmode_callback;
	private OnItemLongClickListener mLongClickListener = new OnItemLongClickListener() {

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("bin1111.yang","onCreate");
		isActivity = true;
		onmycreate();
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
		setstatus(mRepeatState);
		mActionBar = findViewById(R.id.action_bar);
		mActionBar.setBackgroundColor(getResources().getColor(R.color.playingcolor));
//		mActionBar.setVisibility(View.GONE);
	}

	private void onmycreate() {
		setContentView(R.layout.activity_main);
		try {
			
			InputStream is = getAssets().open("TianJin.xml");
			Log.d("bin1111.yang","info : "+is);
			PullLocalInfoParser parser = new PullLocalInfoParser();
			List<LocalInfo> localInfos = parser.parse(is);
			Map<Integer,List<Progrem>> map = parser.getmap();
			Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			Log.d("bin1111.yang","time : "+hour+":"+minute);
			for (int i=0;i<localInfos.size();i++) {
				Log.d("bin1111.yang","info : "
						+localInfos.get(i).getpostion()+" "
						+localInfos.get(i).gettag()+" "
						+localInfos.get(i).getstationname()+" "
						+localInfos.get(i).getchannel() +" "
						+localInfos.get(i).getradio_ID());
				for(int j=0;j<map.get(localInfos.get(i).getchannel()).size();j++){
					Log.d("bin1111.yang","progrem : "
							+ localInfos.get(i).getchannel()+" "
							+ map.get(localInfos.get(i).getchannel()).get(j).getcontent()+" "

					);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		ConnectMainManager.getInstance().getCenterList(new RequestCallBack<OnLineRadioPattern>() {
			@Override
			public void onSuccess(OnLineRadioPattern val) {
				String a = val.getCurrentStationList().get(0).getStationTitle();
				Log.d("bin1111.yang","mTitle : "+a);
			}

			@Override
			public void onFail(String errorMessage) {
				Log.d("bin1111.yang","error : "+errorMessage);
			}
		});

//		mFlingView = new FlingView(getBaseContext());
		mList = MainService.list;
		mFlingView = (FlingView) findViewById(R.id.fling_view);
		mFlingViewBack = (LinearLayout) findViewById(R.id.fling_view_back);
		FindViewbyId();
		SetOnClickListener();
		SetOnLongClickListener();
//		mLrcView = (LrcView) findViewById(R.id.textView1);
		mPlayBar.setOnSeekBarChangeListener(new SeekBarListener());
		mUserAdapter = new UserAdapter(this, R.layout.listitem, mList);
		mListView.setAdapter(mUserAdapter);
		mListView.setOnItemClickListener(new ItemClickListener());
		mListView.setOnItemLongClickListener(mLongClickListener);
		mFlingView.setOnScrollToScreenListener(new FlingView.OnScrollToScreenListener() {
			@Override
			public void operation(int currentScreen, int screenCount) {
				stop();
				mListPosition = currentScreen;
				mHandler.sendEmptyMessage(PLAY_NEXT_VIEW);
			}
		});
		mHandler.sendEmptyMessage(SHOW_NOTIFICATION);
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			opendialog(PlayerDialog.EXIT);
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			onmycreate();
			onResume();
		}
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			onmycreate();
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
		setstatus(mRepeatState);
		mMusicNameView.setText(mList.get(mListPosition).getTitle());
//		mFlingView.setToScreen(mListPosition, true);
		setcurrentmusic(mListPosition);

		if(!mService.isAdded){
			try {
				if(ActivityCompat.checkSelfPermission(getApplicationContext(),
						Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED ) {
					ActivityCompat.requestPermissions(this,
							new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, 0);
				}
				mService.createFloatView();
				mLrcOnOff.setBackground(getResources().getDrawable(R.drawable.lrc_on));
			}catch (SecurityException s){
				Toast.makeText(this,"check the premission",Toast.LENGTH_SHORT);
			}
		}
		mService.initLrc(mList.get(mListPosition).getPath());
		mFlingView.setToScreen(mListPosition, false);
		if (MainService.isPlay)
			startplayanim(mListPosition);
//		mHandler.sendEmptyMessage(REMOVE_NOTIFICATION);
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
	public int getstatus(){
		return mRepeatState;
	}

	public void setstatus(int status) {
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
					if (mService.getplayerstatus()) {
						stop();
					}
				}
			}
			if (action.equals(UPDATE_LIST_ACTIVITY_ACTION)) {
				mList = MainService.list;
				mListPosition = intent.getIntExtra("current_music", 0);
				mUserAdapter.addItems(mList,true);
				mFlingView.setToScreen(mListPosition,false);
				PlayMusicUIUpdata(mListPosition);
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
				PlayMusicUIUpdata(mListPosition);
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
				CheckBox box = (CheckBox)arg1.findViewById(R.id.checkBox1);
				if(mList.get(arg2).getSelected()) {
					mList.get(arg2).setSeleted(false);
					box.setChecked(false);
				} else {
					mList.get(arg2).setSeleted(true);
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
		Thread t  = new Thread(new Runnable() {
			@Override
			public void run() {
				mService.play(0, pos);
			}
		});
		t.start();
		PlayMusicUIUpdata(pos);
	}

	private void roate(int progress) {
		mFlingView.getChildAt(mListPosition).setPivotX(
				mFlingView.getChildAt(mListPosition).getWidth() / 2);
		mFlingView.getChildAt(mListPosition).setPivotY(
				mFlingView.getChildAt(mListPosition).getHeight() / 2);
		mFlingView.getChildAt(mListPosition).setRotation(progress / 360);
//		mFlingView.getChildAt(mListPosition).getRotation();
	}

	private class SeekBarListener implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
									  boolean fromUser) {
			// TODO Auto-generated method stub
			if (fromUser) {
				mService.progress(progress);
				roate(progress);
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

	private void FindViewbyId() {
		// TODO Auto-generated method stub
		mPlayBar = (SeekBar) findViewById(R.id.playBar);
		mPreBut = (Button) findViewById(R.id.pre);
		mNextBut = (Button) findViewById(R.id.next);
		mStartBut = (Button) findViewById(R.id.start);
		mStopBut = (Button) findViewById(R.id.stop);
		mPauseBut = (Button) findViewById(R.id.pause);
		mTestButton = (Button) findViewById(R.id.test_button);
		mFloatBut = (FloatingActionButton) findViewById(R.id.fab);
		mCurrentTimeView = (TextView) findViewById(R.id.currenttime);
		mAllTimeView = (TextView) findViewById(R.id.alltime);
		mMusicNameView = (TextView) findViewById(R.id.musicname);
        mLrcOnOff = (ImageView) findViewById(R.id.lrc_on_off);
		if (mMusicNameView != null) {
			mMusicNameView.setFocusable(true);
		}
		mListView = (ListView) findViewById(R.id.list);
		mPlayBar = (SeekBar) findViewById(R.id.playBar);
		if (mPlayBar != null) {
			mPlayBar.setEnabled(false);
		}
		mCurrentStatusView = (TextView) findViewById(R.id.current_status);
	}

	public void setcurrentmusic(int position) {
		mListView.setSelection(position);
	}

	private void updateSingleRow(long id) {
		if (mListView != null) {
			int start = mListView.getFirstVisiblePosition();
			for (int i = start, j = mListView.getLastVisiblePosition(); i <= j; i++)
				if (id == mUserAdapter.getItemId(i)) {
					//View view = mListView.getChildAt(i - mStartBut);
					mUserAdapter.notifyDataSetChanged();
					setcurrentmusic(i);
					break;
				}
		}
	}

	public void startplayanim(final int pos) {
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

	public void stopplayanim() {
		if (mRotateAnimation != null) {
			mFlingView.getChildAt(mListPosition).setRotation(0);
			mRotateAnimation.cancel();
			mRotateAnimation.reset();
			mRotateAnimation = null;
		}
	}

	private void PlayMusicUIUpdata(int position) {
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
		startplayanim(position);
	}

	private void SetOnLongClickListener() {
		// TODO Auto-generated method stub
		OnLongClickListener longClickListener = new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
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
			}
		};
		mPreBut.setOnLongClickListener(longClickListener);
		mNextBut.setOnLongClickListener(longClickListener);
	}

	protected void quick_right() {
		// TODO Auto-generated method stub
//		Log.d("bin1111.yang", "quick_right()");
		mHandler.sendEmptyMessage(QUICK_RIGHT);
	}

	protected void quick_left() {
		// TODO Auto-generated method stub
		mHandler.sendEmptyMessage(QUICK_LEFT);
	}
//	private boolean isrecord = false;
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
						PlayMusicUIUpdata(mListPosition);
						mService.play(0, mListPosition);
						isFirstTime = false;
					} else {
						PlayMusicUIUpdata(mListPosition);
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
		if(mListPosition + 1 == mList.size()) {
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
			stopplayanim();
		mListPosition = mListPosition - 1;
		if (mListPosition >= 0) {
			mService.playpre();
			PlayMusicUIUpdata(mListPosition);
		} else {
			Toast.makeText(this, "fist one", Toast.LENGTH_SHORT).show();
		}
	}

	public void next() {
		mService.isPause = false;
		if(mListPosition +1 != mList.size())
			stopplayanim();
		mListPosition = mListPosition + 1;
		if (mListPosition < mList.size()) {
			mService.playnext();
			PlayMusicUIUpdata(mListPosition);
		} else {
			Toast.makeText(this, "last one", Toast.LENGTH_SHORT).show();
		}
	}

	public void pause() {
		mStartBut.setVisibility(View.VISIBLE);
		mStopBut.setVisibility(View.GONE);
		mPauseBut.setVisibility(View.VISIBLE);
		mService.pause();
		stopplayanim();
	}

	public void stop() {
		//mHandler.removeCallbacks(doupdatecurrenttime);
		mPlayBar.setEnabled(false);
		stopplayanim();
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

	void opendialog(int type) {
		PlayerDialog playerDialog;
		playerDialog = PlayerDialog.newInstance(type);
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
			opendialog(PlayerDialog.STATUE);
			return true;
		}
		if (id == R.id.Media_seting) {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this,MediaPlayerTest.class);
			startActivity(intent);
		}

		if (id == R.id.searchmusic) {
			opendialog(PlayerDialog.SEARCH);
		}
		if(id == R.id.filter_settings){
			Intent intent = new Intent();
			intent.setClass(MainActivity.this,FilterSettings.class);
			startActivity(intent);
		}
		if(id == R.id.test){
			try {
				InputStream is = getAssets().open("TianJin.xml");
				PullLocalInfoParser parser = new PullLocalInfoParser();
				List<LocalInfo> localInfos = parser.parse(is);
				for (LocalInfo local : localInfos) {
					Log.d("bin1111.yang","info : "
							+local.getpostion()+" "
							+local.gettag()+" "
							+local.getstationname()+" "
							+local.getchannel());
				}
			} catch (IOException e) {
				e.printStackTrace();
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
				mList.get(i).setSeleted(false);
			}
			mActionBar.setVisibility(View.VISIBLE);
			mUserAdapter.notifyDataSetChanged();
		}
	}
}
