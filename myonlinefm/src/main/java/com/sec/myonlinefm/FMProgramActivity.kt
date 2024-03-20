package com.sec.myonlinefm

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Bitmap
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.constraintlayout.widget.ConstraintLayout
import com.sec.myonlinefm.OnLineFMPlayerListener.OberverOnLinePlayerManager
import com.sec.myonlinefm.OnLineFMPlayerListener.ObserverPlayerListener
import com.sec.myonlinefm.abstructObserver.OnLineInfo
import com.sec.myonlinefm.data.Station
import com.sec.myonlinefm.data.StationProgram
import com.sec.myonlinefm.updataUIListener.ObserverUIListenerManager

/**
 * Created by srct on 2018/1/12.
 */
@RequiresApi(VERSION_CODES.M)
class FMProgramActivity : AppCompatActivity(), ServiceConnection, ObserverPlayerListener {
    private val TAG: String = "FMProgramActivity"
    private var mStationPic: ImageView? = null
    private var mStationName: TextView? = null
    private var mStationFreq: TextView? = null
    private var day_info = 1
    private var topBarActionBarView: View? = null
    private var mProgramList: ListView? = null
    private var mProgramAdapter: ProgramList? = null
    private var get_ID = 0
    private var get_List_Type = 0
    private var get_Different_Type = 0
    private var mPlayType = 0
    private val bitmap: Bitmap? = null
    private var mPlayer: OnLineFMConnectManager? = null
    private var mOnLineStationPlay: LinearLayout? = null
    private var mStationInfo: LinearLayout? = null
    private var mOnLinePlayerController: FrameLayout? = null
    private var mControllerStationName: TextView? = null
    private var mControllerProgramName: TextView? = null
    private val mStartTime: TextView? = null
    private var mEndTime: TextView? = null
    private var mPlayButton: ImageButton? = null
    private var mNextButton: ImageButton? = null
    private var mPrevButton: ImageButton? = null
    private var mControllerBar: SeekBar? = null
    private var mStation: Station? = null
    private var map: MutableMap<Int?, MutableList<StationProgram?>?>? = null
    private var mProgram: MutableList<StationProgram?>? = null
    private var duration = 0
    private val GTE_STATION_INFO: String = "fm.action.GTE_STATION_INFO"
    private var receiver: Receiver? = null
    private var mClickListener: View.OnClickListener? = View.OnClickListener { v ->
        when (v.id) {
            R.id.play_pause_btn -> if (mService!!.isPlay) {
                mService!!.StopPlay()
                setPlayButton()
                mOnLinePlayerController!!.visibility = View.GONE
                mStationInfo!!.visibility = View.VISIBLE
            } else {
                mService!!.StartPlay(mService!!.mPlayer_url, mPlayType) //need check ..
            }
            R.id.playe_station -> {
                mPlayType = OnLineFMPlayerService.PLAY_STATION
                mPlayer!!.getReplayUrl(mStation!!.getStationId(),
                        null, null, mPlayType)
                setPauseButton()
                updateControllerUI(mPlayType, mStation!!.getCurrentProgram())
            }
            R.id.prev_btn -> {
            }
            R.id.next_btn -> {
            }
            R.id.yesterday_btn -> {
                day_info = 0
                mPlayer!!.getOneDayProgram(mStation!!, day_info)
                setTopBarActivityButton(day_info)
            }
            R.id.today_btn -> {
                day_info = 1
                mPlayer!!.getOneDayProgram(mStation!!, day_info)
                setTopBarActivityButton(day_info)
            }
            R.id.tomorrow_btn -> {
                day_info = 2
                mPlayer!!.getOneDayProgram(mStation!!, day_info)
                setTopBarActivityButton(day_info)
            }
            else -> {
            }
        }
    }
    private var mUpdateOnLineInfo: UpdateOnLineInfo? = null

