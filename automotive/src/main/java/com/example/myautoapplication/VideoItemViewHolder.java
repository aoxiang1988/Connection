package com.example.myautoapplication;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class VideoItemViewHolder extends RecyclerView.ViewHolder {

    private final View mConvertView;
    private TextView mTitle;
    private TextView mArtist;
    private ImageView mImageView;
    private TextView mTime;

    public VideoItemViewHolder(View convertView) {
        super(convertView);
        mConvertView = convertView;
    }

    public void setArtistViewId(int viewId) {
        this.mArtist = mConvertView.findViewById(viewId);
    }

    public void setImageViewId(int viewId) {
        this.mImageView = mConvertView.findViewById(viewId);
    }

    public void setTimeViewId(int viewId) {
        this.mTime = mConvertView.findViewById(viewId);
    }

    public void setTitleViewId(int viewId) {
        this.mTitle = mConvertView.findViewById(viewId);
    }

    public ImageView getImageView() {
        return mImageView;
    }

    public TextView getTimeView() {
        return mTime;
    }

    public TextView getArtistView() {
        return mArtist;
    }

    public TextView getTitleView() {
        return mTitle;
    }

    @SuppressLint("ResourceAsColor")
    public void itemPlayingDisplayOff() {
        mTitle.setTextColor(R.color.black);
        mArtist.setTextColor(R.color.black);
        mTime.setTextColor(R.color.black);
    }

    @SuppressLint("ResourceAsColor")
    public void itemPlayingDisplayOn() {
        mTitle.setTextColor(R.color.video_on);
        mArtist.setTextColor(R.color.video_on);
        mTime.setTextColor(R.color.video_on);
    }
}
