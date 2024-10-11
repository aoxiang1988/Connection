package com.sec.connection.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;

import com.sec.connection.view.MusicImageResource;

import java.io.Serializable;

public class Audio implements Serializable {

    public String[] info = new String[6];
	private final String mTitle;
    private final String mTitleKey;
    private final String mArtist;
    private final String mArtistKey;
    private final String mComposer;
    private final String mAlbum;
    private final String mAlbumKey;
    private final String mDisplayName;
    private final String mMimeType;
    private final String mPath;
    private final String mFolderPath;
    private final String mNetTitle;
    private final String mNetArtist;
    private final String mNetUrl;
	private final int mId,
				mArtistId,
				mAlbumId,
				mYear, 
				mTrack;
	private int mDuration = 0;
    private int mSize = 0;
    private final int mPosition = 0;
	private boolean isSelected = false;
    private final int isRingtone,
                    isPodcast,
            		isAlarm,
            		isMusic ,
            		isNotification ;

	private Bitmap mAlbumbitmap = null;
	@SuppressLint("SuspiciousIndentation")
    public Audio(Bundle bundle, Context context){
		mId = bundle.getInt(MediaStore.Audio.Media._ID);
		mTitle = bundle.getString(MediaStore.Audio.Media.TITLE);
		mTitleKey = bundle.getString(MediaStore.Audio.Media.TITLE_KEY);
		mArtist = bundle.getString(MediaStore.Audio.Media.ARTIST);
		mArtistKey = bundle.getString(MediaStore.Audio.Media.ARTIST_KEY);
		mComposer = bundle.getString(MediaStore.Audio.Media.COMPOSER);
		mAlbum = bundle.getString(MediaStore.Audio.Media.ALBUM);
		mAlbumKey = bundle.getString(MediaStore.Audio.Media.ALBUM_KEY);
		mDisplayName = bundle.getString(MediaStore.Audio.Media.DISPLAY_NAME);
		mMimeType = bundle.getString(MediaStore.Audio.Media.MIME_TYPE);
		mPath = bundle.getString(MediaStore.Audio.Media.DATA);
        mFolderPath = setFolderPath();
		mArtistId = bundle.getInt(MediaStore.Audio.Media.ARTIST_ID);
		mAlbumId = bundle.getInt(MediaStore.Audio.Media.ALBUM_ID);
		mYear = bundle.getInt(MediaStore.Audio.Media.YEAR);
		mTrack = bundle.getInt(MediaStore.Audio.Media.TRACK);
		mDuration = bundle.getInt(MediaStore.Audio.Media.DURATION);
		mSize = bundle.getInt(MediaStore.Audio.Media.SIZE);
		isRingtone = bundle.getInt(MediaStore.Audio.Media.IS_RINGTONE);
		isPodcast  = bundle.getInt(MediaStore.Audio.Media.IS_PODCAST);
		isAlarm = bundle.getInt(MediaStore.Audio.Media.IS_ALARM);
		isMusic = bundle.getInt(MediaStore.Audio.Media.IS_MUSIC);
		isNotification  = bundle.getInt(MediaStore.Audio.Media.IS_NOTIFICATION);
		mNetTitle = bundle.getString("TITLE");
		mNetArtist = bundle.getString("ARTIST");
		mNetUrl = bundle.getString("NETURL");
		mAlbumbitmap = MusicImageResource.getArtwork(context, mId, mAlbumId);
        addToInfo();
	}
	public int getId() {  
        return mId;  
    }  
    public Bitmap getBitmap(){
    	return mAlbumbitmap;
    }
    public String getMimeType () {
        return mMimeType;
    }

    public int getDuration () {  
        return mDuration;  
    }

    public int getSize () {  
        return mSize;  
    }  

    public int isRingtone () {
        return isRingtone;
    }

    public int isPodcast () {
        return isPodcast;
    }

    public int isAlarm () {
        return isAlarm;
    }

    public int isMusic () {
        return isMusic;  
    }

    public int isNotification () {
        return isNotification;
    }

    public String getTitle () {  
        return mTitle;  
    }  
      
    public String getTitleKey () {
        return mTitleKey;
    }

    public String getArtist () {
        return mArtist;
    }  
      
    public int getArtistId () {
        return mArtistId;
    }

    public String getArtistKey () {
        return mArtistKey;
    }

    public String getComposer () {
        return mComposer;
    }

    public String getAlbum () {  
        return mAlbum;  
    }  
      
    public int getAlbumId () {
        return mAlbumId;
    }

    public String getAlbumKey () {
        return mAlbumKey;
    }

    public String getDisplayName () {  
        return mDisplayName;  
    }  
      
    public int getYear () {  
        return mYear;  
    }  
      
    public int getTrack () {  
        return mTrack;  
    }  
      
    public String getPath () {  
        return mPath;  
    }
    
    public String getNetTitle(){
    	return mNetTitle;
    }
    public String getNetArtist(){
    	return mNetArtist;
    }
    public String getNetUrl(){
    	return mNetUrl;
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
        info[4] = Integer.toString(mYear);
        info[5] = mComposer;
    }

    private String setFolderPath(){
        String mFolderPath = null;
        if(mPath != null) {
            String new_path = mPath.replace(".", "@");
            String[] splitpath = new_path.split("@");
            mFolderPath = splitpath[0].substring(0, splitpath[0].lastIndexOf("/"));
        }
        return mFolderPath;
    }
    public String getFolderPath(){
        return mFolderPath;
    }

    public String getinfo(int i) {
        return info[i];
    }
}
