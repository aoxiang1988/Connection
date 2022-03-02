package com.sec.myonlinefm.classificationprogram.fragment

import com.sec.myonlinefm.OnLineFMConnectManager.Companion.mMainInfoCode
import com.sec.myonlinefm.R
import com.sec.myonlinefm.OnLineFMConnectManager
import com.sec.myonlinefm.classificationprogram.data.WaPiData
import com.sec.myonlinefm.classificationprogram.data.WaPiDataPattern
import android.content.*
import android.os.*
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.sec.myonlinefm.abstructObserver.RequestCallBack
import com.sec.myonlinefm.classificationprogram.dataimport.RecommendsData
import com.sec.myonlinefm.classificationprogram.ChannelProgramActivity
import com.sec.myonlinefm.OnLineStationsActivity
import com.sec.myonlinefm.UpdateListViewAsyncTask

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the [RadioLiveFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RadioLiveFragment : Fragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var mPlayer: OnLineFMConnectManager? = null
    private var mContext: Context? = null
    private val mRecommendsDataList: MutableList<RecommendsData?>? = null
    private var mWaPiDataList: MutableList<WaPiData?>? = null
    private val mHandler: Handler? = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                UPDATE_WAPI_VIEW -> {
                    var i = 0
                    while (i < mWaPiDataList!!.size) {
                        val view = LayoutInflater.from(mContext).inflate(R.layout.demand_channel_item, null)
                        val imageView = view.findViewById<ImageView?>(R.id.demand_channel_pic)
                        val asyncTask = UpdateListViewAsyncTask(imageView,
                                mWaPiDataList!!.get(i)!!.getName(),
                                mPlayer, 60, 60)
                        asyncTask.execute(mWaPiDataList!!.get(i)!!.getCover())
                        val titleView = view.findViewById<TextView?>(R.id.demand_channel_title)
                        titleView.text = mWaPiDataList!!.get(i)!!.getName()
                        val descView = view.findViewById<TextView?>(R.id.demand_channel_description)
                        descView.text = mWaPiDataList!!.get(i)!!.getDesc()
                        val briefNameView = view.findViewById<TextView?>(R.id.play_count)
                        briefNameView.text = mWaPiDataList!!.get(i)!!.getName()
                        val parentNameView = view.findViewById<TextView?>(R.id.context_total)
                        parentNameView.text = mWaPiDataList!!.get(i)!!.getDesc()
                        view.isClickable = true
                        view.background = resources.getDrawable(R.drawable.bg_pressed, null)
                        val finalI = i
                        view.setOnClickListener {
                            val i = Intent(mContext, ChannelProgramActivity::class.java)
                            i.putExtra("channel_id", mWaPiDataList!!.get(finalI)!!.getId())
                            i.putExtra("channel_name", mWaPiDataList!!.get(finalI)!!.getName())
                            startActivity(i)
                        }
                        mScrollerView!!.addView(view)
                        Log.d("bin1111.yang", "UPDATE_WAPI_VIEW")
                        i++
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        mHandler!!.removeMessages(UPDATE_WAPI_VIEW)
        view = null
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        mPlayer!!.getWaPiDataList(3608, object : RequestCallBack<WaPiDataPattern?> {
            override fun onSuccess(`val`: WaPiDataPattern?) {
                mWaPiDataList = `val`!!.getWaPiDataList()
                mHandler!!.sendEmptyMessage(UPDATE_WAPI_VIEW)
            }

            override fun onFail(errorMessage: String?) {}
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
        mPlayer = mMainInfoCode
    }

    private var mScrollerView: LinearLayout? = null
    internal var view: View? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_radio_live, container, false)
        mScrollerView = view!!.findViewById(R.id.scroller_view)
        val mLocalBut = view!!.findViewById<Button?>(R.id.local_but)
        mLocalBut.setOnClickListener(this)
        val mNetRadioBut = view!!.findViewById<Button?>(R.id.net_radio_but)
        mNetRadioBut.setOnClickListener(this)
        return view
    }

    override fun onAttach(context: Context?) {
        mContext = context
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onClick(v: View?) {
        when (v!!.getId()) {
            R.id.local_but -> {
                val intent = Intent(mContext, OnLineStationsActivity::class.java)
                startActivity(intent)
            }
            R.id.net_radio_but -> {
            }
        }
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        val TAG: String? = "RadioLiveFragment"
        private val ARG_PARAM1: String? = "param1"
        private val ARG_PARAM2: String? = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RadioLiveFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): RadioLiveFragment? {
            val fragment = RadioLiveFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }

        private const val UPDATE_WAPI_VIEW = 1
    }
}