package com.sec.connection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sec.connection.data.Audio;

import java.util.List;

public class UserAdapter extends ArrayAdapter<Audio> {

	private int resource;
	private Context context;
	private List<Audio> objects;
	private boolean update_position = false;
	private int which = 0;

	public UserAdapter(Context context, int resource, List<Audio> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.resource = resource;
		this.context = context;
		this.objects = objects;
		Log.d("bin1111.yang","   "+objects.toString());
	}
	public UserAdapter(Context context, int resource, List<Audio> objects ,int which) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.resource = resource;
		this.context = context;
		this.objects = objects;
		this.which = which;

	}

	private static class ViewHolder {
		TextView title;
		TextView artist;
		TextView time;
		ImageView imageView;
		CheckBox checkBox;
	}
	@NonNull
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Audio audio = objects.get(position);
		ViewHolder holder;
        Log.d("bin1111.yang","title : "+audio.getTitle());
		if(convertView == null) {
			convertView = LayoutInflater.from(context).inflate(resource, null);
			holder = new ViewHolder();
			convertView.setTag(holder);
		}
		else holder = (ViewHolder) convertView.getTag();

		holder.title = convertView.findViewById(R.id.musictitle);
		holder.artist = convertView.findViewById(R.id.musicartist);
		holder.time = convertView.findViewById(R.id.musictime);
		holder.imageView = convertView.findViewById(R.id.listmusicpicture);
		holder.checkBox = convertView.findViewById(R.id.checkBox1);

		Bitmap bitmap = audio.getBitmap();
		if(bitmap != null){
			holder.imageView.setImageBitmap(bitmap);
		}
		if(which == 1){
			holder.title.setText(audio.getNetTitle());
			holder.artist.setText(audio.getNetArtist());
			holder.time.setVisibility(View.GONE);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				holder.title.setTextColor(context.getResources().getColor(R.color.noplaycolor, null));
				holder.artist.setTextColor(context.getResources().getColor(R.color.noplaycolor, null));
			} else {
				holder.title.setTextColor(context.getResources().getColor(R.color.noplaycolor));
				holder.artist.setTextColor(context.getResources().getColor(R.color.noplaycolor));
			}

		}else{
			holder.title.setText(audio.getTitle());
			holder.artist.setText(audio.getArtist());

			int min = (audio.getDuration() / 1000) / 60;
			int sec = (audio.getDuration() / 1000) % 60;
			String string_min;
			String string_sec;
			if (min < 10) {
                string_min = String.format("0%s", min);
            } else {
                string_min = String.format("%s", min);
            }
			if (sec < 10) {
                string_sec = String.format("0%s", sec);
            } else {
                string_sec = String.format("%s", sec);
            }
			holder.time.setText(String.format("%s:%s", string_min, string_sec));
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				if(MainService.isPlay && position == MainActivity.getcurrentposition()){
					holder.title.setTextColor(context.getResources().getColor(R.color.playingcolor, null));
					holder.artist.setTextColor(context.getResources().getColor(R.color.playingcolor, null));
					holder.time.setTextColor(context.getResources().getColor(R.color.playingcolor, null));
				}else{
					holder.title.setTextColor(context.getResources().getColor(R.color.noplaycolor, null));
					holder.artist.setTextColor(context.getResources().getColor(R.color.noplaycolor, null));
					holder.time.setTextColor(context.getResources().getColor(R.color.noplaycolor, null));
				}
			} else {
				if(MainService.isPlay && position == MainActivity.getcurrentposition()){
					holder.title.setTextColor(context.getResources().getColor(R.color.playingcolor));
					holder.artist.setTextColor(context.getResources().getColor(R.color.playingcolor));
					holder.time.setTextColor(context.getResources().getColor(R.color.playingcolor));
				}else{
					holder.title.setTextColor(context.getResources().getColor(R.color.noplaycolor));
					holder.artist.setTextColor(context.getResources().getColor(R.color.noplaycolor));
					holder.time.setTextColor(context.getResources().getColor(R.color.noplaycolor));
				}
			}

			if(MainActivity._inActivity.mIsActionMode){
				holder.checkBox.setVisibility(View.VISIBLE);
				if(objects.get(position).getSelected())
					holder.checkBox.setChecked(true);
				else
					holder.checkBox.setChecked(false);
			}
			else{
				holder.checkBox.setVisibility(View.GONE);
				holder.checkBox.setChecked(false);
			}
		}
		return convertView;
	}
	void addItems(List<Audio> list, boolean update_position){
		this.objects.clear();
		this.objects.addAll(list);
		setupdatepostion(update_position);
		notifyDataSetChanged();
	}

	private void setupdatepostion(boolean update_position) {
		this.update_position = update_position;
	}
	public boolean getupdatepostion(){
		return update_position;
	}
}
