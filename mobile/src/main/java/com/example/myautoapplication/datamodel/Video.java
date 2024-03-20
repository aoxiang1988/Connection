package com.example.myautoapplication.datamodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;

public class Video {
    public String[] info = new String[6];
    private String mTitle ,
            mArtist,
            mAlbum,
            mDisplayName,
            mMimeType,
            mPath,
            mFolderPath;

    private int mId;
    private int mDuration = 0,
            mSize = 0,
            mPosition = 0;
    private boolean isSelected = false,
                    isNormalVideo = true;

    private Bitmap mAlbumBitmap = null;
    private Drawable mVideoDrawable = null;
    public Video(Bundle bundle, Context context){
        mId = bundle.getInt(MediaStore.Video.Media._ID);
        mTitle = bundle.getString(MediaStore.Video.Media.TITLE);
        mArtist = bundle.getString(MediaStore.Video.Media.ARTIST);
        mAlbum = bundle.getString(MediaStore.Video.Media.ALBUM);
        mDisplayName = bundle.getString(MediaStore.Video.Media.DISPLAY_NAME);
        mMimeType = bundle.getString(MediaStore.Video.Media.MIME_TYPE);
        mPath = bundle.getString(MediaStore.Video.Media.DATA);
        mFolderPath = setFolderPath();
        mDuration = bundle.getInt(MediaStore.Video.Media.DURATION);
        if (mDuration <= 0) {
            isNormalVideo = false;
        }
        mSize = bundle.getInt(MediaStore.Video.Media.SIZE);
        //mAlbumBitmap = MusicImageResource.getArtwork(context, mId, mAlbumId);
        addToInfo();
    }
    public int getId() {
        return mId;
    }
    public Bitmap getBitmap(){
        return mAlbumBitmap;
    }

    public Drawable getVideoDrawable(){
        return mVideoDrawable;
    }

    public void setVideoBitmap(Drawable img){
        mVideoDrawable = img;
    }

    public String getMimeType () {
        return mMimeType;
    }

    public boolean getIsNormalVideo() {
        return isNormalVideo;
    }

    public String getDuration () {
        if (mDuration <= 0) {
            isNormalVideo = false;
        }
        int hour = ((mDuration / 1000) / 60) /60;
        int min = (mDuration / 1000) / 60;
        int sec = (mDuration / 1000) % 60;

        String string_min;
        String string_sec;
        String string_hour;

        if (hour < 10) {
            string_hour = String.format("0%s", hour);
        } else {
            string_hour = String.format("%s", hour);
        }
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
        return String.format("%s:%s:%s", string_hour, string_min, string_sec);
    }

    public int getSize () {
        return mSize;
    }

    public String getTitle () {
        return mTitle;
    }

    public String getArtist () {
        return mArtist;
    }

    public String getAlbum () {
        return mAlbum;
    }

    public String getDisplayName () {
        return mDisplayName;
    }

    public String getPath () {
        return mPath;
    }

    public void setPosition (int position) {
        position = mPosition;
    }

    public void setSelected(boolean isSelected){
        this.isSelected = isSelected;
    }

    public boolean getSelected() {
        return isSelected;
    }

    private void addToInfo(){
        info[0] = mTitle;
        info[1] = mArtist;
        info[2] = mFolderPath;
        info[3] = Integer.toString(mSize);
    }

    private String setFolderPath(){
        String mFolderPath = null;
        if(mPath != null) {
            String new_path = mPath.replace(".", "@");
            String[] splitPath = new_path.split("@");
            mFolderPath = splitPath[0].substring(0, splitPath[0].lastIndexOf("/"));
        }
        return mFolderPath;
    }
    public String getFolderPath(){
        return mFolderPath;
    }

    public String getInfo(int i) {
        return info[i];
    }
}