    private inner class UpdateOnLineInfo(private val context: Context?) : OnLineInfo() {
        override fun addToObserverList() {
            ObserverUIListenerManager.getInstance()!!.add(this)
        }

        override fun removeToObserverList() {
            ObserverUIListenerManager.getInstance()!!.remove(this)
        }

        override fun observerLiveRadioLocalUIUpData(mStations: MutableList<Station?>?, map: MutableMap<Int?, MutableList<StationProgram?>?>?) {
            //TODO update local stations information
            if (mProgram == null) mProgram = map!![get_ID]
            if (mProgramAdapter == null) {
                mProgramAdapter = ProgramList(context, mProgram)
                mProgramList!!.adapter = mProgramAdapter
            } else {
                mProgramAdapter!!.setNewList(mProgram)
                mProgramAdapter!!.notifyDataSetChanged()
                mProgramList!!.setSelection(mCurrent_Position)
            }
        }

        override fun observerLiveRadioCenterUIUpData(mStations: MutableList<Station?>?, map: MutableMap<Int?, MutableList<StationProgram?>?>?) {
            //TODO update local stations information
            if (mProgram == null) mProgram = map!!.get(get_ID)
            if (mProgramAdapter == null) {
                mProgramAdapter = ProgramList(context, mProgram)
                mProgramList!!.adapter = mProgramAdapter
            } else {
                mProgramAdapter!!.setNewList(mProgram)
                mProgramAdapter!!.notifyDataSetChanged()
                mProgramList!!.setSelection(mCurrent_Position)
            }
        }

        override fun observerOneDayProgramUpData(mOneDayPrograms: MutableList<StationProgram?>?) {
            Log.d(TAG, "observerOneDayProgramUpData")
            mProgram!!.clear()
            mProgram = null
            mProgram = mOneDayPrograms
            mProgramAdapter!!.setNewList(mProgram)
            mProgramAdapter!!.notifyDataSetChanged()
            mProgramList!!.setSelection(mCurrent_Position)
        }
    }

