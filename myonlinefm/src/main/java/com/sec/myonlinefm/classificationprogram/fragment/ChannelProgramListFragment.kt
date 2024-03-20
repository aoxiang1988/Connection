package com.sec.myonlinefm.classificationprogram.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.sec.myonlinefm.OnLineFMConnectManager
import com.sec.myonlinefm.OnLineFMConnectManager.Companion.mMainInfoCode
import com.sec.myonlinefm.R
import com.sec.myonlinefm.abstructObserver.RequestCallBack
import com.sec.myonlinefm.classificationprogram.data.ChannelProgram
import com.sec.myonlinefm.classificationprogram.data.ChannelProgramPattern
import com.sec.myonlinefm.classificationprogram.ChannelProgramActivity
import com.sec.myonlinefm.defineview.RefreshListView

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the [ChannelProgramListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChannelProgramListFragment : Fragment(), View.OnClickListener {
    private var mContext: Context? = null
    private var mTotalProgramView: TextView? = null
    private var mSelectPageView: TextView? = null
    private var mOrderText: TextView? = null

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var mChannelID = -1
    private var mCurrentPage = 1
    private var mOrder = 0 //0正序, 1倒序
    private var mChannelProgramList: MutableList<ChannelProgram?>? = null
    private var mProgramAdapter: DemandProgramAdapter? = null
    private var mPlayer: OnLineFMConnectManager? = null
    private var mProgramList: RefreshListView? = null
    private var mTotalPage = 0
    private var mTotalProgram = 0
    internal var view: View? = null
    private var builder: AlertDialog? = null

    private val mHandler: Handler? =
            @SuppressLint("HandlerLeak")
            object : Handler() {
                @SuppressLint("HandlerLeak")
                override fun handleMessage(msg: Message) {
                    super.handleMessage(msg!!)
                    when (msg.what) {
                        UPDATE_PROGRAM_LIST -> {
                            if (mProgramAdapter == null) {
                                mProgramAdapter = DemandProgramAdapter()
                                mProgramList!!.setAdapter(mProgramAdapter)
                            } else {
                                mProgramAdapter!!.notifyDataSetChanged()
                                mProgramList?.completeRefresh()
                            }
                            mTotalProgramView?.setText(String.format("共%d期", mTotalProgram))
                            mProgramList?.setSelection(0)
                            mProgramList?.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
                                val channelID = mChannelProgramList?.get(position)?.getChannelID()
                                val programID: Int? = mChannelProgramList?.get(position)?.getId()
                            })
                            Log.d(TAG, "UPDATE_PROGRAM_LIST")
                        }
                    }
                }
            }

    /*override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putInt("channel_id", mChannelID)
        outState?.putInt("current_page", mCurrentPage)
        super.onSaveInstanceState(outState)
    }*/

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume : $mChannelID $mCurrentPage")
        getClassifyContextList(mChannelID)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            mChannelID = savedInstanceState.getInt("channel_id", -1)
            mCurrentPage = savedInstanceState.getInt("current_page", 1)
            Log.d(TAG, "$mChannelID $mCurrentPage")
        }
        mPlayer = mMainInfoCode
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
            mChannelID = arguments!!.getInt(ARG_CHANNEL_ID)
        }
    }

    private fun getClassifyContextList(mChannelID: Int) {
        if (mChannelID != -1) {
//            mProgramList.setOnItemClickListener(null);
            mPlayer!!.getCurrentDemandChannelPrograms(mChannelID,
                    mCurrentPage,
                    mOrder,
                    object : RequestCallBack<ChannelProgramPattern?> {
                        override fun onSuccess(`val`: ChannelProgramPattern?) {
                            mChannelProgramList = `val`!!.getChannelProgramList()
                            mTotalProgram = `val`.getTotal()
                            mTotalPage = `val`.getTotal() / 30
                            if (`val`.getTotal() % 30 != 0) mTotalPage = mTotalPage + 1
                            Log.d(TAG, "onSuccess")
                            mHandler!!.sendEmptyMessage(UPDATE_PROGRAM_LIST)
                        }

                        override fun onFail(errorMessage: String?) {
                            Log.d(TAG, "ChannelProgramListFragment onFail : $errorMessage")
                        }
                    })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView")
        view = inflater.inflate(R.layout.fragment_channel_program_list, container, false)
        mProgramList = view?.findViewById(R.id.program_list)
        mTotalProgramView = view?.findViewById(R.id.total_program)
        mSelectPageView = view?.findViewById(R.id.select)
        mOrderText = view?.findViewById(R.id.order)
        if (mOrder == 0) mOrderText?.setText("正序") else mOrderText?.setText("倒序")
        mOrderText?.setOnClickListener(this)
        mSelectPageView?.setText("选集")
        mSelectPageView?.setOnClickListener(this)
        mProgramList?.setOnRefreshListener(object : RefreshListView.OnRefreshListener {
            override fun onPullRefresh() {
                if (mCurrentPage > 1) {
                    mCurrentPage = mCurrentPage - 1
                    getClassifyContextList(mChannelID)
                } else {
                    mProgramList?.completeRefresh()
                }
            }

            override fun onLoadingMore() {
                if (mCurrentPage < mTotalPage) {
                    mProgramList?.startScrollAnim()
                    mCurrentPage = mCurrentPage + 1
                    getClassifyContextList(mChannelID)
                } else {
                    mProgramList?.completeRefresh()
                }
            }
        })
        return view
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onDestroyView() {
        view = null
        mProgramAdapter = null
        mProgramList!!.setOnRefreshListener(null)
        mProgramList = null
        mTotalProgramView = null
        mSelectPageView = null
        mOrderText = null
        val tabHost: TabHost? = ChannelProgramActivity.Companion._activity?.getTabHost()
        if (tabHost != null && !tabHost.currentTabTag.equals(TAG, ignoreCase = true)
                && tabHost.tabWidget != null) {
            tabHost.tabWidget.getChildTabViewAt(0).requestFocus()
        }
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler?.removeMessages(UPDATE_PROGRAM_LIST)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.getId()) {
                R.id.select -> mCustomDialog()
                R.id.order -> {
                    if (mOrder == 0) {
                        mOrder = 1
                        mOrderText!!.setText("倒序")
                    } else if (mOrder == 1) {
                        mOrder = 0
                        mOrderText!!.setText("正序")
                    }
                    getClassifyContextList(mChannelID)
                }
                R.id.reset_but -> if (builder != null) builder!!.cancel()
            }
        }
    }

    fun setCurrentChannelID(currentChannelID: Int) {
        mChannelID = currentChannelID
        getClassifyContextList(mChannelID)
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        open fun onFragmentInteraction(uri: Uri?)
    }

    private inner class DemandProgramAdapter : BaseAdapter() {
        private var current_position = 0
        fun setCurrentPosition(current_position: Int) {
            this.current_position = current_position
        }

        override fun getCount(): Int {
            return mChannelProgramList?.size!!
        }

        override fun getItem(position: Int): Any? {
            return mChannelProgramList?.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        @SuppressLint("SetTextI18n")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var convertView = convertView
            val holder: ViewHolder
            if (convertView == null) {
                val inflater = (mContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)!!
                convertView = inflater.inflate(R.layout.demand_program_item, null)
                holder = ViewHolder()
                holder.mTitle = convertView.findViewById(R.id.program_title_text)
                holder.mUpdateTime = convertView.findViewById(R.id.update_time_text)
                holder.mDuration = convertView.findViewById(R.id.duration)
                convertView.tag = holder
            } else holder = convertView.tag as ViewHolder
            holder.mTitle?.setText(mChannelProgramList!!.get(position)!!.getTitle())
            holder.mUpdateTime?.setText(mChannelProgramList!!.get(position)!!.getUpdateTime())
            val min = (mChannelProgramList?.get(position)!!.getDuration()!! / 60) as Int
            val sec = (mChannelProgramList?.get(position)!!.getDuration()!! % 60) as Int
            val stringMin: String
            val stringSec: String
            stringMin = if (min < 10) {
                String.format("0%s", min)
            } else {
                String.format("%s", min)
            }
            stringSec = if (sec < 10) {
                String.format("0%s", sec)
            } else {
                String.format("%s", sec)
            }
            holder.mDuration!!.setText(String.format("%s:%s", stringMin, stringSec))
            return convertView
        }
    }

    internal class ViewHolder {
        var mTitle: TextView? = null
        var mUpdateTime: TextView? = null
        var mDuration: TextView? = null
    }

    private fun mCustomDialog() {
        if (builder != null) builder!!.cancel()
        builder = AlertDialog.Builder(mContext!!, R.style.CreatDialog).create()
        builder!!.show()
        val factory = LayoutInflater.from(mContext)
        val view = factory.inflate(R.layout.select_dialog_layout, null)
        val mCancel = view.findViewById<View?>(R.id.reset_but) as Button
        mCancel.setOnClickListener(this)
        val mPageGridView = view.findViewById<View?>(R.id.page_view) as GridView
        mPageGridView.adapter = PageGridAdapter()
        builder!!.setContentView(view)
        val dialogWindow = builder!!.getWindow()
        dialogWindow?.setGravity(Gravity.BOTTOM) //显示在底部
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            dialogWindow?.setWindowAnimations(R.style.take_photo_anim)
        }
        val m = activity!!.windowManager
        val d = m.defaultDisplay // 获取屏幕宽、高用
        val p = dialogWindow?.getAttributes() // 获取对话框当前的参数值
        val mConfiguration = this.resources.configuration //获取设置的配置信息
        val ori = mConfiguration.orientation //获取屏幕方向
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            p?.height = (d.width * 0.4) as Int // 高度设置为屏幕的0.5
            p?.width = d.height as Int // 宽度设置为屏幕宽
            //横屏
        } else if (ori == Configuration.ORIENTATION_PORTRAIT) {
            p?.height = (d.height * 0.4) as Int // 高度设置为屏幕的0.5
            p?.width = d.width as Int // 宽度设置为屏幕宽
            //竖屏
        }
        builder!!.setOnCancelListener(DialogInterface.OnCancelListener { })
        dialogWindow!!.setAttributes(p)
    }

    private inner class PageGridAdapter : BaseAdapter() {
        private var convertView: View? = null
        override fun getCount(): Int {
            return mTotalPage
        }

        override fun getItem(position: Int): Any? {
            return null
        }

        override fun getItemId(position: Int): Long {
            return (position + 1).toLong()
        }

        @SuppressLint("DefaultLocale", "NewApi")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var convertView = convertView
            if (convertView == null) {
                val inflater = (mContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)!!
                convertView = inflater.inflate(R.layout.page_button, null)
                this.convertView = convertView
            }
            val mSetPageBut = convertView!!.findViewById<View?>(R.id.page_but) as Button
            if (position != mTotalPage - 1) mSetPageBut.text = String.format("%d~%d", 1 + position * 30, 30 + position * 30) else mSetPageBut.text = String.format("%d~%d", 1 + position * 30, mTotalProgram)
            mSetPageBut.setOnClickListener {
                mCurrentPage = getItemId(position) as Int
                getClassifyContextList(mChannelID)
                notifyDataSetChanged()
            }
            if (getItemId(position) as Int == mCurrentPage) {
                mSetPageBut.setTextColor(mContext!!.getResources().getColor(R.color.net_fm_back_b))
                mSetPageBut.background = mContext!!.getDrawable(R.drawable.selceted_back_ground)
            } else {
                mSetPageBut.setTextColor(mContext!!.getResources().getColor(R.color.black))
                mSetPageBut.background = mContext!!.getDrawable(R.drawable.about_app_info_background)
            }
            return convertView
        }
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        var TAG: String? = "ChannelProgramListFragment"
        private val ARG_PARAM1: String? = "param1"
        private val ARG_PARAM2: String? = "param2"
        private val ARG_CHANNEL_ID: String? = "arg_channel_id"
        private const val UPDATE_PROGRAM_LIST = 1

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChannelProgramListFragment.
         */
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChannelProgramListFragment.
         */
        
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): ChannelProgramListFragment? {
            val fragment = ChannelProgramListFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }

        fun newInstance(channel_id: Int): ChannelProgramListFragment? {
            val fragment = ChannelProgramListFragment()
            val args = Bundle()
            args.putInt(ARG_CHANNEL_ID, channel_id)
            fragment.arguments = args
            return fragment
        }
    }
}