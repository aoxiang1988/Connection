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

	@NonNull
    @SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Audio audio = objects.get(position);
        Log.d("bin1111.yang","title : "+audio.getTitle());
		LinearLayout linearLayout = new LinearLayout(getContext());
		String inString = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(inString);
		inflater.inflate(resource, linearLayout, true);
		TextView title = (TextView) linearLayout.findViewById(R.id.musictitle);
		TextView artist = (TextView) linearLayout.findViewById(R.id.musicartist);
		TextView time = (TextView) linearLayout.findViewById(R.id.musictime);
		ImageView imageView = (ImageView) linearLayout.findViewById(R.id.listmusicpicture);
		CheckBox checkBox = (CheckBox) linearLayout.findViewById(R.id.checkBox1);

		Bitmap bitmap = null;
		bitmap = audio.getBitmap();
		if(bitmap != null){
			imageView.setImageBitmap(bitmap);
		}
		if(which == 1){
			title.setText(audio.getNetTitle());
			artist.setText(audio.getNetArtist());
			time.setVisibility(View.GONE);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				title.setTextColor(context.getResources().getColor(R.color.noplaycolor, null));
				artist.setTextColor(context.getResources().getColor(R.color.noplaycolor, null));
			} else {
				title.setTextColor(context.getResources().getColor(R.color.noplaycolor));
				artist.setTextColor(context.getResources().getColor(R.color.noplaycolor));
			}

		}else{
			title.setText(audio.getTitle());
			artist.setText(audio.getArtist());

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
			time.setText(String.format("%s:%s", string_min, string_sec));
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				if(MainService.isPlay && position == MainActivity.getcurrentposition()){
					title.setTextColor(context.getResources().getColor(R.color.playingcolor, null));
					artist.setTextColor(context.getResources().getColor(R.color.playingcolor, null));
					time.setTextColor(context.getResources().getColor(R.color.playingcolor, null));
				}else{
					title.setTextColor(context.getResources().getColor(R.color.noplaycolor, null));
					artist.setTextColor(context.getResources().getColor(R.color.noplaycolor, null));
					time.setTextColor(context.getResources().getColor(R.color.noplaycolor, null));
				}
			} else {
				if(MainService.isPlay && position == MainActivity.getcurrentposition()){
					title.setTextColor(context.getResources().getColor(R.color.playingcolor));
					artist.setTextColor(context.getResources().getColor(R.color.playingcolor));
					time.setTextColor(context.getResources().getColor(R.color.playingcolor));
				}else{
					title.setTextColor(context.getResources().getColor(R.color.noplaycolor));
					artist.setTextColor(context.getResources().getColor(R.color.noplaycolor));
					time.setTextColor(context.getResources().getColor(R.color.noplaycolor));
				}
			}

			if(MainActivity._inActivity.mIsActionMode){
				checkBox.setVisibility(View.VISIBLE);
				if(objects.get(position).getSelected())
					checkBox.setChecked(true);
				else
					checkBox.setChecked(false);
			}
			else{
				checkBox.setVisibility(View.GONE);
				checkBox.setChecked(false);
			}
		}
		return linearLayout;
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
