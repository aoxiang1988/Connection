package com.sec.myonlinefm.onlineSearchUI;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sec.myonlinefm.R;
import com.sec.myonlinefm.abstructObserver.OnLineInfo;
import com.sec.myonlinefm.data.SearchType;
import com.sec.myonlinefm.updataUIListener.ObserverUIListenerManager;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SearchResultListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    public static String TAG = "SearchResultListFragment";
    private int mCurrentTab = 0;
    private Context mContext;
    private int UPDATE_LIST = 1;
    private int mCurrentPage = 1;
    // TODO: Rename and change types of parameters
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            if(msg.what == UPDATE_LIST) {
                if(mSearchListAdapter != null)
                    mSearchListAdapter.notifyDataSetChanged();
                else
                    mSearchListAdapter = new SearchListAdapter();
                mSearchScanningView.setVisibility(View.GONE);
                mSearchListView.setVisibility(View.VISIBLE);
                mSearchListView.setAdapter(mSearchListAdapter);
            }
        }
    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BaseListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchResultListFragment newInstance(String query) {
        return new SearchResultListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUpdateOnLineInfo = new UpdateOnLineInfo(mContext);
        mUpdateOnLineInfo.addToObserverList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUpdateOnLineInfo.removeToObserverList();
    }

    private ListView mSearchListView;
    private TextView mSearchScanningView;
    private SearchListAdapter mSearchListAdapter;
    private List<SearchType.ChannelLive> mSearchChannelLiveList = null;
    private List<SearchType.ProgramLive> mSearchProgramLiveList = null;

    private AdapterView.OnItemClickListener mClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(mCurrentTab == 0) {
                SearchType.ChannelLive mChannelLive = mSearchChannelLiveList.get(position-1);
            } else if(mCurrentTab == 1) {
                SearchType.ProgramLive mProgramLive = mSearchProgramLiveList.get(position-1);
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_base_list, container, false);
        mSearchListView = view.findViewById(R.id.search_result_list);
        mSearchListView.setOnItemClickListener(mClickListener);
        mSearchScanningView = view.findViewById(R.id.search_scanning);
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

    class SearchListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            int mCount = 0;
            if(mCurrentTab == 0) {
                mCount = mSearchChannelLiveList.size();
            } else if(mCurrentTab == 1) {
                mCount = mSearchProgramLiveList.size();
            }
            return mCount;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.search_result_item_layout, parent, false);
            ImageView mSearch_Cover = convertView.findViewById(R.id.search_cover);
            TextView mSearchTitle = convertView.findViewById(R.id.search_title);
            TextView mSearchFreq = convertView.findViewById(R.id.search_freq);
            if(mCurrentTab == 0) {
                SearchType.ChannelLive mChannelLive = mSearchChannelLiveList.get(position);
                mSearchTitle.setText(mChannelLive.getChannelLiveTitle());
                mSearch_Cover.setImageBitmap(mChannelLive.getChannelLiveCover());
                mSearchFreq.setText(String.format("%s MHz", mChannelLive.getChannelLiveFreqs()));
                mSearchFreq.setVisibility(View.VISIBLE);
            } else if(mCurrentTab == 1) {
                SearchType.ProgramLive mProgramLive = mSearchProgramLiveList.get(position);
                mSearchTitle.setText(mProgramLive.getProgramLiveTitle());
                mSearch_Cover.setImageBitmap(mProgramLive.getProgramLiveCover());
            }
            return convertView;
        }
    }

    private UpdateOnLineInfo mUpdateOnLineInfo;

    private class UpdateOnLineInfo extends OnLineInfo {
        private Context context;

        UpdateOnLineInfo(Context context){
            this.context = context;
        }

        public void addToObserverList(){
            ObserverUIListenerManager.getInstance().add(this);
        }
        public void removeToObserverList(){
            ObserverUIListenerManager.getInstance().remove(this);
        }

        @Override
        public void observerChannelLiveUpData(@NonNull List<SearchType.ChannelLive> search_channel_live_list) {
            //update UI
            mCurrentTab = 0;
            mSearchChannelLiveList = search_channel_live_list;
            mHandler.sendEmptyMessage(UPDATE_LIST);
        }

        @Override
        public void observerProgramLiveUpData(@NonNull List<SearchType.ProgramLive> mSearchProgramLiveList) {
            //update UI
            mCurrentTab = 1;
            SearchResultListFragment.this.mSearchProgramLiveList = mSearchProgramLiveList;
            mHandler.sendEmptyMessage(UPDATE_LIST);
        }
    }
}
