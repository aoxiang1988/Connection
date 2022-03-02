package com.sec.myonlinefm.classificationprogram.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.sec.myonlinefm.OnLineFMConnectManager
import com.sec.myonlinefm.OnLineFMConnectManager.Companion.mMainInfoCode
import com.sec.myonlinefm.R
import com.sec.myonlinefm.abstructObserver.RequestCallBack
import com.sec.myonlinefm.classificationprogram.data.DemandChannel
import com.sec.myonlinefm.classificationprogram.dataimport.DemandChannelPattern
import com.sec.myonlinefm.classificationprogram.ChannelProgramActivity
import com.sec.myonlinefm.defineview.RefreshListView
import com.sec.myonlinefm.UpdateListViewAsyncTask


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the [ChannelRecommendListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChannelRecommendListFragment : Fragment() {
    private var mContext: Context? = null
    private var mPlayer: OnLineFMConnectManager? = null

    // TODO: Rename and change types of parameters
    internal var view: View? = null
    private var mRecommendListView: RefreshListView? = null
    private var mDemandChannelsList: MutableList<DemandChannel?>? = null
    private var mDemandChannelAdapter: DemandChannelAdapter? = null
    private var mCategoryID = -1
    private var mTotalPage = 0
    private var mCurrentPage = 1
    private val mAttrId: Array<Int?>? = null

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("category_id", mCategoryID)
        outState.putInt("current_page", mCurrentPage)
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume : $mCategoryID $mCurrentPage")
        getClassifyContextList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPlayer = mMainInfoCode
        if (savedInstanceState != null) {
            mCategoryID = savedInstanceState.getInt("category_id", -1)
            mCurrentPage = savedInstanceState.getInt("current_page", 1)
            Log.d(TAG, "$mCategoryID $mCurrentPage")
        }
        if (arguments != null) {
            mCategoryID = arguments!!.getInt(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_channel_recommend_list, container, false)
        mRecommendListView = view?.findViewById(R.id.recommend_list)
        mRecommendListView?.setOnRefreshListener(object : RefreshListView.OnRefreshListener {
            override fun onPullRefresh() {
                if (mCurrentPage > 1) {
                    mCurrentPage = mCurrentPage - 1
                    getClassifyContextList()
                } else {
                    mRecommendListView?.completeRefresh()
                }
            }

            override fun onLoadingMore() {
                if (mCurrentPage < mTotalPage) {
                    mRecommendListView?.startScrollAnim()
                    mCurrentPage = mCurrentPage + 1
                    getClassifyContextList()
                } else {
                    mRecommendListView?.completeRefresh()
                }
            }
        })
        mRecommendListView?.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            if (mDemandChannelsList != null) {
                val current_position = position - 1 //list has head view...
                val channel_id = mDemandChannelsList!!.get(current_position)!!.getId()
                val channel_name = mDemandChannelsList!!.get(current_position)!!.getTitle()
                ChannelProgramActivity.Companion._activity!!.setUpdateCurrentDemandChannelInfo(channel_name, channel_id, true)
            }
        })
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context
    }

    override fun onDestroyView() {
        view = null
        mRecommendListView!!.completeRefresh()
        mRecommendListView = null
        mDemandChannelAdapter = null
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
    }

    var mHandler: Handler? = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                UPDATE_CONTEXT_LIST -> upDateContextList()
                EMPTY_CONTEXT_LIST -> Toast.makeText(mContext, "数据为空，请检查网络或修改查询条件", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun upDateContextList() {
        if (isDetached) return
        if (mDemandChannelAdapter == null) {
            mDemandChannelAdapter = DemandChannelAdapter()
            mRecommendListView!!.setAdapter(mDemandChannelAdapter)
        } else {
            mDemandChannelAdapter!!.notifyDataSetChanged()
            mRecommendListView!!.completeRefresh()
        }
        mRecommendListView!!.setSelection(0)
    }

    private fun getClassifyContextList() {
        if (mCategoryID != -1) {
            mPlayer!!.getClassifyInfoContext(mCurrentPage, mCategoryID,
                    mAttrId,
                    object : RequestCallBack<DemandChannelPattern?> {
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

    fun setCurrentCategoryID(currentCategoryID: Int) {
        mCategoryID = currentCategoryID
        getClassifyContextList()
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
            val holder: ViewHolder
            if (convertView == null) {
                val inflater = (mContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)!!
                convertView = inflater.inflate(R.layout.demand_channel_item, null)
                holder = ViewHolder()
                holder.mDemandChannelPic = convertView.findViewById(R.id.demand_channel_pic)
                holder.mDemandChannelTitle = convertView.findViewById(R.id.demand_channel_title)
                holder.mDemandChannelDescription = convertView.findViewById(R.id.demand_channel_description)
                holder.mDemandChannelPlayer = convertView.findViewById(R.id.play_count)
                holder.mDemandChannelTotal = convertView.findViewById(R.id.context_total)
                convertView.tag = holder
            } else holder = convertView.tag as ViewHolder
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

    internal class ViewHolder {
        var mDemandChannelPic: ImageView? = null
        var mDemandChannelTitle: TextView? = null
        var mDemandChannelDescription: TextView? = null
        var mDemandChannelPlayer: TextView? = null
        var mDemandChannelTotal: TextView? = null
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1: String? = "param1"
        private val ARG_PARAM2: String? = "param2"
        var TAG: String? = "ChannelRecommendListFragment"
        private const val UPDATE_CONTEXT_LIST = 1
        private const val EMPTY_CONTEXT_LIST = 2

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment ChannelRecommendListFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(category_id: Int): ChannelRecommendListFragment? {
            val fragment = ChannelRecommendListFragment()
            val args = Bundle()
            args.putInt(ARG_PARAM1, category_id)
            fragment.arguments = args
            return fragment
        }

        fun newInstance(): ChannelRecommendListFragment? {
            return ChannelRecommendListFragment()
        }
    }
}