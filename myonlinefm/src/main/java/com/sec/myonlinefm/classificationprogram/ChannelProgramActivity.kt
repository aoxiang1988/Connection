package com.sec.myonlinefm.classificationprogram

import android.annotation.SuppressLint
import android.content.*
import android.os.*
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import com.sec.myonlinefm.abstructObserver.RequestCallBack

import android.database.sqlite.SQLiteDatabase

import com.sec.myonlinefm.R

import com.sec.myonlinefm.defineview.RoundImageView

import com.sec.myonlinefm.OnLineFMConnectManager


import android.widget.TabHost.TabContentFactory
import android.widget.TabHost.OnTabChangeListener

import com.sec.myonlinefm.classificationprogram.data.DemandChannel
import com.sec.myonlinefm.classificationprogram.dataimport.DemandChannelPattern

import com.sec.myonlinefm.classificationprogram.fragment.ChannelProgramListFragment

import com.sec.myonlinefm.classificationprogram.fragment.ChannelRecommendListFragment
import com.sec.myonlinefm.dbdata.MySQLHelper
import com.sec.myonlinefm.UpdateListViewAsyncTask


/* https://blog.csdn.net/xx23x/article/details/54617928
 * */
class ChannelProgramActivity : AppCompatActivity(), View.OnClickListener {
    private var mChannelID = -1
    private var mChannelName: String? = null
    private var mTabHost: TabHost? = null
    private var mTabWidget: TabWidget? = null
    private var mPlayer: OnLineFMConnectManager? = null
    private var currentDemandChannel: DemandChannel? = null
    private var topBarActionBarView: View? = null
    private var mCurrentChannelPic: ImageView? = null
    private var mCurrentChannelDescription: TextView? = null
    private var mPodCasterPic: RoundImageView? = null
    private var mPodCasterName: TextView? = null
    private var mChannelInfoView: LinearLayout? = null
    private var mySQLHelper: MySQLHelper? = null
    private var db: SQLiteDatabase? = null
    private var mFavOnLineChannel: ImageView? = null
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

    override fun onDestroy() {
        mHandler!!.removeMessages(UPDATE_CURRENT_DEMAND_CHANNEL_INFO)
        System.gc()
        super.onDestroy()
    }

    private val mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                UPDATE_CURRENT_DEMAND_CHANNEL_INFO -> {
                    //                    mCurrentChannelPic.setImageBitmap(mPlayer.getBitmap(currentDemandChannel.getThumbs(),
//                            100, 100));
                    val asyncTask = UpdateListViewAsyncTask(mCurrentChannelPic,
                            currentDemandChannel!!.getTitle(),
                            mPlayer, 60, 60)
                    asyncTask.execute(currentDemandChannel!!.getThumbs())
                    val asyncTaskForBlur = UpdateListViewAsyncTask(baseContext,
                            currentDemandChannel!!.getTitle(),
                            mPlayer, mChannelInfoView, true,
                            mChannelInfoView!!.getWidth(), mChannelInfoView!!.getHeight())
                    asyncTaskForBlur.execute(currentDemandChannel!!.getThumbs())
                    //                    blur(mPlayer.getBitmap(currentDemandChannel.getThumbs(),
//                            mChannelInfoView.getWidth(), mChannelInfoView.getHeight()), mChannelInfoView);
                    mCurrentChannelDescription!!.setText(currentDemandChannel!!.getDescription())
                    if (currentDemandChannel!!.getDetail()!!.podCasters != null
                            && !currentDemandChannel!!.getDetail()!!.podCasters!!.isEmpty()) {
//                        mPodCasterPic.setImageBitmap(mPlayer.getBitmap(currentDemandChannel.getDetail().getPodCasters().get(0).getImgUrl(),
//                                40, 40));
                        val asyncTaskCaster = UpdateListViewAsyncTask(mPodCasterPic,
                                currentDemandChannel!!.getTitle(),
                                mPlayer, 60, 60)
                        asyncTaskCaster.execute(currentDemandChannel!!.getDetail()!!.podCasters!![0]!!.getImgUrl())
                        mPodCasterName!!.setText(currentDemandChannel!!.getDetail()!!.podCasters!![0]!!.getNickName())
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel_program)
        _activity = this
        val i = intent
        mChannelID = i.getIntExtra("channel_id", -1)
        mChannelName = i.getStringExtra("channel_name")
        mFavOnLineChannel = findViewById<View?>(R.id.fav_on_line_channel) as ImageView
        mFavOnLineChannel!!.setClickable(true)
        mFavOnLineChannel!!.setOnClickListener(this)
        initView()
        mPlayer = OnLineFMConnectManager.mMainInfoCode
        setUpdateCurrentDemandChannelInfo(mChannelName, mChannelID, false)
        mySQLHelper = MySQLHelper.Companion.getInstances()
        db = mySQLHelper!!.getWritableDatabase()
    }

