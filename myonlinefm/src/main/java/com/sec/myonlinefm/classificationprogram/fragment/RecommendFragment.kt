package com.sec.myonlinefm.classificationprogram.fragment

import com.sec.myonlinefm.OnLineFMConnectManager.Companion.mMainInfoCode
import android.graphics.Bitmap
import com.sec.myonlinefm.R
import com.sec.myonlinefm.OnLineFMConnectManager
import android.widget.AdapterView.OnItemClickListener

import com.sec.myonlinefm.classificationprogram.data.ObservableController
import com.sec.myonlinefm.classificationprogram.data.ClassifyRecommend
import com.sec.myonlinefm.classificationprogram.data.ClassifyRecommendPattern
import com.sec.myonlinefm.classificationprogram.data.RequestProgramClassify
import com.sec.myonlinefm.classificationprogram.data.RequestProgramClassifyListPattern

import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener

import android.content.*

import android.os.*
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.sec.myonlinefm.abstructObserver.RequestCallBack
import com.sec.myonlinefm.classificationprogram.ChannelProgramActivity
import com.sec.myonlinefm.classificationprogram.InfoContextActivity
import java.lang.Exception
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the [RecommendFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecommendFragment : Fragment(), Observer {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var mViewPager: ViewPager? = null
    private var mPlayer: OnLineFMConnectManager? = null
    private var mListView: ListView? = null
    private var mRecommendAdapter: RecommendAdapter? = null
    private var mPagerAdapter: PagerAdapter? = null
    private var naviPoint: LinearLayout? = null
    private var mContext: Context? = null
    private var lastPos = 0
    private var mRequestProgramClassifyList: MutableList<RequestProgramClassify?>? = null
    private var imageViews: MutableList<ImageView?>? = null
    private val mObservable: ObservableController? = ObservableController.Companion.getInstance()
    private val mHandler: Handler? = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg!!)
            if (msg.what == UPDATE_LIST_VIEW) {
                mRequestProgramClassifyList = RequestProgramClassifyListPattern.Companion.getInstance()!!.getRequestProgramClassifyList()
                mRecommendAdapter!!.setList(mRequestProgramClassifyList)
            }
            if (msg.what == UPDATE_ITEM_TITLE) {
                mRecommendAdapter!!.updateTitleView()
            }
            if (msg.what == UPDATE_ITEM_THUMB) {
                mRecommendAdapter!!.updateThumbView()
            }
            if (msg.what == UPDATE_HEADER) {
                updateImageView()
            }
        }
    }

    private fun initRecommendData(`val`: RequestProgramClassifyListPattern?) {
        for (i in 0..4) {
            val item = `val`!!.getRequestProgramClassifyList()!![i]
            mPlayer!!.getFiveRecmThumb(object : RequestCallBack<ClassifyRecommend?> {
                override fun onSuccess(`val`: ClassifyRecommend?) {
                    initHeaderThumb(i, `val`)
                }

                override fun onFail(errorMessage: String?) {
                    Log.d("gaolin", "onFail : $errorMessage")
                }
            }, item!!.sectionId)
        }
        for (item in `val`!!.getRequestProgramClassifyList()!!) {
            mPlayer!!.getRequestRecmmendProgram(object : RequestCallBack<ClassifyRecommend?> {
                override fun onSuccess(`val`: ClassifyRecommend?) {
                    val map: ClassifyRecommendPattern = ClassifyRecommendPattern.Companion.getInstance()!!
                    map.addRecommendMap(`val`!!.getCategoryID(), `val`)
                    ObservableController.Companion.getInstance()!!.notifyObservers(ObservableController.Companion.UPDATETITLE)
                    initRecommendThumbs(`val`)
                }

                override fun onFail(errorMessage: String?) {
                    Log.d("gaolin", "onFail : $errorMessage")
                }
            }, item!!.id)
        }
    }

    private fun initHeaderThumb(index: Int, item: ClassifyRecommend?) {
        val url = item!!.getThumbUrl(0)
        if (url != null) {
            mPlayer!!.getRecommendThumb(object : RequestCallBack<Bitmap?> {
                override fun onSuccess(`val`: Bitmap?) {
                    ClassifyRecommendPattern.Companion.scrollBitmap!![index] = `val`
                    ObservableController.Companion.getInstance()!!.notifyObservers(ObservableController.Companion.UPDATEHEADER)
                }

                override fun onFail(errorMessage: String?) {
                    Log.d("gaolin", "onFail : $errorMessage")
                }
            }, url)
        }
    }

    private fun initRecommendThumbs(item: ClassifyRecommend?) {
        for (i in 0..2) {
            val url = item!!.getThumbUrl(i)
            if (url != null) {
                mPlayer!!.getRecommendThumb(object : RequestCallBack<Bitmap?> {
                    override fun onSuccess(`val`: Bitmap?) {
                        item.setThumb(i, `val`!!)
                        val map: ClassifyRecommendPattern = ClassifyRecommendPattern.Companion.getInstance()!!
                        ObservableController.Companion.getInstance()!!.notifyObservers(ObservableController.Companion.UPDATETHUMB)
                    }

                    override fun onFail(errorMessage: String?) {
                        Log.d("gaolin", "onFail : $errorMessage")
                    }
                }, url)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
        mPlayer = mMainInfoCode
        mObservable!!.addObserver(this)
        mPlayer!!.getRequestProgramClassify(object : RequestCallBack<RequestProgramClassifyListPattern?> {
            override fun onSuccess(`val`: RequestProgramClassifyListPattern?) {
                Log.d("gaolin", "onSuccess : ")
                ObservableController.Companion.getInstance()!!.notifyObservers(ObservableController.Companion.REFRESH)
                initRecommendData(`val`)
            }

            override fun onFail(errorMessage: String?) {
                Log.d("gaolin", "onFail : $errorMessage")
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_recommend, container, false)
        mListView = view.findViewById(R.id.recommed_list)
        mRecommendAdapter = RecommendAdapter(mContext)
        mListView!!.setAdapter(mRecommendAdapter)
        mRecommendAdapter!!.setListView(mListView)
        mRequestProgramClassifyList = RequestProgramClassifyListPattern.Companion.getInstance()!!.getRequestProgramClassifyList()
        if (mRequestProgramClassifyList != null) {
            mHandler!!.sendEmptyMessage(UPDATE_LIST_VIEW)
        }
        val headView = LayoutInflater.from(mContext).inflate(R.layout.fragment_header, null)
        mViewPager = headView.findViewById<View?>(R.id.top_vp) as ViewPager
        naviPoint = headView.findViewById<View?>(R.id.navi_point) as LinearLayout
        initImageViews()
        initViewPager()
        mHandler!!.postDelayed(TimerRunnable(), 2000)
        mListView!!.addHeaderView(headView)
        mListView!!.setOnItemClickListener(OnItemClickListener { parent, view, position, id -> Toast.makeText(mContext, "gaolin gaolin", Toast.LENGTH_SHORT).show() })
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context
    }

    override fun onDetach() {
        super.onDetach()
    }

    private inner class RecommendAdapter internal constructor(context: Context?) : BaseAdapter() {
        private var mRequestProgramClassifyList: MutableList<RequestProgramClassify?>? = null
        private var mContext: Context? = null
        private var convertView: View? = null
        private var lv: ListView? = null
        private val indexMap: HashMap<Int?, Int?>? = HashMap()
        fun setList(request_program_classify_list: MutableList<RequestProgramClassify?>?) {
            mRequestProgramClassifyList = request_program_classify_list
            notifyDataSetChanged()
        }

        fun setListView(view: ListView?) {
            lv = view
        }

        override fun getCount(): Int {
            return if (mRequestProgramClassifyList == null) 0 else mRequestProgramClassifyList!!.size
        }

        override fun getItem(position: Int): Any? {
            return if (mRequestProgramClassifyList == null) null else mRequestProgramClassifyList!!.get(position)
        }

        override fun getItemId(position: Int): Long {
            return if (mRequestProgramClassifyList == null) 0 else position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var convertView = convertView
            return if (mRequestProgramClassifyList == null) null else {
                val category_id = mRequestProgramClassifyList!!.get(position)!!.getId()
                val holder: ViewHolder?
                val categoryTag: CategoryTag
                if (convertView == null) {
                    val inflater = (mContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)!!
                    convertView = inflater.inflate(R.layout.recommend_item_layout, null)
                    this.convertView = convertView
                    holder = ViewHolder()
                    holder.navigate = convertView.findViewById<View?>(R.id.navigate) as LinearLayout
                    holder.recommendClass = convertView.findViewById<View?>(R.id.recmmend_name) as TextView
                    holder.naviIcon = convertView.findViewById<View?>(R.id.navigate_bar) as ImageView
                    holder.firstPart = convertView.findViewById<View?>(R.id.rLay1) as LinearLayout
                    holder.firstIm = convertView.findViewById<View?>(R.id.recmmend1) as ImageView
                    holder.firstTx = convertView.findViewById<View?>(R.id.recmmend1_text) as TextView
                    holder.secondPart = convertView.findViewById<View?>(R.id.rLay2) as LinearLayout
                    holder.secondIm = convertView.findViewById<View?>(R.id.recmmend2) as ImageView
                    holder.secondTx = convertView.findViewById<View?>(R.id.recmmend2_text) as TextView
                    holder.thirdPart = convertView.findViewById<View?>(R.id.rLay3) as LinearLayout
                    holder.thirdIm = convertView.findViewById<View?>(R.id.recmmend3) as ImageView
                    holder.thirdTx = convertView.findViewById<View?>(R.id.recmmend3_text) as TextView
                    holder.empty = convertView.findViewById(R.id.empty_view) as View
                    convertView.tag = holder
                    categoryTag = CategoryTag(category_id)
                    holder.navigate!!.setTag(categoryTag)
                } else {
                    holder = convertView.tag as ViewHolder
                    if (holder == null) {
                        return null
                    }
                }
                holder.navigate!!.setOnClickListener(View.OnClickListener {
                    val i = Intent()
                    i.putExtra("category_id", mRequestProgramClassifyList!!.get(position)!!.getId())
                    i.putExtra("category_name", mRequestProgramClassifyList!!.get(position)!!.getName())
                    i.setClass(mContext!!, InfoContextActivity::class.java)
                    startActivity(i)
                })
                holder.recommendClass!!.setText(mRequestProgramClassifyList!!.get(position)!!.getName())
                val item: ClassifyRecommend = ClassifyRecommendPattern.Companion.classifyRecommendMap!!.get(category_id)!!
                if (item != null && item.getThumb(0) != null) {
                    holder.firstIm!!.setImageBitmap(item.getThumb(0))
                    var idTag1 = holder.firstIm!!.getTag() as RadioIdTag
                    if (idTag1 == null) {
                        idTag1 = RadioIdTag(item.getId(0))
                        holder.firstIm!!.setTag(idTag1)
                    }
                }
                if (item != null && item.getTitle(0) != null) {
                    holder.firstTx!!.setText(item.getTitle(0))
                    var idTag = holder.firstPart!!.getTag() as RadioIdTag
                    if (idTag == null) {
                        idTag = RadioIdTag(item.getId(0))
                        holder.firstPart!!.setTag(idTag)
                    }
                }
                holder.firstPart!!.setOnClickListener(View.OnClickListener {
                    val channel_id = item.getId(0)
                    val channel_name = item.getTitle(0)
                    val i = Intent(mContext, ChannelProgramActivity::class.java)
                    i.putExtra("channel_id", channel_id)
                    i.putExtra("channel_name", channel_name)
                    startActivity(i)
                })
                if (item != null && item.getThumb(1) != null) {
                    holder.secondIm!!.setImageBitmap(item.getThumb(1))
                    var idTag2 = holder.secondIm!!.getTag() as RadioIdTag
                    if (idTag2 == null) {
                        idTag2 = RadioIdTag(item.getId(1))
                        holder.secondIm!!.setTag(idTag2)
                    }
                }
                if (item != null && item.getTitle(1) != null) {
                    holder.secondTx!!.setText(item.getTitle(1))
                    var idTag = holder.secondPart!!.getTag() as RadioIdTag
                    if (idTag == null) {
                        idTag = RadioIdTag(item.getId(1))
                        holder.secondPart!!.setTag(idTag)
                    }
                }
                holder.secondPart!!.setOnClickListener(View.OnClickListener {
                    val channel_id = item.getId(1)
                    val channel_name = item.getTitle(1)
                    val i = Intent(mContext, ChannelProgramActivity::class.java)
                    i.putExtra("channel_id", channel_id)
                    i.putExtra("channel_name", channel_name)
                    startActivity(i)
                })
                if (item != null && item.getThumb(2) != null) {
                    holder.thirdIm!!.setImageBitmap(item.getThumb(2))
                    var idTag3 = holder.thirdIm!!.getTag() as RadioIdTag
                    if (idTag3 == null) {
                        idTag3 = RadioIdTag(item.getId(2))
                        holder.thirdIm!!.setTag(idTag3)
                    }
                }
                if (item != null && item.getTitle(2) != null) {
                    holder.thirdTx!!.setText(item.getTitle(2))
                    var idTag = holder.thirdPart!!.getTag() as RadioIdTag
                    if (idTag == null) {
                        idTag = RadioIdTag(item.getId(2))
                        holder.thirdPart!!.setTag(idTag)
                    }
                }
                holder.thirdPart!!.setOnClickListener(View.OnClickListener {
                    val channel_id = item.getId(2)
                    val channel_name = item.getTitle(2)
                    val i = Intent(mContext, ChannelProgramActivity::class.java)
                    i.putExtra("channel_id", channel_id)
                    i.putExtra("channel_name", channel_name)
                    startActivity(i)
                })
                notifyDataSetChanged()
                convertView
            }
        }

        fun updateTitleView() {
            val first = lv!!.getFirstVisiblePosition()
            val last = lv!!.getLastVisiblePosition()
            val keySet: MutableSet<Int?> = ClassifyRecommendPattern.Companion.classifyRecommendMap!!.keys
            for (i in first until last) {
                val view = lv!!.getChildAt(i) ?: continue
                val holder = view.tag as ViewHolder
                        ?: continue
                val tag = holder.navigate!!.getTag() as CategoryTag ?: continue
                val idTag = holder.firstPart!!.getTag() as RadioIdTag
                if (idTag != null) {
                    continue
                }
                if (keySet.contains(tag.mCategoryId)) {
                    holder.firstTx!!.setText(ClassifyRecommendPattern.Companion.classifyRecommendMap!!.get(tag.mCategoryId)!!.getTitle(0))
                    val firstTag = RadioIdTag(ClassifyRecommendPattern.Companion.classifyRecommendMap!!.get(tag.mCategoryId)!!.getId(0))
                    holder.firstPart!!.setTag(firstTag)
                    holder.secondTx!!.setText(ClassifyRecommendPattern.Companion.classifyRecommendMap!!.get(tag.mCategoryId)!!.getTitle(1))
                    val secondTag = RadioIdTag(ClassifyRecommendPattern.Companion.classifyRecommendMap!!.get(tag.mCategoryId)!!.getId(1))
                    holder.secondPart!!.setTag(secondTag)
                    holder.secondPart!!.setOnClickListener(View.OnClickListener {
                        val channel_id: Int = ClassifyRecommendPattern.Companion.classifyRecommendMap!!.get(tag.mCategoryId)!!.getId(1)
                        val channel_name: String = ClassifyRecommendPattern.Companion.classifyRecommendMap!!.get(tag.mCategoryId)!!.getTitle(1)!!
                        val i = Intent(mContext, ChannelProgramActivity::class.java)
                        i.putExtra("channel_id", channel_id)
                        i.putExtra("channel_name", channel_name)
                        startActivity(i)
                    })
                    holder.thirdTx!!.setText(ClassifyRecommendPattern.Companion.classifyRecommendMap!!.get(tag.mCategoryId)!!.getTitle(2))
                    val thirdTag = RadioIdTag(ClassifyRecommendPattern.Companion.classifyRecommendMap!!.get(tag.mCategoryId)!!.getId(2))
                    holder.thirdPart!!.setTag(thirdTag)
                    holder.secondPart!!.setOnClickListener(View.OnClickListener {
                        val channel_id: Int = ClassifyRecommendPattern.Companion.classifyRecommendMap!!.get(tag.mCategoryId)!!.getId(1)
                        val channel_name: String = ClassifyRecommendPattern.Companion.classifyRecommendMap!!!!.get(tag.mCategoryId)!!.getTitle(1)!!
                        val i = Intent(mContext, ChannelProgramActivity::class.java)
                        i.putExtra("channel_id", channel_id)
                        i.putExtra("channel_name", channel_name)
                        startActivity(i)
                    })
                }
            }
        }

        fun updateThumbView() {
            val first = lv!!.getFirstVisiblePosition()
            val last = lv!!.getLastVisiblePosition()
            val keySet: MutableSet<Int?> = ClassifyRecommendPattern.Companion.classifyRecommendMap!!.keys
            for (i in first until last) {
                val view = lv!!.getChildAt(i) ?: continue
                val holder = view.tag as ViewHolder
                        ?: continue
                val tag = holder.navigate!!.getTag() as CategoryTag ?: continue
                if (keySet.contains(tag.mCategoryId)) {
                    val idTag1 = holder.firstIm!!.getTag() as RadioIdTag
                    if (idTag1 == null) {
                        notifyDataSetChanged()
                    }
                    val idTag2 = holder.secondIm!!.getTag() as RadioIdTag
                    if (idTag2 == null) {
                        notifyDataSetChanged()
                    }
                    val idTag3 = holder.thirdIm!!.getTag() as RadioIdTag
                    if (idTag3 == null) {
                        notifyDataSetChanged()
                    }
                }
            }
        }

        init {
            mContext = context
        }
    }

    private inner class ViewHolder {
        var navigate: LinearLayout? = null
        var recommendClass: TextView? = null
        var naviIcon: ImageView? = null
        var firstPart: LinearLayout? = null
        var firstIm: ImageView? = null
        var firstTx: TextView? = null
        var secondPart: LinearLayout? = null
        var secondIm: ImageView? = null
        var secondTx: TextView? = null
        var thirdPart: LinearLayout? = null
        var thirdIm: ImageView? = null
        var thirdTx: TextView? = null
        var empty: View? = null
    }

    private inner class CategoryTag internal constructor(var mCategoryId: Int)
    private inner class RadioIdTag internal constructor(var mId: Int)

    private fun initImageViews() {
        imageViews = ArrayList()
        val imageResIds = intArrayOf(R.drawable.fm_radio_now_playing_01,
                R.drawable.fm_radio_now_playing_02,
                R.drawable.fm_radio_now_playing_03,
                R.drawable.hybrid_radio_info_no_image,
                R.drawable.radio_about)
        var imageView: ImageView
        var pointView: View
        for (i in imageResIds.indices) {
            imageView = ImageView(mContext)
            if (ClassifyRecommendPattern.Companion.scrollBitmap!!.get(i) != null) {
                imageView.setImageBitmap(ClassifyRecommendPattern.Companion.scrollBitmap!!.get(i))
            } else {
                imageView.setImageResource(imageResIds[i])
            }
            (imageViews as ArrayList<ImageView?>).add(imageView)
            //add naviPoint
            pointView = View(mContext)
            pointView.setBackgroundResource(R.drawable.point_selector)
            val layoutParams = LinearLayout.LayoutParams(20, 20)
            if (i != 0) {
                layoutParams.leftMargin = 20
            }
            pointView.isEnabled = false
            naviPoint!!.addView(pointView, layoutParams)
        }
    }

    private inner class TimerRunnable : Runnable {
        override fun run() {
            val curItem = mViewPager!!.getCurrentItem()
            mViewPager!!.setCurrentItem(curItem + 1)
            mHandler?.postDelayed(this, 2000)
        }
    }

    private fun initViewPager() {
        mPagerAdapter = MyPagerAdapter()
        mViewPager!!.setAdapter(mPagerAdapter)
        mViewPager!!.setCurrentItem(imageViews!!.size * 1000)
        mViewPager!!.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                changePoints(position % imageViews!!.size)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        lastPos = 0
        naviPoint!!.getChildAt(0).isEnabled = true
    }

    private inner class MyPagerAdapter : PagerAdapter() {
        override fun getCount(): Int {
            return 10000
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun getItemPosition(`object`: Any): Int {
            return super.getItemPosition(`object`)
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View?)
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            try {
                container.addView(imageViews!!.get(position % imageViews!!.size))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return imageViews!!.get(position % imageViews!!.size)!!
        }
    }

    fun changePoints(newPosition: Int) {
        naviPoint!!.getChildAt(lastPos).isEnabled = false
        naviPoint!!.getChildAt(newPosition).isEnabled = true
        lastPos = newPosition
    }

    fun updateImageView() {
        for (i in 0..4) {
            if (ClassifyRecommendPattern.Companion.scrollBitmap!!.get(i) != null) {
                imageViews!!.get(i)!!.setImageBitmap(ClassifyRecommendPattern.Companion.scrollBitmap!!.get(i))
            }
        }
    }

    override fun update(o: Observable?, arg: Any?) {
        val event = arg as Int
        Log.d("gaolin", "event = $event")
        when (event) {
            1 -> mHandler!!.sendEmptyMessage(UPDATE_LIST_VIEW)
            2 -> mHandler!!.sendEmptyMessage(UPDATE_ITEM_TITLE)
            3 -> mHandler!!.sendEmptyMessage(UPDATE_ITEM_THUMB)
            4 -> mHandler!!.sendEmptyMessage(UPDATE_HEADER)
        }
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        val TAG: String? = "RecommendFragment"
        private val ARG_PARAM1: String? = "param1"
        private val ARG_PARAM2: String? = "param2"

        // add by gaolin 4/19
        private const val UPDATE_LIST_VIEW = 1
        private const val UPDATE_ITEM_TITLE = 2
        private const val UPDATE_ITEM_THUMB = 3
        private const val UPDATE_HEADER = 4

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RecommendFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): RecommendFragment? {
            val fragment = RecommendFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}