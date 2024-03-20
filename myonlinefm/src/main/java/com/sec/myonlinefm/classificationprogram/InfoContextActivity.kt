package com.sec.myonlinefm.classificationprogram

import android.app.AlertDialog
import android.content.*
import android.content.res.Configuration
import android.os.*
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import com.sec.myonlinefm.abstructObserver.RequestCallBack
import com.sec.myonlinefm.data.PropertyInfo
import java.util.HashMap

import com.sec.myonlinefm.R
import android.annotation.SuppressLint
import com.sec.myonlinefm.OnLineFMConnectManager
import android.widget.AdapterView.OnItemClickListener
import com.sec.myonlinefm.classificationprogram.data.DemandChannel
import com.sec.myonlinefm.classificationprogram.dataimport.DemandChannelPattern
import com.sec.myonlinefm.defineview.RefreshListView
import com.sec.myonlinefm.defineview.NoScrollBarGridView
import com.sec.myonlinefm.data.ClassificationAttributePattern
import com.sec.myonlinefm.UpdateListViewAsyncTask

@SuppressLint("DefaultLocale")
class InfoContextActivity : AppCompatActivity(), RefreshListView.OnRefreshListener, View.OnClickListener {
    private var mPlayer: OnLineFMConnectManager? = null
    private var mContext: Context? = null
    private var mCategoryID = -1
    private var mCategoryName: String? = ""
    private var mDemandChannelsList: MutableList<DemandChannel?>? = null
    private var mAttributeInfo: MutableList<PropertyInfo?>? = null
    private var mAttributeInfoMap: HashMap<Int?, MutableList<PropertyInfo.values?>?>? = null
    private val mClassifyList: MutableList<PropertyInfo.values?>? = null
    private var mCurrentClassifyAttr = 0
    private var mTabClassifyHead: LinearLayout? = null
    private var mTabClassifyTitleView: TextView? = null
    private var mTabClassifyTitleImg: ImageView? = null
    private var mTabBoutiqueHead: LinearLayout? = null
    private var mTabBoutiqueTitleView: TextView? = null
    private var mTabBoutiqueTitleImg: ImageView? = null
    private var mTabSelectHead: LinearLayout? = null
    private var mTabSelectTitleView: TextView? = null
    private var mTabSelectTitleImg: ImageView? = null
    private var mContextList: RefreshListView? = null
    private var mPageNumTextView: TextView? = null
    private var mTotalPage = 0
    private var mCurrentPage = 1
    private var mAttrId: Array<Int?>? = null
    private val mAttrName: String? = null
    private val mHandler: Handler? = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                UPDATE_CLASSIFY_SPINNER_LIST -> {
                    mTabClassifyTitleView!!!!.setText(mAttributeInfoMap!!.get(mAttributeInfo!!.get(0)!!.getPropertyInfoId())!!.get(0)!!.getvaluesname())
                    mTabClassifyHead!!.setClickable(true)
                    mTabClassifyHead!!.setOnClickListener(View.OnClickListener {
                        mTabClassifyTitleView!!.setTextColor(resources.getColor(R.color.net_fm_back_b))
                        mTabClassifyTitleImg!!.setImageResource(R.drawable.drop_up)
                        mCustomDialog(UPDATE_CLASSIFY_SPINNER_LIST)
                    })
                    mTabSelectTitleView!!.setText("筛选")
                    mTabSelectHead!!.setClickable(true)
                    mTabSelectHead!!.setOnClickListener(View.OnClickListener {
                        mTabSelectTitleView!!.setTextColor(resources.getColor(R.color.net_fm_back_b))
                        mTabSelectTitleImg!!.setImageResource(R.drawable.drop_up)
                        mCustomDialog(UPDATE_SELECT_SPINNER_LIST)
                    })
                }
                UPDATE_CONTEXT_LIST -> upDateContextList()
                EMPTY_CONTEXT_LIST -> Toast.makeText(baseContext, "数据为空，请检查网络或修改查询条件", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        mAttrId = null
        mCurrentClassifyAttr = 0
        super.onDestroy()
    }

