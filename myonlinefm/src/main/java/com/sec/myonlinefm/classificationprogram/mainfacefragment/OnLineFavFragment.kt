package com.sec.myonlinefm.classificationprogram.mainfacefragment

import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import com.sec.myonlinefm.R
import android.annotation.SuppressLint
import android.widget.AdapterView.OnItemClickListener

import com.sec.myonlinefm.classificationprogram.data.FavData
import android.graphics.BitmapFactory

import android.content.*

import android.os.*
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.sec.myonlinefm.classificationprogram.ChannelProgramActivity
import com.sec.myonlinefm.dbdata.MySQLHelper
import java.io.BufferedInputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the [OnLineFavFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OnLineFavFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    internal var view: View? = null
    private var mFavListView: ListView? = null
    private var mFavList: MutableList<FavData?>? = null
    private var db: SQLiteDatabase? = null
    private var mySQLHelper: MySQLHelper? = null
    private var favListAdapter: FavListAdapter? = null
    private val UPDATE_FAV_LIST_VIEW = 1
    private val topBarActionBarView: View? = null
    private var mContext: Context? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_on_line_fav, container, false)
        mFavListView = view!!.findViewById<View?>(R.id.on_line_fav_list) as ListView
        mySQLHelper = MySQLHelper.Companion.getInstances()
        db = mySQLHelper!!.getWritableDatabase()
        mFavList = ArrayList()
        val t = Thread(GetFavData)
        t.start()
        return view
    }

    override fun onDestroyView() {
        view = null
        super.onDestroyView()
    }

    override fun onAttach(context: Context?) {
        mContext = context
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    @SuppressLint("HandlerLeak")
    private val mHandler: Handler? = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == UPDATE_FAV_LIST_VIEW) {
                favListAdapter = FavListAdapter()
                mFavListView!!.setAdapter(favListAdapter)
                mFavListView!!.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
                    val i = Intent(mContext, ChannelProgramActivity::class.java)
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
        if (!mFavList!!.isEmpty())
            mHandler!!.sendEmptyMessage(UPDATE_FAV_LIST_VIEW)
    }

    internal class ViewHolder {
        var mDemandChannelPic: ImageView? = null
        var mDemandChannelTitle: TextView? = null
        var mPodCasterName: TextView? = null
        var mCategoryName: TextView? = null
        var imgProgramIcon_layout: LinearLayout? = null
        var imgInfoIcon: ImageView? = null
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
            val holder: ViewHolder
            if (convertView == null) {
                val inflater = (mContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)!!
                convertView = inflater.inflate(R.layout.fav_channel_item, null)
                holder = ViewHolder()
                holder.mDemandChannelPic = convertView.findViewById(R.id.demand_channel_pic)
                holder.mDemandChannelTitle = convertView.findViewById(R.id.demand_channel_title)
                holder.mPodCasterName = convertView.findViewById(R.id.play_count)
                holder.imgProgramIcon_layout = convertView.findViewById(R.id.img_program_icon_layout)
                holder.mCategoryName = convertView.findViewById(R.id.context_total)
                holder.imgInfoIcon = convertView.findViewById(R.id.program_icon)
                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder //取出ViewHolder对象
            }
            holder.mDemandChannelPic!!.setImageBitmap(mFavList!!.get(position)!!.getChannelIcon())
            holder.mDemandChannelTitle!!.setText(mFavList!!.get(position)!!.getChannelName())
            holder.mPodCasterName!!.setText(mFavList!!.get(position)!!.getPodCasterName())
            if (mFavList!!.get(position)!!.getCategoryID() != 5)
                holder.mCategoryName!!.setText("点播节目")
            if (mFavList!!.get(position)!!.getCategoryID() == 5)
                holder.mCategoryName!!.setText("直播节目")
            holder.imgInfoIcon!!.setImageResource(R.drawable.hybrid_radio_on_star)
            parent!!.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                val selectedView = mFavListView!!.getSelectedView() ?: return@OnKeyListener false
                holder.imgProgramIcon_layout = selectedView.findViewById(R.id.img_program_icon_layout)
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    if (event.action == KeyEvent.ACTION_UP) {
                        holder.imgProgramIcon_layout!!.setPressed(true)
                    }
                    true
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (holder.imgProgramIcon_layout!!.isPressed() && event.action == KeyEvent.ACTION_UP) {
                        holder.imgProgramIcon_layout!!.performClick()
                    } else if (!holder.imgProgramIcon_layout!!.isPressed() && event.action == KeyEvent.ACTION_UP) {
                        mFavListView!!.performItemClick(selectedView,
                                mFavListView!!.getSelectedItemPosition(),
                                mFavListView!!.getSelectedItemId()
                        )
                    }
                    true
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    holder.imgProgramIcon_layout!!.setPressed(false)
                    mFavListView!!.setActivated(true)
                    true
                } else {
                    holder.imgProgramIcon_layout!!.setPressed(false)
                    mFavListView!!.setActivated(true)
                    false
                }
            })
            holder.imgProgramIcon_layout!!.setOnClickListener(View.OnClickListener {
                holder.imgInfoIcon!!.setImageResource(R.drawable.hybrid_radio_off_star)
                db!!.delete(
                        "fav_channels",
                        "channel_id = ?",
                        arrayOf<String?>(mFavList!!.get(position)!!.getChannelID().toString())
                )
                Toast.makeText(mContext, "收藏移除", Toast.LENGTH_SHORT).show()
                //                    mFavOnLineChannel.setImageResource(R.drawable.hybrid_radio_off_star);
                mFavList!!.remove(mFavList!!.get(position))
                notifyDataSetChanged()
            })
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

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1: String? = "param1"
        private val ARG_PARAM2: String? = "param2"
        val TAG: String? = "OnLineFavFragment"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OnLineFavFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): Fragment? {
            val fragment = OnLineFavFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}