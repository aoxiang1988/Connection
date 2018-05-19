package com.sec.myonlinefm.classificationprogram.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sec.myonlinefm.OnLineFMConnectManager;
import com.sec.myonlinefm.R;
import com.sec.myonlinefm.classificationprogram.ChannelProgramActivity;
import com.sec.myonlinefm.classificationprogram.RequestCallBack;
import com.sec.myonlinefm.classificationprogram.data.DemandChannel;
import com.sec.myonlinefm.classificationprogram.data.DemandChannelPattern;
import com.sec.myonlinefm.defineview.RefreshListView;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ChannelRecommendListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChannelRecommendListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Context mContext;
    private OnLineFMConnectManager mPlayer;

    public static String TAG = "ChannelRecommendListFragment";

    // TODO: Rename and change types of parameters
    private View view;
    private RefreshListView mRecommendListView;
    private List<DemandChannel> mDemandChannelsList;
    private DemandChannelAdapter mDemandChannelAdapter;

    private int mCategoryID = -1;
    private int mTotalPage = 0;
    private int mCurrentPage = 1;

    private Integer[] mAttrId;
    private static final int UPDATE_CONTEXT_LIST = 1;
    private static final int EMPTY_CONTEXT_LIST = 2;

    public ChannelRecommendListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ChannelRecommendListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChannelRecommendListFragment newInstance(int category_id) {
        ChannelRecommendListFragment fragment = new ChannelRecommendListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, category_id);
        fragment.setArguments(args);
        return fragment;
    }

    public static ChannelRecommendListFragment newInstance() {
        return new ChannelRecommendListFragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("category_id",mCategoryID);
        outState.putInt("current_page",mCurrentPage);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume : "+mCategoryID+" "+mCurrentPage);
        getClassifyContextList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlayer = OnLineFMConnectManager.Companion.getMMainInfoCode();
        if(savedInstanceState != null) {
            mCategoryID = savedInstanceState.getInt("category_id", -1);
            mCurrentPage = savedInstanceState.getInt("current_page", 1);
            Log.d(TAG,""+mCategoryID+" "+mCurrentPage);
        }
        if (getArguments() != null) {
            mCategoryID = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_channel_recommend_list, container, false);
        mRecommendListView = view.findViewById(R.id.recommend_list);
        mRecommendListView.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onPullRefresh() {
                if(mCurrentPage > 1) {
                    mCurrentPage = mCurrentPage - 1;
                    getClassifyContextList();
                } else {
                    mRecommendListView.completeRefresh();
                }
            }

            @Override
            public void onLoadingMore() {
                if(mCurrentPage < mTotalPage) {
                    mRecommendListView.startScrollAnim();
                    mCurrentPage = mCurrentPage + 1;
                    getClassifyContextList();
                } else {
                    mRecommendListView.completeRefresh();
                }
            }
        });
        mRecommendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mDemandChannelsList != null) {
                    int current_position = position - 1;//list has head view...
                    int channel_id = mDemandChannelsList.get(current_position).getId();
                    String channel_name = mDemandChannelsList.get(current_position).getTitle();
                    ChannelProgramActivity._activity.setUpdateCurrentDemandChannelInfo(channel_name, channel_id, true);
                }
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
    public void onDestroyView() {
        view = null;
        mRecommendListView.completeRefresh();
        mRecommendListView = null;
        mDemandChannelAdapter = null;
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_CONTEXT_LIST:
                    upDateContextList();
                    break;
                case EMPTY_CONTEXT_LIST:
                    Toast.makeText(mContext, "数据为空，请检查网络或修改查询条件", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void upDateContextList() {
        if(isDetached()) return;
        if(mDemandChannelAdapter == null) {
            mDemandChannelAdapter = new DemandChannelAdapter();
            mRecommendListView.setAdapter(mDemandChannelAdapter);
        } else {
            mDemandChannelAdapter.notifyDataSetChanged();
            mRecommendListView.completeRefresh();
        }
        mRecommendListView.setSelection(0);
    }

    private void getClassifyContextList() {
        if(mCategoryID != -1) {
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

    public void setCurrentCategoryID(int currentCategoryID) {
        this.mCategoryID = currentCategoryID;
        getClassifyContextList();
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
            final ViewHolder holder;
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert inflater != null;
                convertView = inflater.inflate(R.layout.demand_channel_item, parent);

                holder = new ViewHolder();
                holder.mDemandChannelPic = convertView.findViewById(R.id.demand_channel_pic);
                holder.mDemandChannelTitle = convertView.findViewById(R.id.demand_channel_title);
                holder.mDemandChannelDescription = convertView.findViewById(R.id.demand_channel_description);
                holder.mDemandChannelPlayer = convertView.findViewById(R.id.play_count);
                holder.mDemandChannelTotal = convertView.findViewById(R.id.context_total);
                convertView.setTag(holder);

            } else holder = (ViewHolder) convertView.getTag();

            holder.mDemandChannelPic.setImageBitmap(mDemandChannelsList.get(position).getThumbs());

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
    static class ViewHolder {

        ImageView mDemandChannelPic;

        TextView mDemandChannelTitle;

        TextView mDemandChannelDescription;

        TextView mDemandChannelPlayer;

        TextView mDemandChannelTotal;

    }
}
