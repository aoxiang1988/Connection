package com.example.myautoapplication;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

class ListViewHolder  extends RecyclerView.ViewHolder {

    private final View mConvertView;
    public ListViewHolder(View convertView) {
        super(convertView);
        mConvertView = convertView;
    }

    private TextView mAudioTitle;
    public void setAudioTitleViewId(int viewId) {
        this.mAudioTitle = mConvertView.findViewById(viewId);
    }
    public TextView getAudioTitleView() {
        return mAudioTitle;
    }
}
