package com.sec.myonlinefm;

/**
 * Created by srct on 2018/1/12.
 */

import android.support.v7.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sec.myonlinefm.OnLineFMPlayerListener.OberverOnLinePlayerManager;
import com.sec.myonlinefm.OnLineFMPlayerListener.ObserverPlayerListener;
import com.sec.myonlinefm.data.Station;
import com.sec.myonlinefm.data.StationProgram;
import com.sec.myonlinefm.updataUIListener.ObserverUIListenerManager;
import com.sec.myonlinefm.abstructObserver.OnLineInfo;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.os.Build.VERSION_CODES.M;

@RequiresApi(M)
public class FMProgramActivity extends AppCompatActivity implements ServiceConnection,
        ObserverPlayerListener {

    private String TAG = "FMProgramActivity";
    private ImageView mStationPic;
    private TextView mStationName;
    private TextView mStationFreq;
    private int day_info = 1;
    private View topBarActionBarView = null;

    private ListView mProgramList;
    private ProgramList mProgramAdapter;

    private int get_ID;
    private int get_List_Type;
    private int get_Different_Type;
    private int mPlayType;

    private Bitmap bitmap = null;
    private OnLineFMConnectManager mPlayer;
    private static OnLineFMPlayerService mService;
    private LinearLayout mOnLineStationPlay;
    private LinearLayout mStationInfo;

    private ConstraintLayout mOnLinePlayerController;
    private TextView mControllerStationName;
    private TextView mControllerProgramName;
    private TextView mStartTime;
    private TextView mEndTime;
    private ImageButton mPlayButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private SeekBar mControllerBar;

    private Station mStation = null;
    private Map<Integer, List<StationProgram>> map = null;
    private List<StationProgram> mProgram;
    private int duration;

    private String GTE_STATION_INFO = "fm.action.GTE_STATION_INFO";
    private Receiver receiver;

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.play_pause_btn:
                    if(mService.isPlay) {
                        mService.StopPlay();
                        setPlayButton();
                        mOnLinePlayerController.setVisibility(View.GONE);
                        mStationInfo.setVisibility(View.VISIBLE);
                    } else {
                        mService.StartPlay(mService.mPlayer_url, mPlayType);//need check ..
                    }
                    break;
                case R.id.playe_station:
                    mPlayType = OnLineFMPlayerService.PLAY_STATION;
                    mPlayer.getReplayUrl(mStation.getStationId(),
                            null, null,mPlayType);
                    setPauseButton();
                    updateControllerUI(mPlayType, mStation.getCurrentProgram());
                    break;
                case R.id.prev_btn:
                    break;
                case R.id.next_btn:
                    break;
                case R.id.yesterday_btn:
                    day_info = 0;
                    mPlayer.getOneDayProgram(mStation,day_info);
                    setTopBarActivityButton(day_info);
                    break;
                case R.id.today_btn:
                    day_info = 1;
                    mPlayer.getOneDayProgram(mStation,day_info);
                    setTopBarActivityButton(day_info);
                    break;
                case R.id.tomorrow_btn:
                    day_info = 2;
                    mPlayer.getOneDayProgram(mStation,day_info);
                    setTopBarActivityButton(day_info);
                    break;
                default:
                    break;
            }
        }
    };

    private UpdateOnLineInfo mUpdateOnLineInfo;

    private class UpdateOnLineInfo extends OnLineInfo {
        private Context context;

        public UpdateOnLineInfo(Context context){
            this.context = context;
        }

        public void addToObserverList(){
            ObserverUIListenerManager.getInstance().add(this);
        }
        public void removeToObserverList(){
            ObserverUIListenerManager.getInstance().remove(this);
        }

        public void observerLiveRadioLocalUIUpData(@NonNull List<Station> mStations, @NonNull Map<Integer, List<StationProgram>> map){
            //TODO update local stations information
            if(mProgram == null)
                mProgram = map.get(get_ID);
            if(mProgramAdapter == null) {
                mProgramAdapter = new ProgramList(context, mProgram);
                mProgramList.setAdapter(mProgramAdapter);
            } else {
                mProgramAdapter.setNewList(mProgram);
                mProgramAdapter.notifyDataSetChanged();
                mProgramList.setSelection(mCurrent_Position);
            }
        }
        public void observerLiveRadioCenterUIUpData(List<Station> mStations, Map<Integer, List<StationProgram>> map){
            //TODO update local stations information
            if(mProgram == null)
                mProgram = map.get(get_ID);
            if(mProgramAdapter == null) {
                mProgramAdapter = new ProgramList(context, mProgram);
                mProgramList.setAdapter(mProgramAdapter);
            } else {
                mProgramAdapter.setNewList(mProgram);
                mProgramAdapter.notifyDataSetChanged();
                mProgramList.setSelection(mCurrent_Position);
            }
        }

        @Override
        public void observerOneDayProgramUpData(@NonNull List<StationProgram> mOneDayPrograms) {
            Log.d(TAG,"observerOneDayProgramUpData");
            mProgram.clear();
            mProgram = null;
            mProgram = mOneDayPrograms;
            mProgramAdapter.setNewList(mProgram);
            mProgramAdapter.notifyDataSetChanged();
            mProgramList.setSelection(mCurrent_Position);
        }
    }
    void initController() {
        mOnLinePlayerController = (ConstraintLayout) findViewById(R.id.voice_list_clip_panel_layout);
        mPlayButton = (ImageButton) findViewById(R.id.play_pause_btn);
        mPlayButton.setOnClickListener(mClickListener);
        mControllerStationName = (TextView) findViewById(R.id.online_station_name);
        mControllerProgramName = (TextView) findViewById(R.id.online_program_name);
        mEndTime = (TextView) findViewById(R.id.PlayerFullTimeTextView);
        mNextButton  = (ImageButton) findViewById(R.id.prev_btn);
        mNextButton.setOnClickListener(mClickListener);
        mPrevButton = (ImageButton) findViewById(R.id.next_btn);
        mPrevButton.setOnClickListener(mClickListener);
        mControllerBar = (SeekBar) findViewById(R.id.progressbar);
        mControllerBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mService.progress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    void updateControllerUI(int play_type,StationProgram program) {
        mControllerStationName.setText(mStation.getStationTitle());
        mControllerProgramName.setText(program.getProgramTitle());
        mEndTime.setText(program.getProgramEnd_Time());
        if(play_type == OnLineFMPlayerService.PLAY_STATION) {
            //start update system time
        } else {

        }
    }
    private Button mYesterdayBut;
    private Button mTodayBut;
    private Button mTomorrowBut;

    public void setTopPanelOnActionBar() {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(false);
            bar.setDisplayShowHomeEnabled(false);
            bar.setDisplayShowTitleEnabled(false);
            bar.setBackgroundDrawable(getDrawable(R.drawable.titlebar_bg));
            bar.setElevation(0);
        }
        topBarActionBarView = getLayoutInflater().inflate(R.layout.top_bar_panel_online, null);
        LinearLayout mTopBarController = topBarActionBarView.findViewById(R.id.top_bar_controller);
        ImageView mBackBut = topBarActionBarView.findViewById(R.id.back_but);
        mBackBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView mTopBarTitle = topBarActionBarView.findViewById(R.id.top_bar_title);
        mTopBarTitle.setText("直播列表");
        mTopBarController.setVisibility(View.VISIBLE);
        mYesterdayBut = topBarActionBarView.findViewById(R.id.yesterday_btn);
        mTodayBut = topBarActionBarView.findViewById(R.id.today_btn);
        mTomorrowBut = topBarActionBarView.findViewById(R.id.tomorrow_btn);
        mYesterdayBut.setOnClickListener(mClickListener);
        mTodayBut.setOnClickListener(mClickListener);
        mTomorrowBut.setOnClickListener(mClickListener);
        setTopBarActivityButton(day_info);

        if (bar != null) {
            bar.setCustomView(topBarActionBarView);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            bar.show();
//            bar.hide();
        }
    }

    @Override
    public void onSupportActionModeStarted(@NonNull android.support.v7.view.ActionMode mode) {
        super.onSupportActionModeStarted(mode);
        if (topBarActionBarView != null)
            topBarActionBarView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSupportActionModeFinished(@NonNull android.support.v7.view.ActionMode mode) {
        super.onSupportActionModeFinished(mode);
        if (topBarActionBarView != null)
            topBarActionBarView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fm_program);
        mUpdateOnLineInfo = new UpdateOnLineInfo(this);
        OberverOnLinePlayerManager.getInstance().add(this);
        mUpdateOnLineInfo.addToObserverList();
        mPlayer = OnLineFMConnectManager.Companion.getMMainInfoCode();
        mService = OnLineFMPlayerService.getMyservice();
        setTopPanelOnActionBar();

        receiver = new Receiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(OnLineFMPlayerService.UPDATE_REPLAY_UI_ACTION);
        registerReceiver(receiver, intentFilter);

        Intent i = getIntent();
        get_ID = i.getIntExtra("STATION_ID",-1);
        get_List_Type = i.getIntExtra("LIST_TYPE",0);
        get_Different_Type = i.getIntExtra("DIFFERENT",0);
        getAllInfo(get_ID, get_List_Type, get_Different_Type);

        initController();
        mStationPic = (ImageView) findViewById(R.id.station_pic);
        mStationName = (TextView) findViewById(R.id.station_name);
        mStationFreq = (TextView) findViewById(R.id.station_freq);
        mProgramList = (ListView) findViewById(R.id.program_list);
        mOnLineStationPlay = (LinearLayout) findViewById(R.id.playe_station);
        mOnLineStationPlay.setOnClickListener(mClickListener);
        mStationInfo = (LinearLayout) findViewById(R.id.station_info);

        if (mStation != null) {
            mStationName.setText(mStation.getStationTitle());
            mStationFreq.setText(String.format("%s MHz", mStation.getStationFreq()));
//            mStationPic.setImageBitmap(mPlayer.getBitmap(mStation.getStationThumbs(), 100, 100));
            UpdateListViewAsyncTask asyncTask = new UpdateListViewAsyncTask(mStationPic,
                    mStation.getStationTitle(),
                    mPlayer, 100, 100);
            asyncTask.execute(mStation.getStationThumbs());
            try {
                mProgramAdapter = new ProgramList(this, mProgram);
                mProgramList.setAdapter(mProgramAdapter);
            } catch (NullPointerException e) {
                Toast.makeText(this,"请等待，网络刷新中", Toast.LENGTH_SHORT).show();
            }
        } else {
            //map 为空  网络错误
            Toast.makeText(this,"请等待，网络刷新中", Toast.LENGTH_SHORT).show();
        }

        mProgramList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPlayType = OnLineFMPlayerService.REPLAY_PROGRAM;
                mPlayer.getReplayUrl(mStation.getStationId(),
                        mProgram.get(position).getProgramStart_time(),
                        mProgram.get(position).getProgramEnd_Time(),mPlayType);
                setPauseButton();
                updateControllerUI(mPlayType, mProgram.get(position));
            }
        });
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);
//        mIsActionMode = false;
        if (topBarActionBarView != null)
            topBarActionBarView.setVisibility(View.VISIBLE);
