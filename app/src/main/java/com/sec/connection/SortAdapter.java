package com.sec.connection;

/**
 * Created by SRC-TJ-MM-BinYang on 2017/9/14.
 */


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.sec.connection.data.SortModel;

import java.util.List;

@SuppressLint("NewApi")
public class SortAdapter extends BaseAdapter implements SectionIndexer{
    private List<SortModel> list = null;
    private Context mContext;
    private int playingposition = 0;

    public SortAdapter(Context mContext, List<SortModel> list) {
        this.mContext = mContext;
        this.list = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     * @param list
     */
    public void updateListView(List<SortModel> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final SortModel mContent = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item, null);
            viewHolder.side_musictitle = (TextView) view.findViewById(R.id.side_musictitle);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
            viewHolder.side_listmusicpicture = (ImageView)view.findViewById(R.id.side_listmusicpicture) ;
            viewHolder.side_musicartist = (TextView) view.findViewById(R.id.side_musicartist);
            viewHolder.side_musictime = (TextView) view.findViewById(R.id.side_musictime);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        //根据position获取分类的首字母的char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if(position == getPositionForSection(section)){
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.getSortLetters());
        }else{
            viewHolder.tvLetter.setVisibility(View.GONE);
        }

        if(this.list.get(position).getAudio().getBitmap() != null)
            viewHolder.side_listmusicpicture.setImageBitmap(this.list.get(position).getAudio().getBitmap());
        else
            viewHolder.side_listmusicpicture.setImageResource(R.drawable.ic);

        if(mContext.getClass().getName().equals("MusicApplation")){
            if (this.list.get(position).getAudio() != null) {
                viewHolder.side_musictitle.setText(this.list.get(position).getAudio().getNetTitle());
                viewHolder.side_musicartist.setText(this.list.get(position).getAudio().getNetArtist());
            }
            viewHolder.side_musictime.setVisibility(View.GONE);
            viewHolder.side_musictitle.setTextColor(mContext.getResources().getColor(R.color.noplaycolor, null));
            viewHolder.side_musicartist.setTextColor(mContext.getResources().getColor(R.color.noplaycolor, null));
        }else{
            if (this.list.get(position).getAudio() != null) {
                viewHolder.side_musictitle.setText(this.list.get(position).getAudio().getTitle());
                viewHolder.side_musicartist.setText(this.list.get(position).getAudio().getArtist());

                int min = (this.list.get(position).getAudio().getDuration() / 1000) / 60;
                int sec = (this.list.get(position).getAudio().getDuration() / 1000) % 60;
                String stringmin;
                String stringsec;
                if (min < 10) {
                    stringmin = String.format("0%s", min);
                } else {
                    stringmin = String.format("%s", min);
                }
                if (sec < 10) {
                    stringsec = String.format("0%s", sec);
                } else {
                    stringsec = String.format("%s", sec);
                }
                viewHolder.side_musictime.setText(String.format("%s:%s", stringmin, stringsec));
            }
            if(MainService.isPlay && position == playingposition){
                viewHolder.side_musictitle.setTextColor(mContext.getResources().getColor(R.color.playingcolor, null));
                viewHolder.side_musicartist.setTextColor(mContext.getResources().getColor(R.color.playingcolor, null));
                viewHolder.side_musictime.setTextColor(mContext.getResources().getColor(R.color.playingcolor, null));
            }else{
                viewHolder.side_musictitle.setTextColor(mContext.getResources().getColor(R.color.noplaycolor, null));
                viewHolder.side_musicartist.setTextColor(mContext.getResources().getColor(R.color.noplaycolor, null));
                viewHolder.side_musictime.setTextColor(mContext.getResources().getColor(R.color.noplaycolor, null));
            }
        }


        return view;

    }



    final static class ViewHolder {
        TextView tvLetter;
        TextView side_musictitle;
        ImageView side_listmusicpicture;
        TextView side_musicartist;
        TextView side_musictime;
    }

    public void setPlayingPosition(int playingposition){
        this.playingposition = playingposition;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的char ascii值
     */
    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }


    @Override
    public Object[] getSections() {
        return null;
    }
}
