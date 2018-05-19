package com.sec.myonlinefm;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
public class OnLineStationsActivity extends AppCompatActivity implements ServiceConnection,
        View.OnClickListener, ObserverPlayerListener {

    private String TAG = "OnLineStationsActivity";

    private static OnLineFMPlayerService mService;
    private RelativeLayout mOnLineLocalStation;
    private TextView mLocalNameView;
    private ImageView mAllLocalNameView;
    private Button mOnLineCenterStation;
//    fm_list_info
    private ListView mListViewInfo;
    private List<Station> mStations = null;
    private OnLineStationAdapter adapter;

    private OnLineFMConnectManager.Companion mPlayer;
    protected Handler mHandler = null;
    private AudioManager mAudioManager = null;
    final private int UPDATE_LIST_SELECT = 10;
    private int ListType = 0;

    private RadioDialogFragment dialog;

    private int mLocalStation;

    public static OnLineStationsActivity _activity;
    private UpdateOnLineInfo mUpdateOnLineInfo;

    private int CLOSE_SCAN_DIALOG = 1;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == CLOSE_SCAN_DIALOG){
                closeDialog(RadioDialogFragment.ONLINE_INFO_PROGRESS_DIALOG);
            }
        }
    };

    private View topBarActionBarView = null;

    private Map<Integer, List<StationProgram>> mMap;

    public void setTopPanelOnActionBar(String mChannelName) {
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(false);
            bar.setDisplayShowHomeEnabled(false);
            bar.setDisplayShowTitleEnabled(false);
            bar.setBackgroundDrawable(getDrawable(R.drawable.titlebar_bg));
            bar.setElevation(0);
        }
        topBarActionBarView = getLayoutInflater().inflate(R.layout.top_bar_panel_online, null);
        ImageView mBackBut = topBarActionBarView.findViewById(R.id.back_but);
        mBackBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView mTopBarTitle = topBarActionBarView.findViewById(R.id.top_bar_title);
        mTopBarTitle.setText(mChannelName);
        mTopBarTitle.setFocusable(true);
        if (bar != null) {
            bar.setCustomView(topBarActionBarView);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
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
        setContentView(R.layout.fmlist2);
        _activity = this;
        mUpdateOnLineInfo = new UpdateOnLineInfo(_activity);
        setTopPanelOnActionBar("电台列表");
        mService = OnLineFMPlayerService.getMyservice();
        mUpdateOnLineInfo.addToObserverList();
        OberverOnLinePlayerManager.getInstance().add(this);
        mPlayer = OnLineFMConnectManager.Companion;
        mOnLineLocalStation = (RelativeLayout) findViewById(R.id.fm_list_local);
        mOnLineCenterStation = (Button) findViewById(R.id.fm_list_center);
        mListViewInfo = (ListView) findViewById(R.id.fm_list_info);
        mOnLineLocalStation.setOnClickListener(this);
        mLocalNameView = (TextView) findViewById(R.id.fm_list_local_name);
        mLocalNameView.setText(mPlayer.getMGPS_Name());
        mAllLocalNameView = (ImageView) findViewById(R.id.other_local_list);
        mAllLocalNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(RadioDialogFragment.SHOW_ALL_LOCAL_NAME, -1);
            }
        });
        mOnLineCenterStation.setOnClickListener(this);
        mStations = mPlayer.getMMainInfoCode().getOnLineStations();
        mHandler = new Handler();
        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        changeColor(ListType);
        if(mStations != null && adapter == null) {
            mLocalStation = 0;
            adapter = new OnLineStationAdapter(this);
            mListViewInfo.setAdapter(adapter);
            mListViewInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//                    if (mPlayer.isBusy()) {
//                        Log.d(TAG, "RadioPlayer is busy. ignore it");
//                        return;
//                    }
                    if (Objects.equals(mStations.get(position).getStationFreq(), "") /*||
                            (mStations != RadioPlayer.getInstance().getOnLineCenterStations()
                                    &&RadioPlayer.mLocal_ID != mLocal_ID)*/) {
                        openDialog(RadioDialogFragment.IF_ONLINE_PLAY_DIALOG, position);
//                    } else {
//                        mHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                String mute = "fm_radio_mute=1";
//                                mAudioManager.setParameters(mute);
//                                try {
//                                    boolean isOn = mPlayer.isOn();
//                                    if (mStations.size() > position) {
//                                        mPlayer.tuneAsyncEx(
//                                                (int)(Float.parseFloat(mStations.get(position).getStationFreq())*100));
//                                    }
//                                    if (!isOn) {
//                                        SettingsActivity.activateTurnOffAlarm();
//                                    }
//                                    mSendMessage(UPDATE_LIST_SELECT,
//                                            (int)(Float.parseFloat(mStations.get(position).getStationFreq())*100));
//                                } catch (SemFmPlayerException e) {
//                                    RadioToast.showToast(getBaseContext(), e);
//                                }
//                            }
//                        });
                    }

                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (topBarActionBarView != null)
            topBarActionBarView.setVisibility(View.VISIBLE);
    }

    public void openDialog(int type, int position){
        dialog = RadioDialogFragment.newInstance(type, position);
        dialog.show(getFragmentManager(), String.valueOf(type));
    }
    private String mLocalName;
    private int mLocal_ID;
    public void setLocalName(String mLocalName, int mLocal_ID){
//        if(mPlayer.isOn())
//            IntervalListeningClass.getIntense().stopListener();
        openDialog(RadioDialogFragment.ONLINE_INFO_PROGRESS_DIALOG, -1);
        this.mLocalName = mLocalName;
        this.mLocal_ID = mLocal_ID;
        mLocalStation = 1;
        if(ListType == 0) {
            if(mLocal_ID == mPlayer.getMLocal_ID()) {
                handler.sendEmptyMessageDelayed(CLOSE_SCAN_DIALOG,100);
                mStations = mPlayer.getMMainInfoCode().getOnLineStations();
                mMap = mPlayer.getMMainInfoCode().getOnLineStationProgramMap();
                if(adapter == null){
                    adapter = new OnLineStationAdapter(this);
                    mListViewInfo.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
            mLocalNameView.setText(this.mLocalName);
        }
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

    private static final int MENU_LIST = 14;
    private static final int MENU_SEARCH = 15;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_LIST, MENU_LIST, "do runnable 1");
        menu.add(0, MENU_SEARCH, MENU_SEARCH, "do runnable 1");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == MENU_LIST) {

        } else if(item.getItemId() == MENU_SEARCH) {

        }
        return super.onOptionsItemSelected(item);
    }

    public void StartPlay(int position){
        mPlayer.getMMainInfoCode().getReplayUrl(mStations.get(position).getStationId(),
                null, null,OnLineFMPlayerService.PLAY_STATION);
    }

    @Override
    protected void onDestroy() {
        mUpdateOnLineInfo.removeToObserverList();
        OberverOnLinePlayerManager.getInstance().remove(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fm_list_local:
                ListType = 0;
                mLocalNameView.setText(mPlayer.getMGPS_Name());
                mLocalStation = 0;
                changeColor(ListType);
                mStations = OnLineFMConnectManager.Companion.getMMainInfoCode().getOnLineStations();
                if(adapter == null){
                    adapter = new OnLineStationAdapter(this);
                    mListViewInfo.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.fm_list_center:
                ListType = 2;
                changeColor(ListType);
                mStations = OnLineFMConnectManager.Companion.getMMainInfoCode().getOnLineCenterStations();
                adapter.notifyDataSetChanged();
                break;
        }
    }

    private void changeColor(int ListType) {
        if (ListType == 0) {
            mLocalNameView.setTextColor(getResources().getColor(R.color.tab_selected, null));
            mAllLocalNameView.setClickable(true);
            mOnLineCenterStation.setTextColor(getResources().getColor(R.color.tab_unselected, null));
        } else {
            mLocalNameView.setTextColor(getResources().getColor(R.color.tab_unselected, null));
            mLocalNameView.setText(mPlayer.getMGPS_Name());
            mAllLocalNameView.setClickable(false);
            mLocalStation = 0;
            mOnLineCenterStation.setTextColor(getResources().getColor(R.color.tab_selected, null));
        }
    }

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

        @Override
        public void observerLiveRadioLocalUIUpData(List<Station> stations, Map<Integer, List<StationProgram>> map){
            //TODO update local stations information

            if(ListType == 0) {
                mStations = stations;
                if(adapter == null){
                    adapter = new OnLineStationAdapter(context);
                    mListViewInfo.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void observerLiveRadioCenterUIUpData(List<Station> stations, Map<Integer, List<StationProgram>> map){
            //TODO update local stations information

            if(ListType != 0) {
                mStations = stations;
                if(adapter == null){
                    adapter = new OnLineStationAdapter(context);
                    mListViewInfo.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void observerDifferentInfoUIUpData(List<Station> stations, Map<Integer, List<StationProgram>> map) {
            if(ListType == 0) {
//                if(mPlayer.isOn())
//                    IntervalListeningClass.getIntense().startListener();
                closeDialog(RadioDialogFragment.ONLINE_INFO_PROGRESS_DIALOG);
                mStations = stations;
                mMap = map;
                if(adapter == null){
                    adapter = new OnLineStationAdapter(context);
                    mListViewInfo.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void observerStartPlayer(String mPlayer_Url, int play_type) {
        mService.StartPlay(mPlayer_Url, play_type);
    }

    @Override
    public void observerStartDNS(boolean isStart) {
        //do nothing..
    }

    private class OnLineStationAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context mContext = null;
        public OnLineStationAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
            this.mContext = context;
        }
        @Override
        public int getCount() {
            if(mStations != null)
                return mStations.size();
            else
                return 0;
        }

        @Override
        public Station getItem(int position) {
            return mStations.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.fmlist_item, null);
                holder = new ViewHolder();
                holder.imgProgramIconLayout = convertView.findViewById(R.id.img_program_icon_layout);
                holder.channelFreq = convertView.findViewById(R.id.list_ChanFreq);
                holder.channelName = convertView.findViewById(R.id.list_ChanName);
                holder.proLayout = convertView.findViewById(R.id.list_proLayout);
                holder.ProgramText = convertView.findViewById(R.id.list_Pro);
                holder.ProgramText.setFocusable(true);
                holder.currentProgramText = convertView.findViewById(R.id.list_currentPro);
                convertView.setTag(holder);//绑定ViewHolder对象
            } else {
                holder = (ViewHolder) convertView.getTag();//取出ViewHolder对象
            }
            holder.imgProgramIconLayout.setBackground(new BitmapDrawable(getResources(), mStations.get(position).getStationThumbs()));
//            if(mStations.get(position).getStationFreq() == null || Objects.equals(mStations.get(position).getStationFreq(), "")) {
//                holder.channelFreq.setVisibility(View.INVISIBLE);
//            }
            holder.channelFreq.setText(mStations.get(position).getStationFreq()+" MHz");
            holder.channelName.setText(mStations.get(position).getStationTitle());
            if(mStations.get(position).getCurrentProgram() != null) {
                holder.ProgramText.setText("正在播放:"+mStations.get(position).getCurrentProgram().getProgramTitle());
                holder.currentProgramText.setText("当前节目:"+mStations.get(position).getCurrentProgram().getProgramTitle()
                        +"/下一个节目:"+mStations.get(position).getNextProgram());
            }
            parent.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if (event.getAction() == KeyEvent.ACTION_UP) {
                            holder.imgProgramIconLayout.setPressed(true);
                        }
                        return true;
                    } else if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER) || (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        if (holder.imgProgramIconLayout.isPressed()
                                && event.getAction() == KeyEvent.ACTION_UP) {
                            holder.imgProgramIconLayout.performClick();
                        }
                        return true;
                    }  else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        holder.imgProgramIconLayout.setPressed(false);
                        return true;
                    } else {
                        holder.imgProgramIconLayout.setPressed(false);
                        return false;
                    }
                }
            });
            holder.imgProgramIconLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int ID = mStations.get(position).getStationId();
                    Intent intent = new Intent(mContext,FMProgramActivity.class);
                    intent.putExtra("STATION_ID",ID);
                    intent.putExtra("LIST_TYPE",ListType);
                    intent.putExtra("DIFFERENT",mLocalStation);
                    if(mLocalStation != 0){
                        setDifferentStation(mStations.get(position));
                        setDifferentStationProgram(mMap.get(ID));
                    }
                    //intent.putExtra("src",imageSrc);
                    startActivity(intent);
                }
            });

            try {
//                if (Objects.equals(Manager.convertToMhz(RadioPlayer.getInstance().getFrequency()),
//                        mStations.get(position).getStationFreq())) {
//                    holder.channelFreq.setPadding(0, 0, 0, 0);
//                    holder.channelFreq.setTextColor(
//                            getResources().getColor(R.color.channel_frequency_text_playing, null));
//                    holder.channelName.setTextColor(
//                            getResources().getColor(R.color.channel_frequency_text_playing, null));
//                    //            holder.proLayout.setVisibility(View.VISIBLE);
//                    holder.currentProgramText.setVisibility(View.GONE);
//                    holder.ProgramText.setVisibility(View.VISIBLE);
//                    holder.ProgramText.setSelected(true);
//                } else {
                    holder.channelFreq.setPadding(0, 0, 0, 0);
                    holder.channelFreq.setTextColor(
                            getResources().getColor(R.color.channel_frequency_text, null));
                    holder.channelName.setTextColor(
                            getResources().getColor(R.color.channel_frequency_text, null));
                    holder.ProgramText.setSelected(false);
                    holder.ProgramText.setVisibility(View.GONE);
                    holder.currentProgramText.setVisibility(View.VISIBLE);

                    //        holder.proLayout.setVisibility(View.GONE);
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            ((ViewGroup) convertView).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            return convertView;
        }
    }

    static class ViewHolder {
        TextView channelFreq;

        TextView channelName;

        TextView ProgramText;

        TextView currentProgramText;

        LinearLayout imgProgramIconLayout;

        LinearLayout proLayout;
    }
    @SuppressLint("HandlerLeak")
    Handler mListHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_LIST_SELECT:
                    android.util.Log.d("testw-", "handleMessage: UPDATE_LIST_SELECT");
                    //      android.util.Log.d("testw-", "handleMessage: " + mAdapter + " " + mPlayer.getFrequency());
