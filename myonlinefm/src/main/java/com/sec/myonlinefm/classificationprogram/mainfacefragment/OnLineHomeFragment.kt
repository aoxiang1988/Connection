package com.sec.myonlinefm.classificationprogram.mainfacefragment

import android.content.*
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.sec.myonlinefm.abstructObserver.RequestCallBack
import java.util.ArrayList

import com.sec.myonlinefm.R
import com.sec.myonlinefm.OnLineFMConnectManager
import android.os.Bundle
import android.widget.TabHost
import android.widget.TabWidget
import com.sec.myonlinefm.classificationprogram.data.ObservableController
import com.sec.myonlinefm.classificationprogram.data.RequestProgramClassify
import com.sec.myonlinefm.classificationprogram.data.RequestProgramClassifyListPattern
import com.sec.myonlinefm.classificationprogram.fragment.BoutiqueFragment
import com.sec.myonlinefm.classificationprogram.fragment.ClassifyFragment
import com.sec.myonlinefm.classificationprogram.fragment.RadioLiveFragment
import androidx.viewpager.widget.ViewPager
import com.sec.myonlinefm.classificationprogram.fragment.RecommendFragment
import androidx.viewpager.widget.ViewPager.OnPageChangeListener

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the [OnLineHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OnLineHomeFragment : Fragment() {
    private val mContext: Context? = null
    private var mPlayer: OnLineFMConnectManager? = null

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var viewPager: ViewPager? = null
    private var viewContainter: MutableList<Fragment?>? = ArrayList() //存放容器
    private var mTabWidget: TabWidget? = null
    private var count = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
        if (savedInstanceState != null) mCurrentTabId = savedInstanceState.getInt("current_tab")
        mPlayer = OnLineFMConnectManager.mMainInfoCode
        count = 0
        initViewPagerContainer()
    }

    override fun onResume() {
        super.onResume()
        initData()
        viewPager!!.setCurrentItem(mCurrentTabId)
    }

    private var main_view: View? = null
    private var mCurrentTabId = 0
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("current_tab", mCurrentTabId)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        main_view = inflater.inflate(R.layout.fragment_on_line_home, container, false)
        viewPager = main_view!!.findViewById<View?>(R.id.viewpager) as ViewPager
        val viewPagerAdapter = ViewPagerAdapter(childFragmentManager, viewContainter)
        viewPager!!.setAdapter(viewPagerAdapter)
        viewPager!!.addOnPageChangeListener(object : OnPageChangeListener {
            var pos = 0
            var s = 0
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                pos = position
            }

            //当 滑动 切换时
            override fun onPageSelected(position: Int) {
                mTabWidget!!.setCurrentTab(position)
                initMyTabHost(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                s = state
            }
        })
        //        viewPager.setPageTransformer(true, new NewView());
        val mTabHost = main_view!!.findViewById<View?>(android.R.id.tabhost) as TabHost
        mTabHost.setup()
        mTabWidget = mTabHost.tabWidget
        for (i in 0 until mTabWidget!!.getChildCount()) {
            initMyTabHost(i)
        }
        /*
         * newTabSpec()    就是给每个Tab设置一个ID
         * setIndicator()  每个Tab的标题
         * setContent()    每个Tab的标签页布局
         */mTabHost.addTab(mTabHost.newTabSpec("classify_tab")
                .setContent(R.id.tab1).setIndicator(getString(R.string.classify_tab)))
        mTabHost.addTab(mTabHost.newTabSpec("recommend_tab")
                .setContent(R.id.tab2).setIndicator(getString(R.string.recommend_tab)))
        mTabHost.addTab(mTabHost.newTabSpec("boutique_tab")
                .setContent(R.id.tab3).setIndicator(getString(R.string.boutique_tab)))

        /* mTabHost.addTab(mTabHost.newTabSpec("live_tab")
                .setContent(R.id.tab4).setIndicator(getString(R.string.live_tab)));
                */mTabHost.addTab(mTabHost.newTabSpec("radio_live_tab")
                .setContent(R.id.tab3).setIndicator(getString(R.string.radio_live_tab)))
        //TabHost的监听事件
        mTabHost.setOnTabChangedListener { tabId ->
            when (tabId) {
                "classify_tab" -> {
                    count = count + 1
                    viewPager!!.setCurrentItem(0)
                    initMyTabHost(0)
                    mCurrentTabId = 0
                }
                "recommend_tab" -> {
                    count = count + 1
                    viewPager!!.setCurrentItem(1)
                    initMyTabHost(1)
                    mCurrentTabId = 2
                }
                "boutique_tab" -> {
                    count = count + 1
                    viewPager!!.setCurrentItem(2)
                    initMyTabHost(2)
                    mCurrentTabId = 2
                }
                "radio_live_tab" -> {
                    count = count + 1
                    viewPager!!.setCurrentItem(3)
                    initMyTabHost(3)
                    mCurrentTabId = 3
                }
            }
        }

        //解决开始时不显示viewPager
        mTabHost.currentTab = 1
        mTabHost.currentTab = 0
        return main_view
    }

    fun initViewPagerContainer() {
        Log.d("bin1111.yang", "initViewPagerContainer")
        //加入ViewPage的容器
        var mClassifyFragment = childFragmentManager.findFragmentByTag(ClassifyFragment.Companion.TAG)
        if (mClassifyFragment == null) {
            mParam2 = "ClassifyFragment"
            mClassifyFragment = ClassifyFragment.Companion.newInstance(mParam1, mParam2)
        }
        var mBoutiqueFragment = childFragmentManager.findFragmentByTag(BoutiqueFragment.Companion.TAG)
        if (mBoutiqueFragment == null) {
            mParam2 = "BoutiqueFragment"
            mBoutiqueFragment = BoutiqueFragment.Companion.newInstance(mParam1, mParam2)
        }
        var mRadioLiveFragment = childFragmentManager.findFragmentByTag(RadioLiveFragment.Companion.TAG) as RadioLiveFragment?
        if (mRadioLiveFragment == null) {
            mParam2 = "RadioLiveFragment"
            mRadioLiveFragment = RadioLiveFragment.Companion.newInstance(mParam1, mParam2)
        }
        var mRecommendFragment = childFragmentManager.findFragmentByTag(RecommendFragment.Companion.TAG) as RecommendFragment?
        if (mRecommendFragment == null) {
            mParam2 = "RecommendFragment"
            mRecommendFragment = RecommendFragment.Companion.newInstance(mParam1, mParam2)
        }
        viewContainter!!.add(mClassifyFragment)
        viewContainter!!.add(mRecommendFragment)
        viewContainter!!.add(mBoutiqueFragment)
        viewContainter!!.add(mRadioLiveFragment)
    }

    private fun initMyTabHost(position: Int) {
        //修改背景
        mTabWidget!!.getChildAt(position).setBackgroundResource(
                R.drawable.custom_tab_indicator)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewContainter!!.clear()
        viewContainter = null
        mTabWidget!!.removeAllViews()
        mTabWidget = null
        main_view = null
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    private inner class ViewPagerAdapter(fm: FragmentManager?, var viewContainter: MutableList<Fragment?>?) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment? {
            return viewContainter!!.get(position)
        }

        //该方法 决定 并 返回 viewpager中组件的数量
        override fun getCount(): Int {
            return viewContainter!!.size
        }

        override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE
        }
    }

    /* ***
   add by gaolin 5/4
 */
    private var classifyList: MutableList<RequestProgramClassify?>? = null
    private fun initData() {
        mPlayer = OnLineFMConnectManager.mMainInfoCode
        mPlayer!!.getRequestProgramClassify(object : RequestCallBack<RequestProgramClassifyListPattern?> {
            override fun onSuccess(`val`: RequestProgramClassifyListPattern?) {
                Log.d("gaolin", "onSuccess : ")
                ObservableController.Companion.getInstance()!!.notifyObservers(ObservableController.Companion.REFRESH)
                //                initRecommendData(val);
                classifyList = `val`!!.getRequestProgramClassifyList()
            }

            override fun onFail(errorMessage: String?) {
                Log.d("gaolin", "onFail : $errorMessage")
            }
        })
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1: String? = "param1"
        private val ARG_PARAM2: String? = "param2"
        val TAG: String? = "OnLineHomeFragment"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OnLineHomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): Fragment? {
            val fragment = OnLineHomeFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}