    fun initController() {
        mOnLinePlayerController = findViewById<View?>(R.id.voice_list_clip_panel_layout) as FrameLayout
        mPlayButton = findViewById<View?>(R.id.play_pause_btn) as ImageButton
        mPlayButton!!.setOnClickListener(mClickListener)
        mControllerStationName = findViewById<View?>(R.id.online_station_name) as TextView
        mControllerProgramName = findViewById<View?>(R.id.online_program_name) as TextView
        mEndTime = findViewById<View?>(R.id.PlayerFullTimeTextView) as TextView
        mNextButton = findViewById<View?>(R.id.prev_btn) as ImageButton
        mNextButton!!.setOnClickListener(mClickListener)
        mPrevButton = findViewById<View?>(R.id.next_btn) as ImageButton
        mPrevButton!!.setOnClickListener(mClickListener)
        mControllerBar = findViewById<View?>(R.id.progressbar) as SeekBar
        mControllerBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mService!!.progress(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    fun updateControllerUI(play_type: Int, program: StationProgram?) {
        mControllerStationName!!.text = mStation!!.getStationTitle()
        mControllerProgramName!!.text = program!!.getProgramTitle()
        mEndTime!!.text = program.getProgramEnd_Time()
        if (play_type == OnLineFMPlayerService.PLAY_STATION) {
            //start update system time
        } else {
        }
    }

    private var mYesterdayBut: Button? = null
    private var mTodayBut: Button? = null
    private var mTomorrowBut: Button? = null
    @SuppressLint("InflateParams")
    fun setTopPanelOnActionBar() {
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
        val mTopBarController = topBarActionBarView!!.findViewById<LinearLayout?>(R.id.top_bar_controller)
        val mBackBut = topBarActionBarView!!.findViewById<ImageView?>(R.id.back_but)
        mBackBut.setOnClickListener { finish() }
        val mTopBarTitle = topBarActionBarView!!.findViewById<TextView?>(R.id.top_bar_title)
        mTopBarTitle.text = "直播列表"
        mTopBarController.visibility = View.VISIBLE
        mYesterdayBut = topBarActionBarView!!.findViewById(R.id.yesterday_btn)
        mTodayBut = topBarActionBarView!!.findViewById(R.id.today_btn)
        mTomorrowBut = topBarActionBarView!!.findViewById(R.id.tomorrow_btn)
        mYesterdayBut!!.setOnClickListener(mClickListener)
        mTodayBut!!.setOnClickListener(mClickListener)
        mTomorrowBut!!.setOnClickListener(mClickListener)
        setTopBarActivityButton(day_info)
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
        if (topBarActionBarView != null) topBarActionBarView!!.visibility = View.VISIBLE
    }

    override fun onSupportActionModeFinished(mode: ActionMode) {
        super.onSupportActionModeFinished(mode)
        if (topBarActionBarView != null) topBarActionBarView!!.visibility = View.VISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fm_program)
        mUpdateOnLineInfo = UpdateOnLineInfo(this)
        OberverOnLinePlayerManager.Companion.getInstance()!!.add(this)
        mUpdateOnLineInfo!!.addToObserverList()
        mPlayer = OnLineFMConnectManager.mMainInfoCode
        mService = OnLineFMPlayerService.getMyservice()
        setTopPanelOnActionBar()
        receiver = Receiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(OnLineFMPlayerService.UPDATE_REPLAY_UI_ACTION)
        registerReceiver(receiver, intentFilter)
        val i = intent
        get_ID = i.getIntExtra("STATION_ID", -1)
        get_List_Type = i.getIntExtra("LIST_TYPE", 0)
        get_Different_Type = i.getIntExtra("DIFFERENT", 0)
        getAllInfo(get_ID, get_List_Type, get_Different_Type)
        initController()
        mStationPic = findViewById<View?>(R.id.station_pic) as ImageView
        mStationName = findViewById<View?>(R.id.station_name) as TextView
        mStationFreq = findViewById<View?>(R.id.station_freq) as TextView
        mProgramList = findViewById<View?>(R.id.program_list) as ListView
        mOnLineStationPlay = findViewById<View?>(R.id.playe_station) as LinearLayout
        mOnLineStationPlay!!.setOnClickListener(mClickListener)
        mStationInfo = findViewById<View?>(R.id.station_info) as LinearLayout
        if (mStation != null) {
            mStationName!!.text = mStation!!.getStationTitle()
            mStationFreq!!.text = String.format("%s MHz", mStation!!.getStationFreq())
            //            mStationPic.setImageBitmap(mPlayer.getBitmap(mStation.getStationThumbs(), 100, 100));
            val asyncTask = UpdateListViewAsyncTask(mStationPic,
                    mStation!!.getStationTitle(),
                    mPlayer, 100, 100)
            asyncTask.execute(mStation!!.getStationThumbs())
            try {
                mProgramAdapter = ProgramList(this, mProgram)
                mProgramList!!.adapter = mProgramAdapter
            } catch (e: NullPointerException) {
                Toast.makeText(this, "请等待，网络刷新中", Toast.LENGTH_SHORT).show()
            }
        } else {
            //map 为空  网络错误
            Toast.makeText(this, "请等待，网络刷新中", Toast.LENGTH_SHORT).show()
        }
        mProgramList!!.setOnItemClickListener { parent, view, position, id ->
            mPlayType = OnLineFMPlayerService.REPLAY_PROGRAM
            mPlayer!!.getReplayUrl(mStation!!.getStationId(),
                    mProgram!!.get(position)!!.getProgramStart_time(),
                    mProgram!!.get(position)!!.getProgramEnd_Time(), mPlayType)
            setPauseButton()
            updateControllerUI(mPlayType, mProgram!!.get(position))
        }
    }

    /* @Override
    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);
//        mIsActionMode = false;
        if (topBarActionBarView != null)
            topBarActionBarView.setVisibility(View.VISIBLE);
//        if (mOptionsMenu != null) {
//            onCreateOptionsMenu(mOptionsMenu);
//        }
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        super.onActionModeStarted(mode);
//        mIsActionMode = true;
        if (topBarActionBarView != null)
            topBarActionBarView.setVisibility(View.VISIBLE);
    }*/
    private fun setTopBarActivityButton(day_info: Int) {
        mYesterdayBut!!.setTextColor(resources.getColor(R.color.channel_frequency_text, null))
        mYesterdayBut!!.isClickable = true
        mTodayBut!!.setTextColor(resources.getColor(R.color.channel_frequency_text, null))
        mTodayBut!!.isClickable = true
        mTomorrowBut!!.setTextColor(resources.getColor(R.color.channel_frequency_text, null))
        mTomorrowBut!!.isClickable = true
        if (day_info == 0) {
            mYesterdayBut!!.setTextColor(resources.getColor(R.color.playingcolor, null))
            mYesterdayBut!!.isClickable = false
        }
        if (day_info == 1) {
            mTodayBut!!.setTextColor(resources.getColor(R.color.playingcolor, null))
            mTodayBut!!.isClickable = false
        }
        if (day_info == 2) {
            mTomorrowBut!!.setTextColor(resources.getColor(R.color.playingcolor, null))
            mTomorrowBut!!.isClickable = false
        }
    }

    override fun onResume() {
        super.onResume()
        if (topBarActionBarView != null) topBarActionBarView!!.visibility = View.VISIBLE
    }

    private fun setPauseButton() {
        mPlayButton!!.setImageResource(R.drawable.listplayer_control_pause_btn)
        mPlayButton!!.contentDescription = getString(R.string.desc_pause)
    }

    private fun setPlayButton() {
        mPlayButton!!.setImageResource(R.drawable.listplayer_control_play_btn)
        mPlayButton!!.contentDescription = getString(R.string.tts_play_button)
    }

    override fun onDestroy() {
        super.onDestroy()
        mStation = null
        map = null
        mProgram = null
        mClickListener = null
        mStationPic = null
        mStationName = null
        mStationFreq = null
        mProgramList = null
        mOnLineStationPlay = null
        mStationInfo = null
        unregisterReceiver(receiver)
        OberverOnLinePlayerManager.getInstance()!!.remove(this)
        mUpdateOnLineInfo!!.removeToObserverList()
    }

    private fun getAllInfo(ID: Int, ListType: Int, get_Different_Type: Int) {
        try {
            if (ListType == 0) {
                if (get_Different_Type != 0) {
                    mStation = OnLineStationsActivity._activity!!.getDifferentStation()
                    mProgram = OnLineStationsActivity._activity!!.getDifferentStationProgram()
                } else {
                    mStation = mPlayer!!.getOnLineStationMap(mPlayer!!.getOnLineStations())[ID]
                    map = mPlayer!!.getOnLineStationProgramMap()
                    mProgram = map!!.get(mStation!!.getStationId())
                }
            } else {
                mStation = mPlayer!!.getOnLineStationMap(mPlayer!!.getOnLineCenterStations())[ID]
                map = mPlayer!!.getOnLineStationProgramCentermap()
                mProgram = map!!.get(mStation!!.getStationId())
            }
        } catch (e: NullPointerException) {
            Log.d(TAG, "the station may be null, check if the station you tip is not station that with info")
        }
    }

    override fun observerStartPlayer(mPlayer_Url: String?, play_type: Int) {
        //StartPlayer
        mOnLinePlayerController!!.visibility = View.VISIBLE
        mStationInfo!!.visibility = View.GONE
        mService!!.StartPlay(mPlayer_Url, play_type)
    }

    override fun observerStartDNS(isStart: Boolean) {
        //do nothing..
    }

    private var mCurrent_Position = 0

    private inner class ProgramList(var mContext: Context?, mPrograms: MutableList<StationProgram?>?) : BaseAdapter() {
        var mPrograms: MutableList<StationProgram?>? = null
        fun setNewList(mPrograms: MutableList<StationProgram?>?) {
            this.mPrograms = mPrograms
        }

        override fun getCount(): Int {
            return if (mPrograms == null) 0 else mPrograms!!.size
        }

        override fun getItem(position: Int): Any? {
            return mPrograms!!.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        @SuppressLint("SetTextI18n")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var convertView = convertView
            if (convertView == null) {
                val inflater = mContext?.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                convertView = inflater.inflate(R.layout.program_item_layout, null)
            }
            val mProgramName = convertView!!.findViewById<TextView?>(R.id.program_name)
            val mProgram_Broad_Caster = convertView.findViewById<TextView?>(R.id.program_broadcaster)
            val mStartTime = convertView.findViewById<TextView?>(R.id.start_time)
            val mFinishTime = convertView.findViewById<TextView?>(R.id.finish_time)
            val mCanReplay = convertView.findViewById<ImageView?>(R.id.can_replay)
            convertView.isClickable = false
            convertView.setBackgroundColor(resources.getColor(R.color.progress_thumb_ripple_color, null))
            mProgramName.setTextColor(resources.getColor(R.color.channel_frequency_text, null))
            mCurrent_Position = 0
            if (day_info == 0) {
                convertView.isClickable = false
                convertView.setBackgroundColor(resources.getColor(R.color.main_bg_color, null))
            }
            if (day_info == 1) {
                convertView.isClickable = true
                mCurrent_Position = position
                if (mStation!!.getCurrentProgramTime() == mPrograms!!.get(position)!!.getProgramStart_time()) {
                    mProgramName.setTextColor(resources.getColor(R.color.playingcolor, null))
                }
                if (position < mStation!!.getWhichItem()) {
                    convertView.isClickable = false
                    convertView.setBackgroundColor(resources.getColor(R.color.main_bg_color, null))
                }
            }
            if (day_info == 2) {
                convertView.isClickable = true
                convertView.setBackgroundColor(resources.getColor(R.color.progress_thumb_ripple_color, null))
            }
            mProgramName.text = mPrograms!!.get(position)!!.getProgramTitle()
            mStartTime.text = "开始时间:" + mPrograms!!.get(position)!!.getProgramStart_time()
            if (mPrograms!!.get(position)!!.getBroadcaster() != null)
                mProgram_Broad_Caster.text = mPrograms!!.get(position)!!.getBroadcaster()!![0]!!.getBroadcastersName()
            mFinishTime.text = "结束时间:" + mPrograms!!.get(position)!!.getProgramEnd_Time()
            notifyDataSetChanged()
            return convertView
        }

        init {
            this.mPrograms = mPrograms
        }
    }

    /* connect to service
    * **/
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        if (service is OnLineFMPlayerService.ServiceBinder) {
            val binder = service as OnLineFMPlayerService.ServiceBinder?
            binder?.getService()
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {}
    private inner class Receiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent!!.action == OnLineFMPlayerService.UPDATE_REPLAY_UI_ACTION) {
                duration = intent.getIntExtra("Duration", 0)
            }
        }
    }

    fun openDialog(type: Int, position: Int) {
        val dialog: RadioDialogFragment = RadioDialogFragment.newInstance(type, position)!!
        dialog.show(fragmentManager, type.toString())
    }

    private fun closeDialog(type: Int) {
        Log.v(TAG, "closeDialog() - start $type")
        val dialog = fragmentManager.findFragmentByTag(type.toString()) as RadioDialogFragment
        try {
            Log.v(TAG, "removeDialog() - $type")
            //proper dismissing the dialog for checked item if this item is changed/deleted from somewhere else
            dialog.dismissAllowingStateLoss()
        } catch (e: IllegalStateException) {
            Log.d(TAG, "IllegalStateException in closeDialog:- " + e.message)
        }
    }

    companion object {
        private var mService: OnLineFMPlayerService? = null
    }
}