package com.sec.connection.setting;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.sec.connection.MusicApplication;
import com.sec.connection.data.FolderPathData;
import com.sec.connection.MainService;
import com.sec.connection.PlayerDialog;
import com.sec.connection.vpview.TestViewPagerActivity;
import com.sec.connection.R;
import java.util.List;

/**
 * Created by SRC-TJ-MM-BinYang on 2017/10/25.
 */

public class FilterSettings extends Activity {

    private Switch firstfilterswitch;
    private Switch secondfilterswitch;
    private SeekBar firstfilterseekbar;
    private SeekBar secondfilterseekbar;
    private Button thridfilterbutton;
    private TextView mindirectiontext;
    private TextView minsizetext;

    public static FilterSettings _inActivity;

    private String SET_FILTER_ACTION = "com.example.action.SET_FILTER_ACTION";
    private static final String IS_SET_SIZE_VALUE = "is set size value";
    private static final String IS_SET_DURATION_VALUE = "is set duration value";
    private static final String IS_SET_FILTER_FOLDER = "is set filter folder";
    private static final String SET_SIZE_VALUE = "set size value";
    private static final String SET_DURATION_VALUE = "set duration value";

    private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener;

    private int MaxSize = 2097152;
    private int MaxDuration = 300000;
    public static FolderPathData mFolderPathData;

    //设置限制值
    private boolean isSetSizeValue = false;
    private boolean isSetDurationValue = false;
    private boolean isSetFilterFolder = false;
    private int mSetSizeValue = 0;
    private int mSetDurationValue = 0;

