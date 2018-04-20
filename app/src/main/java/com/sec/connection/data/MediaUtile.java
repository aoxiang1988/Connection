package com.sec.connection.data;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.sec.connection.R;
import com.sec.connection.StartActivity;
import com.sec.connection.aspectcode.PermissionCheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MediaUtile {
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
	@PermissionCheck(permession = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE,})
	public static List<Audio> getAudioList(Context context){
		List<Audio> audioList = new ArrayList<>();
		Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, AUDIO_KEY, null, null, null);
		assert cursor != null;
		for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
			Bundle bundle = new Bundle();
			for (final String key : AUDIO_KEY) {
				final int columnIndex = cursor.getColumnIndex(key);
				final int type = cursor.getType(columnIndex);
				switch (type) {
					case Cursor.FIELD_TYPE_BLOB:
						break;
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
						if (Objects.equals(key, MediaStore.Audio.Media.ARTIST)
								&& Objects.equals(stringValue, "<unknown>"))
							stringValue = context.getString(R.string.unknown);
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
}
