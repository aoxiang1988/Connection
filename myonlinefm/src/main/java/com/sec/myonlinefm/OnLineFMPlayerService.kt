package com.sec.myonlinefm

import android.app.Service
import android.net.Uri
import android.os.*
import android.util.Log
import java.io.IOException
import java.lang.IllegalStateException

import android.widget.Toast
import android.content.Intent
import android.media.MediaPlayer
import android.media.AudioManager
import android.media.MediaPlayer.OnPreparedListener
import android.media.AudioManager.OnAudioFocusChangeListener


class OnLineFMPlayerService : Service() {
    private val TAG: String? = "OnLineFMPlayerService"
    var UPDATE_ONLINE_PLAY_UI_ACTION: String? = "update online play ui"
    private var mPlayType = 0
    var mediaPlayer: MediaPlayer? = null
    private var duration = 0
    private val c_duration = 0
    var isPlay = false
    var mPlayer_url: String? = null
    var audioManager: AudioManager? = null
    private var mPlayer: OnLineFMConnectManager? = null
    var t: Thread? = null
    fun StartPlay(url: String?, play_type: Int) {
        Log.d(TAG, "play url : $url")
        if (play_type == PLAY_STATION) {
            playStation(url, play_type)
        }
        if (play_type == REPLAY_PROGRAM) {
            playStation(url, play_type)
            //            playProgram(url, mPlayType);
        }
    }

    private fun playStation(url: String?, play_type: Int) {
        mPlayType = play_type
        mPlayer_url = url
        audioManager!!.requestAudioFocus(AudioListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        if (audioManager!!.isSpeakerphoneOn()) Toast.makeText(baseContext, "不支持外放，请通过耳机收听", Toast.LENGTH_LONG).show()
        isPlay = true
        if (t != null) {
            t!!.interrupt()
            t = null
        }
        t = Thread {
            val uri = Uri.parse(mPlayer_url)
            try {
                mediaPlayer!!.reset()
                mediaPlayer!!.setDataSource(applicationContext, uri)
                mediaPlayer!!.prepare()
                mediaPlayer!!.setOnPreparedListener(PreparedListener(0))
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
                Log.e(TAG, "net source has not load right now")
            }
        }
        t!!.start()
    }

    fun StopPlay() {
        isPlay = false
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            //            mediaPlayer.reset();
        }
        if (t != null) t!!.interrupt()
        t = null
    }

    private inner class PreparedListener  // TODO Auto-generated constructor stub
    internal constructor(private val currentTime: Int) : OnPreparedListener {
        override fun onPrepared(mp: MediaPlayer?) {
            // TODO Auto-generated method stub
            mediaPlayer!!.start()
            mediaPlayer!!.seekTo(currentTime)
            duration = mediaPlayer!!.getDuration()
            val intent = Intent(UPDATE_REPLAY_UI_ACTION)
            intent.putExtra("Duration", duration)
            sendBroadcast(intent)
        }
    }

    fun progress(bar_progress: Int) {
        if (mediaPlayer != null) {
            mediaPlayer!!.seekTo(bar_progress)
        }
    }

    //    public void pause(){
    //        if(mediaPlayer != null && mediaPlayer.isPlaying()){
    //            mediaPlayer.pause();
    //            pause_time = getcurrenttime();
    //            isPause = true;
    //        }
    //    }
    inner class ServiceBinder internal constructor(service: OnLineFMPlayerService?) : Binder() {
        private var mService: OnLineFMPlayerService? = null
        fun getService() {
            // TODO Auto-generated method stub
            myservice = mService
        }

        init {
            // TODO Auto-generated constructor stub
            mService = service
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        // TODO: Return the communication channel to the service.
        return ServiceBinder(this)
    }

    var handler: Handler? = null
    override fun onCreate() {
        super.onCreate()
        mPlayer = OnLineFMConnectManager.mMainInfoCode
        //        MainActivity.initservice(this);
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer!!.reset()
            mediaPlayer = null
        }
        mediaPlayer = MediaPlayer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        MainActivity.initservice(this);
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer!!.reset()
            mediaPlayer = null
        }
        t!!.interrupt()
        t = null
        audioManager!!.abandonAudioFocus(AudioListener)
    }

    private val AudioListener: OnAudioFocusChangeListener? = OnAudioFocusChangeListener { focusChange -> // TODO Auto-generated method stub
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
            }
            AudioManager.AUDIOFOCUS_GAIN -> if (!isPlay) StartPlay(mPlayer_url, mPlayType)
            AudioManager.AUDIOFOCUS_LOSS -> if (isPlay) StopPlay()
            else -> {
            }
        }
    }

    companion object {
        var myservice: OnLineFMPlayerService? = null
        var UPDATE_REPLAY_UI_ACTION: String? = "update replay ui"
        var PLAY_STATION = 1
        var REPLAY_PROGRAM = 2
        @JvmName("getMyservice1")
        fun getMyservice(): OnLineFMPlayerService? {
            return myservice
        }
    }
}