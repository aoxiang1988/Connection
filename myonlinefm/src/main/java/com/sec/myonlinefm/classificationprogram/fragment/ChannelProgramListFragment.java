package com.sec.myonlinefm.classificationprogram.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
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
import android.widget.GridView;
import android.widget.TabHost;
import android.widget.TextView;

import com.sec.myonlinefm.OnLineFMConnectManager;
import com.sec.myonlinefm.R;
import com.sec.myonlinefm.classificationprogram.ChannelProgramActivity;
import com.sec.myonlinefm.abstructObserver.RequestCallBack;
import com.sec.myonlinefm.classificationprogram.data.ChannelProgram;
import com.sec.myonlinefm.classificationprogram.data.ChannelProgramPattern;
import com.sec.myonlinefm.defineview.RefreshListView;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ChannelProgramListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChannelProgramListFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    public static String TAG = "ChannelProgramListFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_CHANNEL_ID = "arg_channel_id";
    private Context mContext;
    private TextView mTotalProgramView;
    private TextView mSelectPageView;
    private TextView mOrderText;

    private static final int UPDATE_PROGRAM_LIST = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int mChannelID = -1;
    private int mCurrentPage = 1;
    private int mOrder = 0; //0正序, 1倒序

    private List<ChannelProgram> mChannelProgramList;
    private DemandProgramAdapter mProgramAdapter = null;

    private OnLineFMConnectManager mPlayer;
    private RefreshListView mProgramList;
    private int mTotalPage = 0;
    private int mTotalProgram = 0;
    private View view = null;
    private AlertDialog builder = null;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_PROGRAM_LIST:
                    if(mProgramAdapter == null) {
                        mProgramAdapter = new DemandProgramAdapter();
                        mProgramList.setAdapter(mProgramAdapter);
                    } else {
                        mProgramAdapter.notifyDataSetChanged();
                        mProgramList.completeRefresh();
                    }
                    mTotalProgramView.setText(String.format("共%d期", mTotalProgram));
                    mProgramList.setSelection(0);
                    mProgramList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String channelID = mChannelProgramList.get(position).getChannelID();
                            int programID = mChannelProgramList.get(position).getId();
                        }
                    });
                    Log.d(TAG,"UPDATE_PROGRAM_LIST");
                    break;
            }
        }
    };

    public ChannelProgramListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChannelProgramListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChannelProgramListFragment newInstance(String param1, String param2) {
        ChannelProgramListFragment fragment = new ChannelProgramListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static ChannelProgramListFragment newInstance(int channel_id) {
        ChannelProgramListFragment fragment = new ChannelProgramListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CHANNEL_ID, channel_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("channel_id",mChannelID);
        outState.putInt("current_page",mCurrentPage);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume : "+mChannelID+" "+mCurrentPage);
        getClassifyContextList(mChannelID);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            mChannelID = savedInstanceState.getInt("channel_id", -1);
            mCurrentPage = savedInstanceState.getInt("current_page", 1);
            Log.d(TAG,""+mChannelID+" "+mCurrentPage);
        }
        mPlayer = OnLineFMConnectManager.Companion.getMMainInfoCode();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mChannelID = getArguments().getInt(ARG_CHANNEL_ID);
        }
    }

    private void getClassifyContextList(int mChannelID) {
        if(mChannelID != -1) {
//            mProgramList.setOnItemClickListener(null);
            mPlayer.getCurrentDemandChannelPrograms(mChannelID,
                    mCurrentPage,
                    mOrder,
                    new RequestCallBack<ChannelProgramPattern>() {
                        @Override
                        public void onSuccess(ChannelProgramPattern val) {
                            mChannelProgramList = val.getChannelProgramList();
                            mTotalProgram = val.getTotal();
                            mTotalPage = val.getTotal()/30;
                            if(val.getTotal()%30 != 0)
                                mTotalPage = mTotalPage + 1;
                            Log.d(TAG,"onSuccess");
                            mHandler.sendEmptyMessage(UPDATE_PROGRAM_LIST);
                        }

                        @Override
                        public void onFail(String errorMessage) {
                            Log.d(TAG,"ChannelProgramListFragment onFail : "+errorMessage);
                        }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG ,"onCreateView");
        view = inflater.inflate(R.layout.fragment_channel_program_list, container, false);

        mProgramList = view.findViewById(R.id.program_list);
        mTotalProgramView = view.findViewById(R.id.total_program);
        mSelectPageView = view.findViewById(R.id.select);
        mOrderText = view.findViewById(R.id.order);

        if(mOrder == 0)
            mOrderText.setText("正序");
        else mOrderText.setText("倒序");
        mOrderText.setOnClickListener(this);

        mSelectPageView.setText("选集");
        mSelectPageView.setOnClickListener(this);

        mProgramList.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onPullRefresh() {
                if(mCurrentPage > 1) {
                    mCurrentPage = mCurrentPage - 1;
                    getClassifyContextList(mChannelID);
                } else {
                    mProgramList.completeRefresh();
                }
            }

            @Override
            public void onLoadingMore() {
                if(mCurrentPage < mTotalPage) {
                    mProgramList.startScrollAnim();
                    mCurrentPage = mCurrentPage + 1;
                    getClassifyContextList(mChannelID);
                } else {
                    mProgramList.completeRefresh();
                }
            }
        });
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onDestroyView() {
        view = null;
        mProgramAdapter = null;
        mProgramList.setOnRefreshListener(null);
        mProgramList = null;

        mTotalProgramView = null;
        mSelectPageView = null;
        mOrderText = null;

        TabHost tabHost = ((ChannelProgramActivity) getActivity()).getTabHost();
        if (tabHost != null && !tabHost.getCurrentTabTag().equalsIgnoreCase(TAG)
                && tabHost.getTabWidget() != null) {
            tabHost.getTabWidget().getChildTabViewAt(0).requestFocus();
        }

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null)
            mHandler.removeMessages(UPDATE_PROGRAM_LIST);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select:
                mCustomDialog();
                break;
            case R.id.order:
                if(mOrder == 0) {
                    mOrder = 1;
                    mOrderText.setText("倒序");
                }
                else if(mOrder == 1) {
                    mOrder = 0;
                    mOrderText.setText("正序");
                }
                getClassifyContextList(mChannelID);
                break;
            case R.id.reset_but:
                if(builder != null)
                    builder.cancel();
                break;
        }
    }

    public void setCurrentChannelID(int currentChannelID) {
        this.mChannelID = currentChannelID;
        getClassifyContextList(this.mChannelID);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class DemandProgramAdapter extends BaseAdapter {

        private int current_position = 0;

        public void setCurrentPosition(int current_position) {
            this.current_position = current_position;
        }

        @Override
        public int getCount() {
            return mChannelProgramList.size();
        }

        @Override
        public Object getItem(int position) {
            return mChannelProgramList.get(position);
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
                convertView = inflater.inflate(R.layout.demand_program_item, null);

                holder = new ViewHolder();
                holder.mTitle = convertView.findViewById(R.id.program_title_text);
                holder.mUpdateTime = convertView.findViewById(R.id.update_time_text);
                holder.mDuration = convertView.findViewById(R.id.duration);
                convertView.setTag(holder);

            } else holder = (ViewHolder) convertView.getTag();

            holder.mTitle.setText(mChannelProgramList.get(position).getTitle());
            holder.mUpdateTime.setText(mChannelProgramList.get(position).getUpdateTime());

            int min = (int) (mChannelProgramList.get(position).getDuration() / 60);
            int sec = (int) (mChannelProgramList.get(position).getDuration() % 60);
            String stringMin;
            String stringSec;
            if (min < 10) {
                stringMin = String.format("0%s", min);
            } else {
                stringMin = String.format("%s", min);
            }
            if (sec < 10) {
                stringSec = String.format("0%s", sec);
            } else {
                stringSec = String.format("%s", sec);
            }
            holder.mDuration.setText(String.format("%s:%s", stringMin, stringSec));
            return convertView;
        }
    }

    static class ViewHolder {
        TextView mTitle;
        TextView mUpdateTime;
        TextView mDuration;
    }
    private void mCustomDialog() {
        if (builder != null)
            builder.cancel();
        builder = new AlertDialog.Builder(mContext, R.style.CreatDialog).create();
        builder.show();
        LayoutInflater factory = LayoutInflater.from(mContext);
        View view = factory.inflate(R.layout.select_dialog_layout, null);
        Button mCancel = (Button) view.findViewById(R.id.reset_but);
        mCancel.setOnClickListener(this);
        GridView mPageGridView = (GridView) view.findViewById(R.id.page_view);
        mPageGridView.setAdapter(new PageGridAdapter());
        builder.setContentView(view);
        final Window dialogWindow = builder.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);//显示在底部
        dialogWindow.setWindowAnimations(R.style.take_photo_anim);

        WindowManager m = getActivity().getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        final WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值

        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            p.height = (int) (d.getWidth() * 0.4); // 高度设置为屏幕的0.5
            p.width = (int) (d.getHeight()); // 宽度设置为屏幕宽
            //横屏
        } else if (ori == Configuration.ORIENTATION_PORTRAIT) {
            p.height = (int) (d.getHeight() * 0.4); // 高度设置为屏幕的0.5
            p.width = (int) (d.getWidth()); // 宽度设置为屏幕宽
            //竖屏
        }
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
        dialogWindow.setAttributes(p);
    }

    private class PageGridAdapter extends BaseAdapter {
        private View convertView ;
        @Override
        public int getCount() {
            return mTotalPage;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position + 1;
        }

        @SuppressLint("DefaultLocale")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert inflater != null;
                convertView = inflater.inflate(R.layout.page_button, null);
                this.convertView = convertView;
            }
            Button mSetPageBut = (Button) convertView.findViewById(R.id.page_but);
            if(position != mTotalPage - 1)
                mSetPageBut.setText(String.format("%d~%d", 1 + position * 30, 30 + position * 30));
            else
                mSetPageBut.setText(String.format("%d~%d", 1 + position * 30, mTotalProgram));

            mSetPageBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentPage = (int) getItemId(position);
                    getClassifyContextList(mChannelID);
                    notifyDataSetChanged();
                }
            });

            if((int) getItemId(position) == mCurrentPage) {
                mSetPageBut.setTextColor(mContext.getResources().getColor(R.color.net_fm_back_b));
                mSetPageBut.setBackground(mContext.getDrawable(R.drawable.selceted_back_ground));
            }
            else {
                mSetPageBut.setTextColor(mContext.getResources().getColor(R.color.black));
                mSetPageBut.setBackground(mContext.getDrawable(R.drawable.about_app_info_background));
            }
            return convertView;
        }
    }
}
