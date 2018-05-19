package com.sec.myonlinefm.classificationprogram.mainfacefragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.sec.myonlinefm.OnLineFMConnectManager;
import com.sec.myonlinefm.R;
import com.sec.myonlinefm.classificationprogram.RequestCallBack;
import com.sec.myonlinefm.classificationprogram.data.ClassifyRecommend;
import com.sec.myonlinefm.classificationprogram.data.ClassifyRecommendPattern;
import com.sec.myonlinefm.classificationprogram.data.ObservableController;
import com.sec.myonlinefm.classificationprogram.data.RequestProgramClassify;
import com.sec.myonlinefm.classificationprogram.data.RequestProgramClassifyListPattern;
import com.sec.myonlinefm.classificationprogram.fragment.BoutiqueFragment;
import com.sec.myonlinefm.classificationprogram.fragment.ClassifyFragment;
import com.sec.myonlinefm.classificationprogram.fragment.RadioLiveFragment;
import com.sec.myonlinefm.classificationprogram.fragment.RecommendFragment;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.FragmentManager;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link OnLineHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnLineHomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Context mContext;

    private OnLineFMConnectManager mPlayer;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static final String TAG = "OnLineHomeFragment";

    private ViewPager viewPager = null;
    private List<Fragment> viewContainter = new ArrayList<>();   //存放容器
    private TabWidget mTabWidget = null;
    private int count;

    public OnLineHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OnLineHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OnLineHomeFragment newInstance(String param1, String param2) {
        OnLineHomeFragment fragment = new OnLineHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        if(savedInstanceState != null) mCurrentTabId = savedInstanceState.getInt("current_tab");
        mPlayer = OnLineFMConnectManager.Companion.getMMainInfoCode();
        count = 0;
        initViewPagerContainer();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
        viewPager.setCurrentItem(mCurrentTabId);
    }

    private View main_view;
    private int mCurrentTabId = 0;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("current_tab",mCurrentTabId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        main_view = inflater.inflate(R.layout.fragment_on_line_home, container, false);
        viewPager = (ViewPager) main_view.findViewById(R.id.viewpager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), viewContainter);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
