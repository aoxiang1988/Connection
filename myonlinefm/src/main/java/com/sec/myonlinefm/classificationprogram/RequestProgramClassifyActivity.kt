package com.sec.myonlinefm.classificationprogram

import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

import android.os.Bundle

import android.content.Intent
import com.sec.myonlinefm.classificationprogram.mainfacefragment.OnLineFavFragment

import com.sec.myonlinefm.classificationprogram.mainfacefragment.OnLineHomeFragment
import android.content.ServiceConnection
import android.content.ComponentName
import android.os.IBinder
import com.sec.myonlinefm.R
import com.sec.myonlinefm.dbdata.MySQLHelper
import com.sec.myonlinefm.OnLineFMPlayerService
import com.sec.myonlinefm.SearchActivity

class RequestProgramClassifyActivity : AppCompatActivity(), ServiceConnection, View.OnClickListener {
    private var topBarActionBarView: View? = null
    private val TAG: String? = "RequestProgramClassifyActivity"
    private val mParam1 = TAG
    private var mParam2: String? = null
    private var home: Fragment? = null
    private var favFragment: Fragment? = null
    private var mCurrentFragmentTag: String? = null
    private fun setTopPanelOnActionBar(title: String?, isShowSearchView: Boolean) {
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
        mBackBut.visibility = View.INVISIBLE
        val mTopBarTitle = topBarActionBarView!!.findViewById<TextView?>(R.id.top_bar_title)
        val mOnLineSearchView = topBarActionBarView!!.findViewById<SearchView?>(R.id.online_search)
        mOnLineSearchView.setOnClickListener {
            val i = Intent(this@RequestProgramClassifyActivity, SearchActivity::class.java)
            startActivity(i)
        }
        mTopBarTitle.text = title
        if (isShowSearchView) {
            mTopBarTitle.visibility = View.GONE
            mOnLineSearchView.visibility = View.VISIBLE
        } else {
            mTopBarTitle.visibility = View.VISIBLE
            mOnLineSearchView.visibility = View.GONE
        }
        if (bar != null) {
            bar.customView = topBarActionBarView
            bar.setDisplayHomeAsUpEnabled(true)
            bar.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            bar.show()
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

    private var ft: FragmentTransaction? = null
    private var mFavIcon: ImageView? = null
    private var mHomeIcon: ImageView? = null
    private var mDownLoadIcon: ImageView? = null
    private var mUserIcon: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_program_classify)
        val mySQLHelper = MySQLHelper(this, "onLineFM.db", null, 1)
        ft = supportFragmentManager.beginTransaction()
        home = supportFragmentManager.findFragmentByTag(OnLineHomeFragment.Companion.TAG) as OnLineHomeFragment?
        mCurrentFragmentTag = OnLineHomeFragment.Companion.TAG
        if (home == null) {
            mParam2 = "home"
            ft!!.add(OnLineHomeFragment.Companion.newInstance(mParam1, mParam2)!!, OnLineHomeFragment.Companion.TAG)
        } else {
            ft!!.attach(home!!)
        }
        ft!!.commitAllowingStateLoss()
        mFavIcon = findViewById<View?>(R.id.into_fave_icon) as ImageView
        mHomeIcon = findViewById<View?>(R.id.home) as ImageView
        mDownLoadIcon = findViewById<View?>(R.id.down_load_file) as ImageView
        mUserIcon = findViewById<View?>(R.id.user_info) as ImageView
        mFavIcon!!.setClickable(true)
        mFavIcon!!.setOnClickListener(this)
        mHomeIcon!!.setClickable(true)
        mHomeIcon!!.setOnClickListener(this)
        mDownLoadIcon!!.setClickable(true)
        mDownLoadIcon!!.setOnClickListener(this)
        mUserIcon!!.setClickable(true)
        mUserIcon!!.setOnClickListener(this)
        mService = OnLineFMPlayerService.Companion.getMyservice()
        upDateActionBarView()
        upDateImageTint()
    }

