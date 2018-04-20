package com.sec.connection.setting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sec.connection.R;
import com.sec.connection.xmlcheck.Progrem;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class FMProgremActivity extends AppCompatActivity {

    private ImageView mStationPic;
    private TextView mStationName;
    private TextView mStationFreq;
    private ListView mProgremList;

    private String get_name;
    private String get_freq;
    private String get_src;

    private Bitmap bitmap = null;
    private Map<String,List<Progrem>> map = null;
    List<Progrem> progrems = null;

    private String GTE_STATION_INFO = "fm.action.GTE_STATION_INFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fm_progrem);

        Intent i = getIntent();
        get_name = i.getStringExtra("name");
        get_freq = i.getStringExtra("channel");
        get_src = i.getStringExtra("src");

        mStationPic = (ImageView)findViewById(R.id.station_pic);
        mStationName = (TextView)findViewById(R.id.station_name);
        mStationFreq = (TextView)findViewById(R.id.station_freq);
        mProgremList = (ListView)findViewById(R.id.progrem_list);

        progrems = map.get(get_name);

        bitmap = getBitmap(get_src);
        mStationPic.setImageBitmap(bitmap);
        mStationName.setText(get_name);
        mStationFreq.setText(get_freq);
        mProgremList.setAdapter(new ProgremList(this));
    }

    private Bitmap getBitmap(String url) {
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
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }

    private class ProgremList extends BaseAdapter {

        Context mContext ;
        public ProgremList(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return progrems.size();
        }

        @Override
        public Object getItem(int position) {
            return progrems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.progrem_item_layout, null);
            }
            TextView mProgremName = convertView.findViewById(R.id.progrem_name);
            TextView mProgremer = convertView.findViewById(R.id.progremer);
            TextView mStartTime = convertView.findViewById(R.id.start_time);
            TextView mFinishTime = convertView.findViewById(R.id.finish_time);

            mProgremName.setText(progrems.get(position).getcontent());
            mStartTime.setText(progrems.get(position).getstarttime());
            mFinishTime.setText(progrems.get(position).getfinishtime());
            return convertView;
        }
    }
}
