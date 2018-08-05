package com.sec.myonlinefm.classificationprogram.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sec.myonlinefm.OnLineFMConnectManager;
import com.sec.myonlinefm.R;
import com.sec.myonlinefm.classificationprogram.ChannelProgramActivity;
import com.sec.myonlinefm.classificationprogram.InfoContextActivity;
import com.sec.myonlinefm.abstructObserver.RequestCallBack;
import com.sec.myonlinefm.classificationprogram.data.ClassifyRecommend;
import com.sec.myonlinefm.classificationprogram.data.ClassifyRecommendPattern;
import com.sec.myonlinefm.classificationprogram.data.ObservableController;
import com.sec.myonlinefm.classificationprogram.data.RequestProgramClassify;
import com.sec.myonlinefm.classificationprogram.data.RequestProgramClassifyListPattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link RecommendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecommendFragment extends Fragment implements Observer{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String TAG = "RecommendFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // add by gaolin 4/19
    private static final int UPDATE_LIST_VIEW = 1;
    private static final int UPDATE_ITEM_TITLE = 2;
    private static final int UPDATE_ITEM_THUMB = 3;
    private static final int UPDATE_HEADER     = 4;

    private ViewPager mViewPager;
    private OnLineFMConnectManager mPlayer;
    private ListView mListView;
    private RecommendAdapter mRecommendAdapter;
    private PagerAdapter mPagerAdapter;
    private LinearLayout naviPoint;
    private Context mContext;
    private int lastPos;

    private List<RequestProgramClassify> mRequestProgramClassifyList = null;
    private List<ImageView> imageViews;
    private ObservableController mObservable = ObservableController.getInstance();

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == UPDATE_LIST_VIEW) {
                mRequestProgramClassifyList = RequestProgramClassifyListPattern.getInstance().getRequestProgramClassifyList();
                mRecommendAdapter.setList(mRequestProgramClassifyList);
            }
            if(msg.what == UPDATE_ITEM_TITLE) {
                mRecommendAdapter.updateTitleView();
            }
            if(msg.what == UPDATE_ITEM_THUMB) {
                mRecommendAdapter.updateThumbView();
            }
            if(msg.what == UPDATE_HEADER) {
                updateImageView();
            }
        }
    };;

    public RecommendFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecommendFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecommendFragment newInstance(String param1, String param2) {
        RecommendFragment fragment = new RecommendFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mPlayer = OnLineFMConnectManager.Companion.getMMainInfoCode();
        mObservable.addObserver(this);

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        mListView  = view.findViewById(R.id.recommed_list);
        mRecommendAdapter = new RecommendAdapter(mContext);
        mListView.setAdapter(mRecommendAdapter);
        mRecommendAdapter.setListView(mListView);

        mRequestProgramClassifyList = RequestProgramClassifyListPattern.getInstance().getRequestProgramClassifyList();
        if (mRequestProgramClassifyList != null) {
            mHandler.sendEmptyMessage(UPDATE_LIST_VIEW);
        }

        View headView = LayoutInflater.from(mContext).inflate(R.layout.fragment_header, null);
        mViewPager = (ViewPager) headView.findViewById(R.id.top_vp);
        naviPoint = (LinearLayout) headView.findViewById(R.id.navi_point);
        initImageViews();
        initViewPager();
        mHandler.postDelayed(new TimerRunnable(), 2000);

        mListView.addHeaderView(headView);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(mContext, "gaolin gaolin" , Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class RecommendAdapter extends BaseAdapter {

        private List<RequestProgramClassify> mRequestProgramClassifyList = null;
        private Context mContext = null;
        private View convertView = null;
        private ListView lv = null;
        private HashMap<Integer, Integer> indexMap = new HashMap<>();

        RecommendAdapter(Context context){
            mContext = context;
        }

        public void setList(List<RequestProgramClassify> request_program_classify_list) {
            mRequestProgramClassifyList = request_program_classify_list;
            notifyDataSetChanged();
        }


        public void setListView(ListView view) {
            lv = view;
        }

        @Override
        public int getCount() {
            if(mRequestProgramClassifyList == null)
                return 0;
            else
                return mRequestProgramClassifyList.size();
        }

        @Override
        public Object getItem(int position) {
            if(mRequestProgramClassifyList == null)
                return null;
            else
                return mRequestProgramClassifyList.get(position);
        }

        @Override
        public long getItemId(int position) {
            if(mRequestProgramClassifyList == null)
                return 0;
            else
                return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (mRequestProgramClassifyList == null)
                return null;
            else {
                int category_id = mRequestProgramClassifyList.get(position).getId();
                final ViewHolder holder;
                CategoryTag categoryTag;
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    assert inflater != null;
                    convertView = inflater.inflate(R.layout.recommend_item_layout, null);
                    this.convertView = convertView;
                    holder = new ViewHolder();
                    holder.navigate = (LinearLayout) convertView.findViewById(R.id.navigate);
                    holder.recommendClass = (TextView) convertView.findViewById(R.id.recmmend_name);
                    holder.naviIcon = (ImageView) convertView.findViewById(R.id.navigate_bar);
                    holder.firstPart = (LinearLayout) convertView.findViewById(R.id.rLay1);
                    holder.firstIm = (ImageView) convertView.findViewById(R.id.recmmend1);
                    holder.firstTx = (TextView) convertView.findViewById(R.id.recmmend1_text);
                    holder.secondPart = (LinearLayout) convertView.findViewById(R.id.rLay2);
                    holder.secondIm = (ImageView) convertView.findViewById(R.id.recmmend2);
                    holder.secondTx = (TextView) convertView.findViewById(R.id.recmmend2_text);
                    holder.thirdPart = (LinearLayout) convertView.findViewById(R.id.rLay3);
                    holder.thirdIm = (ImageView) convertView.findViewById(R.id.recmmend3);
                    holder.thirdTx = (TextView) convertView.findViewById(R.id.recmmend3_text);
                    holder.empty = (View) convertView.findViewById(R.id.empty_view);
                    convertView.setTag(holder);
                    categoryTag = new CategoryTag(category_id);
                    holder.navigate.setTag(categoryTag);
                }else {
                    holder = (ViewHolder) convertView.getTag();
                    if(holder == null) {
                        return null;
                    }
                }
                holder.navigate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent();
                        i.putExtra("category_id", mRequestProgramClassifyList.get(position).getId());
                        i.putExtra("category_name", mRequestProgramClassifyList.get(position).getName());
                        i.setClass(mContext, InfoContextActivity.class);
                        startActivity(i);
                    }
                });
                holder.recommendClass.setText(mRequestProgramClassifyList.get(position).getName());

                final ClassifyRecommend item = ClassifyRecommendPattern.classifyRecommendMap.get(category_id);

                if (item != null && item.getThumb(0) != null) {
                    holder.firstIm.setImageBitmap(item.getThumb(0));
                    RadioIdTag idTag1 = (RadioIdTag) holder.firstIm.getTag();
                    if(idTag1 == null) {
                        idTag1 = new RadioIdTag(item.getId(0));
                        holder.firstIm.setTag(idTag1);
                    }
                }

                if (item != null && item.getTitle(0) != null) {
                    holder.firstTx.setText(item.getTitle(0));
                    RadioIdTag idTag = (RadioIdTag) holder.firstPart.getTag();
                    if(idTag == null) {
                        idTag = new RadioIdTag(item.getId(0));
                        holder.firstPart.setTag(idTag);
                    }
                }

                holder.firstPart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int channel_id = item.getId(0);
                        String channel_name = item.getTitle(0);
                        Intent i = new Intent(mContext, ChannelProgramActivity.class);
                        i.putExtra("channel_id", channel_id);
                        i.putExtra("channel_name", channel_name);
                        startActivity(i);
                    }
                });

                if (item != null && item.getThumb(1) != null) {
                    holder.secondIm.setImageBitmap(item.getThumb(1));
                    RadioIdTag idTag2 = (RadioIdTag) holder.secondIm.getTag();
                    if(idTag2 == null) {
                        idTag2 = new RadioIdTag(item.getId(1));
                        holder.secondIm.setTag(idTag2);
                    }
                }

                if (item != null && item.getTitle(1) != null) {
                    holder.secondTx.setText(item.getTitle(1));
                    RadioIdTag idTag = (RadioIdTag) holder.secondPart.getTag();
                    if(idTag == null) {
                        idTag = new RadioIdTag(item.getId(1));
                        holder.secondPart.setTag(idTag);
                    }
                }

                holder.secondPart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int channel_id = item.getId(1);
                        String channel_name = item.getTitle(1);
                        Intent i = new Intent(mContext, ChannelProgramActivity.class);
                        i.putExtra("channel_id", channel_id);
                        i.putExtra("channel_name", channel_name);
                        startActivity(i);
                    }
                });

                if (item != null && item.getThumb(2) != null) {
                    holder.thirdIm.setImageBitmap(item.getThumb(2));
                    RadioIdTag idTag3 = (RadioIdTag) holder.thirdIm.getTag();
                    if(idTag3 == null) {
                        idTag3 = new RadioIdTag(item.getId(2));
                        holder.thirdIm.setTag(idTag3);
                    }
                }

                if (item != null && item.getTitle(2) != null) {
                    holder.thirdTx.setText(item.getTitle(2));
                    RadioIdTag idTag = (RadioIdTag) holder.thirdPart.getTag();
                    if(idTag == null) {
                        idTag = new RadioIdTag(item.getId(2));
                        holder.thirdPart.setTag(idTag);
                    }
                }

                holder.thirdPart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int channel_id = item.getId(2);
                        String channel_name = item.getTitle(2);
                        Intent i = new Intent(mContext, ChannelProgramActivity.class);
                        i.putExtra("channel_id", channel_id);
                        i.putExtra("channel_name", channel_name);
                        startActivity(i);
                    }
                });
                notifyDataSetChanged();
                return convertView;
            }
        }

        public void updateTitleView() {
            int first = lv.getFirstVisiblePosition();
            int last = lv.getLastVisiblePosition();

            Set<Integer> keySet = ClassifyRecommendPattern.classifyRecommendMap.keySet();
            for(int i = first; i < last; i++) {
                View view = lv.getChildAt(i);
                if(view == null) {
                    continue;
                }
                ViewHolder holder = (ViewHolder) view.getTag();
                if(holder == null) {
                    continue;
                }
                final CategoryTag tag = (CategoryTag) holder.navigate.getTag();
                if(tag == null) {
                    continue;
                }
                RadioIdTag idTag = (RadioIdTag) holder.firstPart.getTag();
                if(idTag != null) {
                    continue;
                }
                if(keySet.contains(tag.mCategoryId)) {

                    holder.firstTx.setText(ClassifyRecommendPattern.classifyRecommendMap.get(tag.mCategoryId).getTitle(0));
                    RadioIdTag firstTag = new RadioIdTag(ClassifyRecommendPattern.classifyRecommendMap.get(tag.mCategoryId).getId(0));
                    holder.firstPart.setTag(firstTag);


                    holder.secondTx.setText(ClassifyRecommendPattern.classifyRecommendMap.get(tag.mCategoryId).getTitle(1));
                    RadioIdTag secondTag = new RadioIdTag(ClassifyRecommendPattern.classifyRecommendMap.get(tag.mCategoryId).getId(1));
                    holder.secondPart.setTag(secondTag);
                    holder.secondPart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int channel_id = ClassifyRecommendPattern.classifyRecommendMap.get(tag.mCategoryId).getId(1);
                            String channel_name = ClassifyRecommendPattern.classifyRecommendMap.get(tag.mCategoryId).getTitle(1);
                            Intent i = new Intent(mContext, ChannelProgramActivity.class);
                            i.putExtra("channel_id", channel_id);
                            i.putExtra("channel_name", channel_name);
                            startActivity(i);
                        }
                    });

                    holder.thirdTx.setText(ClassifyRecommendPattern.classifyRecommendMap.get(tag.mCategoryId).getTitle(2));
                    RadioIdTag thirdTag = new RadioIdTag(ClassifyRecommendPattern.classifyRecommendMap.get(tag.mCategoryId).getId(2));
                    holder.thirdPart.setTag(thirdTag);
                    holder.secondPart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int channel_id = ClassifyRecommendPattern.classifyRecommendMap.get(tag.mCategoryId).getId(1);
                            String channel_name = ClassifyRecommendPattern.classifyRecommendMap.get(tag.mCategoryId).getTitle(1);
                            Intent i = new Intent(mContext, ChannelProgramActivity.class);
                            i.putExtra("channel_id", channel_id);
                            i.putExtra("channel_name", channel_name);
                            startActivity(i);
                        }
                    });
                }
            }
        }

        public void updateThumbView() {
            int first = lv.getFirstVisiblePosition();
            int last = lv.getLastVisiblePosition();

            Set<Integer> keySet = ClassifyRecommendPattern.classifyRecommendMap.keySet();
            for(int i = first; i < last; i++) {
                View view = lv.getChildAt(i);
                if(view == null) {
                    continue;
                }
                ViewHolder holder = (ViewHolder) view.getTag();
                if(holder == null) {
                    continue;
                }

                CategoryTag tag = (CategoryTag) holder.navigate.getTag();
                if(tag == null) {
                    continue;
                }
                if(keySet.contains(tag.mCategoryId)) {
                    RadioIdTag idTag1 = (RadioIdTag) holder.firstIm.getTag();
                    if(idTag1 == null) {
                        notifyDataSetChanged();
                    }
                    RadioIdTag idTag2 = (RadioIdTag) holder.secondIm.getTag();
                    if(idTag2 == null) {
                        notifyDataSetChanged();
                    }
                    RadioIdTag idTag3 = (RadioIdTag) holder.thirdIm.getTag();
                    if(idTag3 == null) {
                        notifyDataSetChanged();
                    }
                }
            }
        }
    }

    private final class ViewHolder {
        LinearLayout navigate;
        TextView recommendClass;
        ImageView naviIcon;
        LinearLayout firstPart;
        ImageView firstIm;
        TextView firstTx;
        LinearLayout secondPart;
        ImageView secondIm;
        TextView secondTx;
        LinearLayout thirdPart;
        ImageView thirdIm;
        TextView thirdTx;
        View empty;
    }

    private final class CategoryTag {
        int mCategoryId;
        CategoryTag(int categoryId) {
            mCategoryId = categoryId;
        }
    }

    private final class RadioIdTag {
        int mId;
        RadioIdTag(int id) {
            mId = id;
        }
    }

    private void initImageViews() {
        imageViews = new ArrayList<>();
        int[] imageResIds = new int[]{R.drawable.fm_radio_now_playing_01,
                R.drawable.fm_radio_now_playing_02,
                R.drawable.fm_radio_now_playing_03,
                R.drawable.hybrid_radio_info_no_image,
                R.drawable.radio_about};
        ImageView imageView;
        View pointView;
        for (int i = 0; i < imageResIds.length; i++) {
            imageView = new ImageView(mContext);
            if(ClassifyRecommendPattern.scrollBitmap[i] != null) {
                imageView.setImageBitmap(ClassifyRecommendPattern.scrollBitmap[i]);
            }else {
                imageView.setImageResource(imageResIds[i]);
            }
            imageViews.add(imageView);
            //add naviPoint
            pointView = new View(mContext);
            pointView.setBackgroundResource(R.drawable.point_selector);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(20,20);
            if(i != 0) {
                layoutParams.leftMargin = 20;
            }
            pointView.setEnabled(false);
            naviPoint.addView(pointView, layoutParams);
        }
    }

    private class TimerRunnable implements Runnable{
        @Override
        public void run() {
            int curItem = mViewPager.getCurrentItem();
            mViewPager.setCurrentItem(curItem + 1);
            if (mHandler != null) {
                mHandler.postDelayed(this,2000);
            }
        }
    }

    private void initViewPager() {
        mPagerAdapter = new MyPagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(imageViews.size()*1000);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changePoints(position%imageViews.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        lastPos = 0;
        naviPoint.getChildAt(0).setEnabled(true);
    }

    private class MyPagerAdapter extends PagerAdapter{
        public MyPagerAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return 10000;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            try {
                container.addView(imageViews.get(position%imageViews.size()));
            }catch (Exception e) {
                e.printStackTrace();
            }
            return imageViews.get(position%imageViews.size());
        }
    }

    public void changePoints(int newPosition) {
        naviPoint.getChildAt(lastPos).setEnabled(false);
        naviPoint.getChildAt(newPosition).setEnabled(true);
        lastPos = newPosition;
    }

    public void updateImageView() {
        for(int i = 0; i < 5; i++) {
            if(ClassifyRecommendPattern.scrollBitmap[i] != null) {
                imageViews.get(i).setImageBitmap(ClassifyRecommendPattern.scrollBitmap[i]);
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        int event = (int) arg;
        Log.d("gaolin","event = " + event);
        switch (event) {
            case 1:
                mHandler.sendEmptyMessage(UPDATE_LIST_VIEW);
                break;
            case 2:
                mHandler.sendEmptyMessage(UPDATE_ITEM_TITLE);
                break;
            case 3:
                mHandler.sendEmptyMessage(UPDATE_ITEM_THUMB);
                break;
            case 4:
                mHandler.sendEmptyMessage(UPDATE_HEADER);
                break;
        }
    }
}
