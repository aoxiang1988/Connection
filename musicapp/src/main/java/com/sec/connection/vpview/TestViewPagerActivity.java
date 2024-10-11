package com.sec.connection.vpview;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.sec.connection.BaseListInfo;
import com.sec.connection.MainActivity;
import com.sec.connection.MainService;
import com.sec.connection.R;
import com.sec.connection.view.NewImageView;
import com.sec.connection.vpview.FragmentViewPager.AlbumListFragment;
import com.sec.connection.vpview.FragmentViewPager.AllListFragment;
import com.sec.connection.vpview.FragmentViewPager.ArtistMusicListFragment;
import com.sec.connection.vpview.FragmentViewPager.FilterListFragment;

import java.util.ArrayList;
import java.util.List;

public class TestViewPagerActivity extends AppCompatActivity {

    private static final String UPDATE_ACTION = "com.example.action.UPDATE_ACTION";
    private static final String UPDATE_LIST_ACTIVITY_ACTION = "com.example.action.UPDATE_LIST_ACTIVITY_ACTION";
    //	public static final String DELETE_ITEM = "com.example.action.DELETE_ITEM";
    private static final String PLAY_STATUE = "com.example.action.PLAY_STATUE";
    public static final String CTL_ACTION = "com.example.action.CTL_ACTION";
    private static final String MUSIC_CURRENT = "com.example.action.MUSIC_CURRENT";
    private static final String MUSIC_DURATION = "com.example.action.MUSIC_DURATION";
    private static final String CURRENT_ID = "com.example.action.CURRENT_ID";

    private final String TAG = "TestViewPagerActivity";
    private ViewPager viewPager = null;
    private final List<Fragment> viewContainer = new ArrayList<Fragment>();   //存放容器
    private TabWidget mTabWidget = null;
    @SuppressLint("StaticFieldLeak")
    public static TestViewPagerActivity _activity;
    private int count;

    /*controller*/
    private NewImageView listMusicView;
    private Button mListPre;
    private Button mListNext;
    private Button mListStart;
    private Button mListStop;
    private TextView mListMusicName;

    View actionBar;
    private ViewReceiver mViewReceiver;

    @SuppressLint({"MissingInflatedId", "UnspecifiedRegisterReceiverFlag"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_view_pager);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UPDATE_ACTION);
        intentFilter.addAction(PLAY_STATUE);
        intentFilter.addAction(CTL_ACTION);
        intentFilter.addAction(MUSIC_DURATION);
        intentFilter.addAction(MUSIC_CURRENT);
        intentFilter.addAction(CURRENT_ID);
        intentFilter.addAction(UPDATE_LIST_ACTIVITY_ACTION);
        mViewReceiver = new ViewReceiver();
        registerReceiver(mViewReceiver, intentFilter);

