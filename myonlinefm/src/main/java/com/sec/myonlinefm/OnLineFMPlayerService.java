package com.sec.myonlinefm;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class OnLineFMPlayerService extends Service {

    private String TAG = "OnLineFMPlayerService";
    public static OnLineFMPlayerService myservice;
    public static String UPDATE_REPLAY_UI_ACTION = "update replay ui";
    public String UPDATE_ONLINE_PLAY_UI_ACTION = "update online play ui";
    public static int PLAY_STATION = 1;
    public static int REPLAY_PROGRAM = 2;
    private int mPlayType;
    public MediaPlayer mediaPlayer = null;
    private int duration;
    private int c_duration;
    public boolean isPlay = false;
    public String mPlayer_url;
    AudioManager audioManager;
    private OnLineFMConnectManager mPlayer;
    Thread t;
    public static OnLineFMPlayerService getMyservice() {
        return myservice;
    }

    public void StartPlay(final String url, int play_type) {
        Log.d(TAG,"play url : "+url);
        if(play_type == PLAY_STATION) {
            playStation(url, play_type);
        }
        if(play_type == REPLAY_PROGRAM) {
            playStation(url, play_type);
//            playProgram(url, mPlayType);
        }
    }
    private void playStation(final String url, int play_type){
        mPlayType = play_type;
        mPlayer_url = url;
        audioManager.requestAudioFocus(AudioListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if(audioManager.isSpeakerphoneOn())
            Toast.makeText(getBaseContext(),"不支持外放，请通过耳机收听", Toast.LENGTH_LONG).show();
        isPlay = true;
        if(t != null) {
            t.interrupt();
            t = null;
        }
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri uri = Uri.parse(mPlayer_url);
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(getApplicationContext(),uri);
                    mediaPlayer.prepare();
                    mediaPlayer.setOnPreparedListener(new PreparedListener(0));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    Log.e(TAG,"net source has not load right now");
                }
            }
        });
        t.start();
    }

    public void StopPlay(){
        isPlay = false;
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
//            mediaPlayer.reset();
        }
        if(t != null)
            t.interrupt();
        t = null;
    }

    private class PreparedListener implements MediaPlayer.OnPreparedListener {
        private int currentTime;
        PreparedListener(int currentTime) {
            // TODO Auto-generated constructor stub
            this.currentTime = currentTime;
        }
        @Override
        public void onPrepared(MediaPlayer mp) {
            // TODO Auto-generated method stub
            mediaPlayer.start();
            mediaPlayer.seekTo(currentTime);
            duration = mediaPlayer.getDuration();
            Intent intent = new Intent(UPDATE_REPLAY_UI_ACTION);
            intent.putExtra("Duration",duration);
            sendBroadcast(intent);
        }
    }

    public void progress(int bar_progress){
        if(mediaPlayer != null){
            mediaPlayer.seekTo(bar_progress);
        }
    }
//    public void pause(){
//        if(mediaPlayer != null && mediaPlayer.isPlaying()){
//            mediaPlayer.pause();
//            pause_time = getcurrenttime();
//            isPause = true;
//        }
//    }

    public class ServiceBinder extends Binder {
        private OnLineFMPlayerService mService = null;
        ServiceBinder(OnLineFMPlayerService service) {
            // TODO Auto-generated constructor stub
            mService = service;
        }
        public void getService() {
            // TODO Auto-generated method stub
            myservice = mService;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new ServiceBinder(this);
    }
    Handler handler;
    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = OnLineFMConnectManager.Companion.getMMainInfoCode();
//        MainActivity.initservice(this);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer.reset();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        MainActivity.initservice(this);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer.reset();
            mediaPlayer = null;
        }
        t.interrupt();
        t = null;
        audioManager.abandonAudioFocus(AudioListener);
    }

    private AudioManager.OnAudioFocusChangeListener AudioListener = new AudioManager.OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(int focusChange) {
            // TODO Auto-generated method stub
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:

                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    if(!isPlay)
                        StartPlay(mPlayer_url, mPlayType);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    if(isPlay)
                        StopPlay();
                    break;
                default:
                    break;
            }
        }

    };
}
