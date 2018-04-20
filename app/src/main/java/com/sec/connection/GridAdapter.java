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

    private List<String> alumblist;
    private Context mContext;
    private Map<String, List<Audio>> map_alumb = null;
    private View convertView = null;

    public GridAdapter(Context context,List<String> alumblist,Map<String, List<Audio>> map_alumb){
        this.mContext = context;
        this.alumblist = alumblist;
        this.map_alumb = map_alumb;
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
            this.convertView = convertView;
        }
        ImageView alumb_image = (ImageView)convertView.findViewById(R.id.alumb_imageView);
        if(map_alumb.get(alumblist.get(position)).get(0).getBitmap() != null){
            alumb_image.setImageBitmap(map_alumb.get(alumblist.get(position)).get(0).getBitmap());
        }else{
            alumb_image.setImageResource(R.drawable.ic);
        }

        TextView alumb_text = (TextView)convertView.findViewById(R.id.alumb_text);
        alumb_text.setText(alumblist.get(position));

        return convertView;
    }

    public void setItemHide(int i) {
        switch (i){
            case -1:
                break;
            default:
                break;
        }
    }
}
