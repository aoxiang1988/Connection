package com.sec.myonlinefm.classificationprogram.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.sec.myonlinefm.OnLineFMConnectManager;
import com.sec.myonlinefm.R;
import com.sec.myonlinefm.classificationprogram.InfoContextActivity;
import com.sec.myonlinefm.classificationprogram.data.ObservableController;
import com.sec.myonlinefm.classificationprogram.data.RequestProgramClassify;
import com.sec.myonlinefm.classificationprogram.data.RequestProgramClassifyListPattern;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ClassifyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClassifyFragment extends Fragment implements Observer {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String TAG = "ClassifyFragment";

    private static final int UPDATE_GRID_VIEW = 1;
    private OnLineFMConnectManager mPlayer;
    private GridView mClassifyView;
    private ClassifyAdapter mClassifyAdapter;
    private Context mContext;

    private List<RequestProgramClassify> mRequestProgramClassifyList = null;
    private ObservableController mObservable = ObservableController.getInstance();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d("gaolin"," handleMessage ");
            if(msg.what == UPDATE_GRID_VIEW) {
                Log.d("gaolin"," UPDATE_GRID_VIEW ");
                mRequestProgramClassifyList = RequestProgramClassifyListPattern.getInstance().getRequestProgramClassifyList();
                mClassifyAdapter.setList(mRequestProgramClassifyList);
            }
        }
    };

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ClassifyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClassifyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClassifyFragment newInstance(String param1, String param2) {
        ClassifyFragment fragment = new ClassifyFragment();
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

        mPlayer = OnLineFMConnectManager.Companion.getMMainInfoCode();
        mObservable.addObserver(this);
    }
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_classify, container, false);
        mClassifyView = view.findViewById(R.id.classify_list);
        mClassifyAdapter = new ClassifyAdapter(mContext);
        mClassifyView.setAdapter(mClassifyAdapter);
        mClassifyView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RequestProgramClassify mCurrentClassify = mRequestProgramClassifyList.get(position);
                Intent i = new Intent();
                i.putExtra("category_id", mCurrentClassify.getId());
                i.putExtra("category_name", mCurrentClassify.getName());
                i.setClass(mContext, InfoContextActivity.class);
                startActivity(i);
            }
        });

     /*   mPlayer.getRequestProgramClassify(new RequestCallBack<RequestProgramClassifyListPattern>() {
            @Override
            public void onSuccess(RequestProgramClassifyListPattern val) {
                Log.d(TAG,"onSuccess : ");
                mRequestProgramClassifyList = val.getRequestProgramClassifyList();
                mHandler.sendEmptyMessage(UPDATE_GRID_VIEW);
            }

            @Override
            public void onFail(String errorMessage) {
                Log.d(TAG,"onFail : "+errorMessage);
            }
        });
*/
        mRequestProgramClassifyList = RequestProgramClassifyListPattern.getInstance().getRequestProgramClassifyList();
        if (mRequestProgramClassifyList != null) {
            mHandler.sendEmptyMessage(UPDATE_GRID_VIEW);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        mHandler.removeMessages(UPDATE_GRID_VIEW);
        view = null;
        super.onDestroyView();
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

    private class ClassifyAdapter extends BaseAdapter {

        private List<RequestProgramClassify> mRequestProgramClassifyList = null;
        private Context mContext = null;

        ClassifyAdapter(Context context) {
            mContext = context;
        }

        void setList(List<RequestProgramClassify> request_program_classify_list) {
            Log.d("gaolin"," request_program_classify_list :  " + request_program_classify_list);
            mRequestProgramClassifyList = request_program_classify_list;
            notifyDataSetChanged();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (mRequestProgramClassifyList == null)
                return null;
            else {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    assert inflater != null;
                    convertView = inflater.inflate(R.layout.classify_grid_item_layout, null);
                    holder = new ViewHolder();
                    holder.classify_icon = convertView.findViewById(R.id.classify_icon);
                    holder.classify_name = (TextView) convertView.findViewById(R.id.classify_name);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();//取出ViewHolder对象
                }
                holder.classify_name.setText(mRequestProgramClassifyList.get(position).getName());
                return convertView;
            }
        }
    }

    static class ViewHolder {
        ImageView classify_icon;
        TextView classify_name;
    }

    @Override
    public void update(Observable o, Object arg) {
        int event = (int) arg;
        Log.d("gaolin","event = " + event);
        switch (event) {
            case 1:
                mHandler.sendEmptyMessage(UPDATE_GRID_VIEW);
                break;
        }
    }
}
