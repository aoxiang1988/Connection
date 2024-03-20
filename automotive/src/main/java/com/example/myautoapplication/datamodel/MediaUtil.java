package com.example.myautoapplication.datamodel;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MediaUtil {

    private static final String TAG = "MediaUtil";

    /**
     * audio info*/
    private static final String[] AUDIO_KEY = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.TITLE_KEY,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_KEY,
            MediaStore.Audio.Media.COMPOSER,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_KEY,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.IS_RINGTONE,
            MediaStore.Audio.Media.IS_PODCAST,
            MediaStore.Audio.Media.IS_ALARM,
            MediaStore.Audio.Media.IS_MUSIC,
            MediaStore.Audio.Media.IS_NOTIFICATION,
    };

    public static List<Audio> getAudioList(Context context){

        Log.d(TAG, "getAudioList start!!");

        List<Audio> audioList = new ArrayList<>();
        Cursor cursor = context
                .getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        null, null, null, null);
        assert cursor != null;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            Bundle bundle = new Bundle();
            for (final String key : AUDIO_KEY) {
                final int columnIndex = cursor.getColumnIndex(key);
                final int type = cursor.getType(columnIndex);
                switch (type) {
                    case Cursor.FIELD_TYPE_FLOAT:
                        float floatValue = cursor.getFloat(columnIndex);
                        bundle.putFloat(key, floatValue);
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        int intValue = cursor.getInt(columnIndex);
                        bundle.putInt(key, intValue);
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        String stringValue = cursor.getString(columnIndex);
                        if (Objects.equals(key, MediaStore.Audio.Media.ARTIST)
                                && Objects.equals(stringValue, "<unknown>"))
                            stringValue = "<unknown>";
                        Log.d(TAG, stringValue);
                        bundle.putString(key, stringValue);
                        break;

                    default:
                        break;
                }
            }
            Audio audio = new Audio(bundle,context);
            audioList.add(audio);
        }
        cursor.close();
        return audioList;
    }

    /**
     * video info*/
    private static final String[] VIDEO_KEY = {
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.ARTIST,
            MediaStore.Video.Media.ALBUM,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
    };

    public static List<Video> getVideoList(Context context){
        List<Video> videoList = new ArrayList<>();
        Cursor cursor = context
                .getContentResolver()
                .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        null, null, null, null);
        assert cursor != null;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            Bundle bundle = new Bundle();
            for (final String key : VIDEO_KEY) {
                final int columnIndex = cursor.getColumnIndex(key);
                final int type = cursor.getType(columnIndex);
                switch (type) {
                    case Cursor.FIELD_TYPE_FLOAT:
                        float floatValue = cursor.getFloat(columnIndex);
                        bundle.putFloat(key, floatValue);
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        int intValue = cursor.getInt(columnIndex);
                        bundle.putInt(key, intValue);
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        String stringValue = cursor.getString(columnIndex);
                        if (Objects.equals(key, MediaStore.Audio.Media.ARTIST)
                                && Objects.equals(stringValue, "<unknown>"))
                            stringValue = "<unknown>";
                        Log.d(TAG, stringValue);
                        bundle.putString(key, stringValue);
                        break;

                    default:
                        break;
                }
            }
            Video Video = new Video(bundle,context);
            videoList.add(Video);
        }
        cursor.close();
        return videoList;
    }
}