//                    if (mPlayer.getFrequency() == msg.arg1) {
//                        if (adapter != null) {
//                            adapter.notifyDataSetChanged();
//                        }
//                        if (mListHandler.hasMessages(UPDATE_LIST_SELECT)) {
//                            mListHandler.removeMessages(UPDATE_LIST_SELECT);
//                        }
//                    } else {
                        if (mListHandler.hasMessages(UPDATE_LIST_SELECT)) {
                            mListHandler.removeMessages(UPDATE_LIST_SELECT);
                        }
                        mSendMessage(UPDATE_LIST_SELECT, msg.arg1, 100);
//                    }
                    break;
            }
        }
    };
    private void mSendMessage(int what, int arg1) {
        Message mMessage = new Message();
        mMessage.what = what;
        mMessage.arg1 = arg1;
        mListHandler.sendMessage(mMessage);
    }

    private void mSendMessage(int what, String data) {
        Message mMessage = new Message();
        mMessage.what = what;
        mMessage.obj = data;
        mListHandler.sendMessage(mMessage);
    }

    private void mSendMessage(int what, int arg1, long delay) {
        Message mMessage = new Message();
        mMessage.what = what;
        mMessage.arg1 = arg1;
        mListHandler.sendMessageDelayed(mMessage, delay);
    }

    private Station mStation;
    private List<StationProgram> mProgram;
    private void setDifferentStation(Station mStation){
        this.mStation = mStation;
    }
    private void setDifferentStationProgram(List<StationProgram> mProgram){
        this.mProgram = mProgram;
    }
    public Station getDifferentStation(){
        return mStation;
    }
    public List<StationProgram> getDifferentStationProgram(){
        return mProgram;
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
}