    override fun onResume() {
        super.onResume()
        if (!checkIfChannelInDB(mChannelID)) {
            mFavOnLineChannel!!.setImageResource(R.drawable.hybrid_radio_on_star_ripple)
            mFavOnLineChannel!!.setColorFilter(resources.getColor(R.color.net_fm_back_b))
        } else {
            mFavOnLineChannel!!.setImageResource(R.drawable.hybrid_radio_radio_off_start_ripple)
            mFavOnLineChannel!!.setColorFilter(resources.getColor(R.color.net_fm_back_a))
        }
    }

    override fun onClick(v: View?) {
        when (v!!.getId()) {
            R.id.fav_on_line_channel -> if (checkIfChannelInDB(mChannelID)) {
                val values = ContentValues()
                values.put("channel_id", mChannelID)
                values.put("channel_name", mChannelName)
                values.put("category_id", currentDemandChannel!!.getCategoryId())
                values.put("channel_them_url", currentDemandChannel!!.getThumbsUrl())
                values.put("pod_caster_name", currentDemandChannel!!.getDetail()!!.podCasters!![0]!!.getNickName())
                db!!.insert("fav_channels", null, values)
                Toast.makeText(this, "收藏完成", Toast.LENGTH_SHORT).show()
                mFavOnLineChannel!!.setImageResource(R.drawable.hybrid_radio_on_star_ripple)
                mFavOnLineChannel!!.setColorFilter(resources.getColor(R.color.net_fm_back_b))
            } else {
                db!!.delete("fav_channels", "channel_id = ?", arrayOf<String?>(mChannelID.toString()))
                Toast.makeText(this, "收藏移除", Toast.LENGTH_SHORT).show()
                mFavOnLineChannel!!.setImageResource(R.drawable.hybrid_radio_radio_off_start_ripple)
                mFavOnLineChannel!!.setColorFilter(resources.getColor(R.color.net_fm_back_a))
            }
        }
    }