    private String TAG = "FilterSettings";
    private SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_settings_layout);
        _inActivity = this;
        ImageView back = (ImageView)findViewById(R.id.media_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG,"set value : "+isSetSizeValue+" "+isSetDurationValue+" "+mSetSizeValue+" "+mSetDurationValue);

                savePreferences(IS_SET_SIZE_VALUE, isSetSizeValue);
                savePreferences(IS_SET_DURATION_VALUE, isSetDurationValue);
                savePreferences(SET_SIZE_VALUE, mSetSizeValue);
                savePreferences(SET_DURATION_VALUE, mSetDurationValue);

                Intent intent = new Intent(SET_FILTER_ACTION);
                intent.putExtra(IS_SET_SIZE_VALUE, isSetSizeValue);
                intent.putExtra(IS_SET_DURATION_VALUE, isSetDurationValue);
                intent.putExtra(SET_SIZE_VALUE, mSetSizeValue);
                intent.putExtra(SET_DURATION_VALUE, mSetDurationValue);
                intent.putExtra(IS_SET_FILTER_FOLDER,isSetFilterFolder);
                intent.setClass(FilterSettings.this, TestViewPagerActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mFolderPathData = MainService.mFolderPathData ;

        initview();
        setSwitchSelectOn();

        mSeekBarChangeListener = new SeekBarChangeListener();
        firstfilterseekbar.setOnSeekBarChangeListener(mSeekBarChangeListener);
        secondfilterseekbar.setOnSeekBarChangeListener(mSeekBarChangeListener);

        thridfilterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> mPathListForDialog = mFolderPathData.getPathList();
                opendialog(PlayerDialog.FOLDER_PATH, mPathListForDialog);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isSetSizeValue = (boolean)restorePreferences(IS_SET_SIZE_VALUE);
        isSetDurationValue = (boolean)restorePreferences(IS_SET_DURATION_VALUE);
        mSetSizeValue = (int) restorePreferences(SET_SIZE_VALUE);
        mSetDurationValue = (int) restorePreferences(SET_DURATION_VALUE);
        if(isSetSizeValue) {
            firstfilterswitch.setChecked(isSetSizeValue);
            firstfilterseekbar.setEnabled(isSetSizeValue);
            firstfilterseekbar.setProgress(mSetSizeValue);
        }
        if(isSetDurationValue) {
            secondfilterswitch.setChecked(isSetDurationValue);
            secondfilterseekbar.setEnabled(isSetDurationValue);
            secondfilterseekbar.setProgress(mSetDurationValue);
        }
    }

    private void opendialog(int type,
                            List<String> mPathListForDialog) {
        PlayerDialog playerDialog;
        playerDialog = PlayerDialog.newInstance(type,
                mPathListForDialog);
        playerDialog.setStyle(R.style.ActionBar ,0);
        playerDialog.show(getFragmentManager(), String.valueOf(type));
    }

    private void initview() {
        firstfilterswitch = findViewById(R.id.first_filter_switch);

        secondfilterswitch = findViewById(R.id.second_filter_switch);

        firstfilterseekbar = findViewById(R.id.first_filter_seekbar);
        firstfilterseekbar.setMax(MaxSize);
        firstfilterseekbar.setEnabled(false);

        secondfilterseekbar = findViewById(R.id.second_filter_seekbar);
        secondfilterseekbar.setMax(MaxDuration);
        secondfilterseekbar.setEnabled(false);

        thridfilterbutton = findViewById(R.id.thrid_filter_button);

        mindirectiontext = findViewById(R.id.set_min_direction_text);
        minsizetext = findViewById(R.id.set_min_size_text);
    }

    private void setSwitchSelectOn() {
        firstfilterswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSetSizeValue = isChecked;
                if(isChecked)
                    firstfilterseekbar.setEnabled(isChecked);
                 else {
                    mSetSizeValue = 0;
                    firstfilterseekbar.setProgress(mSetSizeValue);
                    firstfilterseekbar.setEnabled(isChecked);
                }
            }
        });
        secondfilterswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSetDurationValue = isChecked;
                if(isChecked)
                    secondfilterseekbar.setEnabled(isChecked);
                else {
                    mSetDurationValue = 0;
                    secondfilterseekbar.setProgress(mSetDurationValue);
                    secondfilterseekbar.setEnabled(isChecked);
                }
            }
        });
    }

    class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()) {
                case R.id.first_filter_seekbar:
                    mSetSizeValue = progress;
                    getMinSize(mSetSizeValue);
                break;
                case R.id.second_filter_seekbar:
                    mSetDurationValue = progress;
                    getMinDerection(mSetDurationValue);
                break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
    private Object restorePreferences(String SetValuse) {
        preferences = getSharedPreferences(MusicApplication.PREF_NAME, MODE_PRIVATE);
        Object status = null;
        switch (SetValuse) {
            case IS_SET_SIZE_VALUE:
                status = preferences.getBoolean(IS_SET_SIZE_VALUE, false);
                break;
            case IS_SET_DURATION_VALUE:
                status = preferences.getBoolean(IS_SET_DURATION_VALUE, false);
                break;
            case SET_SIZE_VALUE:
                status = preferences.getInt(SET_SIZE_VALUE, 0);
                break;
            case SET_DURATION_VALUE:
                status = preferences.getInt(SET_DURATION_VALUE, 0);
                break;
            case IS_SET_FILTER_FOLDER:
                status = preferences.getBoolean(IS_SET_FILTER_FOLDER, false);
                break;
        }
        return status;
    }
    private void savePreferences(String SetValuse,Object status) {
        preferences = getSharedPreferences(MusicApplication.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        switch (SetValuse) {
            case IS_SET_SIZE_VALUE:
                editor.putBoolean(IS_SET_SIZE_VALUE, (Boolean) status);
                break;
            case IS_SET_DURATION_VALUE:
                editor.putBoolean(IS_SET_DURATION_VALUE, (Boolean) status);
                break;
            case SET_SIZE_VALUE:
                editor.putInt(SET_SIZE_VALUE, (int) status);
                break;
            case SET_DURATION_VALUE:
                editor.putInt(SET_DURATION_VALUE, (int) status);
                break;
            case IS_SET_FILTER_FOLDER:
                editor.putBoolean(IS_SET_FILTER_FOLDER, (Boolean) status);
                break;
        }
        editor.apply();
    }

    private void getMinDerection(int DurationValue){
        int min = (DurationValue / 1000) / 60;
        int sec = (DurationValue / 1000) % 60;
        String stringmin;
        String stringsec;
        if (min < 10) {
            stringmin = String.format("0%s", min);
        } else {
            stringmin = String.format("%s", min);
        }
        if (sec < 10) {
            stringsec = String.format("0%s", sec);
        } else {
            stringsec = String.format("%s", sec);
        }
        mindirectiontext.setText(String.format("%s:%s", stringmin, stringsec));
    }
    private void getMinSize(int SizeValue){
        int size = SizeValue/1024;
        String stringsize;
        stringsize = String.format("%s", size);
        minsizetext.setText(String.format("%sKB", stringsize));
    }
    public void setfilterFolder(boolean isset){
        isSetFilterFolder = isset;
        savePreferences(IS_SET_FILTER_FOLDER, isSetFilterFolder);
    }
}