    fun upDateActionBarView() {
        when (mCurrentFragmentTag) {
            OnLineHomeFragment.Companion.TAG -> setTopPanelOnActionBar("NetRadio", true)
            OnLineFavFragment.Companion.TAG -> setTopPanelOnActionBar("我的收藏", false)
        }
    }

    override fun onResume() {
        super.onResume()
        if (topBarActionBarView != null) topBarActionBarView!!.setVisibility(View.VISIBLE)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    /* connect to service
    * **/
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        if (service is OnLineFMPlayerService.ServiceBinder) {
            val binder = service as OnLineFMPlayerService.ServiceBinder?
            binder!!.getService()
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {}
    private var mCurrentMainFragment = R.id.home
    fun upDateImageTint() {
        when (mCurrentMainFragment) {
            R.id.home -> {
                mHomeIcon!!.setColorFilter(resources.getColor(R.color.net_fm_back_b))
                mFavIcon!!.setColorFilter(resources.getColor(R.color.net_fm_back_a))
                mDownLoadIcon!!.setColorFilter(resources.getColor(R.color.net_fm_back_a))
                mUserIcon!!.setColorFilter(resources.getColor(R.color.net_fm_back_a))
            }
            R.id.into_fave_icon -> {
                mHomeIcon!!.setColorFilter(resources.getColor(R.color.net_fm_back_a))
                mFavIcon!!.setColorFilter(resources.getColor(R.color.net_fm_back_b))
                mDownLoadIcon!!.setColorFilter(resources.getColor(R.color.net_fm_back_a))
                mUserIcon!!.setColorFilter(resources.getColor(R.color.net_fm_back_a))
            }
            R.id.down_load_file -> {
                mHomeIcon!!.setColorFilter(resources.getColor(R.color.net_fm_back_a))
                mFavIcon!!.setColorFilter(resources.getColor(R.color.net_fm_back_a))
                mDownLoadIcon!!.setColorFilter(resources.getColor(R.color.net_fm_back_b))
                mUserIcon!!.setColorFilter(resources.getColor(R.color.net_fm_back_a))
            }
            R.id.user_info -> {
                mHomeIcon!!.setColorFilter(resources.getColor(R.color.net_fm_back_a))
                mFavIcon!!.setColorFilter(resources.getColor(R.color.net_fm_back_a))
                mDownLoadIcon!!.setColorFilter(resources.getColor(R.color.net_fm_back_a))
                mUserIcon!!.setColorFilter(resources.getColor(R.color.net_fm_back_b))
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.getId()) {
            R.id.into_fave_icon -> {
                favFragment = supportFragmentManager.findFragmentByTag(OnLineFavFragment.Companion.TAG)
                mCurrentFragmentTag = OnLineFavFragment.Companion.TAG
                if (favFragment == null) {
                    mParam2 = "favFragment"
                    favFragment = OnLineFavFragment.Companion.newInstance(mParam1, mParam2)
                }
                ft = supportFragmentManager.beginTransaction()
                ft!!.replace(R.id.main_online_fragment, favFragment!!)
                ft!!.commit()
                mCurrentMainFragment = R.id.into_fave_icon
            }
            R.id.home -> {
                mCurrentFragmentTag = OnLineHomeFragment.Companion.TAG
                home = supportFragmentManager.findFragmentByTag(OnLineHomeFragment.Companion.TAG) as OnLineHomeFragment?
                if (home == null) {
                    mParam2 = "home"
                    home = OnLineHomeFragment.Companion.newInstance(mParam1, mParam2)
                }
                ft = supportFragmentManager.beginTransaction()
                ft!!.replace(R.id.main_online_fragment, home!!)
                ft!!.commit()
                mCurrentMainFragment = R.id.home
            }
        }
        upDateActionBarView()
        upDateImageTint()
    }

    companion object {
        private var mService: OnLineFMPlayerService? = null
    }
}