    private fun checkIfChannelInDB(mChannelID: Int): Boolean {
        val cursor = db!!.query("fav_channels", null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            do {
                val pid = cursor.getInt(cursor.getColumnIndex("channel_id"))
                val name = cursor.getString(cursor.getColumnIndex("channel_name"))
                if (pid == mChannelID) {
                    Toast.makeText(this, "已收藏$name", Toast.LENGTH_SHORT).show()
                    cursor.close()
                    return false
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        return true
    }

    fun setUpdateCurrentDemandChannelInfo(mChannelName: String?, mChannelID: Int, isRecommend: Boolean) {
        if (isRecommend) {
            this.mChannelName = mChannelName
            this.mChannelID = mChannelID
        }
        initializeTabContent()
        setTopPanelOnActionBar(mChannelName)
        if (mChannelID != -1) {
            mPlayer!!.getCurrentDemandChannel(mChannelID, object : RequestCallBack<DemandChannelPattern?> {
                override fun onSuccess(`val`: DemandChannelPattern?) {
                    currentDemandChannel = `val`!!.getCurrentDemandChannel()
                    mHandler!!.sendEmptyMessage(UPDATE_CURRENT_DEMAND_CHANNEL_INFO)
                }

                override fun onFail(errorMessage: String?) {
                    Log.d("bin1111.yang", "CurrentDemandChannel onFail : $errorMessage")
                }
            })
        }
    }

    private fun initView() {
        mCurrentChannelPic = findViewById<View?>(R.id.current_channel_pic) as ImageView
        mCurrentChannelDescription = findViewById<View?>(R.id.current_channel_description) as TextView
        mPodCasterPic = findViewById<View?>(R.id.podcasters_pic) as RoundImageView
        mPodCasterPic!!.setShapeType(1)
        mPodCasterName = findViewById<View?>(R.id.podcasters_name) as TextView
        mChannelInfoView = findViewById<View?>(R.id.channel_info_view) as LinearLayout
    }

    private fun initMyTabHost(position: Int) {
        //修改背景
        mTabWidget!!.getChildAt(position).setBackgroundResource(
                R.drawable.custom_tab_indicator)
    }

    private fun initializeTabContent() {
        mTabHost = findViewById<View?>(android.R.id.tabhost) as TabHost
        mTabWidget = mTabHost!!.findViewById(android.R.id.tabs)
        mTabHost!!.setup()
        mTabHost!!.clearAllTabs()
        mTabHost!!.setOnTabChangedListener(mTabChangeListener)

        /*
        * * Defining tab builder for Channels tab */
        val stationsTab = mTabHost!!.newTabSpec(ChannelProgramListFragment.Companion.TAG!!)
        stationsTab.setIndicator("节目")
        stationsTab.setContent(FMTabContent(baseContext))
        mTabHost!!.addTab(stationsTab)

        /*
        * * Defining tab builder for program tab */
        val programTab = mTabHost!!.newTabSpec(ChannelRecommendListFragment.Companion.TAG!!)
        programTab.setIndicator("推荐")
        programTab.setContent(FMTabContent(baseContext))
        mTabHost!!.addTab(programTab)
    }

    class FMTabContent(private val mContext: Context?) : TabContentFactory {
        override fun createTabContent(tag: String?): View? {
            return View(mContext)
        }
    }

    fun getTabHost(): TabHost? {
        return mTabHost
    }

    var mTabChangeListener: OnTabChangeListener? = OnTabChangeListener { tabId ->
        val mType: String? = null
        when (tabId) {
            "ChannelProgramListFragment" -> initMyTabHost(0)
            "ChannelRecommendListFragment" -> initMyTabHost(1)
        }
        val fm = supportFragmentManager
        val channelProgramListFragment = fm.findFragmentByTag(ChannelProgramListFragment.Companion.TAG) as ChannelProgramListFragment
        val channelRecommendListFragment = fm.findFragmentByTag(ChannelRecommendListFragment.Companion.TAG) as ChannelRecommendListFragment
        val ft = fm.beginTransaction()
        if (channelProgramListFragment != null) ft.detach(channelProgramListFragment)
        if (channelRecommendListFragment != null) ft.detach(channelRecommendListFragment)
        if (tabId.equals(ChannelProgramListFragment.Companion.TAG, ignoreCase = true)) {
            if (channelProgramListFragment == null) {
                ft.add(android.R.id.tabcontent, ChannelProgramListFragment.Companion.newInstance(mChannelID)!!, ChannelProgramListFragment.Companion.TAG)
            } else {
                channelProgramListFragment.setCurrentChannelID(mChannelID)
                ft.attach(channelProgramListFragment)
            }
        } else {
            if (channelRecommendListFragment == null) {
                ft.add(android.R.id.tabcontent, ChannelRecommendListFragment.Companion.newInstance(currentDemandChannel?.getCategoryId()!!)!!, ChannelRecommendListFragment.Companion.TAG)
            } else {
                channelRecommendListFragment.setCurrentCategoryID(currentDemandChannel!!.getCategoryId())
                ft.attach(channelRecommendListFragment)
            }
        }
        ft.commitAllowingStateLoss()
    }

    companion object {
        var _activity: ChannelProgramActivity? = null
        private const val UPDATE_CURRENT_DEMAND_CHANNEL_INFO = 1
    }
}

