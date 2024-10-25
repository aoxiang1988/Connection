package com.dten.videoplayer.data;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

import com.dten.videoplayer.Movie;
import com.dten.videoplayer.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MediaUtils {
	private static final String[] MEDIA_KEY = {
			MediaStore.Video.Media._ID,
			MediaStore.Video.Media.TITLE,
			MediaStore.Video.Media.DATA,
			MediaStore.Video.Media.SIZE,
			//MediaStore.Video.Media._COUNT,
			MediaStore.Video.Media.CD_TRACK_NUMBER,
			MediaStore.Video.Media.DEFAULT_SORT_ORDER,
			MediaStore.Video.Media.DISPLAY_NAME,
			MediaStore.Video.Media.DURATION,
			MediaStore.Video.Media.WIDTH,
			MediaStore.Video.Media.HEIGHT,
			MediaStore.Video.Media.RELATIVE_PATH,
			MediaStore.Video.Media.OWNER_PACKAGE_NAME,
	};

	public static List<Movie> getMediaList(Context context){
		List<Movie> mediaList = new ArrayList<>();
		Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, MEDIA_KEY, null, null, null);
		assert cursor != null;
		for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
			Bundle bundle = new Bundle();
			for (final String key : MEDIA_KEY) {
				final int columnIndex = cursor.getColumnIndexOrThrow(key);
				final int type = cursor.getType(columnIndex);
				switch (type) {
					case Cursor.FIELD_TYPE_BLOB:
					case Cursor.FIELD_TYPE_NULL:
						break;
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
						bundle.putString(key, stringValue);
						break;

					default:
						break;
				}
			}
			Movie movie = new Movie(bundle,context);
			mediaList.add(movie);
		}
		cursor.close();
		return mediaList;
	}
}
