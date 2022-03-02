package com.sec.myonlinefm.classificationprogram.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import com.sec.myonlinefm.classificationprogram.data.ObservableController
import com.sec.myonlinefm.classificationprogram.data.RequestProgramClassify
import com.sec.myonlinefm.classificationprogram.data.RequestProgramClassifyListPattern
import com.sec.myonlinefm.classificationprogram.InfoContextActivity
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the [ClassifyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ClassifyFragment : Fragment(),Observer {
    private var mPlayer: OnLineFMConnectManager? = null
    private var mClassifyView: GridView? = null
    private var mClassifyAdapter: ClassifyAdapter? = null
    private var mContext: Context? = null
    private var mRequestProgramClassifyList: MutableList<RequestProgramClassify?>? = null
    private val mObservable: ObservableController? = ObservableController.Companion.getInstance()
    private var resIds: MutableList<Int?>? = null
    private val mHandler: Handler? = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg!!)
            Log.d("gaolin", " handleMessage ")
            if (msg.what == UPDATE_GRID_VIEW) {
                Log.d("gaolin", " UPDATE_GRID_VIEW ")
                mRequestProgramClassifyList = RequestProgramClassifyListPattern.Companion.getInstance()!!
                        .getRequestProgramClassifyList()
                mClassifyAdapter!!.setList(mRequestProgramClassifyList)
            }
        }
    }

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
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
        mObservable!!.addObserver(this)
    }

    internal var view: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        view = inflater!!.inflate(R.layout.fragment_classify, container, false)
        mClassifyView = view?.findViewById(R.id.classify_list)
        mClassifyAdapter = ClassifyAdapter(mContext)
        mClassifyView?.setAdapter(mClassifyAdapter)
        mClassifyView?.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            val mCurrentClassify = mRequestProgramClassifyList!!.get(position)
            val i = Intent()
            i.putExtra("category_id", mCurrentClassify!!.getId())
            i.putExtra("category_name", mCurrentClassify.getName())
            i.setClass(mContext!!, InfoContextActivity::class.java)
            startActivity(i)
        })

        /*   mPlayer.getRequestProgramClassify(new RequestCallBack<RequestProgramClassifyListPattern>() {
            @Override
            public void onSuccess(RequestProgramClassifyListPattern val) {
                Log.d(TAG,"onSuccess : ");
                mRequestProgramClassifyList = val.getRequestProgramClassifyList();
                mHandler.sendEmptyMessage(UPDATE_GRID_VIEW);
            }

            @Override
            public void onFail(String errorMessage) {
                Log.d(TAG,"onFail : "+errorMessage);
            }
        });
*/mRequestProgramClassifyList = RequestProgramClassifyListPattern.Companion.getInstance()!!.getRequestProgramClassifyList()
        if (mRequestProgramClassifyList != null) {
            mHandler!!.sendEmptyMessage(UPDATE_GRID_VIEW)
        }
        return view
    }

    override fun onDestroyView() {
        mHandler!!.removeMessages(UPDATE_GRID_VIEW)
        view = null
        super.onDestroyView()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context
    }

    override fun onDetach() {
        super.onDetach()
    }

    private inner class ClassifyAdapter internal constructor(context: Context?) : BaseAdapter() {
        private var mRequestProgramClassifyList: MutableList<RequestProgramClassify?>? = null
        private var mContext: Context? = null
        fun setList(request_program_classify_list: MutableList<RequestProgramClassify?>?) {
            Log.d("gaolin", " request_program_classify_list :  $request_program_classify_list")
            mRequestProgramClassifyList = request_program_classify_list
            notifyDataSetChanged()
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
            val holder: ViewHolder
            return if (mRequestProgramClassifyList == null) null else {
                if (convertView == null) {
                    val inflater = (mContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)!!
                    convertView = inflater.inflate(R.layout.classify_grid_item_layout, null)
                    holder = ViewHolder()
                    holder.classify_icon = convertView.findViewById(R.id.classify_icon)
                    holder.classify_name = convertView.findViewById<View?>(R.id.classify_name) as TextView
                    convertView.tag = holder
                } else {
                    holder = convertView.tag as ViewHolder //取出ViewHolder对象
                }
                holder.classify_name!!.setText(mRequestProgramClassifyList!!.get(position)!!.getName())
                holder.classify_icon!!.setImageDrawable(resources.getDrawable(resIds!!.get(position)!!))
                convertView
            }
        }

        init {
            mContext = context
        }
    }

    internal class ViewHolder {
        var classify_icon: ImageView? = null
        var classify_name: TextView? = null
    }

    override fun update(o: Observable?, arg: Any?) {
        val event = arg as Int
        Log.d("gaolin", "event = $event")
        when (event) {
            1 -> mHandler!!.sendEmptyMessage(UPDATE_GRID_VIEW)
        }
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1: String? = "param1"
        private val ARG_PARAM2: String? = "param2"
        val TAG: String? = "ClassifyFragment"
        private const val UPDATE_GRID_VIEW = 1

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ClassifyFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): Fragment? {
            val fragment = ClassifyFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}