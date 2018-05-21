package com.sec.myonlinefm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import android.widget.ListAdapter;

import com.sec.myonlinefm.data.PropertyInfo;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/5/15.
 */

@SuppressLint("ValidFragment")
class RadioDialogFragment extends DialogFragment {

    private static final String KEY_TYPE = "type";
    private static final String KEY_LIST = "list";

    public static final int SHOW_ALL_LOCAL_NAME = 14;
    public static final int ONLINE_INFO_PROGRESS_DIALOG = 15;
    public static final int IF_ONLINE_PLAY_DIALOG = 16;
    public static final int START_DNS = 17;
    public static final int SCAN_PROGRAM_DIALOG = 18;

    public static RadioDialogFragment newInstance(int type, int position) {
        RadioDialogFragment radioDialog = new RadioDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TYPE, type);
        bundle.putInt(KEY_POSITION, position);
        radioDialog.setArguments(bundle);
        return radioDialog;
    }

    private static final String KEY_POSITION = "position";

    Dialog dialog = null;
    public static final String DELETE_ITEM = "com.example.action.DELETE_ITEM";

    public static RadioDialogFragment newInstance(int type) {
        // TODO Auto-generated method stub

        RadioDialogFragment dialogFragment = new RadioDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TYPE, type);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    public static RadioDialogFragment newInstance(int type,
                                           List<String> mPathListForDialog) {
        // TODO Auto-generated method stub

        RadioDialogFragment dialogFragment = new RadioDialogFragment();

//		SerializableMap1 mymap1 = new SerializableMap1();
//		mymap1.SerializableMap1(mPathMapForDialog);

//		SerializableMap2 mymap2 = new SerializableMap2();
//		mymap2.SerializableMap2(mPathSelected);

        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TYPE, type);
//		bundle.putSerializable(KEY_MAP,mymap1);
//		bundle.putSerializable(KEY_SELECT_MAP,mymap2);
        bundle.putStringArrayList(KEY_LIST, (ArrayList<String>) mPathListForDialog);

        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        int type = getArguments().getInt(KEY_TYPE);
        View view = null;
        switch (type) {
            case SCAN_PROGRAM_DIALOG:
                dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("是否刷新节目单")
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(),"正在连接网络刷新节目单,请等待", Toast.LENGTH_LONG).show();
                                ((MainActivity)getActivity()).checkLocationPermission();
                                dialog.cancel();
                            }
                        }).create();
                break;
            case START_DNS:
                dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("请确认")
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((MainActivity)getActivity()).unStartDNSPlay();
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((MainActivity)getActivity()).StartDNSPlay();
                                dialog.cancel();
                            }
                        })
                        .setView(R.layout.online_play_dialog).create();
                break;
            case IF_ONLINE_PLAY_DIALOG:
                dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("请确认")
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @SuppressLint("NewApi")
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((OnLineStationsActivity)getActivity()).StartPlay(getArguments().getInt(KEY_POSITION));
                                dialog.cancel();
                            }
                        })
                        .setView(R.layout.online_play_dialog).create();
                break;
            case ONLINE_INFO_PROGRESS_DIALOG:
                dialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.scan)
                        .setView(R.layout.dialog_progress).create();
                dialog.setCanceledOnTouchOutside(false);
                break;
            case SHOW_ALL_LOCAL_NAME :
                final Activity onlineactivity = getActivity();
                final List<PropertyInfo.values> mLocal;
                final OnLineFMConnectManager player = OnLineFMConnectManager.Companion.getMMainInfoCode();
                assert player != null;
                mLocal = player.getLocalInfo();
                view = LayoutInflater.from(onlineactivity).inflate(R.layout.online_local_names, null);
                ListView listView = view.findViewById(R.id.loca_name);
                OnLineLocalNames names = new OnLineLocalNames(mLocal, onlineactivity);
                listView.setAdapter(names);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        assert mLocal != null;
                        player.setChangedByUser(true);
                        player.setDifferentLocalID(mLocal.get(position).getvaluesId(), 1);
                        ((OnLineStationsActivity)onlineactivity).setLocalName(mLocal.get(position).getvaluesname(),
                                mLocal.get(position).getvaluesId());
                        dialog.cancel();
                    }
                });
                dialog = new AlertDialog.Builder(onlineactivity).setTitle("可选地域").setView(view).create();
                break;
            default:
                break;
        }
        return dialog;
    }

    private class OnLineLocalNames extends BaseAdapter implements ListAdapter {
        private List<PropertyInfo.values> mLocal;
        private Activity onlineactivity;
        OnLineLocalNames(List<PropertyInfo.values> mLocal, Activity onlineactivity) {
            this.mLocal = mLocal;
            this.onlineactivity = onlineactivity;
        }
        @Override
        public int getCount() {
            return mLocal.size();
        }

        @Override
        public Object getItem(int position) {
            return mLocal.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater mInflater = LayoutInflater.from(onlineactivity);
            if (convertView == null) {
                convertView = mInflater.inflate(android.R.layout.simple_list_item_1, null);
            }
            TextView mLocalNem = convertView.findViewById(android.R.id.text1);
            mLocalNem.setText(mLocal.get(position).getvaluesname());
            return convertView;
        }
    }

}