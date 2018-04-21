package com.sec.connection.view;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sec.connection.BaseListInfo;
import com.sec.connection.MusicApplication;
import com.sec.connection.R;
import com.sec.connection.data.Audio;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FilterListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FilterListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterListFragment extends Fragment implements View.OnTouchListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private Context mContext = null;
    private static String IS_SET_SIZE_VALUE = "is set size value";
    private static String IS_SET_DURATION_VALUE = "is set duration value";
    private static String SET_SIZE_VALUE = "set size value";
    private static String SET_DURATION_VALUE = "set duration value";

    private List<Audio> filterDurationList;

    public static String TAG = "FilterListFragment";

    public FilterListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mSetDurationValue Parameter 1.
     * @param mSetSizeValue Parameter 2.
     * @param isSetDurationValue Parameter 3.
     * @param isSetSizeValue Parameter 4.
     * @return A new instance of fragment FilterListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FilterListFragment newInstance(boolean isSetSizeValue, boolean isSetDurationValue ,int mSetSizeValue, int mSetDurationValue) {
        FilterListFragment fragment = new FilterListFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_SET_SIZE_VALUE, isSetSizeValue);
        args.putBoolean(IS_SET_DURATION_VALUE, isSetDurationValue);
        args.putInt(SET_SIZE_VALUE, mSetSizeValue);
        args.putInt(SET_DURATION_VALUE, mSetDurationValue);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<Audio> filterSizeList = new ArrayList<>();
        filterDurationList = new ArrayList<>();
        if (getArguments() != null) {
            boolean isSetSizeValue = getArguments().getBoolean(IS_SET_SIZE_VALUE);
            boolean isSetDurationValue = getArguments().getBoolean(IS_SET_DURATION_VALUE);
            int mSetSizeValue = getArguments().getInt(SET_SIZE_VALUE);
            int mSetDurationValue = getArguments().getInt(SET_DURATION_VALUE);
            List<Audio> mList = BaseListInfo.getInstance().getList();
            Log.d(TAG,"set value : "+ isSetSizeValue +" "+ isSetDurationValue +" "+ mSetSizeValue +" "+ mSetDurationValue);

            if(isSetSizeValue) {
                for(int i = 0; i< mList.size(); i++){
                    if(mList.get(i).getSize() >= mSetSizeValue)
                        filterSizeList.add(mList.get(i));
                }
            } else
                filterSizeList = mList;
            if(isSetDurationValue) {
                for(int i = 0; i< filterSizeList.size(); i++)
                    if(filterSizeList.get(i).getDuration() >= mSetDurationValue)
                        filterDurationList.add(filterSizeList.get(i));
            } else
                filterDurationList = filterSizeList;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_list, container, false);
        LinearLayout scrollView = view.findViewById(R.id.filter_scroll_view);
        for(int i = 0; i< filterDurationList.size(); i++){
            Log.d(TAG,""+ filterDurationList.get(i).getTitle());
        }
        for(int i = 0; i< filterDurationList.size(); i++){
            View view_1 = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1,null);
            TextView infomusictitle = view_1.findViewById(android.R.id.text1);
            infomusictitle.setText(filterDurationList.get(i).getTitle());
            scrollView.addView(view_1);
        }
        view.invalidate();
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
