package com.sec.connection.vpview.FragmentViewPager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sec.connection.BaseListInfo;
import com.sec.connection.R;
import com.sec.connection.data.Audio;
import com.sec.connection.MusicApplication;
import com.sec.connection.UserAdapter;
import com.sec.connection.data.FolderPathData;

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
public class FilterListFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Context mContext = null;
    /**filter setting**/
    private static final String SET_FILTER_ACTION = "com.example.action.SET_FILTER_ACTION";
    private static final String IS_SET_SIZE_VALUE = "is set size value";
    private static final String IS_SET_DURATION_VALUE = "is set duration value";
    private static final String SET_SIZE_VALUE = "set size value";
    private static final String SET_DURATION_VALUE = "set duration value";
    private static final String IS_SET_FILTER_FOLDER = "is set filter folder";

    //设置限制值
    private boolean isSetSizeValue = false;
    private boolean isSetFilterFolder = false;
    private boolean isSetDurationValue = false;
    private int mSetSizeValue = 0;
    private int mSetDurationValue = 0;

    private List<Audio> filterSizelist = new ArrayList<>();
    private List<Audio> filterDurationlist = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public FilterListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FilterListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FilterListFragment newInstance(String param1, String param2) {
        FilterListFragment fragment = new FilterListFragment();
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

        isSetSizeValue = (boolean)restorePreferences(IS_SET_SIZE_VALUE);
        isSetDurationValue = (boolean)restorePreferences(IS_SET_DURATION_VALUE);
        isSetFilterFolder = (boolean) restorePreferences(IS_SET_FILTER_FOLDER);
        mSetSizeValue = (int) restorePreferences(SET_SIZE_VALUE);
        mSetDurationValue = (int) restorePreferences(SET_DURATION_VALUE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blank_fragment4, container, false);
        filterlistshow(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private SharedPreferences preferences;
    private Object restorePreferences(String SetValuse) {
        preferences = mContext.getSharedPreferences(MusicApplication.PREF_NAME, Context.MODE_PRIVATE);
        Object status = null;
        switch (SetValuse) {
            case IS_SET_SIZE_VALUE:
                status = preferences.getBoolean(IS_SET_SIZE_VALUE, false);
                break;
            case IS_SET_DURATION_VALUE:
                status = preferences.getBoolean(IS_SET_DURATION_VALUE, false);
                break;
            case SET_SIZE_VALUE:
                status = preferences.getInt(SET_SIZE_VALUE, 0);
                break;
            case SET_DURATION_VALUE:
                status = preferences.getInt(SET_DURATION_VALUE, 0);
                break;
            case IS_SET_FILTER_FOLDER:
                status = preferences.getBoolean(IS_SET_FILTER_FOLDER, false);
                break;
        }
        return status;
    }

    private void filterlistshow(View view){
        List<Audio> mFilterList;
        if(isSetFilterFolder){
            mFilterList = FolderPathData.getInstance().getfilterlist();
        } else
            mFilterList = BaseListInfo.getInstance().getList();
        if(isSetSizeValue) {
            for(int i = 0; i< mFilterList.size(); i++){
                if(mFilterList.get(i).getSize() >= mSetSizeValue)
                    filterSizelist.add(mFilterList.get(i));
            }
        } else
            filterSizelist = mFilterList;
        if(isSetDurationValue) {
            for(int i = 0; i<filterSizelist.size(); i++)
                if(filterSizelist.get(i).getDuration() >= mSetDurationValue)
                    filterDurationlist.add(filterSizelist.get(i));
        } else
            filterDurationlist = filterSizelist;

        UserAdapter adapter = new UserAdapter(mContext, R.layout.listitem, filterDurationlist);
        ListView filterlistview = view.findViewById(R.id.filter_list);
        filterlistview.setAdapter(adapter);
    }
}
