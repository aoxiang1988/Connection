package com.sec.myonlinefm;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.sec.myonlinefm.onlineSearchUI.SearchResultListFragment;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/2/26.
 */

public class SearchActivity extends AppCompatActivity {

    private SearchView mSearchView;
    private TabHost mTabHost;
    private String TAG = "SearchActivity";
    private OnLineFMConnectManager.Companion mPlayer;
    public static SearchActivity mSearchActivity;
    public String mQuery;
    private int mCurrentPage = 1;
    private View topBarActionBarView = null;

    private static final String SEARCH_CHANNEL_ONDEMAND_TYPE = "channel_ondemand";
    private static final String SEARCH_PROGRAM_ONDEMAND_TYPE = "program_ondemand";
    private static final String SEARCH_CHANNEL_LIVE_TYPE = "channel_live";
    private static final String SEARCH_PROGRAM_LIVE_TYPE = "program_live";
    private static final String SEARCH_ALL_TYPE = "all";
    private static final String SEARCH_REAL_REMIX_TYPE = "real_remix";

    private static final int CHANNEL_LIVE_TAB = 0;
    private static final int PROGRAM_LIVE_TAB = 1;
    private static final int ALL_TAB = 2;
    private static final int CHANNEL_ON_DEMAND_TAB = 3;
    private static final int PROGRAM_ON_DEMAND_TAB = 4;
    private static final int REAL_REMIX_TAB = 5;

