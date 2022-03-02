package com.sec.myonlinefm.classificationprogram.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.sec.myonlinefm.OnLineFMConnectManager
import com.sec.myonlinefm.OnLineFMConnectManager.Companion.mMainInfoCode
import com.sec.myonlinefm.R
import com.sec.myonlinefm.abstructObserver.RequestCallBack
import com.sec.myonlinefm.classificationprogram.data.RequestProgramClassify
import com.sec.myonlinefm.classificationprogram.data.RequestProgramClassifyListPattern
import com.sec.myonlinefm.classificationprogram.data.WaPiData
import com.sec.myonlinefm.classificationprogram.data.WaPiDataPattern
import com.sec.myonlinefm.classificationprogram.ChannelProgramActivity
import com.sec.myonlinefm.UpdateListViewAsyncTask
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the [BoutiqueFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BoutiqueFragment : Fragment() {
    private var mPlayer: OnLineFMConnectManager? = null
    private var mContext: Context? = null
    private var mRequestProgramClassifyList: MutableList<RequestProgramClassify?>? = null
    private var mRankListMap: MutableMap<Int?, MutableList<WaPiData?>?>? = null

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var rankingAdapter: RankingAdapter? = null
    internal var view: View? = null
    private var expandableListView: ExpandableListView? = null
    private var isFinished = true
    private var resIds: MutableList<Int?>? = null

    private val mHandler: Handler? = Handler(Handler.Callback { msg ->
        if (msg.what == UPDATE_LIST) {
            while (isFinished) {
                if (rankingAdapter == null) {
                    rankingAdapter = RankingAdapter()
                    expandableListView?.setAdapter(rankingAdapter)
                } else {
                    rankingAdapter!!.notifyDataSetChanged()
                }
                Tartiest_Click_Listener(expandableListView)
            }
        }
        true
    })

    override fun onResume() {
        super.onResume()
        isFinished = true
        mRequestProgramClassifyList = RequestProgramClassifyListPattern.Companion.getInstance()?.getRequestProgramClassifyList()
        mRankListMap = Hashtable()
        if (mRequestProgramClassifyList != null) {
            for (requestProgramClassify in mRequestProgramClassifyList!!) {
                if (!isFinished) break
                if (requestProgramClassify != null) {
                    mPlayer?.getWaPiDataList(requestProgramClassify.getId(), object : RequestCallBack<WaPiDataPattern?> {
                        override fun onSuccess(`val`: WaPiDataPattern?) {
                            val mWaPiDataList = `val`?.getWaPiDataList()
                            (mRankListMap as Hashtable<Int?, MutableList<WaPiData?>?>)[requestProgramClassify.getId()] = mWaPiDataList
                            mHandler?.sendEmptyMessage(UPDATE_LIST)
                        }

                        override fun onFail(errorMessage: String?) {}
                    })
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
        val ar = resources.obtainTypedArray(R.array.category_drawable)
        val len = ar.length()
        resIds = ArrayList()
        for (i in 0 until len) {
            (resIds as ArrayList<Int?>).add(ar.getResourceId(i, 0))
        }
        ar.recycle()
        mPlayer = mMainInfoCode
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if (inflater != null) {
            view = inflater.inflate(R.layout.fragment_boutique, container, false)
        }
        expandableListView = view?.findViewById(R.id.expandable_list_view)
        //        expandableListView.setChildDivider(null);
        expandableListView?.setGroupIndicator(null)
        //        rankingAdapter = new RankingAdapter();
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isFinished = false
        //        expandableListView.removeAllViews();
        expandableListView?.setOnGroupClickListener(null)
        expandableListView?.setOnChildClickListener(null)
        expandableListView = null
        view = null
        rankingAdapter = null
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler?.removeMessages(UPDATE_LIST)
        mRequestProgramClassifyList = null
        mRankListMap = null
    }

    override fun onAttach(context: Context?) {
        mContext = context
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    private inner class RankingAdapter : BaseExpandableListAdapter() {
        override fun getGroupCount(): Int {
            return if (mRequestProgramClassifyList == null) 0 else mRequestProgramClassifyList!!.size
        }

        override fun getChildrenCount(groupPosition: Int): Int {
            if (mRequestProgramClassifyList == null) return 0
            val key = mRequestProgramClassifyList!!.get(groupPosition)?.getId()
            return if (mRankListMap?.get(key) == null) 0 else mRankListMap?.get(key)!!.size
        }

        override fun getGroup(groupPosition: Int): Any? {
            return if (mRequestProgramClassifyList == null) null else mRequestProgramClassifyList!!.get(groupPosition)
        }

        override fun getChild(groupPosition: Int, childPosition: Int): Any? {
            if (mRequestProgramClassifyList == null) return null
            val key = mRequestProgramClassifyList!!.get(groupPosition)?.getId()
            return if (mRankListMap?.get(key) == null) null else mRankListMap?.get(key)!!.get(childPosition)
        }

        override fun getGroupId(groupPosition: Int): Long {
            return groupPosition.toLong()
        }

        override fun getChildId(groupPosition: Int, childPosition: Int): Long {
            return childPosition.toLong()
        }

        override fun hasStableIds(): Boolean {
            return true
        }

        @SuppressLint("InflateParams")
        override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View? {
            var convertView = convertView
            val holder: GroupViewHolder
            if (convertView == null) {
                val inflater = (mContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)!!
                convertView = inflater.inflate(R.layout.group_item_layout, null)
                holder = GroupViewHolder()
                holder.tv = convertView.findViewById(R.id.group_title)
                holder.mDropUpDown = convertView.findViewById(R.id.group_down)
                holder.mGroupIcon = convertView.findViewById(R.id.group_icon)
                convertView.tag = holder
            } else holder = convertView.tag as GroupViewHolder
            holder.mGroupIcon?.setImageDrawable(resources.getDrawable(resIds?.get(groupPosition)!!))
            holder.tv?.setText(mRequestProgramClassifyList?.get(groupPosition)?.getName())
            if (expandableListView?.isGroupExpanded(groupPosition) == true) {
                holder.tv?.setTextColor(resources.getColor(R.color.net_fm_back_b))
                holder.mDropUpDown?.setImageResource(R.drawable.drop_up)
            } else {
                holder.tv?.setTextColor(resources.getColor(R.color.details_title))
                holder.mDropUpDown?.setImageResource(R.drawable.drop_down)
            }
            return convertView
        }

        override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View? {
            var convertView = convertView
            val holder: ChildViewHolder
            val key = mRequestProgramClassifyList?.get(groupPosition)?.getId()
            if (convertView == null) {
                val inflater = (mContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)!!
                convertView = inflater.inflate(R.layout.demand_channel_item, null)
                holder = ChildViewHolder()
                holder.imageView = convertView.findViewById(R.id.demand_channel_pic)
                holder.childMusicName = convertView.findViewById(R.id.demand_channel_title)
                holder.descView = convertView.findViewById(R.id.demand_channel_description)
                convertView.tag = holder
            } else holder = convertView.tag as ChildViewHolder
            if (mRankListMap?.get(key) != null) {
                val asyncTask = UpdateListViewAsyncTask(holder.imageView,
                        mRankListMap!!.get(key)?.get(childPosition)?.getName(),
                        mPlayer, 60, 60)
                asyncTask.execute(mRankListMap!!.get(key)?.get(childPosition)?.getCover())
                holder.childMusicName?.setText(mRankListMap!!.get(key)?.get(childPosition)?.getName())
                holder.descView?.setText(mRankListMap!!.get(key)!!.get(childPosition)?.getDesc())
            }
            return convertView
        }

        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
            return true
        }
    }

    internal class GroupViewHolder {
        var tv: TextView? = null
        var mGroupIcon: ImageView? = null
        var mDropUpDown: ImageView? = null
    }

    internal class ChildViewHolder {
        var childMusicName: TextView? = null
        var imageView: ImageView? = null
        var descView: TextView? = null
    }

    private val isGroupOpen = false
    private fun Tartiest_Click_Listener(tartiest: ExpandableListView?) {
        if (tartiest != null) {
            tartiest.setOnGroupClickListener(ExpandableListView.OnGroupClickListener { parent, v, groupPosition, id ->
                if (parent.isGroupExpanded(groupPosition)) {
//                    v.setBackgroundColor(getResources().getColor(R.color.main_bg_color, null));
                    parent.collapseGroup(groupPosition)
                } else {
//                    v.setBackgroundColor(getResources().getColor(R.color.net_fm_back_b, null));
                    parent.expandGroup(groupPosition)
                }
                rankingAdapter?.notifyDataSetChanged()
                true
            })
        }
        if (tartiest != null) {
            tartiest.setOnChildClickListener(ExpandableListView.OnChildClickListener { parent, v, groupPosition, childPosition, id ->
                val key = mRequestProgramClassifyList?.get(groupPosition)?.getId()
                val i = Intent(mContext, ChannelProgramActivity::class.java)
                i.putExtra("channel_id", mRankListMap?.get(key)?.get(childPosition)?.getId())
                i.putExtra("channel_name", mRankListMap?.get(key)?.get(childPosition)?.getName())
                startActivity(i)
                true
            })
        }
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1: String? = "param1"
        private val ARG_PARAM2: String? = "param2"
        private const val UPDATE_LIST = 1
        val TAG: String? = "BoutiqueFragment"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BoutiqueFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): Fragment? {
            val fragment = BoutiqueFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}