    private fun getClassifyContextList() {
        if (mCategoryID != -1) {
            mContextList?.setOnItemClickListener(null)
            mPlayer!!.getClassifyInfoContext(mCurrentPage, mCategoryID, mAttrId, object : RequestCallBack<DemandChannelPattern?> {
                override fun onSuccess(`val`: DemandChannelPattern?) {
                    mDemandChannelsList = `val`!!.getDemandChannelsList()
                    mTotalPage = mDemandChannelsList!!.get(0)!!.getTotal() / 30
                    if (mDemandChannelsList!!.get(0)!!.getTotal() % 30 != 0) mTotalPage = mTotalPage + 1
                    mHandler!!.sendEmptyMessage(UPDATE_CONTEXT_LIST)
                }

                override fun onFail(errorMessage: String?) {
                    Log.d("bin1111.yang", "onFail : $errorMessage")
                    mHandler!!.sendEmptyMessage(EMPTY_CONTEXT_LIST)
                }
            })
        }
    }

    private var mDemandChannelAdapter: DemandChannelAdapter? = null

    //    @Override
    //    public void onRefresh() {
    //        mCurrentPage = mCurrentPage + 1;
    //        getClassifyContextList();
    //    }
    override fun onPullRefresh() {
        if (mCurrentPage > 1) {
            mCurrentPage = mCurrentPage - 1
            getClassifyContextList()
        } else {
            mContextList!!.completeRefresh()
        }
    }

    override fun onLoadingMore() {
        if (mCurrentPage < mTotalPage) {
            mContextList!!.startScrollAnim()
            mCurrentPage = mCurrentPage + 1
            getClassifyContextList()
        } else {
            mContextList!!.completeRefresh()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.getId()) {
            R.id.reset_but -> {
                var j = 1
                while (j < mAttributeInfo!!.size) {
                    mAttrId!![j] = -1
                    j++
                }
                if (builder != null) builder!!.cancel()
            }
            R.id.confirm_but -> {
                mCurrentPage = 1
                getClassifyContextList()
                if (builder != null) builder!!.cancel()
            }
        }
    }

    private inner class DemandChannelAdapter : BaseAdapter() {
        private var current_position = 0
        fun setCurrentPosition(current_position: Int) {
            this.current_position = current_position
        }

        override fun getCount(): Int {
            return mDemandChannelsList!!.size
        }

        override fun getItem(position: Int): Any? {
            return mDemandChannelsList!!.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        @SuppressLint("SetTextI18n")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var convertView = convertView
            val holder: DemandChannelViewHolder
            if (convertView == null) {
                val inflater = (mContext!!.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater)!!
                convertView = inflater.inflate(R.layout.demand_channel_item, null)
                holder = DemandChannelViewHolder()
                holder.mDemandChannelPic = convertView.findViewById(R.id.demand_channel_pic)
                holder.mDemandChannelTitle = convertView.findViewById(R.id.demand_channel_title)
                holder.mDemandChannelDescription = convertView.findViewById(R.id.demand_channel_description)
                holder.mDemandChannelPlayer = convertView.findViewById(R.id.play_count)
                holder.mDemandChannelTotal = convertView.findViewById(R.id.context_total)
                convertView.tag = holder
            } else holder = convertView.tag as DemandChannelViewHolder

//            holder.mDemandChannelPic.setImageBitmap(mPlayer.getBitmap(mDemandChannelsList.get(position).getThumbs(), 60, 60));
            val asyncTask = UpdateListViewAsyncTask(holder.mDemandChannelPic,
                    mDemandChannelsList!!.get(position)!!.getTitle(),
                    mPlayer, 60, 60)
            asyncTask.execute(mDemandChannelsList!!.get(position)!!.getThumbs())
            var isBought = ""
            if (mDemandChannelsList!!.get(position)!!.getSaleType() == DemandChannel.Companion.UN_BOUGHT_SALE_TYPE) {
                isBought = "[未购买]"
            }
            if (mDemandChannelsList!!.get(position)!!.getSaleType() == DemandChannel.Companion.BOUGHT_SALE_TYPE) {
                isBought = "[已购买]"
            }
            holder.mDemandChannelTitle!!.setText(mDemandChannelsList!!.get(position)!!.getTitle())
            holder.mDemandChannelDescription!!.setText(mDemandChannelsList!!.get(position)!!.getDescription() + isBought)
            holder.mDemandChannelPlayer!!.setText(mDemandChannelsList!!.get(position)!!.getPlayCount())
            holder.mDemandChannelTotal!!.setText(mDemandChannelsList!!.get(position)!!.getProgramCount().toString())
            return convertView
        }
    }