//        viewPager.setPageTransformer(true, new NewView());
        TabHost mTabHost = (TabHost) main_view.findViewById(android.R.id.tabhost);
        mTabHost.setup();
        mTabWidget = mTabHost.getTabWidget();
        for (int i = 0; i < mTabWidget.getChildCount(); i++){
            initMyTabHost(i);
        }
        /*
         * newTabSpec()    就是给每个Tab设置一个ID
         * setIndicator()  每个Tab的标题
         * setContent()    每个Tab的标签页布局
         */
        mTabHost.addTab(mTabHost.newTabSpec("classify_tab")
                .setContent(R.id.tab1).setIndicator(getString(R.string.classify_tab)));

        mTabHost.addTab(mTabHost.newTabSpec("recommend_tab")
                .setContent(R.id.tab2).setIndicator(getString(R.string.recommend_tab)));

        mTabHost.addTab(mTabHost.newTabSpec("boutique_tab")
                .setContent(R.id.tab3).setIndicator(getString(R.string.boutique_tab)));

        /* mTabHost.addTab(mTabHost.newTabSpec("live_tab")
                .setContent(R.id.tab4).setIndicator(getString(R.string.live_tab)));
                */
        mTabHost.addTab(mTabHost.newTabSpec("radio_live_tab")
                .setContent(R.id.tab3).setIndicator(getString(R.string.radio_live_tab)));
        //TabHost的监听事件
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                switch (tabId) {
                    case "classify_tab":
                        count = count + 1;
                        viewPager.setCurrentItem(0);
                        initMyTabHost(0);
                        mCurrentTabId = 0;
                        break;
                    case "recommend_tab":
                        count = count + 1;
                        viewPager.setCurrentItem(1);
                        initMyTabHost(1);
                        mCurrentTabId = 2;
                        break;
                    case "boutique_tab":
                        count = count + 1;
                        viewPager.setCurrentItem(2);
                        initMyTabHost(2);
                        mCurrentTabId = 2;
                        break;
                    /*case "live_tab":
                        count = count + 1;
                        viewPager.setCurrentItem(3);
                        initMyTabHost(3);
                        break;*/
                    case "radio_live_tab":
                        count = count + 1;
                        viewPager.setCurrentItem(3);
                        initMyTabHost(3);
                        mCurrentTabId = 3;
                        break;
                }
            }
        });

        //解决开始时不显示viewPager
        mTabHost.setCurrentTab(1);
        mTabHost.setCurrentTab(0);
        return main_view;
    }

    public void initViewPagerContainer(){
        Log.d("bin1111.yang","initViewPagerContainer");
        //加入ViewPage的容器
        ClassifyFragment mClassifyFragment = (ClassifyFragment)getChildFragmentManager().findFragmentByTag(ClassifyFragment.TAG);
        if(mClassifyFragment == null) {
            mParam2 = "ClassifyFragment";
            mClassifyFragment = ClassifyFragment.newInstance(mParam1, mParam2);
        }
        BoutiqueFragment mBoutiqueFragment = (BoutiqueFragment)getChildFragmentManager().findFragmentByTag(BoutiqueFragment.TAG);
        if(mBoutiqueFragment == null) {
            mParam2 = "BoutiqueFragment";
            mBoutiqueFragment = BoutiqueFragment.newInstance(mParam1, mParam2);
        }
        RadioLiveFragment mRadioLiveFragment = (RadioLiveFragment)getChildFragmentManager().findFragmentByTag(RadioLiveFragment.TAG);
        if(mRadioLiveFragment == null) {
            mParam2 = "RadioLiveFragment";
            mRadioLiveFragment = RadioLiveFragment.newInstance(mParam1, mParam2);
        }
        RecommendFragment mRecommendFragment = (RecommendFragment)getChildFragmentManager().findFragmentByTag(RecommendFragment.TAG);
        if(mRecommendFragment == null) {
            mParam2 = "RecommendFragment";
            mRecommendFragment = RecommendFragment.newInstance(mParam1, mParam2);
        }

        viewContainter.add(mClassifyFragment);
        viewContainter.add(mRecommendFragment);
        viewContainter.add(mBoutiqueFragment);
        viewContainter.add(mRadioLiveFragment);
    }

    private void initMyTabHost(int position) {
        //修改背景
        mTabWidget.getChildAt(position).setBackgroundResource(
                R.drawable.custom_tab_indicator);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewContainter.clear();
        viewContainter = null;
        mTabWidget.removeAllViews();
        mTabWidget = null;
        main_view = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> viewContainter;
        public ViewPagerAdapter(FragmentManager fm, List<Fragment> viewContainter) {
            super(fm);
            this.viewContainter = viewContainter;
        }

        @Override
        public Fragment getItem(int position) {
            return viewContainter.get(position);
        }

        //该方法 决定 并 返回 viewpager中组件的数量
        @Override
        public int getCount() {
            return viewContainter.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }
    }

    /* ***
   add by gaolin 5/4
 */
    private void initData() {
        mPlayer = OnLineFMConnectManager.Companion.getMMainInfoCode();
        mPlayer.getRequestProgramClassify(new RequestCallBack<RequestProgramClassifyListPattern>() {
            @Override
            public void onSuccess(RequestProgramClassifyListPattern val) {
                Log.d("gaolin","onSuccess : ");
                ObservableController.getInstance().notifyObservers(ObservableController.REFRESH);
                initRecommendData(val);
            }

            @Override
            public void onFail(String errorMessage) {
                Log.d("gaolin","onFail : "+errorMessage);
            }
        });
    }

    private void initRecommendData(RequestProgramClassifyListPattern val) {
        for(int i = 0; i < 5; i++) {
            RequestProgramClassify item = val.getRequestProgramClassifyList().get(i);
            final int index = i;
            mPlayer.getFiveRecmThumb(new RequestCallBack<ClassifyRecommend>() {
                @Override
                public void onSuccess(ClassifyRecommend val) {
                    initHeaderThumb(index, val);
                }

                @Override
                public void onFail(String errorMessage) {
                    Log.d("gaolin","onFail : "+errorMessage);
                }
            }, item.getSectionId());
        }

        for(RequestProgramClassify item : val.getRequestProgramClassifyList()) {
            mPlayer.getRequestRecmmendProgram(new RequestCallBack<ClassifyRecommend>() {
                @Override
                public void onSuccess(ClassifyRecommend val) {
                    ClassifyRecommendPattern map = ClassifyRecommendPattern.getInstance();
                    map.addRecommendMap(val.getCategoryID(), val);
                    ObservableController.getInstance().notifyObservers(ObservableController.UPDATETITLE);
                    initRecommendThumbs(val);
                }

                @Override
                public void onFail(String errorMessage) {
                    Log.d("gaolin","onFail : "+errorMessage);
                }
            }, item.getId());
        }
    }

    private void initHeaderThumb(int index, final ClassifyRecommend item) {
        final String url = item.getThumbUrl(0);
        if(url != null) {
            final int i = index;
            mPlayer.getRecommendThumb(new RequestCallBack<Bitmap>() {
                @Override
                public void onSuccess(Bitmap val) {
                    ClassifyRecommendPattern.scrollBitmap[i] = val;
                    ObservableController.getInstance().notifyObservers(ObservableController.UPDATEHEADER);
                }

                @Override
                public void onFail(String errorMessage) {
                    Log.d("gaolin","onFail : "+errorMessage);
                }
            }, url);
        }
    }

    private void initRecommendThumbs(final ClassifyRecommend item) {
        for(int i = 0; i < 3; i++) {
            final String url = item.getThumbUrl(i);
            if(url != null) {
                final int index = i;
                mPlayer.getRecommendThumb(new RequestCallBack<Bitmap>() {
                    @Override
                    public void onSuccess(Bitmap val) {
                        item.setThumb(index, val);
                        ClassifyRecommendPattern map = ClassifyRecommendPattern.getInstance();
                        ObservableController.getInstance().notifyObservers(ObservableController.UPDATETHUMB);
                    }

                    @Override
                    public void onFail(String errorMessage) {
                        Log.d("gaolin","onFail : "+errorMessage);
                    }
                }, url);
            }
        }
    }

}
