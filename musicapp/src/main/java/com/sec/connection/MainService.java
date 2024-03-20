package com.sec.connection;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaScannerConnection;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.sec.connection.aspectcode.PermissionCheck;
import com.sec.connection.data.Audio;
import com.sec.connection.data.MediaUtile;
import com.sec.connection.view.LrcContent;
import com.sec.connection.view.LrcProcess;
import com.sec.connection.view.LrcView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;

public class MainService extends Service{
	public MediaPlayer mediaPlayer = null;
	public static MainService myService;
	private int duration;
	private int c_duration;
	public static List<Audio> list = null;
	private int c_music;
	public boolean isPause = false;
	public static boolean isPlay = false;
	private int status = 3;

	private AudioManager audioManager;

	private List<LrcContent> lrcContents = new ArrayList<>();
    private int index = 0;

	public static final String UPDATE_ALL_LIST = "com.example.action.UPDATE_ALL_LIST";
	public static final String UPDATE_ACTION = "com.example.action.UPDATE_ACTION";
	public static final String PLAY_STATUE = "com.example.action.PLAY_STATUE";
	public static final String MUSIC_CURRENT = "com.example.action.MUSIC_CURRENT";
	public static final String MUSIC_DURATION = "com.example.action.MUSIC_DURATION";
	public static final String CURRENT_ID = "com.example.action.CURRENT_ID";
	public static final String NOTIFY_PLAY = "com.example.action.NOTIFY_PLAY";
	public static final String NOTIFY_STOP = "com.example.action.NOTIFY_STOP";
	public static final String NOTIFY_NEXT = "com.example.action.NOTIFY_NEXT";
	public static final String NOTIFY_PRE = "com.example.action.NOTIFY_PRE";
	public static final String NOTIFY_REMOVE = "com.example.action.NOTIFY_REMOVE";
	private static final String UPDATE_LIST_ACTIVITY_ACTION = "com.example.action.UPDATE_LIST_ACTIVITY_ACTION";

	private static String FilePath = "/storage/emulated/0";

	private static final int UPDATE_CURRENT_TIME = 1;
	private static final int STOP_CURRENT_TIME = 2;
	public static final String COLLECTION_VIEW_ACTION = "com.example.play.COLLECTION_VIEW_ACTION";

