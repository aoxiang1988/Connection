package com.sec.myonlinefm.classificationprogram;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sec.myonlinefm.OnLineFMConnectManager;
import com.sec.myonlinefm.R;
import com.sec.myonlinefm.UpdateListViewAsyncTask;
import com.sec.myonlinefm.abstructObserver.RequestCallBack;
import com.sec.myonlinefm.defineview.NoScrollBarGridView;
import com.sec.myonlinefm.defineview.RefreshListView;
import com.sec.myonlinefm.classificationprogram.data.DemandChannel;
import com.sec.myonlinefm.classificationprogram.data.DemandChannelPattern;
import com.sec.myonlinefm.data.ClassificationAttributePattern;
import com.sec.myonlinefm.data.PropertyInfo;

import java.util.HashMap;
import java.util.List;

@SuppressLint("DefaultLocale")
public class InfoContextActivity extends AppCompatActivity implements RefreshListView.OnRefreshListener, View.OnClickListener {

    private static final int UPDATE_CLASSIFY_SPINNER_LIST = 1;
    private static final int UPDATE_SELECT_SPINNER_LIST = 2;
    private static final int UPDATE_CONTEXT_LIST = 3;
    private static final int EMPTY_CONTEXT_LIST = 4;

    private OnLineFMConnectManager mPlayer;
    private Context mContext = null;
    private int mCategoryID = -1;
    private String mCategoryName = "";
    private List<DemandChannel> mDemandChannelsList;
    private List<PropertyInfo> mAttributeInfo = null;
    private HashMap<Integer, List<PropertyInfo.values>> mAttributeInfoMap = null;
    private List<PropertyInfo.values> mClassifyList;

    private int mCurrentClassifyAttr = 0;

    private LinearLayout mTabClassifyHead;
    private TextView mTabClassifyTitleView;
    private ImageView mTabClassifyTitleImg;

    private LinearLayout mTabBoutiqueHead;
    private TextView mTabBoutiqueTitleView;
    private ImageView mTabBoutiqueTitleImg;

    private LinearLayout mTabSelectHead;
    private TextView mTabSelectTitleView;
    private ImageView mTabSelectTitleImg;

    private RefreshListView mContextList;
    private TextView mPageNumTextView;
    private int mTotalPage = 0;
    private int mCurrentPage = 1;

    private Integer[] mAttrId;
    private String mAttrName;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_CLASSIFY_SPINNER_LIST:
                    mTabClassifyTitleView.setText(mAttributeInfoMap.get(mAttributeInfo.get(0).getPropertyInfoId()).
                            get(0).
                            getvaluesname());
                    mTabClassifyHead.setClickable(true);
                    mTabClassifyHead.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTabClassifyTitleView.setTextColor(getResources().getColor(R.color.net_fm_back_b));
                            mTabClassifyTitleImg.setImageResource(R.drawable.drop_up);
                            mCustomDialog(UPDATE_CLASSIFY_SPINNER_LIST);
                        }
                    });

                    mTabSelectTitleView.setText("筛选");
                    mTabSelectHead.setClickable(true);
                    mTabSelectHead.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTabSelectTitleView.setTextColor(getResources().getColor(R.color.net_fm_back_b));
                            mTabSelectTitleImg.setImageResource(R.drawable.drop_up);
                            mCustomDialog(UPDATE_SELECT_SPINNER_LIST);
                        }
                    });
                    break;
                case UPDATE_CONTEXT_LIST:
                    upDateContextList();
                    break;
                case EMPTY_CONTEXT_LIST:
                    Toast.makeText(getBaseContext(), "数据为空，请检查网络或修改查询条件", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        mAttrId = null;
        mCurrentClassifyAttr = 0;
        super.onDestroy();
    }

    private void getClassifyContextList() {
        if(mCategoryID != -1) {
            mContextList.setOnItemClickListener(null);
            mPlayer.getClassifyInfoContext(mCurrentPage, mCategoryID, mAttrId, new RequestCallBack<DemandChannelPattern>() {
                @Override
                public void onSuccess(DemandChannelPattern val) {
                    mDemandChannelsList = val.getDemandChannelsList();
                    mTotalPage = mDemandChannelsList.get(0).getTotal()/30;
                    if(mDemandChannelsList.get(0).getTotal()%30 != 0)
                        mTotalPage = mTotalPage + 1;
                    mHandler.sendEmptyMessage(UPDATE_CONTEXT_LIST);
                }

                @Override
                public void onFail(String errorMessage) {
                    Log.d("bin1111.yang","onFail : "+errorMessage);
                    mHandler.sendEmptyMessage(EMPTY_CONTEXT_LIST);
                }
            });
        }
    }

    private DemandChannelAdapter mDemandChannelAdapter;

