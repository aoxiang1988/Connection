package com.sec.myonlinefm

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.view.*
import android.widget.*
import com.sec.myonlinefm.data.PropertyInfo
import java.util.ArrayList

import android.annotation.SuppressLint
import android.os.Bundle
import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Created by SRC-TJ-MM-BinYang on 2018/5/15.
 */
@RequiresApi(Build.VERSION_CODES.M)
@SuppressLint("ValidFragment")
internal class RadioDialogFragment : DialogFragment() {
    //var dialog: Dialog? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog? {
        // TODO Auto-generated method stub
        val type = arguments.getInt(KEY_TYPE)
        var view: View? = null
        when (type) {
            SCAN_PROGRAM_DIALOG -> AlertDialog.Builder(activity)
                    .setTitle("是否刷新节目单")
                    .setNegativeButton(getString(R.string.cancel)) { dialog, which -> dialog.cancel() }
                    .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                        Toast.makeText(context, "正在连接网络刷新节目单,请等待", Toast.LENGTH_LONG).show()
                        (activity as MainActivity).checkLocationPermission()
                        dialog.cancel()
                    }.create()
            START_DNS -> AlertDialog.Builder(activity)
                    .setTitle("请确认")
                    .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                        (activity as MainActivity).unStartDNSPlay()
                        dialog.cancel()
                    }
                    .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                        (activity as MainActivity).StartDNSPlay()
                        dialog.cancel()
                    }
                    .setView(R.layout.online_play_dialog).create()
            IF_ONLINE_PLAY_DIALOG -> AlertDialog.Builder(activity)
                    .setTitle("请确认")
                    .setNegativeButton(getString(R.string.cancel)) { dialog, which -> dialog.cancel() }
                    .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                        OnLineStationsActivity.Companion._activity!!.StartPlay(arguments.getInt(KEY_POSITION))
                        dialog.cancel()
                    }
                    .setView(R.layout.online_play_dialog).create()
            ONLINE_INFO_PROGRESS_DIALOG -> {
                return AlertDialog.Builder(activity)
                        .setTitle(R.string.scan)
                        .setView(R.layout.dialog_progress).create()

            }
            SHOW_ALL_LOCAL_NAME -> {
                val onlineactivity = activity
                val mLocal: MutableList<PropertyInfo.values?>
                val player = OnLineFMConnectManager.mMainInfoCode!!
                mLocal = player.getLocalInfo() as MutableList<PropertyInfo.values?>
                view = LayoutInflater.from(onlineactivity).inflate(R.layout.online_local_names, null)
                val listView = view.findViewById<ListView?>(R.id.loca_name)
                val names = OnLineLocalNames(mLocal, onlineactivity)
                listView.adapter = names
                listView.setOnItemClickListener { parent, view, position, id ->
                    assert(mLocal != null)
                    player.setChangedByUser(true)
                    player.setDifferentLocalID(mLocal[position]!!.getvaluesId(), 1)
                    OnLineStationsActivity.Companion._activity!!.setLocalName(mLocal[position]!!.getvaluesname(),
                            mLocal[position]!!.getvaluesId())
                    dialog!!.cancel()
                }
                return AlertDialog.Builder(onlineactivity).setTitle("可选地域").setView(view).create()
            }
            else -> {
            }
        }
        return dialog
    }

    private inner class OnLineLocalNames internal constructor(private val mLocal: MutableList<PropertyInfo.values?>?, private val onlineactivity: Activity?) : BaseAdapter(), ListAdapter {
        override fun getCount(): Int {
            return mLocal!!.size
        }

        override fun getItem(position: Int): Any? {
            return mLocal!!.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var convertView = convertView
            val mInflater = LayoutInflater.from(onlineactivity)
            if (convertView == null) {
                convertView = mInflater.inflate(android.R.layout.simple_list_item_1, null)
            }
            val mLocalNem = convertView!!.findViewById<TextView?>(android.R.id.text1)
            mLocalNem.text = mLocal!!.get(position)!!.getvaluesname()
            return convertView
        }
    }

    companion object {
        private val KEY_TYPE: String? = "type"
        private val KEY_LIST: String? = "list"
        const val SHOW_ALL_LOCAL_NAME = 14
        const val ONLINE_INFO_PROGRESS_DIALOG = 15
        const val IF_ONLINE_PLAY_DIALOG = 16
        const val START_DNS = 17
        const val SCAN_PROGRAM_DIALOG = 18
        fun newInstance(type: Int, position: Int): RadioDialogFragment? {
            val radioDialog = RadioDialogFragment()
            val bundle = Bundle()
            bundle.putInt(KEY_TYPE, type)
            bundle.putInt(KEY_POSITION, position)
            radioDialog.arguments = bundle
            return radioDialog
        }

        private val KEY_POSITION: String? = "position"
        val DELETE_ITEM: String? = "com.example.action.DELETE_ITEM"
        fun newInstance(type: Int): RadioDialogFragment? {
            // TODO Auto-generated method stub
            val dialogFragment = RadioDialogFragment()
            val bundle = Bundle()
            bundle.putInt(KEY_TYPE, type)
            dialogFragment.arguments = bundle
            return dialogFragment
        }

        fun newInstance(type: Int,
                        mPathListForDialog: MutableList<String?>?): RadioDialogFragment? {
            // TODO Auto-generated method stub
            val dialogFragment = RadioDialogFragment()

//		SerializableMap1 mymap1 = new SerializableMap1();
//		mymap1.SerializableMap1(mPathMapForDialog);

//		SerializableMap2 mymap2 = new SerializableMap2();
//		mymap2.SerializableMap2(mPathSelected);
            val bundle = Bundle()
            bundle.putInt(KEY_TYPE, type)
            //		bundle.putSerializable(KEY_MAP,mymap1);
//		bundle.putSerializable(KEY_SELECT_MAP,mymap2);
            bundle.putStringArrayList(KEY_LIST, mPathListForDialog as ArrayList<String?>?)
            dialogFragment.arguments = bundle
            return dialogFragment
        }
    }
}