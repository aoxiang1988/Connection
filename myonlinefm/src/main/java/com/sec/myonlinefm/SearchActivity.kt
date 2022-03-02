package com.sec.myonlinefm

import android.annotation.SuppressLint
import android.content.*

import com.sec.myonlinefm.OnLineFMConnectManager.Companion.mGPS_Name
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*

import com.sec.myonlinefm.onlineSearchUI.SearchResultListFragment

import android.widget.TabHost.TabContentFactory
import android.widget.TabHost.OnTabChangeListener
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode

/**
 * Created by SRC-TJ-MM-BinYang on 2018/2/26.
 */
class SearchActivity : AppCompatActivity() {
    private var mSearchView: SearchView? = null
    private var mTabHost: TabHost? = null
    private val TAG: String = "SearchActivity"
    private var mPlayer: OnLineFMConnectManager.Companion? = null
    var mQuery: String? = null
    private var mCurrentPage = 1
    private var topBarActionBarView: View? = null
    private var mOptionMenu: Menu? = null
    @SuppressLint("InflateParams")
    fun setTopPanelOnActionBar(mChannelName: String?) {
        val bar = supportActionBar
        if (bar != null) {
            bar.setHomeButtonEnabled(true)
            bar.setDisplayHomeAsUpEnabled(false)
            bar.setDisplayShowHomeEnabled(false)
            bar.setDisplayShowTitleEnabled(false)
            bar.setBackgroundDrawable(getDrawable(R.drawable.titlebar_bg))
            bar.elevation = 0f
        }
        topBarActionBarView = layoutInflater.inflate(R.layout.top_bar_panel_online, null)
        val mBackBut = topBarActionBarView!!.findViewById<ImageView?>(R.id.back_but)
        mBackBut.setOnClickListener { finish() }
        val mTopBarTitle = topBarActionBarView!!.findViewById<TextView?>(R.id.top_bar_title)
        mTopBarTitle.text = mChannelName
        mTopBarTitle.isFocusable = true
        if (bar != null) {
            bar.customView = topBarActionBarView
            bar.setDisplayHomeAsUpEnabled(true)
            bar.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            bar.show()
            //            bar.hide();
        }
    }

    override fun onSupportActionModeStarted(mode: ActionMode) {
        super.onSupportActionModeStarted(mode)
        if (topBarActionBarView != null) topBarActionBarView!!.setVisibility(View.VISIBLE)
    }

