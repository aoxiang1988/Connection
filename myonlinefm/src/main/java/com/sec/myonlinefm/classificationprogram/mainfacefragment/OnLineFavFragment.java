package com.sec.myonlinefm.classificationprogram.mainfacefragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sec.myonlinefm.R;
import com.sec.myonlinefm.classificationprogram.ChannelProgramActivity;
import com.sec.myonlinefm.classificationprogram.data.FavData;
import com.sec.myonlinefm.dbdata.MySQLHelper;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link OnLineFavFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnLineFavFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static final String TAG = "OnLineFavFragment";

    public OnLineFavFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OnLineFavFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OnLineFavFragment newInstance(String param1, String param2) {
        OnLineFavFragment fragment = new OnLineFavFragment();
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
    }

    private View view;
    private ListView mFavListView;
    private List<FavData> mFavList;
    private SQLiteDatabase db;
    private MySQLHelper mySQLHelper;
    private FavListAdapter favListAdapter;
    private final int UPDATE_FAV_LIST_VIEW = 1;
    private View topBarActionBarView = null;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_on_line_fav, container, false);
        mFavListView = (ListView) view.findViewById(R.id.on_line_fav_list);
        mySQLHelper = MySQLHelper.getInstances();
        db = mySQLHelper.getWritableDatabase();
        mFavList = new ArrayList<>();
        Thread t = new Thread(GetFavData);
        t.start();
        return view;
    }

    @Override
    public void onDestroyView() {
        view = null;
        super.onDestroyView();
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

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == UPDATE_FAV_LIST_VIEW) {
                favListAdapter = new FavListAdapter();
                mFavListView.setAdapter(favListAdapter);
                mFavListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(mContext, ChannelProgramActivity.class);
                        i.putExtra("channel_id", mFavList.get(position).getChannelID());
                        i.putExtra("channel_name", mFavList.get(position).getChannelName());
                        startActivity(i);
                    }
                });
            }
        }
    };
    private Runnable GetFavData = new Runnable() {
        @Override
        public void run() {
            Cursor cursor = db.query("fav_channels",null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    FavData favData = new FavData();
                    int cid = cursor.getInt(cursor.getColumnIndex("channel_id"));
                    int category_id = cursor.getInt(cursor.getColumnIndex("category_id"));
                    String name = cursor.getString(cursor.getColumnIndex("channel_name"));
                    String url = cursor.getString(cursor.getColumnIndex("channel_them_url"));
                    String podCasterName = cursor.getString(cursor.getColumnIndex("pod_caster_name"));
                    Log.d("bin1111.yang","favData : "+name+" "+cid+" "+url);
                    favData.setChannelID(cid);
                    favData.setCategoryID(category_id);
                    favData.setChannelName(name);
                    favData.setPodCasterName(podCasterName);
                    favData.setChannelIcon(getBitmap(url));
                    mFavList.add(favData);
                } while (cursor.moveToNext());
            }
            cursor.close();
            if(!mFavList.isEmpty())
                mHandler.sendEmptyMessage(UPDATE_FAV_LIST_VIEW);
        }
    };

    static class ViewHolder {
        ImageView mDemandChannelPic;

        TextView mDemandChannelTitle;

        TextView mPodCasterName;

        TextView mCategoryName;

        LinearLayout imgProgramIcon_layout;

        ImageView imgInfoIcon;
    }

    private class FavListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mFavList.size();
        }

        @Override
        public Object getItem(int position) {
            return mFavList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert inflater != null;
                convertView = inflater.inflate(R.layout.fav_channel_item, null);
                holder = new ViewHolder();

                holder.mDemandChannelPic = convertView.findViewById(R.id.demand_channel_pic);
                holder.mDemandChannelTitle = convertView.findViewById(R.id.demand_channel_title);
                holder.mPodCasterName = convertView.findViewById(R.id.play_count);
                holder.imgProgramIcon_layout = convertView.findViewById(R.id.img_program_icon_layout);
                holder.mCategoryName = convertView.findViewById(R.id.context_total);
                holder.imgInfoIcon = convertView.findViewById(R.id.program_icon);
                convertView.setTag(holder);
            }  else {
                holder = (ViewHolder) convertView.getTag();//取出ViewHolder对象
            }

            holder.mDemandChannelPic.setImageBitmap(mFavList.get(position).getChannelIcon());
            holder.mDemandChannelTitle.setText(mFavList.get(position).getChannelName());
            holder.mPodCasterName.setText(mFavList.get(position).getPodCasterName());

            if(mFavList.get(position).getCategoryID() != 5)
                holder.mCategoryName.setText("点播节目");
            if(mFavList.get(position).getCategoryID() == 5)
                holder.mCategoryName.setText("直播节目");



            holder.imgInfoIcon.setImageResource(R.drawable.hybrid_radio_on_star);

            parent.setOnKeyListener(new View.OnKeyListener() {

                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    View selectedView = mFavListView.getSelectedView();

                    if (selectedView == null) {
                        return false;
                    }

                    holder.imgProgramIcon_layout = selectedView.findViewById(R.id.img_program_icon_layout);

                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if (event.getAction() == KeyEvent.ACTION_UP) {
                            holder.imgProgramIcon_layout.setPressed(true);
                        }
                        return true;
                    } else if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER) || (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        if (holder.imgProgramIcon_layout.isPressed() && event.getAction() == KeyEvent.ACTION_UP) {
                            holder.imgProgramIcon_layout.performClick();
                        } else if (!holder.imgProgramIcon_layout.isPressed() && event.getAction() == KeyEvent.ACTION_UP) {
                            mFavListView.performItemClick(selectedView, mFavListView.getSelectedItemPosition(), mFavListView.getSelectedItemId());
                        }
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        holder.imgProgramIcon_layout.setPressed(false);
                        mFavListView.setActivated(true);
                        return true;
                    } else {
                        holder.imgProgramIcon_layout.setPressed(false);
                        mFavListView.setActivated(true);
                        return false;
                    }
                }
            });

            holder.imgProgramIcon_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.imgInfoIcon.setImageResource(R.drawable.hybrid_radio_off_star);
                    db.delete("fav_channels", "channel_id = ?",
                            new String[]{String.valueOf(mFavList.get(position).getChannelID())});
                    Toast.makeText(mContext, "收藏移除", Toast.LENGTH_SHORT).show();
//                    mFavOnLineChannel.setImageResource(R.drawable.hybrid_radio_off_star);
                    mFavList.remove(mFavList.get(position));
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }
    }
    private Bitmap getBitmap(String url) {
        if(url == null)
            return null;
        Bitmap bm = null;
        try {
            URL iconUrl = new URL(url);
            URLConnection conn = iconUrl.openConnection();
            HttpURLConnection http = (HttpURLConnection) conn;
            int length = http.getContentLength();
            conn.connect();
            // 获得图像的字符流
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is, length);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();// 关闭流
            Log.d("bin1111.yang","getBitmap : "+url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }

}