    internal class DemandChannelViewHolder {
        var mDemandChannelPic: ImageView? = null
        var mDemandChannelTitle: TextView? = null
        var mDemandChannelDescription: TextView? = null
        var mDemandChannelPlayer: TextView? = null
        var mDemandChannelTotal: TextView? = null
    }

    private fun upDateContextList() {
        mContextList!!.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            if (mDemandChannelsList != null) {
                val current_position = position - 1 //list has head view...
                val channel_id = mDemandChannelsList!!.get(current_position)!!.getId()
                val channel_name = mDemandChannelsList!!.get(current_position)!!.getTitle()
                val i = Intent(this@InfoContextActivity, ChannelProgramActivity::class.java)
                i.putExtra("channel_id", channel_id)
                i.putExtra("channel_name", channel_name)
                startActivity(i)
            }
        })
        mPageNumTextView!!.setText(String.format("第%d页", mCurrentPage))
        if (mDemandChannelAdapter == null) {
            mDemandChannelAdapter = DemandChannelAdapter()
            mContextList!!.setAdapter(mDemandChannelAdapter)
        } else {
            mDemandChannelAdapter!!.notifyDataSetChanged()
            mContextList!!.completeRefresh()
        }
        mContextList!!.setSelection(0)
    }

    private var builder: AlertDialog? = null
    private var topBarActionBarView: View? = null
    fun setTopPanelOnActionBar(bar_title: String?) {
        val bar = supportActionBar
        if (bar != null) {
            bar.setHomeButtonEnabled(true)
            bar.setDisplayHomeAsUpEnabled(false)
            bar.setDisplayShowHomeEnabled(false)
            bar.setDisplayShowTitleEnabled(false)
            bar.elevation = 0f
        }
        topBarActionBarView = layoutInflater.inflate(R.layout.top_bar_panel_online, null)
        val mTopBarTitle = topBarActionBarView!!.findViewById<TextView?>(R.id.top_bar_title)
        val mBackBut = topBarActionBarView!!.findViewById<ImageView?>(R.id.back_but)
        mBackBut.setOnClickListener { finish() }
        mTopBarTitle.text = bar_title
        if (bar != null) {
            bar.customView = topBarActionBarView
            bar.setDisplayHomeAsUpEnabled(true)
            bar.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            bar.show()
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

    private fun mCustomDialog(type: Int) {
        if (builder != null) builder!!.cancel()
        builder = AlertDialog.Builder(this, R.style.CreatDialog).create()
        builder!!.show()
        val factory = LayoutInflater.from(this)
        val view = factory.inflate(R.layout.custom_dialog_layout, null)
        val mTabList = view.findViewById<ListView?>(R.id.tab_dialog_list)
        val mScrollView = view.findViewById<ScrollView?>(R.id.scrollview)
        val mSelectView = view.findViewById<LinearLayout?>(R.id.select_linear_view)
        builder!!.setContentView(view)
        val dialogWindow = builder!!.getWindow()!!
        dialogWindow.setGravity(Gravity.BOTTOM) //显示在底部
        dialogWindow.setWindowAnimations(R.style.take_photo_anim)
        val m = windowManager
        val d = m.defaultDisplay // 获取屏幕宽、高用
        val p = dialogWindow.attributes // 获取对话框当前的参数值
        val mConfiguration = this.resources.configuration //获取设置的配置信息
        val ori = mConfiguration.orientation //获取屏幕方向
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            p.height = (d.width * 0.4) as Int // 高度设置为屏幕的0.5
            p.width = d.height as Int // 宽度设置为屏幕宽
            //横屏
        } else if (ori == Configuration.ORIENTATION_PORTRAIT) {
            p.height = (d.height * 0.4) as Int // 高度设置为屏幕的0.5
            p.width = d.width as Int // 宽度设置为屏幕宽
            //竖屏
        }
        builder!!.setOnCancelListener(DialogInterface.OnCancelListener {
            if (type == UPDATE_CLASSIFY_SPINNER_LIST) {
                mTabClassifyTitleView!!.setTextColor(resources.getColor(R.color.black))
                mTabClassifyTitleImg!!.setImageResource(R.drawable.drop_down)
            }
            if (type == UPDATE_SELECT_SPINNER_LIST) {
                mTabSelectTitleView!!.setTextColor(resources.getColor(R.color.black))
                mTabSelectTitleImg!!.setImageResource(R.drawable.drop_down)
            }
        })
        when (type) {
            UPDATE_CLASSIFY_SPINNER_LIST -> {
                mTabList.visibility = View.VISIBLE
                mScrollView.visibility = View.GONE
                val adapter = SpinnerListAdapter(mAttributeInfoMap!!.get(mAttributeInfo!!.get(0)!!.getPropertyInfoId()))
                mTabList.adapter = adapter
                mTabList.setOnItemClickListener { parent, view, position, id ->
                    val values = mAttributeInfoMap!!.get(mAttributeInfo!!.get(0)!!.getPropertyInfoId())!!.get(position)
                    mTabClassifyTitleView!!!!.setText(values!!.getvaluesname())
                    mCurrentClassifyAttr = mAttributeInfoMap!!.get(mAttributeInfo!!.get(0)?.getPropertyInfoId())!!.get(position)!!.getvaluesId()
                    mAttrId!![0] = mCurrentClassifyAttr
                    adapter.notifyDataSetChanged()
                    mCurrentPage = 1
                    getClassifyContextList()
                }
            }
            UPDATE_SELECT_SPINNER_LIST -> {
                mTabList.visibility = View.GONE
                mScrollView.visibility = View.VISIBLE
                var j = 1
                while (j < mAttributeInfo!!.size) {
                    val finalJ = j
                    val mClassifyList = mAttributeInfoMap!!.get(mAttributeInfo!!.get(j)!!.getPropertyInfoId())
                    val select_view = LayoutInflater.from(this).inflate(R.layout.select_spinner_view, null)
                    val select_name = select_view.findViewById<TextView?>(R.id.select_item_title)
                    val select_gird_view: NoScrollBarGridView = select_view.findViewById(R.id.select_item)
                    val classifyAdapter: ClassifyAdapter = ClassifyAdapter(this, mClassifyList, mAttrId!!.get(finalJ)!!)
                    select_gird_view.adapter = classifyAdapter
                    select_gird_view.setOnItemClickListener { parent, view, position, id ->
                        mAttrId!![finalJ] = classifyAdapter.getItem(position)!!.getvaluesId()
                        classifyAdapter.setColor(mAttrId!!.get(finalJ)!!)
                        classifyAdapter.notifyDataSetChanged()
                    }
                    select_name.text = mAttributeInfo!!.get(j)!!.getPropertyInfoname()
                    val psd = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            (classifyAdapter.getRowNum() * resources.getDimension(R.dimen.no_scroll_bar_item_height)) as Int)
                    psd.setMargins(5, 5, 5, 5)
                    select_view.layoutParams = psd
                    mSelectView.addView(select_view)
                    j++
                }
                val select_controller_view = LayoutInflater.from(this).inflate(R.layout.select_controller_layout, null)
                val mCancel = select_controller_view.findViewById<Button?>(R.id.reset_but)
                val mConfirm = select_controller_view.findViewById<Button?>(R.id.confirm_but)
                mCancel.setOnClickListener(this)
                mConfirm.setOnClickListener(this)
                val psd = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        resources.getDimension(R.dimen.no_scroll_bar_item_height) as Int)
                psd.setMargins(5, 5, 5, 5)
                select_controller_view.layoutParams = psd
                mSelectView.addView(select_controller_view)
            }
        }
        dialogWindow.attributes = p
    }

    internal class ClassifyViewHolder {
        var classify_name: TextView? = null
    }

    private inner class ClassifyAdapter internal constructor(context: Context?,
                                                             classify_list: MutableList<PropertyInfo.values?>?,
                                                             selected_position: Int) : BaseAdapter() {
        private var mClassifyList: MutableList<PropertyInfo.values?>? = null
        private var mContext: Context? = null
        private var selected_position = -1
        fun getRowNum(): Int {
            return if (mClassifyList!!.size > 0) if (mClassifyList!!.size % 3 != 0) mClassifyList!!.size / 3 + 1 else mClassifyList!!.size / 3 else 0
        }

        override fun getCount(): Int {
            return mClassifyList?.size ?: 0
        }

        override fun getItem(position: Int): PropertyInfo.values? {
            return mClassifyList?.get(position)
        }

        override fun getItemId(position: Int): Long {
            return (mClassifyList?.get(position)?.getvaluesId() ?: 0) as Long
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var convertView = convertView
            val holder: ClassifyViewHolder
            return if (mClassifyList == null) null else {
                if (convertView == null) {
                    val inflater = (mContext!!.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater)!!
                    convertView = inflater.inflate(R.layout.select_item_layout, null)
                    holder = ClassifyViewHolder()
                    holder.classify_name = convertView.findViewById(R.id.classify_name)
                    convertView.tag = holder
                } else holder = convertView.tag as ClassifyViewHolder
                convertView!!.setFocusable(false)
                holder.classify_name!!.setText(mClassifyList!![position]!!.getvaluesname())
                if (mClassifyList!![position]!!.getvaluesId() == selected_position) {
                    holder.classify_name!!.setTextColor(resources.getColor(R.color.net_fm_back_b))
                    holder.classify_name!!.setBackground(getDrawable(R.drawable.selceted_back_ground))
                } else {
                    holder.classify_name!!.setTextColor(resources.getColor(R.color.black))
                    holder.classify_name!!.setBackground(getDrawable(R.drawable.about_app_info_background))
                }
                convertView
            }
        }

        fun setColor(selected_position: Int) {
            this.selected_position = selected_position
        }

        init {
            this.selected_position = selected_position
            mClassifyList = classify_list
            mContext = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_context)
        mContext = this
        mPlayer = OnLineFMConnectManager.mMainInfoCode
        val i = intent
        mCategoryID = i.getIntExtra("category_id", -1)
        mCategoryName = i.getStringExtra("category_name")
        setTopPanelOnActionBar(mCategoryName)
        initTabView()
        if (mCategoryID != -1) {
            mPlayer!!.getClassificationAttribute(mCategoryID, object : RequestCallBack<ClassificationAttributePattern?> {
                override fun onSuccess(`val`: ClassificationAttributePattern?) {
                    mAttributeInfo = `val`!!.getInfo()
                    mAttributeInfoMap = `val`.getInfoMap()
                    mAttrId = arrayOfNulls<Int?>(mAttributeInfo!!.size)
                    for (i in mAttrId!!.indices) {
                        Log.d("bin1111.yang", "mCategoryID : $mCategoryID")
                        mAttrId!![i] = -1
                    }
                    mHandler!!.sendEmptyMessage(UPDATE_CLASSIFY_SPINNER_LIST)
                }

                override fun onFail(errorMessage: String?) {
                    Log.d("bin1111.yang", "onFail : $errorMessage")
                }
            })
            getClassifyContextList()
        }
    }

    private fun initTabView() {
        mTabClassifyHead = findViewById<View?>(R.id.tab_classify_head) as LinearLayout
        mTabClassifyTitleView = findViewById<View?>(R.id.tab_classify_title) as TextView
        mTabClassifyTitleImg = findViewById<View?>(R.id.tab_classify_title_img) as ImageView
        mTabClassifyHead!!.setClickable(false)
        mTabBoutiqueHead = findViewById<View?>(R.id.tab_boutique_head) as LinearLayout
        mTabBoutiqueTitleView = findViewById<View?>(R.id.tab_boutique_title) as TextView
        mTabBoutiqueTitleImg = findViewById<View?>(R.id.tab_boutique_title_img) as ImageView
        mTabClassifyHead!!.setClickable(false)
        mTabSelectHead = findViewById<View?>(R.id.tab_select_head) as LinearLayout
        mTabSelectTitleView = findViewById<View?>(R.id.tab_select_title) as TextView
        mTabSelectTitleImg = findViewById<View?>(R.id.tab_select_title_img) as ImageView
        mTabClassifyHead!!.setClickable(false)
        mContextList = findViewById<View?>(R.id.classify_context_list) as RefreshListView
        mContextList!!.setOnRefreshListener(this)
        mContextList!!.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            if (mDemandChannelsList != null) {
                val current_position = position - 1 //list has head view...
                val channel_id = mDemandChannelsList!!.get(current_position)!!.getId()
                val channel_name = mDemandChannelsList!!.get(current_position)!!.getTitle()
                val i = Intent(this@InfoContextActivity, ChannelProgramActivity::class.java)
                i.putExtra("channel_id", channel_id)
                i.putExtra("channel_name", channel_name)
                startActivity(i)
            }
        })
        mPageNumTextView = findViewById<View?>(R.id.page_num_text) as TextView
        mPageNumTextView!!.setText(String.format("第%d页", mCurrentPage))
    }

    private inner class SpinnerListAdapter internal constructor(private val list: MutableList<PropertyInfo.values?>?) : BaseAdapter() {
        override fun getCount(): Int {
            return list!!.size
        }

        override fun getItem(position: Int): Any? {
            return list!!.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var convertView = convertView
            val holder: ClassifyViewHolder
            if (convertView == null) {
                val inflater = (mContext!!.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater)!!
                convertView = inflater.inflate(R.layout.simple_spinner_item, null)
                holder = ClassifyViewHolder()
                holder.classify_name = convertView.findViewById(R.id.simple_spinner_text)
                convertView.tag = holder
            } else holder = convertView.tag as ClassifyViewHolder
            holder.classify_name!!.setText(list!!.get(position)!!.getvaluesname())
            if (mAttributeInfoMap!!.get(mAttributeInfo!!.get(0)!!.getPropertyInfoId())!!.get(position)!!.getvaluesId() == mCurrentClassifyAttr) holder.classify_name!!.setTextColor(resources.getColor(R.color.net_fm_back_b)) else holder.classify_name!!.setTextColor(resources.getColor(R.color.black))
            return convertView
        }
    }

    companion object {
        private const val UPDATE_CLASSIFY_SPINNER_LIST = 1
        private const val UPDATE_SELECT_SPINNER_LIST = 2
        private const val UPDATE_CONTEXT_LIST = 3
        private const val EMPTY_CONTEXT_LIST = 4
    }
}