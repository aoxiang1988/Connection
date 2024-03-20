package com.sec.myonlinefm.classificationprogram

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.sec.myonlinefm.R


class NetLiveRadioStationActivity : AppCompatActivity() {
    private var mNetLiveRadioView: ListView? = null
    private val mCategoryID = 5
    private val mAttr = 1234
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_net_live_radio_station)
        mNetLiveRadioView = findViewById<View?>(R.id.net_live_radio_list) as ListView
    }
}