//    @Override
//    public void onRefresh() {
//        mCurrentPage = mCurrentPage + 1;
//        getClassifyContextList();
//    }

    @Override
    public void onPullRefresh() {
        if(mCurrentPage > 1) {
            mCurrentPage = mCurrentPage - 1;
            getClassifyContextList();
        } else {
            mContextList.completeRefresh();
        }
    }

    @Override
    public void onLoadingMore() {
        if(mCurrentPage < mTotalPage) {
            mContextList.startScrollAnim();
            mCurrentPage = mCurrentPage + 1;
            getClassifyContextList();
        } else {
            mContextList.completeRefresh();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset_but:
                for (int j=1; j<mAttributeInfo.size(); j++) {
                    mAttrId[j] = -1;
                }
                if(builder != null)
                    builder.cancel();
                break;
            case R.id.confirm_but:
                mCurrentPage = 1;
                getClassifyContextList();
                if(builder != null)
                    builder.cancel();
                break;
        }
    }

    private class DemandChannelAdapter extends BaseAdapter {
        private int current_position = 0;

        public void setCurrentPosition(int current_position) {
            this.current_position = current_position;
        }

        @Override
        public int getCount() {
            return mDemandChannelsList.size();
        }

        @Override
        public Object getItem(int position) {
            return mDemandChannelsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final DemandChannelViewHolder holder;
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert inflater != null;
                convertView = inflater.inflate(R.layout.demand_channel_item, null);

                holder = new DemandChannelViewHolder();
                holder.mDemandChannelPic = convertView.findViewById(R.id.demand_channel_pic);
                holder.mDemandChannelTitle = convertView.findViewById(R.id.demand_channel_title);
                holder.mDemandChannelDescription = convertView.findViewById(R.id.demand_channel_description);
                holder.mDemandChannelPlayer = convertView.findViewById(R.id.play_count);
                holder.mDemandChannelTotal = convertView.findViewById(R.id.context_total);
                convertView.setTag(holder);

            } else holder = (DemandChannelViewHolder) convertView.getTag();

//            holder.mDemandChannelPic.setImageBitmap(mPlayer.getBitmap(mDemandChannelsList.get(position).getThumbs(), 60, 60));
            UpdateListViewAsyncTask asyncTask = new UpdateListViewAsyncTask(holder.mDemandChannelPic,
                    mDemandChannelsList.get(position).getTitle(),
                    mPlayer, 60, 60);
            asyncTask.execute(mDemandChannelsList.get(position).getThumbs());
            String isBought = "";
            if(mDemandChannelsList.get(position).getSaleType() == DemandChannel.UN_BOUGHT_SALE_TYPE) {
                isBought = "[未购买]";
            }

            if(mDemandChannelsList.get(position).getSaleType() == DemandChannel.BOUGHT_SALE_TYPE) {
                isBought = "[已购买]";
            }

            holder.mDemandChannelTitle.setText(mDemandChannelsList.get(position).getTitle());
            holder.mDemandChannelDescription.setText(mDemandChannelsList.get(position).getDescription()+isBought);
            holder.mDemandChannelPlayer.setText(mDemandChannelsList.get(position).getPlayCount());
            holder.mDemandChannelTotal.setText(String.valueOf(mDemandChannelsList.get(position).getProgramCount()));

            return convertView;
        }
    }

    static class DemandChannelViewHolder {
        ImageView mDemandChannelPic;
        TextView mDemandChannelTitle;
        TextView mDemandChannelDescription;
        TextView mDemandChannelPlayer;
        TextView mDemandChannelTotal;
    }

    private void upDateContextList() {

        mContextList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mDemandChannelsList != null) {
                    int current_position = position - 1;//list has head view...
                    int channel_id = mDemandChannelsList.get(current_position).getId();
                    String channel_name = mDemandChannelsList.get(current_position).getTitle();
                    Intent i = new Intent(InfoContextActivity.this, ChannelProgramActivity.class);
                    i.putExtra("channel_id", channel_id);
                    i.putExtra("channel_name", channel_name);
                    startActivity(i);
                }
            }
        });

        mPageNumTextView.setText(String.format("第%d页", mCurrentPage));
        if(mDemandChannelAdapter == null) {
            mDemandChannelAdapter = new DemandChannelAdapter();
            mContextList.setAdapter(mDemandChannelAdapter);
        } else {
            mDemandChannelAdapter.notifyDataSetChanged();
            mContextList.completeRefresh();
        }
        mContextList.setSelection(0);
    }

    private AlertDialog builder = null;

    private View topBarActionBarView = null;

    public void setTopPanelOnActionBar(String bar_title) {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(false);
            bar.setDisplayShowHomeEnabled(false);
            bar.setDisplayShowTitleEnabled(false);
            bar.setElevation(0);
        }
        topBarActionBarView = getLayoutInflater().inflate(R.layout.top_bar_panel_online, null);
        TextView mTopBarTitle = topBarActionBarView.findViewById(R.id.top_bar_title);
        ImageView mBackBut = topBarActionBarView.findViewById(R.id.back_but);
        mBackBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTopBarTitle.setText(bar_title);
        if (bar != null) {
            bar.setCustomView(topBarActionBarView);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            bar.show();
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

    private void mCustomDialog(final int type) {
        if(builder != null)
            builder.cancel();
        builder = new AlertDialog.Builder(this, R.style.CreatDialog).create();
        builder.show();
        LayoutInflater factory = LayoutInflater.from(this);
        View view = factory.inflate(R.layout.custom_dialog_layout, null);
        ListView mTabList = view.findViewById(R.id.tab_dialog_list);
        ScrollView mScrollView = view.findViewById(R.id.scrollview);
        LinearLayout mSelectView = view.findViewById(R.id.select_linear_view);
        builder.setContentView(view);
        final Window dialogWindow = builder.getWindow();
        assert dialogWindow != null;
        dialogWindow.setGravity( Gravity.BOTTOM);//显示在底部
        dialogWindow.setWindowAnimations(R.style.take_photo_anim);
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        final WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值

        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation ; //获取屏幕方向
        if(ori == Configuration.ORIENTATION_LANDSCAPE){
            p.height = (int) (d.getWidth() * 0.4); // 高度设置为屏幕的0.5
            p.width = (int) (d.getHeight()); // 宽度设置为屏幕宽
            //横屏
        }else if(ori == Configuration.ORIENTATION_PORTRAIT){
            p.height = (int) (d.getHeight() * 0.4); // 高度设置为屏幕的0.5
            p.width = (int) (d.getWidth()); // 宽度设置为屏幕宽
            //竖屏
        }
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(type == UPDATE_CLASSIFY_SPINNER_LIST) {
                    mTabClassifyTitleView.setTextColor(getResources().getColor(R.color.black));
                    mTabClassifyTitleImg.setImageResource(R.drawable.drop_down);
                }
                if(type == UPDATE_SELECT_SPINNER_LIST) {
                    mTabSelectTitleView.setTextColor(getResources().getColor(R.color.black));
                    mTabSelectTitleImg.setImageResource(R.drawable.drop_down);
                }
            }
        });

        switch (type) {
            case UPDATE_CLASSIFY_SPINNER_LIST:
                mTabList.setVisibility(View.VISIBLE);
                mScrollView.setVisibility(View.GONE);
                final SpinnerListAdapter adapter = new SpinnerListAdapter(mAttributeInfoMap.
                        get(mAttributeInfo.get(0).getPropertyInfoId()));
                mTabList.setAdapter(adapter);
                mTabList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        PropertyInfo.values values = mAttributeInfoMap.
                                get(mAttributeInfo.get(0).getPropertyInfoId()).get(position);
                        mTabClassifyTitleView.setText(values.getvaluesname());
                        mCurrentClassifyAttr = mAttributeInfoMap.
                                get(mAttributeInfo.get(0).getPropertyInfoId()).get(position).getvaluesId();
                        mAttrId[0] = mCurrentClassifyAttr;
                        adapter.notifyDataSetChanged();
                        mCurrentPage = 1;
                        getClassifyContextList();
                    }
                });
                break;
            case UPDATE_SELECT_SPINNER_LIST:
                mTabList.setVisibility(View.GONE);
                mScrollView.setVisibility(View.VISIBLE);

                for (int j=1; j<mAttributeInfo.size(); j++) {
                    final int finalJ = j;
                    final List<PropertyInfo.values> mClassifyList = mAttributeInfoMap.get(mAttributeInfo.get(j).getPropertyInfoId());
                    View select_view =LayoutInflater.from(this).inflate(R.layout.select_spinner_view,null);
                    TextView select_name = select_view.findViewById(R.id.select_item_title);
                    NoScrollBarGridView select_gird_view = select_view.findViewById(R.id.select_item);
                    final ClassifyAdapter classifyAdapter = new ClassifyAdapter(this, mClassifyList, mAttrId[finalJ]);
                    select_gird_view.setAdapter(classifyAdapter);
                    select_gird_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            mAttrId[finalJ] = classifyAdapter.getItem(position).getvaluesId();
                            classifyAdapter.setColor(mAttrId[finalJ]);
                            classifyAdapter.notifyDataSetChanged();
                        }
                    });
                    select_name.setText(mAttributeInfo.get(j).getPropertyInfoname());
                    LinearLayout.LayoutParams psd = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            (int) (classifyAdapter.getRowNum()*getResources().getDimension(R.dimen.no_scroll_bar_item_height)));
                    psd.setMargins(5, 5, 5, 5);
                    select_view.setLayoutParams(psd);
                    mSelectView.addView(select_view);
                }
                View select_controller_view =LayoutInflater.from(this).inflate(R.layout.select_controller_layout,null);
                Button mCancel = select_controller_view.findViewById(R.id.reset_but);
                Button mConfirm = select_controller_view.findViewById(R.id.confirm_but);
                mCancel.setOnClickListener(this);
                mConfirm.setOnClickListener(this);
                LinearLayout.LayoutParams psd = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        (int) getResources().getDimension(R.dimen.no_scroll_bar_item_height));
                psd.setMargins(5, 5, 5, 5);
                select_controller_view.setLayoutParams(psd);
                mSelectView.addView(select_controller_view);
                break;
        }
        dialogWindow.setAttributes(p);
    }

    static class ClassifyViewHolder {
        TextView classify_name;
    }
    private class ClassifyAdapter extends BaseAdapter {

        private List<PropertyInfo.values> mClassifyList = null;
        private Context mContext = null;
        private int selected_position = -1;

        ClassifyAdapter(Context context ,
                        List<PropertyInfo.values> classify_list,
                        int selected_position) {
            this.selected_position = selected_position;
            mClassifyList = classify_list;
            mContext = context;
        }

        public int getRowNum() {
            if(mClassifyList.size()>0)
                if(mClassifyList.size()%3 != 0)
                    return mClassifyList.size()/3+1;
                else return mClassifyList.size()/3;
            return 0;
        }

        @Override
        public int getCount() {
            if(mClassifyList == null)
                return 0;
            else
                return mClassifyList.size();
        }

        @Override
        public PropertyInfo.values getItem(int position) {
            if(mClassifyList == null)
                return null;
            else
                return mClassifyList.get(position);
        }

        @Override
        public long getItemId(int position) {
            if(mClassifyList == null)
                return 0;
            else
                return mClassifyList.get(position).getvaluesId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ClassifyViewHolder holder;
            if (mClassifyList == null)
                return null;
            else {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    assert inflater != null;
                    convertView = inflater.inflate(R.layout.select_item_layout, null);
                    holder = new ClassifyViewHolder();
                    holder.classify_name = convertView.findViewById(R.id.classify_name);
                    convertView.setTag(holder);

                } else holder = (ClassifyViewHolder) convertView.getTag();
                convertView.setFocusable(false);
                holder.classify_name.setText(mClassifyList.get(position).getvaluesname());
                if(mClassifyList.get(position).getvaluesId() == selected_position) {
                    holder.classify_name.setTextColor(getResources().getColor(R.color.net_fm_back_b));
                    holder.classify_name.setBackground(getDrawable(R.drawable.selceted_back_ground));
                }
                else {
                    holder.classify_name.setTextColor(getResources().getColor(R.color.black));
                    holder.classify_name.setBackground(getDrawable(R.drawable.about_app_info_background));
                }
                return convertView;
            }
        }

        public void setColor(int selected_position) {
            this.selected_position = selected_position;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_context);
        mContext = this;
        mPlayer = OnLineFMConnectManager.Companion.getMMainInfoCode();
        Intent i = getIntent();
        mCategoryID = i.getIntExtra("category_id", -1);
        mCategoryName = i.getStringExtra("category_name");
        setTopPanelOnActionBar(mCategoryName);
        initTabView();
        if(mCategoryID != -1) {
            mPlayer.getClassificationAttribute(mCategoryID, new RequestCallBack<ClassificationAttributePattern>() {
                @Override
                public void onSuccess(ClassificationAttributePattern val) {
                    mAttributeInfo = val.getInfo();
                    mAttributeInfoMap = val.getInfoMap();
                    mAttrId = new Integer[mAttributeInfo.size()];
                    for(int i = 0; i<mAttrId.length; i++){
                        Log.d("bin1111.yang","mCategoryID : "+mCategoryID);
                        mAttrId[i] = -1;
                    }
                    mHandler.sendEmptyMessage(UPDATE_CLASSIFY_SPINNER_LIST);
                }

                @Override
                public void onFail(String errorMessage) {
                    Log.d("bin1111.yang","onFail : "+errorMessage);
                }
            });
            getClassifyContextList();
        }
    }

    private void initTabView() {
        mTabClassifyHead = (LinearLayout) findViewById(R.id.tab_classify_head);
        mTabClassifyTitleView = (TextView) findViewById(R.id.tab_classify_title);
        mTabClassifyTitleImg = (ImageView) findViewById(R.id.tab_classify_title_img);
        mTabClassifyHead.setClickable(false);

        mTabBoutiqueHead = (LinearLayout) findViewById(R.id.tab_boutique_head);
        mTabBoutiqueTitleView = (TextView) findViewById(R.id.tab_boutique_title);
        mTabBoutiqueTitleImg = (ImageView) findViewById(R.id.tab_boutique_title_img);
        mTabClassifyHead.setClickable(false);

        mTabSelectHead = (LinearLayout) findViewById(R.id.tab_select_head);
        mTabSelectTitleView = (TextView) findViewById(R.id.tab_select_title);
        mTabSelectTitleImg = (ImageView) findViewById(R.id.tab_select_title_img);
        mTabClassifyHead.setClickable(false);

        mContextList = (RefreshListView) findViewById(R.id.classify_context_list);
        mContextList.setOnRefreshListener(this);
        mContextList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mDemandChannelsList != null) {
                    int current_position = position - 1;//list has head view...
                    int channel_id = mDemandChannelsList.get(current_position).getId();
                    String channel_name = mDemandChannelsList.get(current_position).getTitle();
                    Intent i = new Intent(InfoContextActivity.this, ChannelProgramActivity.class);
                    i.putExtra("channel_id", channel_id);
                    i.putExtra("channel_name", channel_name);
                    startActivity(i);
                }
            }
        });

        mPageNumTextView = (TextView) findViewById(R.id.page_num_text);
        mPageNumTextView.setText(String.format("第%d页", mCurrentPage));
    }

    private class SpinnerListAdapter extends BaseAdapter {

        private List<PropertyInfo.values> list;

        SpinnerListAdapter(List<PropertyInfo.values> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ClassifyViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert inflater != null;
                convertView = inflater.inflate(R.layout.simple_spinner_item, null);
                holder = new ClassifyViewHolder();
                holder.classify_name = convertView.findViewById(R.id.simple_spinner_text);
                convertView.setTag(holder);

            } else holder = (ClassifyViewHolder) convertView.getTag();

            holder.classify_name.setText(list.get(position).getvaluesname());
            if(mAttributeInfoMap.get(mAttributeInfo.get(0).
                    getPropertyInfoId()).get(position).getvaluesId() == mCurrentClassifyAttr)
                holder.classify_name.setTextColor(getResources().getColor(R.color.net_fm_back_b));
            else
                holder.classify_name.setTextColor(getResources().getColor(R.color.black));
            return convertView;
        }
    }
}
