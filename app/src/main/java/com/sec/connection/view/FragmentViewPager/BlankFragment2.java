package com.sec.connection.view.FragmentViewPager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.sec.connection.MusicApplication;
import com.sec.connection.R;
import com.sec.connection.data.Audio;
import com.sec.connection.MainActivity;
import com.sec.connection.MainService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BlankFragment2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BlankFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlankFragment2 extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String UPDATE_LIST_ACTIVITY_ACTION = "com.example.action.UPDATE_LIST_ACTIVITY_ACTION";
    private String[] musicname = new String[MusicApplication.list.size()];
    private int n_groupPosition;
    private int n_childPosition;
    private boolean isgroupopen = false;
    private Context mContext;

    List<String> artists = new ArrayList<>();
    List<Audio> misname_artist;
    Map<String, List<Audio>> map_artist = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LocalList locallist;

    public BlankFragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static BlankFragment2 newInstance(String param1, String param2) {
        BlankFragment2 fragment = new BlankFragment2();
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
        getartist_map();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blank_fragment2, container, false);
        ExpandableListView tartiest = (ExpandableListView) view.findViewById(R.id.tab_artist_list);

        locallist = new LocalList();
        tartiest.setAdapter(locallist);
        Tartiest_Click_Listener(tartiest);//set the expandablelist click listener

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

    private class LocalList extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return artists.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            String key = artists.get(groupPosition);
            return map_artist.get(key).size();

        }

        @Override
        public Object getGroup(int groupPosition) {
            return artists.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            String key = artists.get(groupPosition);
            return (map_artist.get(key).get(childPosition));
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


        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.group_item_layout, null);
//                if(isgroupopen){
//                    convertView.setBackgroundColor(getResources().getColor(R.color.playingcolor, null));
//                } else {
//                    convertView.setBackgroundColor(getResources().getColor(R.color.group_item_defult, null));
//                }
            }
            TextView tv = (TextView) convertView.findViewById(R.id.group_artist_name);
            tv.setText(artists.get(groupPosition));
            return tv;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            String key = artists.get(groupPosition);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.child_item_layout, null);
            }
            TextView childmusicname = (TextView) convertView.findViewById(R.id.child_music_name);
            childmusicname.setText(map_artist.get(key).get(childPosition).getTitle());

            TextView textView = (TextView) convertView.findViewById(R.id.child_music_time);
            int min = ((map_artist.get(key).get(childPosition).getDuration())/1000)/60;
            int sec = ((map_artist.get(key).get(childPosition).getDuration())/1000)%60;
            textView.setText(String.format("%s:%s",text(min),text(sec)));

            ImageView childlistmusicpicture = (ImageView) convertView.findViewById(R.id.child_listmusicpicture);
            if((map_artist.get(key).get(childPosition).getBitmap() != null))
                childlistmusicpicture.setImageBitmap((map_artist.get(key).get(childPosition).getBitmap()));
            else
                childlistmusicpicture.setImageResource(R.drawable.ic);
            if(MainService.isPlay && n_groupPosition == groupPosition && n_childPosition == childPosition){
                childmusicname.setTextColor(getResources().getColor(R.color.playingcolor, null));
                textView.setTextColor(getResources().getColor(R.color.playingcolor, null));
            } else {
                childmusicname.setTextColor(getResources().getColor(R.color.noplaycolor, null));
                textView.setTextColor(getResources().getColor(R.color.noplaycolor, null));
            }
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    private void Tartiest_Click_Listener(ExpandableListView tartiest) {
        tartiest.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if(parent.isGroupExpanded(groupPosition)) {
//                    v.setBackgroundColor(getResources().getColor(R.color.group_item_defult, null));
                    parent.collapseGroup(groupPosition);
                    isgroupopen = false;
                } else {
//                    v.setBackgroundColor(getResources().getColor(R.color.playingcolor, null));
                    parent.expandGroup(groupPosition);
                    isgroupopen = true;
                }
                locallist.notifyDataSetChanged();
                return true;
            }
        });

        tartiest.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                MainActivity.mService.isPause = false;
                MainActivity._inActivity.isFirstTime = false;
                MainActivity._inActivity.isPlaying = true;

                MainActivity.mService.playmusic(0, map_artist.get(
                        artists.get(groupPosition)).get(childPosition).getPath(),true);
                n_groupPosition = groupPosition;
                n_childPosition = childPosition;
                locallist.notifyDataSetChanged();
//                updateAllMusiclist();
                return true;
            }
        });
    }

    private void getartist_map () {
        /*get the list we need like get the artist->title**/
        map_artist = new HashMap<>();
        List<String> artists1 = new ArrayList<>();
        for (int a = 0; a < MusicApplication.list.size(); a++) {
            artists1.add( MusicApplication.list.get(a).getArtist());
            musicname[a] = MusicApplication.list.get(a).getTitle();
        }
        artists = removeDuplicate(artists1);
        /*add the music name to artist list*/
        for (int b = 0; b < artists.size(); b++) {
            int m = 0;
            misname_artist = new ArrayList<>();
            for (int a = 0; a < MusicApplication.list.size(); a++) {
                if (Objects.equals(artists.get(b), MusicApplication.list.get(a).getArtist())) {
                    misname_artist.add(m, MusicApplication.list.get(a));
                    m = m+1;
                }
            }
            map_artist.put(artists.get(b), misname_artist);
        }
    }
    private String text(int set_time){
        String get_time;
        if(set_time<10){
            get_time = String.format("0%s",set_time);
        } else {
            get_time = String.format("%s",set_time);
        }
        return get_time;
    }

    private static List<String> removeDuplicate(List<String> list) {
        Set<String> set = new LinkedHashSet<>();
        set.addAll(list);
        list.clear();
        list.addAll(set);
        return list;
    }
//    private void updateAllMusiclist(){
//        for(int a = 0; a < SourceDateList.size(); a++){
//            if(Objects.equals(SourceDateList.get(a).getName(), musicname[MainActivity.getcurrentposition()])){
//                tablespace.setPlayingPosition(a);
//                tablespace.notifyDataSetChanged();
//                talmudic1.setSelection(a);
//                break;
//            }
//        };
//    }
}
