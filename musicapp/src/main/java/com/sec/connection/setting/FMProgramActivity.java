package com.sec.connection.setting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sec.connection.R;
import com.sec.connection.xmlcheck.Program;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class FMProgramActivity extends AppCompatActivity {

    private final Map<String,List<Program>> map = null;
    List<Program> programs = null;

    private final String GTE_STATION_INFO = "fm.action.GTE_STATION_INFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fm_progrem);

        Intent i = getIntent();
        String get_name = i.getStringExtra("name");
        String get_freq = i.getStringExtra("channel");
        String get_src = i.getStringExtra("src");

        ImageView mStationPic = (ImageView) findViewById(R.id.station_pic);
        TextView mStationName = (TextView) findViewById(R.id.station_name);
        TextView mStationFreq = (TextView) findViewById(R.id.station_freq);
        ListView mProgramList = (ListView) findViewById(R.id.progrem_list);

        programs = map.get(get_name);

        Bitmap bitmap = getBitmap(get_src);
        mStationPic.setImageBitmap(bitmap);
        mStationName.setText(get_name);
        mStationFreq.setText(get_freq);
        mProgramList.setAdapter(new ProgramList(this));
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

    private class ProgramList extends BaseAdapter {

        Context mContext ;
        public ProgramList(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return programs.size();
        }

        @Override
        public Object getItem(int position) {
            return programs.get(position);
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
            TextView mProgramName = convertView.findViewById(R.id.progrem_name);
            TextView mProgrammer = convertView.findViewById(R.id.progremer);
            TextView mStartTime = convertView.findViewById(R.id.start_time);
            TextView mFinishTime = convertView.findViewById(R.id.finish_time);

            mProgramName.setText(programs.get(position).getContent());
            mStartTime.setText(programs.get(position).getStartTime());
            mFinishTime.setText(programs.get(position).getFinishTime());
            return convertView;
        }
    }
}
