package com.sec.myonlinefm.classificationprogram

import android.annotation.SuppressLint
import android.os.*
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import java.io.BufferedInputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import com.sec.myonlinefm.R
import android.widget.AdapterView.OnItemClickListener
import android.content.Intent
import com.sec.myonlinefm.classificationprogram.data.FavData
import android.graphics.BitmapFactory
import com.sec.myonlinefm.dbdata.MySQLHelper

class OnLineFaredActivity : AppCompatActivity() {
    private var mFavListView: ListView? = null
    private var mFavList: MutableList<FavData?>? = null
    private var db: SQLiteDatabase? = null
    private var mySQLHelper: MySQLHelper? = null
    private var favListAdapter: FavListAdapter? = null
    private val UPDATE_FAV_LIST_VIEW = 1
    private var topBarActionBarView: View? = null
    private val mHandler: Handler? = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == UPDATE_FAV_LIST_VIEW) {
                favListAdapter = FavListAdapter()
                mFavListView!!.setAdapter(favListAdapter)
                mFavListView!!.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
                    val i = Intent(this@OnLineFaredActivity, ChannelProgramActivity::class.java)
                    i.putExtra("channel_id", mFavList!!.get(position)!!.getChannelID())
                    i.putExtra("channel_name", mFavList!!.get(position)!!.getChannelName())
                    startActivity(i)
                })
            }
        }
    }
    private val GetFavData: Runnable? = Runnable {
        val cursor = db!!.query("fav_channels", null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            do {
                val favData = FavData()
                val cid = cursor.getInt(cursor.getColumnIndex("channel_id"))
                val category_id = cursor.getInt(cursor.getColumnIndex("category_id"))
                val name = cursor.getString(cursor.getColumnIndex("channel_name"))
                val url = cursor.getString(cursor.getColumnIndex("channel_them_url"))
                val podCasterName = cursor.getString(cursor.getColumnIndex("pod_caster_name"))
                Log.d("bin1111.yang", "favData : $name $cid $url")
                favData.channelID = cid
                favData.categoryID = category_id
                favData.channelName = name
                favData.podCasterName = podCasterName
                favData.channelIcon = getBitmap(url)
                mFavList!!.add(favData)
            } while (cursor.moveToNext())
        }
        cursor.close()
        if (!mFavList!!.isEmpty()) mHandler!!.sendEmptyMessage(UPDATE_FAV_LIST_VIEW)
    }

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
        setContentView(R.layout.activity_on_line_faved)
        setTopPanelOnActionBar("我的收藏")
        mFavListView = findViewById<View?>(R.id.on_line_fav_list) as ListView
        mySQLHelper = MySQLHelper.Companion.getInstances()
        db = mySQLHelper!!.getWritableDatabase()
        mFavList = ArrayList()
        val t = Thread(GetFavData)
        t.start()
    }

    private inner class FavListAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return mFavList!!.size
        }

        override fun getItem(position: Int): Any? {
            return mFavList!!.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var convertView = convertView
            if (convertView == null) {
                val inflater = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater)!!
                convertView = inflater.inflate(R.layout.fav_channel_item, null)
            }
            val mDemandChannelPic = convertView!!.findViewById<ImageView?>(R.id.demand_channel_pic)
            mDemandChannelPic.setImageBitmap(mFavList!!.get(position)!!.getChannelIcon())
            val mDemandChannelTitle = convertView!!.findViewById<TextView?>(R.id.demand_channel_title)
            mDemandChannelTitle.text = mFavList!!.get(position)!!.getChannelName()
            val mPodCasterName = convertView.findViewById<TextView?>(R.id.play_count)
            mPodCasterName.text = mFavList!!.get(position)!!.getPodCasterName()
            val mCategoryName = convertView.findViewById<TextView?>(R.id.context_total)
            if (mFavList!!.get(position)!!.getCategoryID() != 5) mCategoryName.text = "点播节目"
            if (mFavList!!.get(position)!!.getCategoryID() == 5) mCategoryName.text = "直播节目"
            val imgProgramIcon_layout = convertView.findViewById<LinearLayout?>(R.id.img_program_icon_layout)
            val imgInfoIcon = convertView.findViewById<ImageView?>(R.id.program_icon)
            imgInfoIcon.setImageResource(R.drawable.hybrid_radio_on_star)
            parent!!.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                val selectedView = mFavListView!!.getSelectedView() ?: return@OnKeyListener false
                val imgProgramIconLayout = selectedView.findViewById<LinearLayout?>(R.id.img_program_icon_layout)
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    if (event.action == KeyEvent.ACTION_UP) {
                        imgProgramIconLayout.isPressed = true
                    }
                    true
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (imgProgramIconLayout.isPressed && event.action == KeyEvent.ACTION_UP) {
                        imgProgramIconLayout.performClick()
                    } else if (!imgProgramIconLayout.isPressed && event.action == KeyEvent.ACTION_UP) {
                        mFavListView!!.performItemClick(selectedView, mFavListView!!.getSelectedItemPosition(), mFavListView!!.getSelectedItemId())
                    }
                    true
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    imgProgramIconLayout.isPressed = false
                    mFavListView!!.setActivated(true)
                    true
                } else {
                    imgProgramIconLayout.isPressed = false
                    mFavListView!!.setActivated(true)
                    false
                }
            })
            imgProgramIcon_layout.setOnClickListener {
                imgInfoIcon.setImageResource(R.drawable.hybrid_radio_off_star)
                db!!.delete("fav_channels", "channel_id = ?", arrayOf<String?>(mFavList!!.get(position)!!.getChannelID().toString()))
                Toast.makeText(this@OnLineFaredActivity, "收藏移除", Toast.LENGTH_SHORT).show()
                //                    mFavOnLineChannel.setImageResource(R.drawable.hybrid_radio_off_star);
                mFavList!!.remove(mFavList!!.get(position))
                notifyDataSetChanged()
            }
            return convertView
        }
    }

    private fun getBitmap(url: String?): Bitmap? {
        if (url == null) return null
        var bm: Bitmap? = null
        try {
            val iconUrl = URL(url)
            val conn = iconUrl.openConnection()
            val http = conn as HttpURLConnection
            val length = http.contentLength
            conn.connect()
            // 获得图像的字符流
            val `is` = conn.getInputStream()
            val bis = BufferedInputStream(`is`, length)
            bm = BitmapFactory.decodeStream(bis)
            bis.close()
            `is`.close() // 关闭流
            Log.d("bin1111.yang", "getBitmap : $url")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bm
    }
}