//        if (mOptionsMenu != null) {
//            onCreateOptionsMenu(mOptionsMenu);
//        }
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        super.onActionModeStarted(mode);
//        mIsActionMode = true;
        if (topBarActionBarView != null)
            topBarActionBarView.setVisibility(View.VISIBLE);
    }

    private void setTopBarActivityButton(int day_info) {
        mYesterdayBut.setTextColor(getResources().getColor(R.color.channel_frequency_text, null));
        mYesterdayBut.setClickable(true);
        mTodayBut.setTextColor(getResources().getColor(R.color.channel_frequency_text, null));
        mTodayBut.setClickable(true);
        mTomorrowBut.setTextColor(getResources().getColor(R.color.channel_frequency_text, null));
        mTomorrowBut.setClickable(true);
        if(day_info == 0) {
            mYesterdayBut.setTextColor(getResources().getColor(R.color.playingcolor, null));
            mYesterdayBut.setClickable(false);
        }
        if(day_info == 1) {
            mTodayBut.setTextColor(getResources().getColor(R.color.playingcolor, null));
            mTodayBut.setClickable(false);
        }
        if(day_info == 2) {
            mTomorrowBut.setTextColor(getResources().getColor(R.color.playingcolor, null));
            mTomorrowBut.setClickable(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (topBarActionBarView != null)
            topBarActionBarView.setVisibility(View.VISIBLE);
    }

    private void setPauseButton() {
        mPlayButton.setImageResource(R.drawable.listplayer_control_pause_btn);
        mPlayButton.setContentDescription(getString(R.string.desc_pause));
    }

    private void setPlayButton() {
        mPlayButton.setImageResource(R.drawable.listplayer_control_play_btn);
        mPlayButton.setContentDescription(getString(R.string.tts_play_button));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStation = null;
        map = null;
        mProgram = null;
        mClickListener = null;

        mStationPic = null;
        mStationName = null;
        mStationFreq = null;
        mProgramList = null;
        mOnLineStationPlay = null;
        mStationInfo = null;

        unregisterReceiver(receiver);
        OberverOnLinePlayerManager.getInstance().remove(this);
        mUpdateOnLineInfo.removeToObserverList();
    }

    private void getAllInfo(int ID, int ListType, int get_Different_Type) {
        try {
            if (ListType == 0) {
                if (get_Different_Type != 0) {
                    mStation = OnLineStationsActivity._activity.getDifferentStation();
                    mProgram = OnLineStationsActivity._activity.getDifferentStationProgram();
                } else {
                    mStation = mPlayer.getOnLineStationMap(mPlayer.getOnLineStations()).get(ID);
                    map = mPlayer.getOnLineStationProgramMap();
                    mProgram = map.get(mStation.getStationId());
                }
            } else {
                mStation = mPlayer.getOnLineStationMap(mPlayer.getOnLineCenterStations()).get(ID);
                map = mPlayer.getOnLineStationProgramCentermap();
                mProgram = map.get(mStation.getStationId());
            }
        } catch (NullPointerException e) {
            Log.d(TAG,"the station may be null, check if the station you tip is not station that with info");
        }
    }

    @Override
    public void observerStartPlayer(String mPlayer_Url, int play_type) {
        //StartPlayer
        mOnLinePlayerController.setVisibility(View.VISIBLE);
        mStationInfo.setVisibility(View.GONE);
        mService.StartPlay(mPlayer_Url, play_type);
    }

    @Override
    public void observerStartDNS(boolean isStart) {
        //do nothing..
    }

    private int mCurrent_Position = 0;

    private class ProgramList extends BaseAdapter {

        Context mContext;
        List<StationProgram> mPrograms = null;

        public ProgramList(Context context , List<StationProgram> mPrograms) {
            this.mContext = context;
            this.mPrograms = mPrograms;
        }

        public void setNewList(List<StationProgram> mPrograms){
            this.mPrograms = mPrograms;
        }

        @Override
        public int getCount() {
            if(mPrograms == null)
                return 0;
            else
                return mPrograms.size();
        }

        @Override
        public Object getItem(int position) {
            return mPrograms.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater =
                        (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.program_item_layout, null);
            }
            TextView mProgramName = convertView.findViewById(R.id.program_name);
            TextView mProgram_Broad_Caster = convertView.findViewById(R.id.program_broadcaster);
            TextView mStartTime = convertView.findViewById(R.id.start_time);
            TextView mFinishTime = convertView.findViewById(R.id.finish_time);
            ImageView mCanReplay = convertView.findViewById(R.id.can_replay);
            convertView.setClickable(false);
            convertView.setBackgroundColor(getResources().getColor(R.color.progress_thumb_ripple_color, null));
            mProgramName.setTextColor(getResources().getColor(R.color.channel_frequency_text, null));
            mCurrent_Position = 0;
            if(day_info == 0) {
                convertView.setClickable(false);
                convertView.setBackgroundColor(getResources().getColor(R.color.main_bg_color, null));
            }
            if(day_info == 1) {
                convertView.setClickable(true);
                mCurrent_Position = position;
                if(Objects.equals(mStation.getCurrentProgramTime(), mPrograms.get(position).getProgramStart_time())){
                    mProgramName.setTextColor(getResources().getColor(R.color.playingcolor, null));
                }
                if(position < mStation.getWhichItem()) {
                    convertView.setClickable(false);
                    convertView.setBackgroundColor(getResources().getColor(R.color.main_bg_color, null));
                }
            }
            if(day_info == 2) {
                convertView.setClickable(true);
                convertView.setBackgroundColor(getResources().getColor(R.color.progress_thumb_ripple_color, null));
            }
            mProgramName.setText(mPrograms.get(position).getProgramTitle());
            mStartTime.setText("开始时间:"+ mPrograms.get(position).getProgramStart_time());
            if(mPrograms.get(position).getBroadcaster() != null)
                mProgram_Broad_Caster.setText(mPrograms.get(position).getBroadcaster()[0].getBroadcastersName());
            mFinishTime.setText("结束时间:"+ mPrograms.get(position).getProgramEnd_Time());
            notifyDataSetChanged();
            return convertView;
        }
    }

    /* connect to service
    * **/
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        if(service instanceof OnLineFMPlayerService.ServiceBinder){
            OnLineFMPlayerService.ServiceBinder binder = (OnLineFMPlayerService.ServiceBinder)service;
            binder.getService();
        }
    }
    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    private class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Objects.equals(intent.getAction(), OnLineFMPlayerService.UPDATE_REPLAY_UI_ACTION)) {
                duration = intent.getIntExtra("Duration",0);
            }
        }
    }

    public void openDialog(int type, int position){
        RadioDialogFragment dialog = RadioDialogFragment.newInstance(type, position);
        dialog.show(getFragmentManager(), String.valueOf(type));
    }
    private void closeDialog(int type) {
        Log.v(TAG, "closeDialog() - start " + type);
        RadioDialogFragment dialog = (RadioDialogFragment) getFragmentManager().findFragmentByTag(
                String.valueOf(type));

        if (dialog != null) {
            try {
                Log.v(TAG, "removeDialog() - " + type);
                //proper dismissing the dialog for checked item if this item is changed/deleted from somewhere else
                dialog.dismissAllowingStateLoss();
            } catch (IllegalStateException e) {
                Log.d(TAG, "IllegalStateException in closeDialog:- " + e.getMessage());
            }
        }
    }
}

