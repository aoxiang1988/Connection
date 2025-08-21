// MainViewModel.java
package com.example.myautoapplication;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myautoapplication.datamodel.Audio;
import com.example.myautoapplication.datamodel.MediaUtil;
import com.example.myautoapplication.toolutils.ToolUtils;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private static final String TAG = "MainViewModel";

    private final MutableLiveData<String> serviceInfo = new MutableLiveData<>("no info");
    private final MutableLiveData<Integer> playButtonText = new MutableLiveData<>(R.string.play_source);
    private final MutableLiveData<List<Audio>> audioList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> permissionGranted = new MutableLiveData<>(false);

    private MyService mService = null;

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getServiceInfo() {
        return serviceInfo;
    }

    public LiveData<Integer> getPlayButtonText() {
        return playButtonText;
    }

    public LiveData<List<Audio>> getAudioList() {
        return audioList;
    }

    public LiveData<Boolean> getPermissionGranted() {
        return permissionGranted;
    }

    public void setServiceInfo(String info) {
        serviceInfo.setValue(info);
    }

    public void setPlayState(boolean isPlaying) {
        playButtonText.setValue(isPlaying ? R.string.pause_source : R.string.play_source);
    }

    public void setPermissionGranted(boolean granted, Activity activity) {
        permissionGranted.setValue(granted);
        if (granted) {
            loadAudioList(activity);
        }
    }

    public void setService(MyService service) {
        this.mService = service;
        if (mService != null && mService.getPlayState()) {
            playButtonText.setValue(R.string.pause_source);
        }
    }

    public MyService getService() {
        return mService;
    }

    private void loadAudioList(Activity activity) {
        if (ToolUtils.checkPermission(activity)) {
            List<Audio> list = MediaUtil.getAudioList(getApplication());
            audioList.setValue(list);
            
            for (Audio audio : list) {
                Log.d(TAG, "MusicInfo:" + audio.getTitle() + "--" + audio.getArtist());
            }
        } else {
            Log.d(TAG, "no permission!!!");
        }
    }

    public void togglePlayPause(int id) {
        if (mService != null) {
            if (mService.getPlayState()) {
                playButtonText.setValue(R.string.play_source);
                mService.pauseSource();
            } else {
                playButtonText.setValue(R.string.pause_source);
                List<Audio> list = audioList.getValue();
                if (list != null && !list.isEmpty()) {
                    Log.d(TAG, "MusicInfo:" + list.get(id).getTitle() + "--" + list.get(id).getArtist());
                    mService.setSourcePath(list.get(id).getPath());
                    mService.playSource();
                }
            }
        }
    }

    public void stopMedia() {
        if (mService != null) {
            mService.stopSource();
        }
    }
}
