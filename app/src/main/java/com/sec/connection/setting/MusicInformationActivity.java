package com.sec.connection.setting;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sec.connection.R;
import com.sec.connection.data.Audio;
import com.sec.connection.MainActivity;
import com.sec.connection.MusicApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SRC-TJ-MM-BinYang on 2017/10/12.
 */

public class MusicInformationActivity extends Activity {
    List<Audio> music = MusicApplication.list;
    CharSequence[] info;
    List<String> l = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_info);
        ImageView back = (ImageView)findViewById(R.id.media_back);
        ImageView infopicture = (ImageView)findViewById(R.id.info_picture);
        TextView music_info_title = (TextView)findViewById(R.id.media_setting_text);
        music_info_title.setText("Music Info");

        LinearLayout scrollview = (LinearLayout)findViewById(R.id.scrollview);

        info = getResources().getStringArray(R.array.info);

        if(music.get(MainActivity.mListPosition).getBitmap() != null)
            infopicture.setImageBitmap(music.get(MainActivity.mListPosition).getBitmap());

        for(int i = 0; i<info.length; i++){
            View view_1 = LayoutInflater.from(this).inflate(R.layout.info_text_view,null);

            TextView infomusictitle = (TextView)view_1.findViewById(R.id.info_music_title);
            infomusictitle.setText(info[i]);
            TextView infomusiccontext = (TextView)view_1.findViewById(R.id.info_music_context);
            infomusiccontext.setText(music.get(MainActivity.mListPosition).getinfo(i));
            scrollview.addView(view_1);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
