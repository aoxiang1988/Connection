package com.sec.myonlinefm.classificationprogram;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.sec.myonlinefm.R;

public class NetLiveRadioStationActivity extends AppCompatActivity {

    private ListView mNetLiveRadioView;
    private int mCategoryID = 5;
    private int mAttr = 1234;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_live_radio_station);
        mNetLiveRadioView = (ListView) findViewById(R.id.net_live_radio_list);
    }
}
