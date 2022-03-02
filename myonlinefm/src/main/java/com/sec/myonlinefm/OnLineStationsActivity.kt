package com.sec.myonlinefm

import android.content.*
import android.os.*
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import com.sec.myonlinefm.OnLineFMPlayerListener.OberverOnLinePlayerManager
import com.sec.myonlinefm.OnLineFMPlayerListener.ObserverPlayerListener
import com.sec.myonlinefm.data.Station
import com.sec.myonlinefm.data.StationProgram
import java.lang.Exception
import java.lang.IllegalStateException

import com.sec.myonlinefm.OnLineFMConnectManager.Companion.mGPS_Name
import com.sec.myonlinefm.OnLineFMConnectManager.Companion.changedByUser
import android.annotation.SuppressLint
import android.widget.AdapterView.OnItemClickListener
import com.sec.myonlinefm.abstructObserver.OnLineInfo
import com.sec.myonlinefm.updataUIListener.ObserverUIListenerManager
import android.os.Build.VERSION_CODES
import android.media.AudioManager

@RequiresApi(VERSION_CODES.M)
class OnLineStationsActivity : AppCompatActivity(), ServiceConnection, View.OnClickListener, ObserverPlayerListener {
    private val TAG: String? = "OnLineStationsActivity"
    private var mOnLineLocalStation: RelativeLayout? = null
    private var mLocalNameView: TextView? = null
    private var mAllLocalNameView: ImageView? = null
    private var mOnLineCenterStation: Button? = null

