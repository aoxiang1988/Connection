package com.sec.connection.view.FragmentViewPager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sec.connection.MusicApplication;
import com.sec.connection.R;
import com.sec.connection.data.CharacterParser;
import com.sec.connection.data.SortModel;
import com.sec.connection.MainActivity;
import com.sec.connection.PinyinComparator;
import com.sec.connection.SortAdapter;
import com.sec.connection.view.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BlankFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BlankFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlankFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String UPDATE_LIST_ACTIVITY_ACTION = "com.example.action.UPDATE_LIST_ACTIVITY_ACTION";
    private Context mContext;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String[] musicname = new String[MusicApplication.list.size()];
    private ListView talmudic1;
    private SideBar sidebar;
    private SortAdapter tablespace;
    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;
    private PinyinComparator pinyinComparator;

    public BlankFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BlankFragment newInstance(String param1, String param2) {
        BlankFragment fragment = new BlankFragment();
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

        List<String> artists1 = new ArrayList<>();
        for (int a = 0; a < MusicApplication.list.size(); a++) {
            artists1.add( MusicApplication.list.get(a).getArtist());
            musicname[a] = MusicApplication.list.get(a).getTitle();
        }

        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        SourceDateList = filledData(musicname);
        Collections.sort(SourceDateList, pinyinComparator);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blank2, container, false);
        talmudic1 = (ListView) view.findViewById(R.id.tab_music_list);
        sidebar = new SideBar(mContext);
        sidebar = (SideBar) view.findViewById(R.id.side_bar);

        tablespace = new SortAdapter(mContext, SourceDateList);
        talmudic1.setAdapter(tablespace);
        talmudic1.setOnItemClickListener(new ItemClickListener());
        sidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = tablespace.getPositionForSection(s.charAt(0));
                if(position != -1){
                    talmudic1.setSelection(position);
                }
            }
        });

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

    /**
     * 为ListView填充数据
     * @param date
     * @return
     */
    private List<SortModel> filledData(String [] date){
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for(int i=0; i<date.length; i++){
            SortModel sortModel = new SortModel();
            sortModel.setName(date[i]);
            sortModel.setAudio(MusicApplication.list.get(i));
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(date[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if(sortString.matches("[A-Z]")){
                sortModel.setSortLetters(sortString.toUpperCase());
            }else{
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    private class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MainActivity.mService.isPause = false;
            MainActivity._inActivity.isFirstTime = false;
            MainActivity._inActivity.isPlaying = true;
            MainActivity.mService.playmusic(0, SourceDateList.get(position).getAudio().getPath(),true);

            updateAllAlumbSingleRow(position);
        }
    }

    private void updateAllAlumbSingleRow(int position) {
        // TODO Auto-generated method stub
        if (talmudic1 != null) {
            int start = talmudic1.getFirstVisiblePosition();
            for (int i = start, j = talmudic1.getLastVisiblePosition(); i <= j; i++) {
                if (tablespace.getItemId(position) == tablespace.getItemId(i)) {
                    //View view = listview.getChildAt(i - start);
                    tablespace.setPlayingPosition(position);
                    tablespace.notifyDataSetChanged();
                    talmudic1.setSelection(position);
                    break;
                }
            }
        }
    }
}
