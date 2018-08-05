package com.sec.myonlinefm.classificationprogram.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.sec.myonlinefm.OnLineFMConnectManager;
import com.sec.myonlinefm.R;
import com.sec.myonlinefm.UpdateListViewAsyncTask;
import com.sec.myonlinefm.classificationprogram.ChannelProgramActivity;
import com.sec.myonlinefm.abstructObserver.RequestCallBack;
import com.sec.myonlinefm.classificationprogram.data.RequestProgramClassify;
import com.sec.myonlinefm.classificationprogram.data.RequestProgramClassifyListPattern;
import com.sec.myonlinefm.classificationprogram.data.WaPiData;
import com.sec.myonlinefm.classificationprogram.data.WaPiDataPattern;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link BoutiqueFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BoutiqueFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int UPDATE_LIST = 1;
    private OnLineFMConnectManager mPlayer;
    private Context mContext;
    public static final String TAG = "BoutiqueFragment";

    private List<RequestProgramClassify> mRequestProgramClassifyList = null;
    private Map<Integer, List<WaPiData>> mRankListMap;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RankingAdapter rankingAdapter = null;
    private View view;
    private ExpandableListView expandableListView;
    private boolean isFinished = true;

    private List<Integer> resIds;

    public BoutiqueFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BoutiqueFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BoutiqueFragment newInstance(String param1, String param2) {
        BoutiqueFragment fragment = new BoutiqueFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == UPDATE_LIST) {
                if(!isFinished) return;
                if(rankingAdapter == null) {
                    rankingAdapter = new RankingAdapter();
                    expandableListView.setAdapter(rankingAdapter);
                } else {
                    rankingAdapter.notifyDataSetChanged();
                }
                Tartiest_Click_Listener(expandableListView);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        isFinished = true;
        mRequestProgramClassifyList = RequestProgramClassifyListPattern.getInstance().getRequestProgramClassifyList();
        mRankListMap = new Hashtable<>();
        if (mRequestProgramClassifyList != null) {
            for (final RequestProgramClassify requestProgramClassify : mRequestProgramClassifyList) {
                if(!isFinished)
                    break;
                mPlayer.getWaPiDataList(requestProgramClassify.getId(), new RequestCallBack<WaPiDataPattern>() {
                    @Override
                    public void onSuccess(WaPiDataPattern val) {
                        List<WaPiData> mWaPiDataList = val.getWaPiDataList();
                        mRankListMap.put(requestProgramClassify.getId(), mWaPiDataList);
                        mHandler.sendEmptyMessage(UPDATE_LIST);
                    }
                    @Override
                    public void onFail(String errorMessage) {

                    }
                });
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
        TypedArray ar = getResources().obtainTypedArray(R.array.category_drawable);
        final int len = ar.length();
        resIds = new ArrayList<>();
        for (int i = 0; i < len; i++){
            resIds.add(ar.getResourceId(i, 0));
        }
        ar.recycle();
        mPlayer = OnLineFMConnectManager.Companion.getMMainInfoCode();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_boutique, container, false);
        expandableListView = view.findViewById(R.id.expandable_list_view);
//        expandableListView.setChildDivider(null);
        expandableListView.setGroupIndicator(null);
//        rankingAdapter = new RankingAdapter();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isFinished = false;
//        expandableListView.removeAllViews();
        expandableListView.setOnGroupClickListener(null);
        expandableListView.setOnChildClickListener(null);
        expandableListView = null;
        view = null;
        rankingAdapter = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null)
        mHandler.removeMessages(UPDATE_LIST);
        mRequestProgramClassifyList = null;
        mRankListMap = null;
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class RankingAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            if(mRequestProgramClassifyList == null)
                return 0;
            else
                return mRequestProgramClassifyList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {

            if(mRequestProgramClassifyList == null)
                return 0;
            int key = mRequestProgramClassifyList.get(groupPosition).getId();
            if(mRankListMap.get(key) == null)
                return 0;
            else
                return mRankListMap.get(key).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            if(mRequestProgramClassifyList == null)
                return null;
            else
                return mRequestProgramClassifyList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            if(mRequestProgramClassifyList == null)
                return null;
            int key = mRequestProgramClassifyList.get(groupPosition).getId();
            if(mRankListMap.get(key) == null)
                return null;
            else
                return (mRankListMap.get(key).get(childPosition));
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }


        @SuppressLint("InflateParams")
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            final GroupViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert inflater != null;
                convertView = inflater.inflate(R.layout.group_item_layout, null);
                holder = new GroupViewHolder();
                holder.tv = convertView.findViewById(R.id.group_title);
                holder.mDropUpDown = convertView.findViewById(R.id.group_down);
                holder.mGroupIcon =  convertView.findViewById(R.id.group_icon);
                convertView.setTag(holder);
            } else holder = (GroupViewHolder) convertView.getTag();

            holder.mGroupIcon.setImageDrawable(getResources().getDrawable(resIds.get(groupPosition)));
            holder.tv.setText(mRequestProgramClassifyList.get(groupPosition).getName());
            if(expandableListView.isGroupExpanded(groupPosition)){
                holder.tv.setTextColor(getResources().getColor(R.color.net_fm_back_b));
                holder.mDropUpDown.setImageResource(R.drawable.drop_up);
            } else {
                holder.tv.setTextColor(getResources().getColor(R.color.details_title));
                holder.mDropUpDown.setImageResource(R.drawable.drop_down);
            }
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final ChildViewHolder holder;
            int key = mRequestProgramClassifyList.get(groupPosition).getId();
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert inflater != null;
                convertView = inflater.inflate(R.layout.demand_channel_item, null);

                holder = new ChildViewHolder();
                holder.imageView = convertView.findViewById(R.id.demand_channel_pic);
                holder.childMusicName = convertView.findViewById(R.id.demand_channel_title);
                holder.descView = convertView.findViewById(R.id.demand_channel_description);
                convertView.setTag(holder);

            } else holder = (ChildViewHolder) convertView.getTag();

            if(mRankListMap.get(key) != null) {
                UpdateListViewAsyncTask asyncTask = new UpdateListViewAsyncTask(holder.imageView,
                        mRankListMap.get(key).get(childPosition).getName(),
                        mPlayer, 60, 60);
                asyncTask.execute(mRankListMap.get(key).get(childPosition).getCover());
                holder.childMusicName.setText(mRankListMap.get(key).get(childPosition).getName());
                holder.descView.setText(mRankListMap.get(key).get(childPosition).getDesc());
            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    static class GroupViewHolder {
        TextView tv;
        ImageView mGroupIcon;
        ImageView mDropUpDown;
    }

    static class ChildViewHolder {
        TextView childMusicName;
        ImageView imageView;
        TextView descView;
    }

    private boolean isGroupOpen = false;

    private void Tartiest_Click_Listener(ExpandableListView tartiest) {
        tartiest.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if(parent.isGroupExpanded(groupPosition)) {
//                    v.setBackgroundColor(getResources().getColor(R.color.main_bg_color, null));
                    parent.collapseGroup(groupPosition);
                } else {
//                    v.setBackgroundColor(getResources().getColor(R.color.net_fm_back_b, null));
                    parent.expandGroup(groupPosition);
                }
                rankingAdapter.notifyDataSetChanged();
                return true;
            }
        });

        tartiest.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                int key = mRequestProgramClassifyList.get(groupPosition).getId();
                Intent i = new Intent(mContext, ChannelProgramActivity.class);
                i.putExtra("channel_id", mRankListMap.get(key).get(childPosition).getId());
                i.putExtra("channel_name", mRankListMap.get(key).get(childPosition).getName());
                startActivity(i);
                return true;
            }
        });
    }
}
