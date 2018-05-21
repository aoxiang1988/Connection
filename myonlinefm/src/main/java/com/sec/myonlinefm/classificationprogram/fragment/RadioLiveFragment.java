package com.sec.myonlinefm.classificationprogram.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sec.myonlinefm.OnLineFMConnectManager;
import com.sec.myonlinefm.OnLineStationsActivity;
import com.sec.myonlinefm.R;
import com.sec.myonlinefm.UpdateListViewAsyncTask;
import com.sec.myonlinefm.classificationprogram.ChannelProgramActivity;
import com.sec.myonlinefm.classificationprogram.RequestCallBack;
import com.sec.myonlinefm.classificationprogram.data.RecommendsData;
import com.sec.myonlinefm.classificationprogram.data.WaPiData;
import com.sec.myonlinefm.classificationprogram.data.WaPiDataPattern;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link RadioLiveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RadioLiveFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    public static final String TAG = "RadioLiveFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnLineFMConnectManager mPlayer;
    private Context mContext;

    public RadioLiveFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RadioLiveFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RadioLiveFragment newInstance(String param1, String param2) {
        RadioLiveFragment fragment = new RadioLiveFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private List<RecommendsData> mRecommendsDataList;
    private List<WaPiData> mWaPiDataList;
    private final static int UPDATE_WAPI_VIEW = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_WAPI_VIEW:
                    for(int i = 0; i < mWaPiDataList.size(); i++) {
                        View view = LayoutInflater.from(mContext).inflate(R.layout.demand_channel_item, null);
                        ImageView imageView = view.findViewById(R.id.demand_channel_pic);
                        UpdateListViewAsyncTask asyncTask = new UpdateListViewAsyncTask(imageView,
                                mWaPiDataList.get(i).getName(),
                                mPlayer, 60, 60);
                        asyncTask.execute(mWaPiDataList.get(i).getCover());
                        TextView titleView = view.findViewById(R.id.demand_channel_title);
                        titleView.setText(mWaPiDataList.get(i).getName());
                        TextView descView = view.findViewById(R.id.demand_channel_description);
                        descView.setText(mWaPiDataList.get(i).getDesc());

                        TextView briefNameView = view.findViewById(R.id.play_count);
                        briefNameView.setText(mWaPiDataList.get(i).getName());
                        TextView parentNameView = view.findViewById(R.id.context_total);
                        parentNameView.setText(mWaPiDataList.get(i).getDesc());
                        view.setClickable(true);
                        view.setBackground(getResources().getDrawable(R.drawable.bg_pressed, null));
                        final int finalI = i;
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(mContext, ChannelProgramActivity.class);
                                i.putExtra("channel_id", mWaPiDataList.get(finalI).getId());
                                i.putExtra("channel_name", mWaPiDataList.get(finalI).getName());
                                startActivity(i);
                            }
                        });
                        mScrollerView.addView(view);
                        Log.d("bin1111.yang","UPDATE_WAPI_VIEW");
                    }
                    break;
            }
        }
    };

    @Override
    public void onDestroyView() {
        mHandler.removeMessages(UPDATE_WAPI_VIEW);
        view = null;
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPlayer.getWaPiDataList(3608, new RequestCallBack<WaPiDataPattern>() {
            @Override
            public void onSuccess(WaPiDataPattern val) {
                mWaPiDataList = val.getWaPiDataList();
                mHandler.sendEmptyMessage(UPDATE_WAPI_VIEW);
            }

            @Override
            public void onFail(String errorMessage) {

            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mPlayer = OnLineFMConnectManager.Companion.getMMainInfoCode();
    }

    private LinearLayout mScrollerView;
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_radio_live, container, false);
        mScrollerView = view.findViewById(R.id.scroller_view);
        Button mLocalBut = view.findViewById(R.id.local_but);
        mLocalBut.setOnClickListener(this);

        Button mNetRadioBut = view.findViewById(R.id.net_radio_but);
        mNetRadioBut.setOnClickListener(this);

        return view;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.local_but:
                Intent intent = new Intent(mContext, OnLineStationsActivity.class);
                startActivity(intent);
                break;
            case R.id.net_radio_but:
                break;
        }
    }
}
