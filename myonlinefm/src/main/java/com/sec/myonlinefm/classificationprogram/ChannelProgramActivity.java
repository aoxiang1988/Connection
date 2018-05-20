package com.sec.myonlinefm.classificationprogram;

import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.sec.myonlinefm.OnLineFMConnectManager;
import com.sec.myonlinefm.R;
import com.sec.myonlinefm.UpdateListViewAsyncTask;
import com.sec.myonlinefm.classificationprogram.data.DemandChannel;
import com.sec.myonlinefm.classificationprogram.data.DemandChannelPattern;
import com.sec.myonlinefm.classificationprogram.fragment.ChannelProgramListFragment;
import com.sec.myonlinefm.classificationprogram.fragment.ChannelRecommendListFragment;
import com.sec.myonlinefm.dbdata.MySQLHelper;
import com.sec.myonlinefm.defineview.FastBlur;
import com.sec.myonlinefm.defineview.RoundImageView;

/* https://blog.csdn.net/xx23x/article/details/54617928
 * */

public class ChannelProgramActivity extends AppCompatActivity implements View.OnClickListener {

    private int mChannelID = -1;
    private String mChannelName;
    private TabHost mTabHost;
    private TabWidget mTabWidget;

    private OnLineFMConnectManager mPlayer;
    private DemandChannel currentDemandChannel;
    public static ChannelProgramActivity _activity;

    private View topBarActionBarView = null;
    private ImageView mCurrentChannelPic;
    private TextView mCurrentChannelDescription;
    private RoundImageView mPodCasterPic;
    private TextView mPodCasterName;
    private LinearLayout mChannelInfoView;
    private MySQLHelper mySQLHelper;
    private SQLiteDatabase db;
    private ImageView mFavOnLineChannel;

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
    protected void onDestroy() {
        mHandler.removeMessages(UPDATE_CURRENT_DEMAND_CHANNEL_INFO);
        System.gc();
        super.onDestroy();
    }

    private static final int UPDATE_CURRENT_DEMAND_CHANNEL_INFO = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_CURRENT_DEMAND_CHANNEL_INFO:
//                    mCurrentChannelPic.setImageBitmap(mPlayer.getBitmap(currentDemandChannel.getThumbs(),
//                            100, 100));
                    UpdateListViewAsyncTask asyncTask = new UpdateListViewAsyncTask(mCurrentChannelPic, mPlayer, 60,60);
                    asyncTask.execute(currentDemandChannel.getThumbs());

