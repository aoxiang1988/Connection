package com.sec.myonlinefm.onlineSearchUI

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.sec.myonlinefm.OnLineFMConnectManager
import com.sec.myonlinefm.OnLineFMConnectManager.Companion.mMainInfoCode
import com.sec.myonlinefm.R
import com.sec.myonlinefm.abstructObserver.OnLineInfo
import com.sec.myonlinefm.data.SearchType
import com.sec.myonlinefm.updataUIListener.ObserverUIListenerManager
import com.sec.myonlinefm.UpdateListViewAsyncTask

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the [SearchResultListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchResultListFragment : Fragment() {
    private var mCurrentTab = 0
    private var mContext: Context? = null
    private val UPDATE_LIST = 1
    private val mCurrentPage = 1
    private var mPlayer: OnLineFMConnectManager? = null

    // TODO: Rename and change types of parameters
    @SuppressLint("HandlerLeak")
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == UPDATE_LIST) {
                if (mSearchListAdapter != null) mSearchListAdapter!!.notifyDataSetChanged() else mSearchListAdapter = SearchListAdapter()
                mSearchScanningView!!.setVisibility(View.GONE)
                mSearchListView!!.setVisibility(View.VISIBLE)
                mSearchListView!!.setAdapter(mSearchListAdapter)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPlayer = mMainInfoCode
        mUpdateOnLineInfo = UpdateOnLineInfo()
        mUpdateOnLineInfo!!.addToObserverList()
    }

    override fun onDestroy() {
        super.onDestroy()
        mUpdateOnLineInfo!!.removeToObserverList()
    }

    private var mSearchListView: ListView? = null
    private var mSearchScanningView: TextView? = null
    private var mSearchListAdapter: SearchListAdapter? = null
    private var mSearchChannelLiveList: MutableList<SearchType.ChannelLive?>? = null
    private var mSearchProgramLiveList: MutableList<SearchType.ProgramLive?>? = null
    private val mClickListener: AdapterView.OnItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
        if (mCurrentTab == 0) {
            val mChannelLive = mSearchChannelLiveList!!.get(position - 1)
        } else if (mCurrentTab == 1) {
            val mProgramLive = mSearchProgramLiveList!!.get(position - 1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_base_list, container, false)
        mSearchListView = view.findViewById(R.id.search_result_list)
        mSearchListView!!.setOnItemClickListener(mClickListener)
        mSearchScanningView = view.findViewById(R.id.search_scanning)
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context
    }

    override fun onDetach() {
        super.onDetach()
    }

    internal inner class SearchListAdapter : BaseAdapter() {
        override fun getCount(): Int {
            var mCount = 0
            if (mCurrentTab == 0) {
                mCount = mSearchChannelLiveList!!.size
            } else if (mCurrentTab == 1) {
                mCount = mSearchProgramLiveList!!.size
            }
            return mCount
        }

        override fun getItem(position: Int): Any? {
            return null
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val mConvertView: View? = LayoutInflater.from(mContext).inflate(R.layout.search_result_item_layout, parent, false)
            val mSearchCover = mConvertView!!.findViewById<ImageView?>(R.id.search_cover)
            val mSearchTitle = mConvertView.findViewById<TextView?>(R.id.search_title)
            val mSearchFreq = mConvertView.findViewById<TextView?>(R.id.search_freq)
            if (mCurrentTab == 0) {
                val mChannelLive = mSearchChannelLiveList!!.get(position)
                mSearchTitle.text = mChannelLive!!.getChannelLiveTitle()
                //                mSearch_Cover.setImageBitmap(mPlayer.getBitmap(mChannelLive.getChannelLiveCover(), 60, 50));
                val asyncTask = UpdateListViewAsyncTask(mSearchCover, mChannelLive.getChannelLiveTitle(), mPlayer, 60, 60)
                asyncTask.execute(mChannelLive.getChannelLiveCover())
                mSearchFreq.text = String.format("%s MHz", mChannelLive.getChannelLiveFreqs())
                mSearchFreq.visibility = View.VISIBLE
            } else if (mCurrentTab == 1) {
                val mProgramLive = mSearchProgramLiveList!!.get(position)
                mSearchTitle.text = mProgramLive!!.getProgramLiveTitle()
                //                mSearch_Cover.setImageBitmap(mPlayer.getBitmap(mProgramLive.getProgramLiveCover(), 60, 50));
                val asyncTask = UpdateListViewAsyncTask(mSearchCover, mProgramLive.getProgramLiveTitle(), mPlayer, 60, 60)
                asyncTask.execute(mProgramLive.getProgramLiveCover())
            }
            return mConvertView
        }
    }

    private var mUpdateOnLineInfo: UpdateOnLineInfo? = null

    private inner class UpdateOnLineInfo : OnLineInfo() {
        override fun addToObserverList() {
            ObserverUIListenerManager.getInstance()!!.add(this)
        }

        override fun removeToObserverList() {
            ObserverUIListenerManager.getInstance()!!.remove(this)
        }

        override fun observerChannelLiveUpData(mSearchChannelLiveList: MutableList<SearchType.ChannelLive?>?) {
            //update UI
            mCurrentTab = 0
            this@SearchResultListFragment.mSearchChannelLiveList = mSearchChannelLiveList
            mHandler.sendEmptyMessage(UPDATE_LIST)
        }

        override fun observerProgramLiveUpData(mSearchProgramLiveList: MutableList<SearchType.ProgramLive?>?) {
            //update UI
            mCurrentTab = 1
            this@SearchResultListFragment.mSearchProgramLiveList = mSearchProgramLiveList
            mHandler.sendEmptyMessage(UPDATE_LIST)
        }
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        var TAG: String? = "SearchResultListFragment"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment BaseListFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(query: String?): SearchResultListFragment {
            return SearchResultListFragment()
        }
    }
}