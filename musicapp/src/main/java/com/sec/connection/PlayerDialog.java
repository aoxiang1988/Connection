package com.sec.connection;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;

import com.sec.connection.data.Audio;
import com.sec.connection.network.SearchOnNetWork;
import com.sec.connection.setting.FilterSettings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class PlayerDialog extends DialogFragment {

	public static final int STATUE = 1;
	public static final int EXIT = 2;
	public static final int DELETE = 3;
	public static final int SEARCH = 4 ;
	public static final int NETWORK = 5 ;
	public static final int FOLDER_PATH = 6;
	private static final String KEY_TYPE = "type";
	private static final String KEY_POSTION = "postion";
	private static final String KEY_MAP = "map";
	private static final String KEY_SELECT_MAP = "select map";
	private static final String KEY_LIST = "list";

	Dialog dialog = null;
	PlayerNotificationManager notificationManager = PlayerNotificationManager.instance();
	public static final String DELETE_ITEM = "com.example.action.DELETE_ITEM";

	public static PlayerDialog newInstance(int type) {
		// TODO Auto-generated method stub
		
		PlayerDialog dialogFragment = new PlayerDialog();
		Bundle bundle = new Bundle();
		bundle.putInt(KEY_TYPE, type);
		dialogFragment.setArguments(bundle);
		return dialogFragment;
	}
	
	public static PlayerDialog newInstance(int type,
										   List<String> mPathListForDialog) {
		// TODO Auto-generated method stub
		
		PlayerDialog dialogFragment = new PlayerDialog();

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
		switch (type) {
		case STATUE:
			mCustomDialog();
			break;
		case EXIT:
			dialog = new AlertDialog.Builder(getActivity()).setPositiveButton(R.string.exit, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					((MainActivity)getActivity()).stop();
					getActivity().finish();
					notificationManager.removenotification(getActivity());
				}
			}).setNegativeButton(R.string.back,null).setTitle(R.string.exit).create();
			break;
		case SEARCH:
			dialog = new AlertDialog.Builder(getActivity()).setPositiveButton(R.string.change_to, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
//					intent.setClass(getActivity(), FMInfoOnLineActivity.class);
					intent.setClass(getActivity(), SearchOnNetWork.class);
					startActivity(intent);
					//notificationManager.removenotification((MainActivity)getContext());
				}
			}).setNegativeButton(R.string.back,null).setTitle(R.string.network_search).create();
			break;
		case DELETE:
			dialog = new AlertDialog.Builder(getActivity()).setPositiveButton(R.string.ok, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					int postion = getArguments().getInt(KEY_POSTION);
					((MainActivity)getActivity()).delete(postion); 
				}
			}).setNegativeButton(R.string.back,null).setTitle(R.string.delete).create();
			break;
		case FOLDER_PATH:
			SerializableMap1 s1 = (SerializableMap1) getArguments().getSerializable(KEY_MAP);
			Map<String, List<Audio>> mPathMapForDialog = null;
			if (s1 != null) {
				mPathMapForDialog = SerializableMap1.getMap();
			}

			SerializableMap2 s2 = (SerializableMap2) getArguments().getSerializable(KEY_SELECT_MAP);
			Map<String, Boolean> mPathSelected = null;
			if (s2 != null) {
				mPathSelected = SerializableMap2.getMap();
			}

			List<String> mPathListForDialog = getArguments().getStringArrayList(KEY_LIST);
			mFolderPathDialog(mPathMapForDialog, mPathListForDialog, mPathSelected);
			break;
		default:
			break;
		}
		return dialog;
	}

    /**文件夹路径设置
	 * @param mPathMapForDialog
	 * @param mPathListForDialog**/
	private void mFolderPathDialog(Map<String, List<Audio>> mPathMapForDialog,
								   final List<String> mPathListForDialog,
								   final Map<String, Boolean> mPathSelected){
		final AlertDialog builder = new AlertDialog.Builder(getActivity()).create();
		builder.show();
		builder.getWindow().setContentView(R.layout.folder_paths_dialog_layout);
		LayoutInflater factory = LayoutInflater.from(getActivity());
		final View view = factory.inflate(R.layout.folder_paths_dialog_layout, null);
		builder.getWindow().setContentView(view);
		final Window dialogWindow = builder.getWindow();
		dialogWindow.setGravity( Gravity.CENTER);//显示在底部
//		dialogWindow.setBackgroundDrawableResource(R.drawable.status_dialog_bg);
		dialogWindow.setWindowAnimations(R.style.take_photo_anim);
		WindowManager m = getActivity().getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值

		Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
		int ori = mConfiguration.orientation ; //获取屏幕方向
		if(ori == Configuration.ORIENTATION_LANDSCAPE){
			p.height = (int) (d.getWidth() * 0.3); // 高度设置为屏幕的0.5
			p.width = (int) (d.getHeight() * 0.9); // 宽度设置为屏幕宽
			//横屏
		}else if(ori == Configuration.ORIENTATION_PORTRAIT){
			p.height = (int) (d.getHeight()*0.5); // 高度设置为屏幕的0.5
			p.width = d.getWidth(); // 宽度设置为屏幕宽
			//竖屏
		}

		dialogWindow.setAttributes(p);

		FolderPathAdapter adapter = new FolderPathAdapter(getActivity(), mPathListForDialog);
		ListView folderpathlistview = view.findViewById(R.id.folder_path_list);
		folderpathlistview.setAdapter(adapter);
		folderpathlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d("bin1111.yang", "onItemClick position : "+position+" ; "+FilterSettings.mFolderPathData.isPathSelected(position));
				FilterSettings.mFolderPathData.setWhicPathOn(position);
				CheckBox foldercheckBox = view.findViewById(R.id.folder_path_checkBox);
				if(FilterSettings.mFolderPathData.isPathSelected(position)){
					foldercheckBox.setChecked(true);
				} else {
					foldercheckBox.setChecked(false);
				}
			}
		});

		ImageView foldernegativebutton = view.findViewById(R.id.folder_path_negative);
		ImageView folderpositivebutton = view.findViewById(R.id.folder_path_positive);

		foldernegativebutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FilterSettings.mFolderPathData.setallpahtoff();
				FilterSettings._inActivity.setfilterFolder(false);
				builder.dismiss();
			}
		});

		folderpositivebutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FilterSettings._inActivity.setfilterFolder(true);
				builder.dismiss();
			}
		});

	}

	/**音乐播放顺序设置(map数组序列化)**/
	private void mCustomDialog() {
		final AlertDialog builder = new AlertDialog.Builder(getActivity(),R.style.CreatDialog).create();
		builder.show();
		builder.getWindow().setContentView(R.layout.custom_dialog_layout);
		LayoutInflater factory = LayoutInflater.from(getActivity());
		View view = factory.inflate(R.layout.custom_dialog_layout, null);
		builder.getWindow().setContentView(view);
		Window dialogWindow = builder.getWindow();
		dialogWindow.setGravity( Gravity.BOTTOM);//显示在底部
		dialogWindow.setBackgroundDrawableResource(R.drawable.status_dialog_bg);
		dialogWindow.setWindowAnimations(R.style.take_photo_anim);
		WindowManager m = getActivity().getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值

		Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
		int ori = mConfiguration.orientation ; //获取屏幕方向
		if(ori == Configuration.ORIENTATION_LANDSCAPE){
			p.height = (int) (d.getWidth() * 0.3); // 高度设置为屏幕的0.5
			p.width = (int) (d.getHeight() * 0.7); // 宽度设置为屏幕宽
			//横屏
		}else if(ori == Configuration.ORIENTATION_PORTRAIT){
			p.height = (int) (d.getHeight() * 0.3); // 高度设置为屏幕的0.5
			p.width = (int) (d.getWidth() * 0.7); // 宽度设置为屏幕宽
			//竖屏
		}

		dialogWindow.setAttributes(p);

		String[] status = new String[]{getString(R.string.signal_round),
				getString(R.string.all_round),
				getString(R.string.order_play),
				getString(R.string.random)};

		RadioGroup radiogroup = view.findViewById(R.id.radio_group);
		RadioButton rb;
		for(int i=0; i<4; i++){
			rb = new RadioButton(getActivity());
			rb.setText(status[i]);
			rb.setTextSize(15);
			rb.setId(i);
			rb.setTextColor(getResources().getColor(R.color.radiotext));
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT, 100);
			radiogroup.addView(rb,layoutParams);
			if(((MainActivity) getActivity()).getstatus() == i+1) {
				Log.d("bin1111.yang","i : "+i);
				radiogroup.check(i);
			}
		}
		radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
				int radiobutton_id = group.getCheckedRadioButtonId();

				switch (checkedId){
					case 0:
						((MainActivity)getActivity()).setstatus(1);
						break;
					case 1:
						((MainActivity)getActivity()).setstatus(2);
						break;
					case 2:
						((MainActivity)getActivity()).setstatus(3);
						break;
					case 3:
						((MainActivity)getActivity()).setstatus(4);
						break;
				}
			}
		});
	}

	private static class SerializableMap1 implements Serializable {
		private static Map<String, List<Audio>> map;
		public static Map<String, List<Audio>> getMap() {
			return map;
		}
		SerializableMap1(Map<String, List<Audio>> map) {
			SerializableMap1.map = map;
		}
	}
	private static class SerializableMap2 implements Serializable {
		private static Map<String, Boolean> map;
		public static Map<String, Boolean> getMap() {
			return map;
		}
		SerializableMap2(Map<String, Boolean> map) {
			SerializableMap2.map = map;
		}
	}

	private  class FolderPathAdapter extends BaseAdapter {

		private Context mContext;
		private List<String> mPathListForDialog;

		private FolderPathAdapter(Context mContext,
								  List<String> mPathListForDialog){
			this.mContext = mContext;
			this.mPathListForDialog = mPathListForDialog;
		}

		@Override
		public int getCount() {
			return mPathListForDialog.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint({"SetTextI18n", "InflateParams"})
        @Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
		    if(convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.folder_path_item, null);
				holder = new ViewHolder();
				convertView.setTag(holder);
		    } else holder = (ViewHolder) convertView.getTag();
			holder.folder_name = convertView.findViewById(R.id.folder_name);

			holder.music_num = convertView.findViewById(R.id.folder_music_no);

			holder.folder_path = convertView.findViewById(R.id.folder_path);
			holder.folder_path.setText(mPathListForDialog.get(position));

			holder.folder_check_box = convertView.findViewById(R.id.folder_path_checkBox);

			String new_path = mPathListForDialog.get(position).replace("0", "@");
			String split_path[] = new_path.split("@");
			holder.folder_name.setText(split_path[1]+":");
			holder.music_num.setText(FilterSettings.mFolderPathData.getlistlangth(position)+"首歌 ：");

			if(FilterSettings.mFolderPathData.isPathSelected(position)){
				holder.folder_check_box.setChecked(true);
			} else {
				holder.folder_check_box.setChecked(false);
			}

			return convertView;
		}
	}

	private static class ViewHolder {
		TextView folder_name;
		TextView music_num;
		TextView folder_path;
		CheckBox folder_check_box;
	}
}
