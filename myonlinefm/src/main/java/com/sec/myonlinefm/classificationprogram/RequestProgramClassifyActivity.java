package com.sec.myonlinefm.classificationprogram;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.sec.myonlinefm.OnLineFMPlayerService;
import com.sec.myonlinefm.R;
import com.sec.myonlinefm.SearchActivity;
import com.sec.myonlinefm.classificationprogram.mainfacefragment.OnLineFavFragment;
import com.sec.myonlinefm.classificationprogram.mainfacefragment.OnLineHomeFragment;
import com.sec.myonlinefm.dbdata.MySQLHelper;

public class RequestProgramClassifyActivity extends AppCompatActivity implements ServiceConnection , View.OnClickListener{

    private static OnLineFMPlayerService mService;

    private View topBarActionBarView = null;

    private String TAG = "RequestProgramClassifyActivity";
    private String mParam1 = TAG;
    private String mParam2;

    private OnLineHomeFragment home;
    private OnLineFavFragment favFragment;

    private String mCurrentFragmentTag;

    private void setTopPanelOnActionBar(String title, boolean isShowSearchView) {
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
        ImageView mBackBut = topBarActionBarView.findViewById(R.id.back_but);
        mBackBut.setVisibility(View.INVISIBLE);
        TextView mTopBarTitle = topBarActionBarView.findViewById(R.id.top_bar_title);
        SearchView mOnLineSearchView = topBarActionBarView.findViewById(R.id.online_search);
        mOnLineSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RequestProgramClassifyActivity.this, SearchActivity.class);
                startActivity(i);
            }
        });
        mTopBarTitle.setText(title);
        if(isShowSearchView) {
            mTopBarTitle.setVisibility(View.GONE);
            mOnLineSearchView.setVisibility(View.VISIBLE);
        } else {
            mTopBarTitle.setVisibility(View.VISIBLE);
            mOnLineSearchView.setVisibility(View.GONE);
        }
        if (bar != null) {
            bar.setCustomView(topBarActionBarView);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            bar.show();
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

    private FragmentTransaction ft;
    private ImageView mFavIcon;
    private ImageView mHomeIcon;
    private ImageView mDownLoadIcon;
    private ImageView mUserIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_program_classify);
        MySQLHelper mySQLHelper = new MySQLHelper(this, "onLineFM.db", null, 1);

        ft = getSupportFragmentManager().beginTransaction();
        home = (OnLineHomeFragment)getSupportFragmentManager().findFragmentByTag(OnLineHomeFragment.TAG);
        mCurrentFragmentTag = OnLineHomeFragment.TAG;
        if(home == null) {
            mParam2 = "home";
            ft.add(OnLineHomeFragment.newInstance(mParam1, mParam2), OnLineHomeFragment.TAG);
        } else {
            ft.attach(home);
        }
        ft.commitAllowingStateLoss();

        mFavIcon = (ImageView) findViewById(R.id.into_fave_icon);
        mHomeIcon = (ImageView) findViewById(R.id.home);
        mDownLoadIcon = (ImageView) findViewById(R.id.down_load_file);
        mUserIcon = (ImageView) findViewById(R.id.user_info);

        mFavIcon.setClickable(true);
        mFavIcon.setOnClickListener(this);

        mHomeIcon.setClickable(true);
        mHomeIcon.setOnClickListener(this);

        mDownLoadIcon.setClickable(true);
        mDownLoadIcon.setOnClickListener(this);

        mUserIcon.setClickable(true);
        mUserIcon.setOnClickListener(this);

        mService = OnLineFMPlayerService.getMyservice();

        upDateActionBarView ();
        upDateImageTint();
    }

    public void upDateActionBarView () {
        switch (mCurrentFragmentTag) {
            case OnLineHomeFragment.TAG:
                setTopPanelOnActionBar("NetRadio", true);
                break;
            case OnLineFavFragment.TAG:
                setTopPanelOnActionBar("我的收藏", false);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (topBarActionBarView != null)
            topBarActionBarView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    private int mCurrentMainFragment = R.id.home;

    void upDateImageTint() {
        switch (mCurrentMainFragment) {
            case R.id.home:
                mHomeIcon.setColorFilter(getResources().getColor(R.color.net_fm_back_b));
                mFavIcon.setColorFilter(getResources().getColor(R.color.net_fm_back_a));
                mDownLoadIcon.setColorFilter(getResources().getColor(R.color.net_fm_back_a));
                mUserIcon.setColorFilter(getResources().getColor(R.color.net_fm_back_a));
                break;
            case R.id.into_fave_icon:
                mHomeIcon.setColorFilter(getResources().getColor(R.color.net_fm_back_a));
                mFavIcon.setColorFilter(getResources().getColor(R.color.net_fm_back_b));
                mDownLoadIcon.setColorFilter(getResources().getColor(R.color.net_fm_back_a));
                mUserIcon.setColorFilter(getResources().getColor(R.color.net_fm_back_a));
                break;
            case R.id.down_load_file:
                mHomeIcon.setColorFilter(getResources().getColor(R.color.net_fm_back_a));
                mFavIcon.setColorFilter(getResources().getColor(R.color.net_fm_back_a));
                mDownLoadIcon.setColorFilter(getResources().getColor(R.color.net_fm_back_b));
                mUserIcon.setColorFilter(getResources().getColor(R.color.net_fm_back_a));
                break;
            case R.id.user_info:
                mHomeIcon.setColorFilter(getResources().getColor(R.color.net_fm_back_a));
                mFavIcon.setColorFilter(getResources().getColor(R.color.net_fm_back_a));
                mDownLoadIcon.setColorFilter(getResources().getColor(R.color.net_fm_back_a));
                mUserIcon.setColorFilter(getResources().getColor(R.color.net_fm_back_b));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.into_fave_icon:
                favFragment = (OnLineFavFragment)getSupportFragmentManager().findFragmentByTag(OnLineFavFragment.TAG);
                mCurrentFragmentTag = OnLineFavFragment.TAG;
                if(favFragment == null) {
                    mParam2 = "favFragment";
                    favFragment = OnLineFavFragment.newInstance(mParam1, mParam2);
                }
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.main_online_fragment, favFragment);
                ft.commit();
                mCurrentMainFragment = R.id.into_fave_icon;
                break;
            case R.id.home:
                mCurrentFragmentTag = OnLineHomeFragment.TAG;
                home = (OnLineHomeFragment)getSupportFragmentManager().findFragmentByTag(OnLineHomeFragment.TAG);
                if(home == null) {
                    mParam2 = "home";
                    home = OnLineHomeFragment.newInstance(mParam1, mParam2);
                }
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.main_online_fragment, home);
                ft.commit();
                mCurrentMainFragment = R.id.home;
                break;
        }
        upDateActionBarView ();
        upDateImageTint();
    }
}
