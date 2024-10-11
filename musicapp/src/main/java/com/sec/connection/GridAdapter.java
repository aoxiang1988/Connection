package com.sec.connection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sec.connection.data.Audio;

import java.util.List;
import java.util.Map;

/**
 * Created by SRC-TJ-MM-BinYang on 2017/9/25.
 */

    /*
        grid adpter
     */
public class GridAdapter extends BaseAdapter {

    private final List<String> alumblist;
    private final Context mContext;
    private Map<String, List<Audio>> mapAlbum = null;

    public GridAdapter(Context context,List<String> alumblist,Map<String, List<Audio>> map_alumb){
        this.mContext = context;
        this.alumblist = alumblist;
        this.mapAlbum = map_alumb;
    }

    @Override
    public int getCount() {
        return alumblist.size();
    }

    @Override
    public Object getItem(int position) {
        return alumblist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grid_item_layout, null);
            View convertView1 = convertView;
        }
        ImageView album_image = (ImageView)convertView.findViewById(R.id.alumb_imageView);
        if(mapAlbum.get(alumblist.get(position)).get(0).getBitmap() != null){
            album_image.setImageBitmap(mapAlbum.get(alumblist.get(position)).get(0).getBitmap());
        }else{
            album_image.setImageResource(R.drawable.ic);
        }

        TextView album_text = (TextView)convertView.findViewById(R.id.alumb_text);
        album_text.setText(alumblist.get(position));

        return convertView;
    }

    public void setItemHide(int i) {
        if (i == -1) {
        }
    }
}
