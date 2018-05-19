package com.sec.myonlinefm

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import com.sec.myonlinefm.classificationprogram.RequestProgramClassifyActivity
import com.sec.myonlinefm.data.Station

class MainActivity : AppCompatActivity() {

    private var mFMListData: NewFMListData? = null
    private var mPlayer : OnLineFMConnectManager? = null
    private val mCurrentStation: Station? = null

    private var topBarActionBarView: View? = null

    private fun setTopPanelOnActionBar(title: String, isShowSearchView: Boolean) {
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
        val mBackBut = topBarActionBarView!!.findViewById<ImageView>(R.id.back_but)
        mBackBut.setOnClickListener({finish()})
//        mBackBut.setVisibility(View.INVISIBLE)
        val mTopBarTitle = topBarActionBarView!!.findViewById<TextView>(R.id.top_bar_title)
        val mOnLineSearchView = topBarActionBarView!!.findViewById<SearchView>(R.id.online_search)
        mOnLineSearchView.setOnClickListener(View.OnClickListener {
            val i = Intent(this, SearchActivity::class.java)
            startActivity(i)
        })
        mTopBarTitle.setText(title)
        if (isShowSearchView) {
            mTopBarTitle.setVisibility(View.GONE)
            mOnLineSearchView.setVisibility(View.VISIBLE)
        } else {
            mTopBarTitle.setVisibility(View.VISIBLE)
            mOnLineSearchView.setVisibility(View.GONE)
        }
        if (bar != null) {
            bar.setCustomView(topBarActionBarView)
            bar.setDisplayHomeAsUpEnabled(true)
            bar.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            bar.show()
        }
    }

    override fun onSupportActionModeStarted(mode: android.support.v7.view.ActionMode) {
        super.onSupportActionModeStarted(mode)
        if (topBarActionBarView != null)
            topBarActionBarView!!.setVisibility(View.VISIBLE)
    }

    override fun onSupportActionModeFinished(mode: android.support.v7.view.ActionMode) {
        super.onSupportActionModeFinished(mode)
        if (topBarActionBarView != null)
            topBarActionBarView!!.setVisibility(View.VISIBLE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTopPanelOnActionBar("MainFace", false)
        setContentView(R.layout.activity_main)
        mPlayer = OnLineFMConnectManager(this)
        val mButton : Button = findViewById(R.id.to_on_line_fm) as Button
        mButton.setOnClickListener(
                {
                    val intent : Intent = Intent()
                    intent.setClass(this, RequestProgramClassifyActivity::class.java)
                    startActivity(intent)
                })
        mFMListData = NewFMListData(this);
    }

    private var mUseDNS = true

    fun StartDNSPlay() {
        if (mCurrentStation != null) {
            mPlayer!!.getReplayUrl(mCurrentStation.getStationId(),
                    null,
                    null,
                    OnLineFMPlayerService.PLAY_STATION)
        };
    }

    fun unStartDNSPlay() {
        mUseDNS = false
    }
    fun checkLocationPermission() {
//         permissionArrayList : ArrayList<String> = FMPermissionUtil
//                .hasPermissions(this, getApplicationContext(), FMPermissionUtil.FM_PERMISSION_REQUEST_GET_LOCATION);
//        if (permissionArrayList != null) {
//            if (permissionArrayList.size() != 0) {
//                FMPermissionUtil.openPermissionDialog(this, permissionArrayList, FMPermissionUtil.FM_PERMISSION_REQUEST_GET_LOCATION);
//            } else {
//                FMPermissionUtil.requestPermission(this, FMPermissionUtil.FM_PERMISSION_REQUEST_GET_LOCATION);
//            }
//        } else {
            mFMListData!!.doConnectApi();
//        }
    }


    override fun onDestroy() {
        mPlayer!!.destroyManager()
        mPlayer = null
        super.onDestroy()
    }
}