    //    fm_list_info
    private var mListViewInfo: ListView? = null
    private var mStations: MutableList<Station?>? = null
    private var adapter: OnLineStationAdapter? = null
    private var mPlayer: OnLineFMConnectManager.Companion? = null
    protected var mHandler: Handler? = null
    private var mAudioManager: AudioManager? = null
    private val UPDATE_LIST_SELECT = 10
    private var ListType = 0
    private var dialog: RadioDialogFragment? = null
    private var mLocalStation = 0
    private var mUpdateOnLineInfo: UpdateOnLineInfo? = null
    private val CLOSE_SCAN_DIALOG = 1
    private val handler: Handler? = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == CLOSE_SCAN_DIALOG) {
                closeDialog(RadioDialogFragment.ONLINE_INFO_PROGRESS_DIALOG)
            }
        }
    }
    private var topBarActionBarView: View? = null
    private var mMap: MutableMap<Int?, MutableList<StationProgram?>?>? = null
    fun setTopPanelOnActionBar(mChannelName: String?) {
        val bar = supportActionBar
        if (bar != null) {
            bar.setHomeButtonEnabled(true)
            bar.setDisplayHomeAsUpEnabled(false)
            bar.setDisplayShowHomeEnabled(false)
            bar.setDisplayShowTitleEnabled(false)
            bar.setBackgroundDrawable(getDrawable(R.drawable.titlebar_bg))
            bar.elevation = 0f
        }
        topBarActionBarView = layoutInflater.inflate(R.layout.top_bar_panel_online, null)
        val mBackBut = topBarActionBarView!!.findViewById<ImageView?>(R.id.back_but)
        mBackBut.setOnClickListener { finish() }
        val mTopBarTitle = topBarActionBarView!!.findViewById<TextView?>(R.id.top_bar_title)
        mTopBarTitle.text = mChannelName
        mTopBarTitle.isFocusable = true
        if (bar != null) {
            bar.customView = topBarActionBarView
            bar.setDisplayHomeAsUpEnabled(true)
            bar.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            bar.show()
            //            bar.hide();
        }
    }

    override fun onSupportActionModeStarted(mode: ActionMode) {
        super.onSupportActionModeStarted(mode)
        if (topBarActionBarView != null) topBarActionBarView!!.setVisibility(View.VISIBLE)
    }

    override fun onSupportActionModeFinished(mode: ActionMode) {
        super.onSupportActionModeFinished(mode)
        if (topBarActionBarView != null) topBarActionBarView!!.setVisibility(View.VISIBLE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fmlist2)
        _activity = this
        mUpdateOnLineInfo = UpdateOnLineInfo(_activity)
        setTopPanelOnActionBar("电台列表")
        mService = OnLineFMPlayerService.Companion.getMyservice()
        mUpdateOnLineInfo!!.addToObserverList()
        OberverOnLinePlayerManager.Companion.getInstance()!!.add(this)
        mPlayer = OnLineFMConnectManager
        mOnLineLocalStation = findViewById<View?>(R.id.fm_list_local) as RelativeLayout
        mOnLineCenterStation = findViewById<View?>(R.id.fm_list_center) as Button
        mListViewInfo = findViewById<View?>(R.id.fm_list_info) as ListView
        mOnLineLocalStation!!.setOnClickListener(this)
        mLocalNameView = findViewById<View?>(R.id.fm_list_local_name) as TextView
        mLocalNameView!!.setText(mGPS_Name)
        mAllLocalNameView = findViewById<View?>(R.id.other_local_list) as ImageView
        mAllLocalNameView!!.setOnClickListener(View.OnClickListener { openDialog(RadioDialogFragment.SHOW_ALL_LOCAL_NAME, -1) })
        mOnLineCenterStation!!.setOnClickListener(this)
        mStations = OnLineFMConnectManager.mMainInfoCode!!.getOnLineStations()
        mHandler = Handler()
        mAudioManager = this.getSystemService(AUDIO_SERVICE) as AudioManager
        changeColor(ListType)
        if (mStations != null && adapter == null) {
            mLocalStation = 0
            adapter = OnLineStationAdapter(this)
            mListViewInfo!!.setAdapter(adapter)
            mListViewInfo!!.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
                //                    if (mPlayer.isBusy()) {
//                        Log.d(TAG, "RadioPlayer is busy. ignore it");
//                        return;
//                    }
                if (mStations!!.get(position)!!.getStationFreq() == "" /*||
                            (mStations != RadioPlayer.getInstance().getOnLineCenterStations()
                                    &&RadioPlayer.mLocal_ID != mLocal_ID)*/) {
                    openDialog(RadioDialogFragment.IF_ONLINE_PLAY_DIALOG, position)
                    //                    } else {
//                        mHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                String mute = "fm_radio_mute=1";
//                                mAudioManager.setParameters(mute);
//                                try {
//                                    boolean isOn = mPlayer.isOn();
//                                    if (mStations.size() > position) {
//                                        mPlayer.tuneAsyncEx(
//                                                (int)(Float.parseFloat(mStations.get(position).getStationFreq())*100));
//                                    }
//                                    if (!isOn) {
//                                        SettingsActivity.activateTurnOffAlarm();
//                                    }
//                                    mSendMessage(UPDATE_LIST_SELECT,
//                                            (int)(Float.parseFloat(mStations.get(position).getStationFreq())*100));
//                                } catch (SemFmPlayerException e) {
//                                    RadioToast.showToast(getBaseContext(), e);
//                                }
//                            }
//                        });
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        topBarActionBarView?.setVisibility(View.VISIBLE)
    }

    fun openDialog(type: Int, position: Int) {
        dialog = RadioDialogFragment.newInstance(type, position)
        if (type == RadioDialogFragment.ONLINE_INFO_PROGRESS_DIALOG) {
            dialog!!.dialog.setCanceledOnTouchOutside(false)
        }
        dialog!!.show(fragmentManager, type.toString())
    }

    private var mLocalName: String? = null
    private var mLocal_ID = 0
    fun setLocalName(mLocalName: String?, mLocal_ID: Int) {
//        if(mPlayer.isOn())
//            IntervalListeningClass.getIntense().stopListener();
        openDialog(RadioDialogFragment.ONLINE_INFO_PROGRESS_DIALOG, -1)
        this.mLocalName = mLocalName
        this.mLocal_ID = mLocal_ID
        mLocalStation = 1
        if (ListType == 0) {
            if (mLocal_ID == OnLineFMConnectManager.mLocal_ID) {
                handler!!.sendEmptyMessageDelayed(CLOSE_SCAN_DIALOG, 100)
                mStations = OnLineFMConnectManager.mMainInfoCode!!.getOnLineStations()
                mMap = OnLineFMConnectManager.mMainInfoCode!!.getOnLineStationProgramMap()
                if (adapter == null) {
                    adapter = OnLineStationAdapter(this)
                    mListViewInfo!!.setAdapter(adapter)
                } else {
                    adapter!!.notifyDataSetChanged()
                }
            }
            mLocalNameView!!.setText(this.mLocalName)
        }
    }

    private fun closeDialog(type: Int) {
        Log.v(TAG, "closeDialog() - start $type")
        val dialog = fragmentManager.findFragmentByTag(type.toString()) as RadioDialogFragment
        if (dialog != null) {
            try {
                Log.v(TAG, "removeDialog() - $type")
                //proper dismissing the dialog for checked item if this item is changed/deleted from somewhere else
                dialog.dismissAllowingStateLoss()
            } catch (e: IllegalStateException) {
                Log.d(TAG, "IllegalStateException in closeDialog:- " + e.message)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu!!.add(0, MENU_LIST, MENU_LIST, "do runnable 1")
        menu.add(0, MENU_SEARCH, MENU_SEARCH, "do runnable 1")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item!!.getItemId() == MENU_LIST) {
        } else if (item.getItemId() == MENU_SEARCH) {
        }
        return super.onOptionsItemSelected(item)
    }

    fun StartPlay(position: Int) {
        OnLineFMConnectManager.mMainInfoCode!!.getReplayUrl(mStations!!.get(position)!!.getStationId(),
                null, null, OnLineFMPlayerService.Companion.PLAY_STATION)
    }

    override fun onDestroy() {
        mUpdateOnLineInfo!!.removeToObserverList()
        OberverOnLinePlayerManager.Companion.getInstance()!!.remove(this)
        changedByUser = false
        super.onDestroy()
    }

    override fun onClick(v: View?) {
        when (v!!.getId()) {
            R.id.fm_list_local -> {
                ListType = 0
                mLocalNameView!!.setText(mGPS_Name)
                mLocalStation = 0
                changeColor(ListType)
                mStations = OnLineFMConnectManager.mMainInfoCode!!.getOnLineStations()
                if (adapter == null) {
                    adapter = OnLineStationAdapter(this)
                    mListViewInfo!!.setAdapter(adapter)
                } else {
                    adapter!!.notifyDataSetChanged()
                }
            }
            R.id.fm_list_center -> {
                ListType = 2
                changeColor(ListType)
                mStations = OnLineFMConnectManager.mMainInfoCode!!.getOnLineCenterStations()
                adapter!!.notifyDataSetChanged()
            }
        }
    }

    private fun changeColor(ListType: Int) {
        if (ListType == 0) {
            mLocalNameView!!.setTextColor(resources.getColor(R.color.tab_selected, null))
            mAllLocalNameView!!.setClickable(true)
            mOnLineCenterStation!!.setTextColor(resources.getColor(R.color.tab_unselected, null))
        } else {
            mLocalNameView!!.setTextColor(resources.getColor(R.color.tab_unselected, null))
            mLocalNameView!!.setText(mGPS_Name)
            mAllLocalNameView!!.setClickable(false)
            mLocalStation = 0
            mOnLineCenterStation!!.setTextColor(resources.getColor(R.color.tab_selected, null))
        }
    }

    private inner class UpdateOnLineInfo(private val context: Context?) : OnLineInfo() {
        override fun addToObserverList() {
            ObserverUIListenerManager.Companion.getInstance()!!.add(this)
        }

        override fun removeToObserverList() {
            ObserverUIListenerManager.Companion.getInstance()!!.remove(this)
        }

        override fun observerLiveRadioLocalUIUpData(stations: MutableList<Station?>?, map: MutableMap<Int?, MutableList<StationProgram?>?>?) {
            //TODO update local stations information
            if (ListType == 0) {
                mStations = stations
                if (adapter == null) {
                    adapter = OnLineStationAdapter(context)
                    mListViewInfo!!.setAdapter(adapter)
                } else {
                    adapter!!.notifyDataSetChanged()
                }
            }
        }

        override fun observerLiveRadioCenterUIUpData(stations: MutableList<Station?>?, map: MutableMap<Int?, MutableList<StationProgram?>?>?) {
            //TODO update local stations information
            if (ListType != 0) {
                mStations = stations
                if (adapter == null) {
                    adapter = OnLineStationAdapter(context)
                    mListViewInfo!!.setAdapter(adapter)
                } else {
                    adapter!!.notifyDataSetChanged()
                }
            }
        }

        override fun observerDifferentInfoUIUpData(stations: MutableList<Station?>?, map: MutableMap<Int?, MutableList<StationProgram?>?>?) {
            if (ListType == 0) {
//                if(mPlayer.isOn())
//                    IntervalListeningClass.getIntense().startListener();
                closeDialog(RadioDialogFragment.ONLINE_INFO_PROGRESS_DIALOG)
                mStations = stations
                mMap = map
                if (adapter == null) {
                    adapter = OnLineStationAdapter(context)
                    mListViewInfo!!.setAdapter(adapter)
                } else {
                    adapter!!.notifyDataSetChanged()
                }
            }
        }
    }

    override fun observerStartPlayer(mPlayer_Url: String?, play_type: Int) {
        mService!!.StartPlay(mPlayer_Url, play_type)
    }

    override fun observerStartDNS(isStart: Boolean) {
        //do nothing..
    }

    private inner class OnLineStationAdapter(context: Context?) : BaseAdapter() {
        private val mInflater: LayoutInflater?
        private var mContext: Context? = null
        override fun getCount(): Int {
            return if (mStations != null)
                mStations!!.size else 0
        }

        override fun getItem(position: Int): Station? {
            return mStations!!.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        @SuppressLint("SetTextI18n")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var convertView = convertView
            val holder: ViewHolder
            if (convertView == null) {
                convertView = mInflater!!.inflate(R.layout.fmlist_item, null)
                holder = ViewHolder()
                holder.imgProgramIconLayout = convertView.findViewById(R.id.img_program_icon_layout)
                holder.channelFreq = convertView.findViewById(R.id.list_ChanFreq)
                holder.channelName = convertView.findViewById(R.id.list_ChanName)
                holder.proLayout = convertView.findViewById(R.id.list_proLayout)
                holder.ProgramText = convertView.findViewById(R.id.list_Pro)
                holder.ProgramText!!.isFocusable = true
                holder.currentProgramText = convertView.findViewById(R.id.list_currentPro)
                convertView.tag = holder //绑定ViewHolder对象
            } else {
                holder = convertView.tag as ViewHolder //取出ViewHolder对象
            }
            //            holder.imgProgramIconLayout.setBackground(new BitmapDrawable(getResources(), mStations.get(position).getStationThumbs()));
            val asyncTask = UpdateListViewAsyncTask(mContext,
                    mStations!!.get(position)!!.getStationTitle(),
                    holder.imgProgramIconLayout, OnLineFMConnectManager.mMainInfoCode,
                    false, 60, 60)
            asyncTask.execute(mStations!!.get(position)!!.getStationThumbs())

            //            if(mStations.get(position).getStationFreq() == null || Objects.equals(mStations.get(position).getStationFreq(), "")) {
//                holder.channelFreq.setVisibility(View.INVISIBLE);
//            }
            holder.channelFreq!!.text = mStations!!.get(position)!!.getStationFreq() + " MHz"
            holder.channelName!!.text = mStations!!.get(position)!!.getStationTitle()
            if (mStations!!.get(position)!!.getCurrentProgram() != null) {
                holder.ProgramText!!.text = "正在播放:" + mStations!!.get(position)!!.getCurrentProgram()!!.getProgramTitle()
                holder.currentProgramText!!.text = ("当前节目:" + mStations!!.get(position)!!.getCurrentProgram()!!.getProgramTitle()
                        + "/下一个节目:" + mStations!!.get(position)!!.getNextProgram())
            }
            parent!!.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    if (event.action == KeyEvent.ACTION_UP) {
                        val b = holder.imgProgramIconLayout?.isPressed ?: true
                    }
                    true
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (holder.imgProgramIconLayout!!.isPressed
                            && event.action == KeyEvent.ACTION_UP) {
                        holder.imgProgramIconLayout!!.performClick()
                    }
                    true
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    holder.imgProgramIconLayout!!.isPressed = false
                    true
                } else {
                    holder.imgProgramIconLayout?.isPressed ?: false
                }
            })
            holder.imgProgramIconLayout!!.setOnClickListener {
                val ID = mStations!!.get(position)!!!!.getStationId()
                val intent = Intent(mContext, FMProgramActivity::class.java)
                intent.putExtra("STATION_ID", ID)
                intent.putExtra("LIST_TYPE", ListType)
                intent.putExtra("DIFFERENT", mLocalStation)
                if (mLocalStation != 0) {
                    setDifferentStation(mStations!!.get(position))
                    setDifferentStationProgram(mMap?.get(ID))
                }
                //intent.putExtra("src",imageSrc);
                startActivity(intent)
            }
            try {
//                if (Objects.equals(Manager.convertToMhz(RadioPlayer.getInstance().getFrequency()),
//                        mStations.get(position).getStationFreq())) {
//                    holder.channelFreq.setPadding(0, 0, 0, 0);
//                    holder.channelFreq.setTextColor(
//                            getResources().getColor(R.color.channel_frequency_text_playing, null));
//                    holder.channelName.setTextColor(
//                            getResources().getColor(R.color.channel_frequency_text_playing, null));
//                    //            holder.proLayout.setVisibility(View.VISIBLE);
//                    holder.currentProgramText.setVisibility(View.GONE);
//                    holder.ProgramText.setVisibility(View.VISIBLE);
//                    holder.ProgramText.setSelected(true);
//                } else {
                holder.channelFreq!!.setPadding(0, 0, 0, 0)
                holder.channelFreq!!.setTextColor(
                        resources.getColor(R.color.channel_frequency_text, null))
                holder.channelName!!.setTextColor(
                        resources.getColor(R.color.channel_frequency_text, null))
                holder.ProgramText!!.isSelected = false
                holder.ProgramText!!.visibility = View.GONE
                holder.currentProgramText!!.visibility = View.VISIBLE

                //        holder.proLayout.setVisibility(View.GONE);
//                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            (convertView as ViewGroup?)!!.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS)
            return convertView
        }

        init {
            mInflater = LayoutInflater.from(context)
            mContext = context
        }
    }

    internal class ViewHolder {
        var channelFreq: TextView? = null
        var channelName: TextView? = null
        var ProgramText: TextView? = null
        var currentProgramText: TextView? = null
        var imgProgramIconLayout: LinearLayout? = null
        var proLayout: LinearLayout? = null
    }

    @SuppressLint("HandlerLeak")
    var mListHandler: Handler? = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                UPDATE_LIST_SELECT -> {
                    Log.d("testw-", "handleMessage: UPDATE_LIST_SELECT")
                    //      android.util.Log.d("testw-", "handleMessage: " + mAdapter + " " + mPlayer.getFrequency());
//                    if (mPlayer.getFrequency() == msg.arg1) {
//                        if (adapter != null) {
//                            adapter.notifyDataSetChanged();
//                        }
//                        if (mListHandler.hasMessages(UPDATE_LIST_SELECT)) {
//                            mListHandler.removeMessages(UPDATE_LIST_SELECT);
//                        }
//                    } else {
                    if (this.hasMessages(UPDATE_LIST_SELECT)) {
                        this.removeMessages(UPDATE_LIST_SELECT)
                    }
                    mSendMessage(UPDATE_LIST_SELECT, msg!!.arg1, 100)
                }
            }
        }
    }

    private fun mSendMessage(what: Int, arg1: Int) {
        val mMessage = Message()
        mMessage.what = what
        mMessage.arg1 = arg1
        mListHandler!!.sendMessage(mMessage)
    }

    private fun mSendMessage(what: Int, data: String?) {
        val mMessage = Message()
        mMessage.what = what
        mMessage.obj = data
        mListHandler!!.sendMessage(mMessage)
    }

    private fun mSendMessage(what: Int, arg1: Int, delay: Long) {
        val mMessage = Message()
        mMessage.what = what
        mMessage.arg1 = arg1
        mListHandler!!.sendMessageDelayed(mMessage, delay)
    }

    private var mStation: Station? = null
    private var mProgram: MutableList<StationProgram?>? = null
    private fun setDifferentStation(mStation: Station?) {
        this.mStation = mStation
    }

    private fun setDifferentStationProgram(mProgram: MutableList<StationProgram?>?) {
        this.mProgram = mProgram
    }

    fun getDifferentStation(): Station? {
        return mStation
    }

    fun getDifferentStationProgram(): MutableList<StationProgram?>? {
        return mProgram
    }

    /* connect to service
    * **/
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        if (service is OnLineFMPlayerService.ServiceBinder) {
            val binder = service as OnLineFMPlayerService.ServiceBinder?
            binder!!.getService()
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {}

    companion object {
        private var mService: OnLineFMPlayerService? = null
        var _activity: OnLineStationsActivity? = null
        private const val MENU_LIST = 14
        private const val MENU_SEARCH = 15
    }
}