    private Menu mOptionMenu = null;

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
        setContentView(R.layout.search_layout);
        setTopPanelOnActionBar("搜索列表");
        mSearchActivity = this;
        mPlayer = OnLineFMConnectManager.Companion;
        mQuery = mPlayer.getMGPS_Name();
        initializeTabContent();
        mSearchView = (SearchView) findViewById(R.id.online_search);
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String mType = null;
                mQuery = query;
                switch (mTabHost.getCurrentTab()) {
                    case CHANNEL_LIVE_TAB:
                        mType = SEARCH_CHANNEL_LIVE_TYPE;
                        break;
                    case PROGRAM_LIVE_TAB:
                        mType = SEARCH_PROGRAM_LIVE_TYPE;
                        break;
                    case ALL_TAB:
                        mType = SEARCH_ALL_TYPE;
                        break;
                    case CHANNEL_ON_DEMAND_TAB:
                        mType = SEARCH_CHANNEL_ONDEMAND_TYPE;
                        break;
                    case PROGRAM_ON_DEMAND_TAB:
                        mType = SEARCH_PROGRAM_ONDEMAND_TYPE;
                        break;
                    case REAL_REMIX_TAB:
                        mType = SEARCH_REAL_REMIX_TYPE;
                        break;
                }
                mPlayer.getMMainInfoCode().getSearchResult(query,mType,mCurrentPage);
                if (mSearchView != null) {
                    // 得到输入管理对象
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        // 这将让键盘在所有的情况下都被隐藏，但是一般我们在点击搜索按钮后，输入法都会乖乖的自动隐藏的。
                        imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0); // 输入法如果是显示状态，那么就隐藏输入法                    }
                        mSearchView.clearFocus(); // 不获取焦点
                    }
                }
                android.app.FragmentManager fm = getFragmentManager();
                SearchResultListFragment searchListFragment = (SearchResultListFragment) fm.findFragmentByTag(SearchResultListFragment.TAG);
                FragmentTransaction ft = fm.beginTransaction();

                if (searchListFragment == null) {
                    ft.add(android.R.id.tabcontent, SearchResultListFragment.newInstance(mQuery), SearchResultListFragment.TAG);
                } else {
                    ft.attach(searchListFragment);
                }
                ft.commitAllowingStateLoss();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (topBarActionBarView != null)
            topBarActionBarView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,CHANNEL_LIVE_TAB, CHANNEL_LIVE_TAB, SEARCH_CHANNEL_LIVE_TYPE).setCheckable(true).setChecked(true);
        menu.add(0, PROGRAM_LIVE_TAB, PROGRAM_LIVE_TAB, SEARCH_PROGRAM_LIVE_TYPE).setCheckable(true).setChecked(true);
        menu.add(0,ALL_TAB, ALL_TAB, SEARCH_ALL_TYPE).setCheckable(true);
        menu.add(0, CHANNEL_ON_DEMAND_TAB, CHANNEL_ON_DEMAND_TAB, SEARCH_CHANNEL_ONDEMAND_TYPE).setCheckable(true);
        menu.add(0, PROGRAM_ON_DEMAND_TAB, PROGRAM_ON_DEMAND_TAB, SEARCH_PROGRAM_ONDEMAND_TYPE).setCheckable(true);
        menu.add(0,REAL_REMIX_TAB, REAL_REMIX_TAB, SEARCH_REAL_REMIX_TYPE).setCheckable(true);
        mOptionMenu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.findItem(CHANNEL_LIVE_TAB).setChecked(mOptionMenu.findItem(CHANNEL_LIVE_TAB).isChecked());
        menu.findItem(PROGRAM_LIVE_TAB).setChecked(mOptionMenu.findItem(PROGRAM_LIVE_TAB).isChecked());
        menu.findItem(ALL_TAB).setChecked(mOptionMenu.findItem(ALL_TAB).isChecked());
        menu.findItem(CHANNEL_ON_DEMAND_TAB).setChecked(mOptionMenu.findItem(CHANNEL_ON_DEMAND_TAB).isChecked());
        menu.findItem(PROGRAM_ON_DEMAND_TAB).setChecked(mOptionMenu.findItem(PROGRAM_ON_DEMAND_TAB).isChecked());
        menu.findItem(REAL_REMIX_TAB).setChecked(mOptionMenu.findItem(REAL_REMIX_TAB).isChecked());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case CHANNEL_LIVE_TAB:
            case PROGRAM_LIVE_TAB:
            case ALL_TAB:
            case CHANNEL_ON_DEMAND_TAB:
            case PROGRAM_ON_DEMAND_TAB:
            case REAL_REMIX_TAB:
                if(mOptionMenu.findItem(id).isChecked()) {
                    mOptionMenu.findItem(id).setChecked(false);
                }
                else {
                    mOptionMenu.findItem(id).setChecked(true);
                }
                initializeTabContent();
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private TabWidget mTabWidget;

    private void initializeTabContent() {
        Log.d(TAG, "initializeTabContent : ");
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabWidget = mTabHost.findViewById(android.R.id.tabs);

        mTabHost.setup();
        mTabHost.clearAllTabs();
        mTabHost.setOnTabChangedListener(mTabChangeListener);

        /*
        * * Defining tab builder for Channels tab */
        TabHost.TabSpec stationsTab = mTabHost.newTabSpec(SearchResultListFragment.TAG);
        stationsTab.setIndicator("电台");
        stationsTab.setContent(new FMTabContent(getBaseContext()));
        mTabHost.addTab(stationsTab);

        /*
        * * Defining tab builder for program tab */
        TabHost.TabSpec programTab = mTabHost.newTabSpec(SearchResultListFragment.TAG);
        programTab.setIndicator("节目");
        programTab.setContent(new FMTabContent(getBaseContext()));
        mTabHost.addTab(programTab);
//        /*
//        * * Defining tab builder for program tab */
//        TabHost.TabSpec channel_ondemandTab = mTabHost.newTabSpec(SearchResultListFragment.TAG);
//        channel_ondemandTab.setIndicator("点播电台");
//        channel_ondemandTab.setContent(new FMTabContent(getBaseContext()));
//        mTabHost.addTab(channel_ondemandTab);
//        /*
//        * * Defining tab builder for program tab */
//        TabHost.TabSpec program_ondemandTab = mTabHost.newTabSpec(SearchResultListFragment.TAG);
//        program_ondemandTab.setIndicator("点播节目");
//        program_ondemandTab.setContent(new FMTabContent(getBaseContext()));
//        mTabHost.addTab(program_ondemandTab);
        /*
        * * Defining tab builder for program tab */
        TabHost.TabSpec allTab = mTabHost.newTabSpec(SearchResultListFragment.TAG);
        allTab.setIndicator("综合");
        allTab.setContent(new FMTabContent(getBaseContext()));
        mTabHost.addTab(allTab);
    }

    private void initMyTabHost(int position) {
        //修改背景
        mTabWidget.getChildAt(position).setBackgroundResource(
                R.drawable.custom_tab_indicator);
    }

    private static class FMTabContent implements TabHost.TabContentFactory {
        private Context mContext;

        FMTabContent(Context context) {
            mContext = context;
        }

        @Override
        public View createTabContent(String tag) {
            View v = new View(mContext);
            return v;
        }
    }
    TabHost.OnTabChangeListener mTabChangeListener = new TabHost.OnTabChangeListener() {
        @Override
        public void onTabChanged(String tabId) {
            Log.d(TAG, "onTabChanged : " + tabId+" xx "+mTabHost.getCurrentTabTag());
            initMyTabHost(mTabHost.getCurrentTab());
            String mType = null;
            if(mTabHost.getCurrentTab() == 0) {
                mType = SEARCH_CHANNEL_LIVE_TYPE;
            } else if(mTabHost.getCurrentTab() == 1){
                mType = SEARCH_PROGRAM_LIVE_TYPE;
            }  else if(mTabHost.getCurrentTab() == 2){
                mType = SEARCH_ALL_TYPE;
            }
            mCurrentPage = 1;
            mPlayer.getMMainInfoCode().getSearchResult(mQuery, mType, mCurrentPage);
            android.app.FragmentManager fm = getFragmentManager();
            SearchResultListFragment searchListFragment = (SearchResultListFragment) fm.findFragmentByTag(SearchResultListFragment.TAG);
            FragmentTransaction ft = fm.beginTransaction();

            if (searchListFragment == null) {
                ft.add(android.R.id.tabcontent, SearchResultListFragment.newInstance(mQuery), SearchResultListFragment.TAG);
            } else {
                ft.attach(searchListFragment);
            }
            ft.commitAllowingStateLoss();
        }
    };
}
