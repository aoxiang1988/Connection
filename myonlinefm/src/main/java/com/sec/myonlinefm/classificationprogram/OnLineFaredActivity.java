package com.sec.myonlinefm.classificationprogram;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.sec.myonlinefm.classificationprogram.data.FavData;
import com.sec.myonlinefm.dbdata.MySQLHelper;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class OnLineFaredActivity extends AppCompatActivity {

    private ListView mFavListView;
    private List<FavData> mFavList;
    private SQLiteDatabase db;
    private MySQLHelper mySQLHelper;
    private FavListAdapter favListAdapter;
    private final int UPDATE_FAV_LIST_VIEW = 1;
    private View topBarActionBarView = null;

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
                        Intent i = new Intent(OnLineFaredActivity.this, ChannelProgramActivity.class);
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

    public void setTopPanelOnActionBar(String mChannelName) {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(false);
            bar.setDisplayShowHomeEnabled(false);
            bar.setDisplayShowTitleEnabled(false);
            bar.setBackgroundDrawable(getDrawable(R.drawable.titlebar_bg));
            bar.setElevation(0);
        }
        topBarActionBarView = getLayoutInflater().inflate(R.layout.top_bar_panel_online, null);
        ImageView mBackBut = topBarActionBarView.findViewById(R.id.back_but);
        mBackBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView mTopBarTitle = topBarActionBarView.findViewById(R.id.top_bar_title);
        mTopBarTitle.setText(mChannelName);
        mTopBarTitle.setFocusable(true);
        if (bar != null) {
            bar.setCustomView(topBarActionBarView);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            bar.show();
//            bar.hide();
        }
    }

    @Override
    public void onSupportActionModeStarted(@NonNull android.support.v7.view.ActionMode mode) {
        super.onSupportActionModeStarted(mode);
        if (topBarActionBarView != null)
            topBarActionBarView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSupportActionModeFinished(@NonNull android.support.v7.view.ActionMode mode) {
        super.onSupportActionModeFinished(mode);
        if (topBarActionBarView != null)
            topBarActionBarView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_line_faved);
        setTopPanelOnActionBar("我的收藏");
        mFavListView = (ListView) findViewById(R.id.on_line_fav_list);
        mySQLHelper = MySQLHelper.getInstances();
        db = mySQLHelper.getWritableDatabase();
        mFavList = new ArrayList<>();
        Thread t = new Thread(GetFavData);
        t.start();
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
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert inflater != null;
                convertView = inflater.inflate(R.layout.fav_channel_item, null);
            }
            ImageView mDemandChannelPic = convertView.findViewById(R.id.demand_channel_pic);
            mDemandChannelPic.setImageBitmap(mFavList.get(position).getChannelIcon());

            TextView mDemandChannelTitle = convertView.findViewById(R.id.demand_channel_title);
            mDemandChannelTitle.setText(mFavList.get(position).getChannelName());

            TextView mPodCasterName = convertView.findViewById(R.id.play_count);
            mPodCasterName.setText(mFavList.get(position).getPodCasterName());

            TextView mCategoryName = convertView.findViewById(R.id.context_total);

            if(mFavList.get(position).getCategoryID() != 5)
                mCategoryName.setText("点播节目");
            if(mFavList.get(position).getCategoryID() == 5)
                mCategoryName.setText("直播节目");

            LinearLayout imgProgramIcon_layout = convertView.findViewById(R.id.img_program_icon_layout);
            final ImageView imgInfoIcon = convertView.findViewById(R.id.program_icon);

            imgInfoIcon.setImageResource(R.drawable.hybrid_radio_on_star);

            parent.setOnKeyListener(new View.OnKeyListener() {

                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    View selectedView = mFavListView.getSelectedView();

                    if (selectedView == null) {
                        return false;
                    }

                    LinearLayout imgProgramIconLayout = selectedView.findViewById(R.id.img_program_icon_layout);

                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if (event.getAction() == KeyEvent.ACTION_UP) {
                            imgProgramIconLayout.setPressed(true);
                        }
                        return true;
                    } else if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER) || (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        if (imgProgramIconLayout.isPressed() && event.getAction() == KeyEvent.ACTION_UP) {
                            imgProgramIconLayout.performClick();
                        } else if (!imgProgramIconLayout.isPressed() && event.getAction() == KeyEvent.ACTION_UP) {
                            mFavListView.performItemClick(selectedView, mFavListView.getSelectedItemPosition(), mFavListView.getSelectedItemId());
                        }
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        imgProgramIconLayout.setPressed(false);
                        mFavListView.setActivated(true);
                        return true;
                    } else {
                        imgProgramIconLayout.setPressed(false);
                        mFavListView.setActivated(true);
                        return false;
                    }
                }
            });

            imgProgramIcon_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imgInfoIcon.setImageResource(R.drawable.hybrid_radio_off_star);
                    db.delete("fav_channels", "channel_id = ?",
                            new String[]{String.valueOf(mFavList.get(position).getChannelID())});
                    Toast.makeText(OnLineFaredActivity.this, "收藏移除", Toast.LENGTH_SHORT).show();
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
