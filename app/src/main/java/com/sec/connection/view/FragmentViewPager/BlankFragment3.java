package com.sec.connection.view.FragmentViewPager;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.sec.connection.MusicApplication;
import com.sec.connection.R;
import com.sec.connection.data.Audio;
import com.sec.connection.GridAdapter;
import com.sec.connection.UserAdapter;
import com.sec.connection.view.SelfGridView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BlankFragment3.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BlankFragment3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlankFragment3 extends BaseFragment implements View.OnTouchListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public BlankFragment3() {
        // Required empty public constructor
    }

    private Context mContext = null;

    private List<String> alumblist = new ArrayList<>();
    private List<Audio> misname_alumb;
    private Map<String, List<Audio>> map_alumb = null;
    private SelfGridView albumlistview;

    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment3.
     */
    // TODO: Rename and change types and number of parameters
    public static BlankFragment3 newInstance(String param1, String param2) {
        BlankFragment3 fragment = new BlankFragment3();
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
        map_alumb = new HashMap<>();
        List<String> alumblist1 = new ArrayList<>();
        for (int a = 0; a < MusicApplication.list.size(); a++) {
            alumblist1.add( MusicApplication.list.get(a).getAlbum());
        }
        alumblist = removeDuplicate(alumblist1);
        /*add the music name to alumb list*/
        for (int b = 0; b < alumblist.size(); b++) {
            int m = 0;
            misname_alumb = new ArrayList<>();
            for (int a = 0; a < MusicApplication.list.size(); a++) {
                if (Objects.equals(alumblist.get(b), MusicApplication.list.get(a).getAlbum())) {
                    misname_alumb.add(m, MusicApplication.list.get(a));
                    m = m+1;
                }
            }
            map_alumb.put(alumblist.get(b), misname_alumb);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blank_fragment3, container, false);
        albumlistview = (SelfGridView) view.findViewById(R.id.new_grid_view);
        final GridAdapter gridadapter = new GridAdapter(mContext,alumblist,map_alumb);
        albumlistview.setAdapter(gridadapter);
        albumlistview.setDragResponseMS(2000);
        albumlistview.setOnChangeListener(new SelfGridView.OnChanageListener() {
            @Override
            public void onChange(int form, int to) {
                Collections.swap(alumblist,form,to);
                gridadapter.notifyDataSetChanged();
            }
        });
        albumlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String alumb_name = alumblist.get(position);
                ArrayList<Audio> alumb_name_list = new ArrayList<>();
                for(int a = 0; a < map_alumb.get(alumb_name).size(); a++){
                    alumb_name_list.add(a, map_alumb.get(alumb_name).get(a));
                }

                mCustomDialog(alumb_name, alumb_name_list);
            }
        });
        return view ;
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(!isVisibleToUser && albumlistview != null)
            albumlistview.removeRunnable();
    }

    private static List<String> removeDuplicate(List<String> list) {
        Set<String> set = new LinkedHashSet<>();
        set.addAll(list);
        list.clear();
        list.addAll(set);
        return list;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void mCustomDialog(String alumb_name, ArrayList<Audio> alumb_name_list) {
        final AlertDialog builder = new AlertDialog.Builder(getActivity(),R.style.CreatDialog).create();
        builder.show();
        builder.getWindow().setContentView(R.layout.custom_list_layout);
        LayoutInflater factory = LayoutInflater.from(getActivity());
        View view = factory.inflate(R.layout.custom_list_layout, null);
        Button alumbname = (Button) view.findViewById(R.id.alumb_name);
        ListView list = (ListView) view.findViewById(R.id.alumb_list);
        UserAdapter adapter = new UserAdapter(mContext, R.layout.listitem, alumb_name_list);
        list.setAdapter(adapter);
        alumbname.setText(alumb_name);
        alumbname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

        builder.getWindow().setContentView(view);
        Window dialogWindow = builder.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);//显示在底部
        dialogWindow.setBackgroundDrawableResource(R.drawable.status_dialog_bg);
        dialogWindow.setWindowAnimations(R.style.take_photo_anim);
        WindowManager m = getActivity().getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值

        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation ; //获取屏幕方向
        if(ori == Configuration.ORIENTATION_LANDSCAPE){
            p.height = (int) (d.getWidth() * 0.3); // 高度设置为屏幕的0.5
            p.width = (int) (d.getHeight() * 0.9); // 宽度设置为屏幕宽
            //横屏
        }else if(ori == Configuration.ORIENTATION_PORTRAIT){
            p.height = (int) (d.getHeight() * 0.7); // 高度设置为屏幕的0.5
            p.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕宽
            //竖屏
        }

        dialogWindow.setAttributes(p);
    }

}
