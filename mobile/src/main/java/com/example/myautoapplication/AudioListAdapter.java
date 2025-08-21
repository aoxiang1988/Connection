package com.example.myautoapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.myautoapplication.datamodel.Audio;

import java.util.List;

public class AudioListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Audio> mAudioList;

    public AudioListAdapter(Context context, List<Audio> audioList) {
        mContext = context;
        mAudioList = audioList;
    }

    public void setAudioList(List<Audio> mAudioList) {
        this.mAudioList = mAudioList;
    }

    @Override
    public int getCount() {
        if (mAudioList == null)
            return 0;
        else
            return mAudioList.size();
    }

    @Override
    public Audio getItem(int position) {
        if (mAudioList == null)
            return null;
        else
            return mAudioList.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (mAudioList == null)
            return 0;
        else
            return mAudioList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListViewHolder listViewHolder;
        Audio audio = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_view, null);
            //View finalConvertView = convertView;
            listViewHolder = new ListViewHolder(convertView);
            convertView.setTag(listViewHolder);
        } else listViewHolder = (ListViewHolder) convertView.getTag();

        listViewHolder.setAudioTitleViewId(R.id.audio_title);
        listViewHolder.getAudioTitleView().setText(audio.getTitle());

        return convertView;
    }
}