    override fun onSupportActionModeFinished(mode: ActionMode) {
        super.onSupportActionModeFinished(mode)
        if (topBarActionBarView != null) topBarActionBarView!!.setVisibility(View.VISIBLE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_layout)
        setTopPanelOnActionBar("搜索列表")
        mSearchActivity = this
        mPlayer = OnLineFMConnectManager
        mQuery = mGPS_Name
        initializeTabContent()
        mSearchView = findViewById<View?>(R.id.online_search) as SearchView
        mSearchView!!.setIconifiedByDefault(false)
        mSearchView!!.setSubmitButtonEnabled(true)
        mSearchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                var mType: String? = null
                mQuery = query
                when (mTabHost!!.getCurrentTab()) {
                    CHANNEL_LIVE_TAB -> mType = SEARCH_CHANNEL_LIVE_TYPE
                    PROGRAM_LIVE_TAB -> mType = SEARCH_PROGRAM_LIVE_TYPE
                    ALL_TAB -> mType = SEARCH_ALL_TYPE
                    CHANNEL_ON_DEMAND_TAB -> mType = SEARCH_CHANNEL_ONDEMAND_TYPE
                    PROGRAM_ON_DEMAND_TAB -> mType = SEARCH_PROGRAM_ONDEMAND_TYPE
                    REAL_REMIX_TAB -> mType = SEARCH_REAL_REMIX_TYPE
                }
                OnLineFMConnectManager.mMainInfoCode!!.getSearchResult(query!!, mType!!, mCurrentPage)
                if (mSearchView != null) {
                    // 得到输入管理对象
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    // 这将让键盘在所有的情况下都被隐藏，但是一般我们在点击搜索按钮后，输入法都会乖乖的自动隐藏的。
                    imm.hideSoftInputFromWindow(mSearchView!!.getWindowToken(), 0) // 输入法如果是显示状态，那么就隐藏输入法                    }
                    mSearchView!!.clearFocus() // 不获取焦点
                }
                val fm = supportFragmentManager
                val searchListFragment = fm.findFragmentByTag(SearchResultListFragment.Companion.TAG) as SearchResultListFragment
                val ft = fm.beginTransaction()
                ft.attach(searchListFragment)
                ft.commitAllowingStateLoss()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (topBarActionBarView != null) topBarActionBarView!!.setVisibility(View.VISIBLE)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu!!.add(0, CHANNEL_LIVE_TAB, CHANNEL_LIVE_TAB, SEARCH_CHANNEL_LIVE_TYPE).setCheckable(true).setChecked(true)
        menu.add(0, PROGRAM_LIVE_TAB, PROGRAM_LIVE_TAB, SEARCH_PROGRAM_LIVE_TYPE).setCheckable(true).setChecked(true)
        menu.add(0, ALL_TAB, ALL_TAB, SEARCH_ALL_TYPE).setCheckable(true)
        menu.add(0, CHANNEL_ON_DEMAND_TAB, CHANNEL_ON_DEMAND_TAB, SEARCH_CHANNEL_ONDEMAND_TYPE).setCheckable(true)
        menu.add(0, PROGRAM_ON_DEMAND_TAB, PROGRAM_ON_DEMAND_TAB, SEARCH_PROGRAM_ONDEMAND_TYPE).setCheckable(true)
        menu.add(0, REAL_REMIX_TAB, REAL_REMIX_TAB, SEARCH_REAL_REMIX_TYPE).setCheckable(true)
        mOptionMenu = menu
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu!!.findItem(CHANNEL_LIVE_TAB).isChecked = mOptionMenu!!.findItem(CHANNEL_LIVE_TAB).isChecked
        menu.findItem(PROGRAM_LIVE_TAB).isChecked = mOptionMenu!!.findItem(PROGRAM_LIVE_TAB).isChecked
        menu.findItem(ALL_TAB).isChecked = mOptionMenu!!.findItem(ALL_TAB).isChecked
        menu.findItem(CHANNEL_ON_DEMAND_TAB).isChecked = mOptionMenu!!.findItem(CHANNEL_ON_DEMAND_TAB).isChecked
        menu.findItem(PROGRAM_ON_DEMAND_TAB).isChecked = mOptionMenu!!.findItem(PROGRAM_ON_DEMAND_TAB).isChecked
        menu.findItem(REAL_REMIX_TAB).isChecked = mOptionMenu!!.findItem(REAL_REMIX_TAB).isChecked
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()
        when (id) {
            CHANNEL_LIVE_TAB, PROGRAM_LIVE_TAB, ALL_TAB, CHANNEL_ON_DEMAND_TAB, PROGRAM_ON_DEMAND_TAB, REAL_REMIX_TAB -> {
                if (mOptionMenu!!.findItem(id).isChecked) {
                    mOptionMenu!!.findItem(id).isChecked = false
                } else {
                    mOptionMenu!!.findItem(id).isChecked = true
                }
                initializeTabContent()
            }
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private var mTabWidget: TabWidget? = null
    private fun initializeTabContent() {
        Log.d(TAG, "initializeTabContent : ")
        mTabHost = findViewById<View?>(android.R.id.tabhost) as TabHost
        mTabWidget = mTabHost!!.findViewById(android.R.id.tabs)
        mTabHost!!.setup()
        mTabHost!!.clearAllTabs()
        mTabHost!!.setOnTabChangedListener(mTabChangeListener)

        /*
        * * Defining tab builder for Channels tab */
        val stationsTab = mTabHost!!.newTabSpec(SearchResultListFragment.Companion.TAG!!)
        stationsTab.setIndicator("电台")
        stationsTab.setContent(FMTabContent(baseContext))
        mTabHost!!.addTab(stationsTab)

        /*
        * * Defining tab builder for program tab */
        val programTab = mTabHost!!.newTabSpec(SearchResultListFragment.Companion.TAG!!)
        programTab.setIndicator("节目")
        programTab.setContent(FMTabContent(baseContext))
        mTabHost!!.addTab(programTab)
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
        val allTab = mTabHost!!.newTabSpec(SearchResultListFragment.Companion.TAG!!)
        allTab.setIndicator("综合")
        allTab.setContent(FMTabContent(baseContext))
        mTabHost!!.addTab(allTab)
    }

    private fun initMyTabHost(position: Int) {
        //修改背景
        mTabWidget!!.getChildAt(position).setBackgroundResource(
                R.drawable.custom_tab_indicator)
    }

    private class FMTabContent(private val mContext: Context?) : TabContentFactory {
        override fun createTabContent(tag: String?): View {
            return View(mContext)
        }
    }

    var mTabChangeListener: OnTabChangeListener? = OnTabChangeListener { tabId ->
        Log.d(TAG, "onTabChanged : " + tabId + " xx " + mTabHost!!.getCurrentTabTag())
        initMyTabHost(mTabHost!!.getCurrentTab())
        var mType: String? = null
        if (mTabHost!!.getCurrentTab() == 0) {
            mType = SEARCH_CHANNEL_LIVE_TYPE
        } else if (mTabHost!!.getCurrentTab() == 1) {
            mType = SEARCH_PROGRAM_LIVE_TYPE
        } else if (mTabHost!!.getCurrentTab() == 2) {
            mType = SEARCH_ALL_TYPE
        }
        mCurrentPage = 1
        OnLineFMConnectManager.mMainInfoCode!!.getSearchResult(mQuery!!, mType!!, mCurrentPage)
        val fm = supportFragmentManager
        val searchListFragment = fm.findFragmentByTag(SearchResultListFragment.TAG) as SearchResultListFragment
        val ft = fm.beginTransaction()
        ft.attach(searchListFragment)
        ft.commitAllowingStateLoss()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var mSearchActivity: SearchActivity? = null
        private const val SEARCH_CHANNEL_ONDEMAND_TYPE: String = "channel_ondemand"
        private const val SEARCH_PROGRAM_ONDEMAND_TYPE: String = "program_ondemand"
        private const val SEARCH_CHANNEL_LIVE_TYPE: String = "channel_live"
        private const val SEARCH_PROGRAM_LIVE_TYPE: String = "program_live"
        private const val SEARCH_ALL_TYPE: String = "all"
        private const val SEARCH_REAL_REMIX_TYPE: String = "real_remix"
        private const val CHANNEL_LIVE_TAB = 0
        private const val PROGRAM_LIVE_TAB = 1
        private const val ALL_TAB = 2
        private const val CHANNEL_ON_DEMAND_TAB = 3
        private const val PROGRAM_ON_DEMAND_TAB = 4
        private const val REAL_REMIX_TAB = 5
    }
}