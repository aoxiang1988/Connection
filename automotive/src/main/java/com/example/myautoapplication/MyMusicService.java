package com.example.myautoapplication;

import static com.example.myautoapplication.datamodel.ActionMode.AUDIO_LIST_READY;

import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.os.IBinder;
import android.support.v4.media.MediaBrowserCompat.MediaItem;

import androidx.media.MediaBrowserServiceCompat;

import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.myautoapplication.datamodel.ActionMode;
import com.example.myautoapplication.datamodel.Audio;
import com.example.myautoapplication.datamodel.MediaUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a MediaBrowser through a service. It exposes the media library to a browsing
 * client, through the onGetRoot and onLoadChildren methods. It also creates a MediaSession and
 * exposes it through its MediaSession.Token, which allows the client to create a MediaController
 * that connects to and send control commands to the MediaSession remotely. This is useful for
 * user interfaces that need to interact with your media session, like Android Auto. You can
 * (should) also use the same service from your app"s UI, which gives a seamless playback
 * experience to the user.
 * <p>
 * To implement a MediaBrowserService, you need to:
 *
 * <ul>
 *
 * <li> Extend {@link MediaBrowserServiceCompat}, implementing the media browsing
 *      related methods {@link MediaBrowserServiceCompat#onGetRoot} and
 *      {@link MediaBrowserServiceCompat#onLoadChildren};
 * <li> In onCreate, start a new {@link MediaSessionCompat} and notify its parent
 *      with the session"s token {@link MediaBrowserServiceCompat#setSessionToken};
 *
 * <li> Set a callback on the {@link MediaSessionCompat#setCallback(MediaSessionCompat.Callback)}.
 *      The callback will receive all the user"s actions, like play, pause, etc;
 *
 * <li> Handle all the actual music playing using any method your app prefers (for example,
 *      {@link android.media.MediaPlayer})
 *
 * <li> Update playbackState, "now playing" metadata and queue, using MediaSession proper methods
 *      {@link MediaSessionCompat#setPlaybackState(android.support.v4.media.session.PlaybackStateCompat)}
 *      {@link MediaSessionCompat#setMetadata(android.support.v4.media.MediaMetadataCompat)} and
 *      {@link MediaSessionCompat#setQueue(java.util.List)})
 *
 * <li> Declare and export the service in AndroidManifest with an intent receiver for the action
 *      android.media.browse.MediaBrowserService
 *
 * </ul>
 * <p>
 * To make your app compatible with Android Auto, you also need to:
 *
 * <ul>
 *
 * <li> Declare a meta-data tag in AndroidManifest.xml linking to a xml resource
 *      with a &lt;automotiveApp&gt; root element. For a media app, this must include
 *      an &lt;uses name="media"/&gt; element as a child.
 *      For example, in AndroidManifest.xml:
 *          &lt;meta-data android:name="com.google.android.gms.car.application"
 *              android:resource="@xml/automotive_app_desc"/&gt;
 *      And in res/values/automotive_app_desc.xml:
 *          &lt;automotiveApp&gt;
 *              &lt;uses name="media"/&gt;
 *          &lt;/automotiveApp&gt;
 *
 * </ul>
 */
public class MyMusicService extends MediaBrowserServiceCompat {

    private static final String TAG = "MyMusicService";
    private MediaSessionCompat mSession;
    public List<Audio> mAudioList = null;

    private String mSourcePath = null;
    private MediaPlayer mPlayer = null;
    private boolean mSourceEnableRestart = false;
    private Intent mServiceIntent = new Intent();
    public boolean mAudioListReady = false;

    private final MyServiceBinder mBinder = new MyServiceBinder();

    public class MyServiceBinder extends Binder {
        MyMusicService getMyService() {
            return MyMusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }
    @Override
    public void onCreate() {
        super.onCreate();

        mSession = new MediaSessionCompat(this, "MyMusicService");
        setSessionToken(mSession.getSessionToken());
        mSession.setCallback(new MediaSessionCallback());
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mAudioList = MediaUtil.getAudioList(this);

        for (Audio audio : mAudioList) {
            Log.d(TAG, "MusicInfo:" + audio.getTitle() + "--" + audio.getArtist());
        }

        sendBroadcast(new Intent(AUDIO_LIST_READY));
        mAudioListReady = true;
        //Toast.makeText(getBaseContext(), "Please check permission!!!", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onDestroy() {
        mSession.release();
    }

    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName,
                                 int clientUid,
                                 Bundle rootHints) {
        return new BrowserRoot("root", null);
    }

    @Override
    public void onLoadChildren(@NonNull final String parentMediaId,
                               @NonNull final Result<List<MediaItem>> result) {
        result.sendResult(new ArrayList<MediaItem>());
    }

    /*
     * play music
     * */

    private int mCurrentAudio = -1;

    public int getCurrentAudio() {
        return mCurrentAudio;
    }

    public void setCurrentAudio(int mCurrentAudio) {
        this.mCurrentAudio = mCurrentAudio;
    }

    public boolean getPlayState() {
        return mPlayer != null && mPlayer.isPlaying();
    }

    public void playSource() {
        mSourcePath = mAudioList.get(mCurrentAudio).getPath();
        Thread thread = new Thread(() -> {
            try {
                synchronized (this) {
                    if (mPlayer == null) {
                        mPlayer = new MediaPlayer();
                    }
                    if (!reStartSource()) {
                        mPlayer.setDataSource(mSourcePath);
                        mPlayer.prepare();
                        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mPlayer.start();
                            }
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public void pauseSource() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            mSourceEnableRestart = true;
        }
    }

    public boolean reStartSource() {
        if (mPlayer != null
                && mSourceEnableRestart
                && !mPlayer.isPlaying()) {
            mPlayer.start();
            return true;
        }
        return false;
    }

    public void stopSource() {
        if (mPlayer != null
                && mSourcePath != null) {
            mPlayer.stop();
            mPlayer.reset();

            mSourceEnableRestart = false;

            mServiceIntent.setAction(ActionMode.MEDIA_SOURCE_STOP_ACTION);
            sendBroadcast(mServiceIntent);
        }
    }

    private final class MediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
        }

        @Override
        public void onSkipToQueueItem(long queueId) {
        }

        @Override
        public void onSeekTo(long position) {
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
        }

        @Override
        public void onPause() {
        }

        @Override
        public void onStop() {
        }

        @Override
        public void onSkipToNext() {
        }

        @Override
        public void onSkipToPrevious() {
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
        }

        @Override
        public void onPlayFromSearch(final String query, final Bundle extras) {
        }
    }
}