	class ServiceBinder extends Binder{
		private MainService mService = null;
		ServiceBinder(MainService service) {
			// TODO Auto-generated constructor stub
			mService = service;
		}
		void getService() {
			// TODO Auto-generated method stub
			myService = mService;
		}
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return new ServiceBinder(this);
	}
/* *******************************************************
 * http://blog.csdn.net/wyyother1/article/details/40091593 *
 * */
	private Timer timer = new Timer();
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){

		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
				case UPDATE_CURRENT_TIME:
					if(mediaPlayer != null && isPlay){
						c_duration = mediaPlayer.getCurrentPosition();
						Intent intent = new Intent(MUSIC_CURRENT);
						intent.putExtra("current_time", c_duration);
						sendBroadcast(intent);//??MainActive?????
						handler.sendEmptyMessageDelayed(UPDATE_CURRENT_TIME, 500);
					}
					break;

				case STOP_CURRENT_TIME:
					 timer.cancel();
						break;
			}
		}
	};
	public int getcurrenttime(){
		//Log.d(TAG, "c_duration"+c_duration);
		return c_duration;
	}
	public boolean getplayerstatus(){
		return mediaPlayer.isPlaying();

	}
	/************************Lrc*********************************/
	//<span style="white-space:pre">	</span>/**
	// /storage/emulated/0/Samsung/Music/Over*/
	public void initLrc(String path){
		LrcProcess lrcProcess = new LrcProcess();
		lrcProcess.readLRC(path);
		lrcContents = lrcProcess.getLrcList();
		if(lrcContents.isEmpty()){
			return;
		}
		MainActivity.mLrcView.setLrcList(lrcContents);
		float_lrcView.setLrcList(lrcContents);
		handler.post(mRunnable);
	}
	Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			MainActivity.mLrcView.setIndex(lrcIndex());
			MainActivity.mLrcView.invalidate();

				float_lrcView.setIndex(lrcIndex());
				float_lrcView.invalidate();

			handler.postDelayed(mRunnable, 500);
		}
	};

	public int lrcIndex() {
		if(mediaPlayer.isPlaying()) {
			c_duration = mediaPlayer.getCurrentPosition();
			duration = mediaPlayer.getDuration();
		}
		if(c_duration < duration) {
			for (int i = 0; i < lrcContents.size(); i++) {
				if (i < lrcContents.size() - 1) {
					if (c_duration < lrcContents.get(i).getlrctime() && i == 0) {
						index = i;
					}
					if (c_duration > lrcContents.get(i).getlrctime()
							&& c_duration < lrcContents.get(i + 1).getlrctime()) {
						index = i;
					}
				}
				if (i == lrcContents.size() - 1
						&& c_duration > lrcContents.get(i).getlrctime()) {
					index = i;
				}
			}
		}
		return index;
	}
	/************************************************************/

	Widget_Receiver widget_Receiver;
	Notify_Receiver mNotifyReceiver;
	MyReceiver myReceiver;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		list = BaseListInfo.getInstance().getList();
		MainActivity.initservice(this);
		scanSdCard();
		mediaPlayer = new MediaPlayer();
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

		widget_Receiver = new Widget_Receiver();
		IntentFilter widget_filter = new IntentFilter();
		widget_filter.addAction(COLLECTION_VIEW_ACTION);
		registerReceiver(widget_Receiver, widget_filter);

		mNotifyReceiver = new Notify_Receiver();
		IntentFilter notify_filter = new IntentFilter();
		notify_filter.addAction(NOTIFY_NEXT);
		notify_filter.addAction(NOTIFY_PLAY);
		notify_filter.addAction(NOTIFY_STOP);
		notify_filter.addAction(NOTIFY_PRE);
		notify_filter.addAction(NOTIFY_REMOVE);
		registerReceiver(mNotifyReceiver, notify_filter);

		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				if(status == 1){//loop one music
					mediaPlayer.start();
				}else if(status == 2){//all loop
					MainActivity._inActivity.stopplayanim();
					c_music++;
					if(c_music > list.size()-1){
						c_music = 0;
					}
					Intent intent = new Intent(UPDATE_ACTION);
					intent.putExtra("current_music", c_music);
					intent.putExtra("duration", duration);
					sendBroadcast(intent);
					PlayerNotificationManager.instance().updatecurrentname(list.get(c_music));
					playmusic(0,list.get(c_music).getPath(), false);
				}else if(status == 3){//????
					MainActivity._inActivity.stopplayanim();
					c_music++;
					if(c_music <= list.size()-1){
						Intent intent = new Intent(UPDATE_ACTION);
						intent.putExtra("current_music", c_music);
						intent.putExtra("duration", duration);
						sendBroadcast(intent);
						PlayerNotificationManager.instance().updatecurrentname(list.get(c_music));
						playmusic(0,list.get(c_music).getPath(), false);
					} else {
						mediaPlayer.seekTo(0);
						c_music = 0;
						Intent intent = new Intent(UPDATE_ACTION);
						intent.putExtra("current_music", c_music);
						intent.putExtra("duration", duration);
						sendBroadcast(intent);
					}
				}else if(status == 4){//???????
					MainActivity._inActivity.stopplayanim();
					c_music = getRandomIndex(list.size() - 1);
                    System.out.println("currentIndex ->" + c_music);
                    Intent sendIntent = new Intent(UPDATE_ACTION);  
                    sendIntent.putExtra("current_music", c_music);  
                    sendIntent.putExtra("duration", duration);
                    // ???????????Activity????е?BroadcastReceiver?????  
                    sendBroadcast(sendIntent);  
                    PlayerNotificationManager.instance().updatecurrentname(list.get(c_music));
                    playmusic(0,list.get(c_music).getPath(), false);
				}
				Intent intent = new Intent(CURRENT_ID);
				intent.putExtra("current_id", c_music);
				sendBroadcast(intent);
				MainActivity._inActivity.startplayanim(c_music);
			}
		});
		myReceiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MainActivity.CTL_ACTION);
		registerReceiver(myReceiver, filter);
	}
	protected int getRandomIndex(int i) {
		// TODO Auto-generated method stub
		return (int) (Math.random() * i);
	}

	//play
	public void play(int currentTime,int position) {
		// TODO Auto-generated method stub
		String path = list.get(position).getPath();
		if(isPause && pause_time != 0)
			currentTime = pause_time;

		playmusic(currentTime, path, false);
		c_music = position;
	}

	public void playmusic(final int c_Time, final String path, boolean isListActivity){

		Thread t ;
		isPlay = true;
		Intent intent = new Intent(PLAY_STATUE);
		intent.putExtra("isplay", isPlay);
		intent.putExtra("current_music", c_music);
		sendBroadcast(intent);
		audioManager.requestAudioFocus(AudioListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
		initLrc(path);
		t = new Thread(new Runnable() {
			@Override
			public void run() {
				mediaPlayer.reset();
				try {
					mediaPlayer.setDataSource(path);
					mediaPlayer.prepare();
					Log.d("bin1111.yang","c_Time: "+c_Time);
					mediaPlayer.setOnPreparedListener(new PreparedListener(c_Time));

					handler.sendEmptyMessage(UPDATE_CURRENT_TIME);
				} catch (IllegalArgumentException | SecurityException
						| IllegalStateException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		t.start();
		if(isListActivity) {
			for (int a = 0; a < list.size(); a++) {
				if (path.equals(list.get(a).getPath())) {
					c_music = a;
					intent = new Intent(UPDATE_ACTION);
					intent.putExtra("isplay", isPlay);
					intent.putExtra("current_music", c_music);
					intent.putExtra("duration", list.get(c_music).getDuration());
					sendBroadcast(intent);
					break;
				}
			}
		}
		PlayerNotificationManager.instance().updatecurrentname(list.get(c_music));
	}
	//pause
	int pause_time = 0;
	public void pause(){
		if(mediaPlayer != null && mediaPlayer.isPlaying()){
			mediaPlayer.pause();
			pause_time = getcurrenttime();
			isPause = true;
		}
	}
	//stop
	public void stop(){
		if(mediaPlayer != null){

			mediaPlayer.stop();
			mediaPlayer.reset();
			pause_time = 0;
			handler.removeMessages(UPDATE_CURRENT_TIME);
			audioManager.abandonAudioFocus(AudioListener);
			isPlay = false;
			Intent intent = new Intent(PLAY_STATUE);
			intent.putExtra("isplay", isPlay);
			intent.putExtra("current_music", c_music);  
			intent.putExtra("duration", duration);
			sendBroadcast(intent);
			if(!MainActivity._inActivity.isActivity)
				PlayerNotificationManager.instance().updatecontureUI();
		}
	}
	//progress
	public void progress(int bar_progress){
		if(mediaPlayer != null && isPlay){
			mediaPlayer.seekTo(bar_progress);
		}
	}

	private OnAudioFocusChangeListener AudioListener = new OnAudioFocusChangeListener() {

		@Override
		public void onAudioFocusChange(int focusChange) {
			// TODO Auto-generated method stub
			switch (focusChange) {
			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
				pause();
				break;
			case AudioManager.AUDIOFOCUS_GAIN:
				play(pause_time, c_music);
				break;
			case AudioManager.AUDIOFOCUS_LOSS:
				MainActivity._inActivity.stop();
				break;
			default:
				break;
			}
		}
		
	};
	private class PreparedListener implements OnPreparedListener{
		private int currentTime;
		PreparedListener(int currentTime) {
			// TODO Auto-generated constructor stub
			this.currentTime = currentTime;
		}
		@Override
		public void onPrepared(MediaPlayer mp) {
			// TODO Auto-generated method stub
			mediaPlayer.start();
			if(currentTime > 0){
				mediaPlayer.seekTo(currentTime);
			}
			Intent intent = new Intent();
			intent.setAction(MUSIC_DURATION);
			duration = mediaPlayer.getDuration();
			intent.putExtra("duration", duration);
			sendBroadcast(intent);
		}
	}
	class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			int control = intent.getIntExtra("control", -1);  
            switch (control) {  
            case 1:  
                status = 1; // ???????????1????????????  
                break;  
            case 2:  
                status = 2; //???????????2???????????  
                break;  
            case 3:  
                status = 3; //???????????3?????????  
                break;  
            case 4:  
                status = 4; //???????????4????????????  
                break;  
            }  
		}
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		MainActivity.initservice(this);
		return Service.START_STICKY;
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mediaPlayer != null){
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
			isPlay = false;
			Intent intent = new Intent(PLAY_STATUE);
			intent.putExtra("isplay", isPlay);
			sendBroadcast(intent);
			audioManager.abandonAudioFocus(AudioListener);
		}

		unregisterReceiver(widget_Receiver);
		unregisterReceiver(mNotifyReceiver);
		unregisterReceiver(scanSdReceiver);
		unregisterReceiver(myReceiver);
	}
	
	private class Widget_Receiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			switch (action) {
				case COLLECTION_VIEW_ACTION:
					c_music = intent.getIntExtra("widget_list_item", 0);
					playmusic(0, list.get(c_music).getPath(), false);
					break;
				default :
					break;
			}
		}
	}
	
	private class Notify_Receiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			switch (action) {
			case NOTIFY_NEXT:
				playnext();
				break;
			case NOTIFY_PLAY:
				PlayerNotificationManager.instance().updatecurrentname(list.get(c_music));
				isPlay = true;
				Intent nintent = new Intent(PLAY_STATUE);
				nintent.putExtra("isplay", isPlay);
				nintent.putExtra("current_music", c_music);
				nintent.putExtra("duration", list.get(c_music).getDuration());
				sendBroadcast(nintent);
				playmusic(0, list.get(c_music).getPath(), false);
				PlayerNotificationManager.instance().updatecontureUI();
				break;
			case NOTIFY_PRE:
				playpre();
				break;
			case NOTIFY_REMOVE:
				stop();
				stopSelf();
				MainActivity._inActivity.finish();
				PlayerNotificationManager.instance().removenotification(getApplicationContext());
				break;
			case NOTIFY_STOP :
				stop();
				break;
			default:
				break;
			}
		}
		
	}
	void playpre(){
    	c_music = c_music - 1;
    	if(c_music >= 0){
    		Intent intent = new Intent(UPDATE_ACTION);
    		intent.putExtra("current_music", c_music);
			intent.putExtra("duration", duration);
			sendBroadcast(intent);
    		playmusic(0, list.get(c_music).getPath(), false);
    		PlayerNotificationManager.instance().updatecurrentname(list.get(c_music));
    	}
    	else {
    		Toast.makeText(this, "fist one", Toast.LENGTH_SHORT).show();
    	}
    }
	void playnext(){
    	c_music = c_music + 1;
    	if(c_music <= list.size()){
    		Intent intent = new Intent(UPDATE_ACTION);
    		intent.putExtra("current_music", c_music);
			intent.putExtra("duration", duration);
			sendBroadcast(intent);
    		playmusic(0, list.get(c_music).getPath(), false);
    		PlayerNotificationManager.instance().updatecurrentname(list.get(c_music));
    	}
    	else {
    		Toast.makeText(this, "last one", Toast.LENGTH_SHORT).show();
    	}
    }

	//Edited by mythou
	//http://www.cnblogs.com/mythou/
	private WindowManager wm = null;
	public boolean isAdded = false;
	private WindowManager.LayoutParams params;
	public LrcView float_lrcView;
	private View child;

	public void dismissFloatView() {
		isAdded = false;
		wm.removeView(child);
		child = null;
	}

	@SuppressLint("ClickableViewAccessibility")
	@PermissionCheck(permession = {Manifest.permission.SYSTEM_ALERT_WINDOW})//AOP面向切面编程,编译时注解
	public void createFloatView() {
		final LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		child = inflater.inflate(R.layout.floatview, null);
		child.setClickable(true);
		float_lrcView = (LrcView) child.findViewById(R.id.float_text_view);
		float_lrcView.setClickable(true);
		float_lrcView.setTextSize(65,55,35);
		wm = (WindowManager) getApplicationContext().getSystemService(
				Context.WINDOW_SERVICE);
		params = new WindowManager.LayoutParams();
		params.gravity = Gravity.BOTTOM;
		// 设置window type
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        /*
         * 如果设置为params.type = WindowManager.LayoutParams.TYPE_PHONE; 那么优先级会降低一些,
         * 即拉下通知栏不可见
         */
		params.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
		// 设置Window flag
		params.flags =
				/*下面的flags属性的效果形同“锁定”。 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
				 *  */
				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
						| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
				/*下面的flags属性的效果形同“非锁定”。 悬浮窗可触摸，接受任何事件,同时影响后面的事件响应。
				 WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
						 | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
				 *  */
		// 设置悬浮窗的长得宽
		params.width = WindowManager.LayoutParams.MATCH_PARENT;
		params.height = 150;
		// 设置悬浮窗的Touch监听
		float_lrcView.setOnTouchListener(new View.OnTouchListener() {
			int lastX, lastY;
			int paramX, paramY;

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						lastX = (int) event.getRawX();
						lastY = (int) event.getRawY();
						paramX = params.x;
						paramY = params.y;
						break;
					case MotionEvent.ACTION_MOVE:
						int dx = (int) event.getRawX() - lastX;
						int dy = (int) event.getRawY() - lastY;
						params.x = paramX + dx;
						params.y = paramY + dy;
						// 更新悬浮窗位置
						wm.updateViewLayout(child, params);
						break;
				}
				return true;
			}
		});
		try {
			wm.addView(child, params);
		}catch (WindowManager.BadTokenException b){
			Toast.makeText(this,"check the WindowManager Premission",Toast.LENGTH_SHORT).show();
		};
		isAdded = true;
	}
	/*存储监听
	* */
	ScanSdReceiver scanSdReceiver;
	public void scanSdCard(){
        IntentFilter intentfilter = new IntentFilter( Intent.ACTION_MEDIA_SCANNER_STARTED);
        intentfilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        intentfilter.addDataScheme("file");
		scanSdReceiver = new ScanSdReceiver();
		registerReceiver(scanSdReceiver, intentfilter);
		Log.d("bin1111.yang"," "+Environment.getExternalStorageDirectory().getAbsolutePath());
//		sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
//				Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath())));
		MediaScannerConnection.scanFile(getBaseContext(),
				new String[]{
				Environment.getExternalStorageDirectory().getAbsolutePath()
				}, null, null);
	}
	public class ScanSdReceiver extends BroadcastReceiver {
		private int count1;
		private int count2;
		private int count;
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_MEDIA_SCANNER_STARTED.equals(action)){
				Cursor c1 = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
						new String[]{MediaStore.Audio.Media._ID},
						null, null, null);
				assert c1 != null;
				count1 = c1.getCount();
				System.out.println("count:"+count);
				Log.d("bin1111.yang","正在扫描存储卡...");
				c1.close();
			}else if(Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)){
				Cursor c2 = context.getContentResolver()
						.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
								new String[]{MediaStore.Audio.Media._ID},
								null, null, null);
				assert c2 != null;
				count2 = c2.getCount();
				count = count2-count1;
				if (count!=0){
					Log.d("bin1111.yang","需要更新list");
					list  = MediaUtile.getAudioList(getBaseContext());
					if(c_music >= list.size()) {
						c_music = 0;
					} else if(c_music != 0){
						if(Objects.equals(list.get(c_music - 1).getTitle(), list.get(c_music).getTitle()))
							c_music = c_music - 1;
					}
					Intent intent1 = new Intent(UPDATE_LIST_ACTIVITY_ACTION);
					intent1.putExtra("current_music",c_music);
					sendBroadcast(intent1);
				}
				c2.close();
			}
		}
	}
}
