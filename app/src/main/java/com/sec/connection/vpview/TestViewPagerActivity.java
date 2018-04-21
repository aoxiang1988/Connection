package com.sec.connection.vpview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.sec.connection.BaseListInfo;
import com.sec.connection.MainService;
import com.sec.connection.R;
import com.sec.connection.MainActivity;
import com.sec.connection.MusicApplication;
import com.sec.connection.vpview.FragmentViewPager.AlbumListFragment;
import com.sec.connection.vpview.FragmentViewPager.AllListFragment;
import com.sec.connection.vpview.FragmentViewPager.ArtistMusicListFragment;
import com.sec.connection.vpview.FragmentViewPager.FilterListFragment;
import com.sec.connection.view.NewImageView;

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

    private String TAG = "TestViewPagerActivity";
    private ViewPager viewPager = null;
    private List<Fragment> viewContainter = new ArrayList<Fragment>();   //存放容器
    private ViewPagerAdapter viewPagerAdapter = null;   //声明适配器
    private TabHost mTabHost = null;
    private TabWidget mTabWidget = null;
    public static TestViewPagerActivity _activity;
    private int count;

    /*controller*/
    private NewImageView listmusicview;
    private Button listpre;
    private Button listnext;
    private Button liststart;
    private Button liststop;
    private Button listpause;
    private TextView listmusicname;

    private Fragment f1;
    private Fragment f2;
    private Fragment f3;
    private Fragment f4;

    View actionBar;
    private ViewReceiver mViewReceiver;

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
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), viewContainter);//);
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
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
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
        if(MainService.list.get(MainActivity.getcurrentposition()).getBitmap() != null)
            listmusicview.setImageBitmap(NewImageView.createReflectedImage(
                    MainService.list.get(MainActivity.getcurrentposition()).getBitmap()
            ));
        listmusicname.setText(MainService.list.get(MainActivity.getcurrentposition()).getTitle());
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
        f1 = AllListFragment.newInstance("All_List_Fragment", "new_Instance");
        f2 = ArtistMusicListFragment.newInstance("Artist_Music_List_Fragment", "new_Instance");
        f3 = AlbumListFragment.newInstance("Album_List_Fragment", "new_Instance");
        f4 = FilterListFragment.newInstance("Filter_Fragment", "new_Instance");

        viewContainter.add(f1);
        viewContainter.add(f2);
        viewContainter.add(f3);
        viewContainter.add(f4);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

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
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }
    }

    private View.OnClickListener listener;

    private void initcontrollerview(){
        listmusicview = (NewImageView) findViewById(R.id.list_ac_p);
        listmusicview.setShapeType(2);
        listmusicview.setRadius(15);
        listmusicview.setBorderWidth(5);
        listmusicview.setStrokeWidth(160);
        listmusicview.setClickable(true);
//        listmusicview.setBorderColor(getResources().getColor(R.color.flingview_borad));
        listmusicview.setPressColor(getResources().getColor(R.color.flingview_press));
        listmusicview.setImageBitmap(NewImageView.createReflectedImage(
                ((BitmapDrawable) getResources().getDrawable(R.drawable.ic)).getBitmap()));
        listmusicview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestViewPagerActivity.this.finish();
            }
        });

        listpre = (Button) findViewById(R.id.st_ac_pre);
        listpre.setOnClickListener(listener);

        listnext = (Button)findViewById(R.id.st_ac_next);
        listnext.setOnClickListener(listener);

        liststart = (Button)findViewById(R.id.st_ac_start);
        liststart.setOnClickListener(listener);

        liststop = (Button)findViewById(R.id.st_ac_stop);
        liststop.setOnClickListener(listener);

        listpause = (Button)findViewById(R.id.st_ac_pause);
        listpause.setOnClickListener(listener);

        listmusicname = (TextView) findViewById(R.id.list_ac_musicname);

        listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
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
                }
            }
        };
    }

    private void PlayMusicUIUpdate(int position) {
        // TODO Auto-generated method stub
        liststart.setVisibility(View.GONE);
        liststop.setVisibility(View.VISIBLE);
        listmusicname.setText(BaseListInfo.getInstance().getList().get(position).getTitle());

        if(position == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                listpre.setBackground(getResources().getDrawable(R.drawable.disable_pre, null));
            } else {
                listpre.setBackgroundDrawable(getResources().getDrawable(R.drawable.disable_pre));
            }
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                listpre.setBackground(getResources().getDrawable(R.drawable.previous_button_ripple, null));
            } else {
                listpre.setBackgroundDrawable(getResources().getDrawable(R.drawable.previous_button_ripple));
            }
        }
        if(position+1 == BaseListInfo.getInstance().getList().size()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                listnext.setBackground(getResources().getDrawable(R.drawable.disable_next, null));
            } else {
                listpre.setBackgroundDrawable(getResources().getDrawable(R.drawable.disable_next));
            }
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                listnext.setBackground(getResources().getDrawable(R.drawable.next_button_ripple, null));
            } else {
                listpre.setBackgroundDrawable(getResources().getDrawable(R.drawable.next_button_ripple));
            }
        }
    }
    private static int listPosition = 0;
    public boolean isPlaying = false;

    private class ViewReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (action.equals(PLAY_STATUE)) {
                isPlaying = intent.getBooleanExtra("isplay", false);
                if (!isPlaying) {
//                    if (mService.getplayerstatus()) {
//                        stop();
//                    }
                }
            }
            if (action.equals(UPDATE_LIST_ACTIVITY_ACTION)) {
                listPosition = intent.getIntExtra("current_music", 0);
                PlayMusicUIUpdate(listPosition);
            }
            if (action.equals(UPDATE_ACTION)) {
                listPosition = intent.getIntExtra("current_music", -1);
                String title = MainService.list.get(listPosition).getTitle();
                listmusicname.setText(title);
                PlayMusicUIUpdate(listPosition);
            }
            if (action.equals(CURRENT_ID)) {
                listPosition = intent.getIntExtra("current_id", -1);
            }
        }
    }
}