        actionBar = findViewById(R.id.action_bar);
        actionBar.setBackgroundColor(getResources().getColor(R.color.playingcolor));
        actionBar.setVisibility(View.GONE);
        initViewPagerContainer();
        _activity = this;
        count = 0;
        initcontrollerview();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
//        initViewPagerContainer();  //初始viewPager
        //声明适配器
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), viewContainer);//);
        //设置adapter的适配器
        viewPager.setAdapter(viewPagerAdapter);
        //设置viewPager的监听器
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int pos;
            int s;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                pos = position;
            }
            //当 滑动 切换时
            @Override
            public void onPageSelected(int position) {
                mTabWidget.setCurrentTab(position);
                initMyTabHost(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                s = state;
            }
        });
        viewPager.setPageTransformer(true, new NewView());
        TabHost mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();
        mTabWidget = mTabHost.getTabWidget();
        for (int i = 0; i < mTabWidget.getChildCount(); i++){
            initMyTabHost(i);
        }
        /*
         * newTabSpec（）   就是给每个Tab设置一个ID
         * setIndicator()   每个Tab的标题
         * setCount()       每个Tab的标签页布局
         */
        mTabHost.addTab(mTabHost.newTabSpec("tab1")
                .setContent(R.id.tab1).setIndicator(getString(R.string.all_musics)));

        mTabHost.addTab(mTabHost.newTabSpec("tab2")
                .setContent(R.id.tab2).setIndicator(getString(R.string.artist)));

        mTabHost.addTab(mTabHost.newTabSpec("tab3")
                .setContent(R.id.tab3).setIndicator(getString(R.string.album)));

        mTabHost.addTab(mTabHost.newTabSpec("tab4")
                .setContent(R.id.tab4).setIndicator(getString(R.string.filter_list)));
        //TabHost的监听事件
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                switch (tabId) {
                    case "tab1":
                        count = count + 1;
                        viewPager.setCurrentItem(0);
                        initMyTabHost(0);
                        break;
                    case "tab2":
                        count = count + 1;
                        viewPager.setCurrentItem(1);
                        initMyTabHost(1);
                        break;
                    case "tab3":
                        count = count + 1;
                        viewPager.setCurrentItem(2);
                        initMyTabHost(2);
                        break;
                    case "tab4":
                        count = count + 1;
                        viewPager.setCurrentItem(3);
                        initMyTabHost(3);
                        break;
                }
            }
        });

        //解决开始时不显示viewPager
        mTabHost.setCurrentTab(1);
        mTabHost.setCurrentTab(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MainService.list.get(MainActivity.getCurrentPosition()).getBitmap() != null)
            listMusicView.setImageBitmap(NewImageView.createReflectedImage(
                    MainService.list.get(MainActivity.getCurrentPosition()).getBitmap()
            ));
        mListMusicName.setText(MainService.list.get(MainActivity.getCurrentPosition()).getTitle());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mViewReceiver);
    }

    public int getviewpageritem() {
        return viewPager.getCurrentItem();
    }
    public int getviewpagercount() {
        return viewPager.getCurrentItem();
    }

    private void initMyTabHost(int position) {
        //修改背景
        mTabWidget.getChildAt(position).setBackgroundResource(
                R.drawable.custom_tab_indicator);
    }

    //初始化viewPager
    public void initViewPagerContainer(){
        //加入ViewPage的容器
        Fragment f1 = AllListFragment.newInstance("All_List_Fragment", "new_Instance");
        Fragment f2 = ArtistMusicListFragment.newInstance("Artist_Music_List_Fragment", "new_Instance");
        Fragment f3 = AlbumListFragment.newInstance("Album_List_Fragment", "new_Instance");
        Fragment f4 = FilterListFragment.newInstance("Filter_Fragment", "new_Instance");

        viewContainer.add(f1);
        viewContainer.add(f2);
        viewContainer.add(f3);
        viewContainer.add(f4);
    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> viewContainer;
        ViewPagerAdapter(FragmentManager fm, List<Fragment> viewContainer) {
            super(fm);
            this.viewContainer = viewContainer;
        }

        @Override
        public Fragment getItem(int position) {
            return viewContainer.get(position);
        }

        //该方法 决定 并 返回 viewpager中组件的数量
        @Override
        public int getCount() {
            return viewContainer.size();
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return PagerAdapter.POSITION_NONE;
        }
    }

    private View.OnClickListener listener;

    private void initcontrollerview(){
        listMusicView = (NewImageView) findViewById(R.id.list_ac_p);
        listMusicView.setShapeType(2);
        listMusicView.setRadius(15);
        listMusicView.setBorderWidth(5);
        listMusicView.setStrokeWidth(160);
        listMusicView.setClickable(true);
//        listmusicview.setBorderColor(getResources().getColor(R.color.flingview_borad));
        listMusicView.setPressColor(getResources().getColor(R.color.flingview_press));
        listMusicView.setImageBitmap(NewImageView.createReflectedImage(
                ((BitmapDrawable) getResources().getDrawable(R.drawable.ic)).getBitmap()));
        listMusicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestViewPagerActivity.this.finish();
            }
        });

        mListPre = (Button) findViewById(R.id.st_ac_pre);
        mListPre.setOnClickListener(listener);

        mListNext = (Button)findViewById(R.id.st_ac_next);
        mListNext.setOnClickListener(listener);

        mListStart = (Button)findViewById(R.id.st_ac_start);
        mListStart.setOnClickListener(listener);

        mListStop = (Button)findViewById(R.id.st_ac_stop);
        mListStop.setOnClickListener(listener);

        Button listPause = (Button) findViewById(R.id.st_ac_pause);
        listPause.setOnClickListener(listener);

        mListMusicName = (TextView) findViewById(R.id.list_ac_musicname);

        listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (v.getId() == R.id.st_ac_start) {
                    PlayMusicUIUpdate(MainActivity.getCurrentPosition());
                } else if (v.getId() == R.id.st_ac_stop) {
                    PlayMusicUIUpdate(MainActivity.getCurrentPosition());
                }

                /*switch (v.getId()) {
                    case R.id.st_ac_pre :

                        break;
                    case R.id.st_ac_next :

                        break;
                    case R.id.st_ac_start :
                        PlayMusicUIUpdate(MainActivity.getcurrentposition());
                        break;
                    case R.id.st_ac_stop :
                        PlayMusicUIUpdate(MainActivity.getcurrentposition());
                        break;
                }*/
            }
        };
    }

    private void PlayMusicUIUpdate(int position) {
        // TODO Auto-generated method stub
        mListStart.setVisibility(View.GONE);
        mListStop.setVisibility(View.VISIBLE);
        mListMusicName.setText(BaseListInfo.getInstance().getList().get(position).getTitle());

        if(position == 0) {
            mListPre.setBackground(getResources().getDrawable(R.drawable.disable_pre, null));
        }
        else{
            mListPre.setBackground(getResources().getDrawable(R.drawable.previous_button_ripple, null));
        }
        if(position+1 == BaseListInfo.getInstance().getList().size()) {
            mListNext.setBackground(getResources().getDrawable(R.drawable.disable_next, null));
        }
        else{
            mListNext.setBackground(getResources().getDrawable(R.drawable.next_button_ripple, null));
        }
    }

    public boolean isPlaying = false;

    private class ViewReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (action.equals(PLAY_STATUE)) {
                isPlaying = intent.getBooleanExtra("isplay", false);
//                if (!isPlaying) {
//                    if (mService.getplayerstatus()) {
//                        stop();
//                    }
//                }
            }
            int listPosition = 0;
            if (action.equals(UPDATE_LIST_ACTIVITY_ACTION)) {
                listPosition = intent.getIntExtra("current_music", 0);
                PlayMusicUIUpdate(listPosition);
            }
            if (action.equals(UPDATE_ACTION)) {
                listPosition = intent.getIntExtra("current_music", -1);
                String title = MainService.list.get(listPosition).getTitle();
                mListMusicName.setText(title);
                PlayMusicUIUpdate(listPosition);
            }
            if (action.equals(CURRENT_ID)) {
                listPosition = intent.getIntExtra("current_id", -1);
            }
        }
    }
}