                    UpdateListViewAsyncTask asyncTaskForBlur = new UpdateListViewAsyncTask(getBaseContext(),
                            mPlayer, mChannelInfoView, true,
                            mChannelInfoView.getWidth(), mChannelInfoView.getHeight());
                    asyncTaskForBlur.execute(currentDemandChannel.getThumbs());
//                    blur(mPlayer.getBitmap(currentDemandChannel.getThumbs(),
//                            mChannelInfoView.getWidth(), mChannelInfoView.getHeight()), mChannelInfoView);
                    mCurrentChannelDescription.setText(currentDemandChannel.getDescription());
                    if(currentDemandChannel.getDetail().getPodCasters() != null
                            && !currentDemandChannel.getDetail().getPodCasters().isEmpty()) {
//                        mPodCasterPic.setImageBitmap(mPlayer.getBitmap(currentDemandChannel.getDetail().getPodCasters().get(0).getImgUrl(),
//                                40, 40));
                        UpdateListViewAsyncTask asyncTaskCaster = new UpdateListViewAsyncTask(mPodCasterPic, mPlayer, 60,60);
                        asyncTaskCaster.execute(currentDemandChannel.getDetail().getPodCasters().get(0).getImgUrl());
                        mPodCasterName.setText(currentDemandChannel.getDetail().getPodCasters().get(0).getNickName());
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_program);
        _activity = this;
        Intent i = getIntent();
        mChannelID = i.getIntExtra("channel_id", -1);
        mChannelName = i.getStringExtra("channel_name");
        mFavOnLineChannel = (ImageView) findViewById(R.id.fav_on_line_channel);
        mFavOnLineChannel.setClickable(true);
        mFavOnLineChannel.setOnClickListener(this);
        initView();
        mPlayer = OnLineFMConnectManager.Companion.getMMainInfoCode();
        setUpdateCurrentDemandChannelInfo(mChannelName, mChannelID, false);
        mySQLHelper = MySQLHelper.getInstances();
        db = mySQLHelper.getWritableDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!checkIfChannelInDB(mChannelID)){
            mFavOnLineChannel.setImageResource(R.drawable.hybrid_radio_on_star_ripple);
            mFavOnLineChannel.setColorFilter(getResources().getColor(R.color.net_fm_back_b));
        } else {
            mFavOnLineChannel.setImageResource(R.drawable.hybrid_radio_radio_off_start_ripple);
            mFavOnLineChannel.setColorFilter(getResources().getColor(R.color.net_fm_back_a));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fav_on_line_channel:
                if(checkIfChannelInDB(mChannelID)){
                    ContentValues values = new ContentValues();
                    values.put("channel_id", mChannelID);
                    values.put("channel_name", mChannelName);
                    values.put("category_id", currentDemandChannel.getCategoryId());
                    values.put("channel_them_url", currentDemandChannel.getThumbsUrl());
                    values.put("pod_caster_name", currentDemandChannel.getDetail().getPodCasters().get(0).getNickName());
                    db.insert("fav_channels", null, values);
                    Toast.makeText(this, "收藏完成", Toast.LENGTH_SHORT).show();
                    mFavOnLineChannel.setImageResource(R.drawable.hybrid_radio_on_star_ripple);
                    mFavOnLineChannel.setColorFilter(getResources().getColor(R.color.net_fm_back_b));
                } else {
                    db.delete("fav_channels", "channel_id = ?", new String[]{String.valueOf(mChannelID)});
                    Toast.makeText(this, "收藏移除", Toast.LENGTH_SHORT).show();
                    mFavOnLineChannel.setImageResource(R.drawable.hybrid_radio_radio_off_start_ripple);
                    mFavOnLineChannel.setColorFilter(getResources().getColor(R.color.net_fm_back_a));
                }
                break;
        }
    }

    private boolean checkIfChannelInDB(int mChannelID) {
        Cursor cursor = db.query("fav_channels",null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int pid = cursor.getInt(cursor.getColumnIndex("channel_id"));
                String name = cursor.getString(cursor.getColumnIndex("channel_name"));
                if(pid == mChannelID) {
                    Toast.makeText(this, "已收藏"+name, Toast.LENGTH_SHORT).show();
                    cursor.close();
                    return false;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return true;
    }

    public void setUpdateCurrentDemandChannelInfo(String mChannelName, int mChannelID , boolean isRecommend) {
        if(isRecommend) {
            this.mChannelName = mChannelName;
            this.mChannelID = mChannelID;
        }
        initializeTabContent();
        setTopPanelOnActionBar(mChannelName);
        if(mChannelID != -1) {
            mPlayer.getCurrentDemandChannel(mChannelID, new RequestCallBack<DemandChannelPattern>() {
                @Override
                public void onSuccess(DemandChannelPattern val) {
                    currentDemandChannel = val.getCurrentDemandChannel();
                    mHandler.sendEmptyMessage(UPDATE_CURRENT_DEMAND_CHANNEL_INFO);
                }

                @Override
                public void onFail(String errorMessage) {
                    Log.d("bin1111.yang","CurrentDemandChannel onFail : "+errorMessage);
                }
            });
        }
    }

    private void initView() {
        mCurrentChannelPic = (ImageView)findViewById(R.id.current_channel_pic);
        mCurrentChannelDescription = (TextView) findViewById(R.id.current_channel_description);
        mPodCasterPic = (RoundImageView)findViewById(R.id.podcasters_pic);
        mPodCasterPic.setShapeType(1);
        mPodCasterName = (TextView) findViewById(R.id.podcasters_name);
        mChannelInfoView = (LinearLayout) findViewById(R.id.channel_info_view);
    }

    private void initMyTabHost(int position) {
        //修改背景
        mTabWidget.getChildAt(position).setBackgroundResource(
                R.drawable.custom_tab_indicator);
    }

    private void initializeTabContent() {
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabWidget = mTabHost.findViewById(android.R.id.tabs);

        mTabHost.setup();
        mTabHost.clearAllTabs();
        mTabHost.setOnTabChangedListener(mTabChangeListener);

        /*
        * * Defining tab builder for Channels tab */
        TabHost.TabSpec stationsTab = mTabHost.newTabSpec(ChannelProgramListFragment.TAG);
        stationsTab.setIndicator("节目");
        stationsTab.setContent(new FMTabContent(getBaseContext()));
        mTabHost.addTab(stationsTab);

        /*
        * * Defining tab builder for program tab */
        TabHost.TabSpec programTab = mTabHost.newTabSpec(ChannelRecommendListFragment.TAG);
        programTab.setIndicator("推荐");
        programTab.setContent(new FMTabContent(getBaseContext()));
        mTabHost.addTab(programTab);
    }

    public static class FMTabContent implements TabHost.TabContentFactory {
        private Context mContext;

        public FMTabContent(Context context) {
            mContext = context;
        }

        @Override
        public View createTabContent(String tag) {
            View v = new View(mContext);
            return v;
        }
    }

    public TabHost getTabHost() {
        return mTabHost;
    }

    TabHost.OnTabChangeListener mTabChangeListener = new TabHost.OnTabChangeListener() {
        @Override
        public void onTabChanged(String tabId) {
            String mType = null;
            switch (tabId) {
                case "ChannelProgramListFragment":
                    initMyTabHost(0);
                    break;
                case "ChannelRecommendListFragment":
                    initMyTabHost(1);
                    break;
            }
            android.app.FragmentManager fm = getFragmentManager();
            ChannelProgramListFragment channelProgramListFragment = (ChannelProgramListFragment) fm.findFragmentByTag(ChannelProgramListFragment.TAG);
            ChannelRecommendListFragment channelRecommendListFragment = (ChannelRecommendListFragment) fm.findFragmentByTag(ChannelRecommendListFragment.TAG);
            FragmentTransaction ft = fm.beginTransaction();

            if (channelProgramListFragment != null)
                ft.detach(channelProgramListFragment);

            if (channelRecommendListFragment != null)
                ft.detach(channelRecommendListFragment);

            if (tabId.equalsIgnoreCase(ChannelProgramListFragment.TAG)) {
                if (channelProgramListFragment == null) {
                    ft.add(android.R.id.tabcontent, ChannelProgramListFragment.newInstance(mChannelID), ChannelProgramListFragment.TAG);
                } else {
                    channelProgramListFragment.setCurrentChannelID(mChannelID);
                    ft.attach(channelProgramListFragment);
                }

            } else {
                if (channelRecommendListFragment == null) {
                    ft.add(android.R.id.tabcontent, ChannelRecommendListFragment.newInstance(currentDemandChannel.getCategoryId()), ChannelRecommendListFragment.TAG);
                } else {
                    channelRecommendListFragment.setCurrentCategoryID(currentDemandChannel.getCategoryId());
                    ft.attach(channelRecommendListFragment);
                }
            }

            ft.commitAllowingStateLoss();
        }
    };
}
