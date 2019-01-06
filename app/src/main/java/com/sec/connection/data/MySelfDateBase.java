package com.sec.connection.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;


public class MySelfDateBase extends SQLiteOpenHelper {

    private static String mDateBaseName = "MyMusicDB.db";
    private static MySelfDateBase dateBase = null;
    private static int ALL_MUSIC = 1;

    public MySelfDateBase(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, mDateBaseName, factory, 1);
        dateBase = this;
    }

    public static MySelfDateBase getDateBase() {
        return dateBase;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE AllMusic(musicid INTEGER PRIMARY KEY AUTOINCREMENT," +
                "musicname VARCHAR(20), " +
                "titlekey  VARCHAR(20)," +
                "artist  VARCHAR(20)," +
                "artistkey VARCHAR(20)," +
                "composer VARCHAR(20)," +
                "album VARCHAR(20)," +
                "albumkey VARCHAR(20)," +
                "displayname VARCHAR(20)," +
                "mimetype VARCHAR(20)," +
                "path VARCHAR(100)," +
                "folderpath VARCHAR(100)," +
                "nettitle VARCHAR(20)," +
                "netartist VARCHAR(20)," +
                "neturl VARCHAR(100)," +
                "duration INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addDataBase(Audio music_info, int db_type) {
        SQLiteDatabase db = dateBase.getWritableDatabase();
        if(ALL_MUSIC == db_type) {
            db.execSQL("INSERT INTO AllMusic(musicid, musicname, " +
                            "artist, album, path, folderpath, duration) values(?,?,?,?,?,?,?)",
                    new String[]{String.valueOf(music_info.getId()),
                            music_info.getTitle(),
                            music_info.getArtist(),
                            music_info.getAlbum(),
                            music_info.getPath(),
                            music_info.getFolderPath(),
                            String.valueOf(music_info.getDuration())});
        }
    }

    public void delete(Integer id, int db_type) {
        SQLiteDatabase db = dateBase.getWritableDatabase();
        if(ALL_MUSIC == db_type) {
            db.execSQL("DELETE FROM AllMusic WHERE musicid = ?",
                    new String[]{String.valueOf(id)});
        }
    }

    public void update(Audio music, int db_type) {
        SQLiteDatabase db = dateBase.getWritableDatabase();
        if(ALL_MUSIC == db_type) {
            db.execSQL("UPDATE AllMusic SET musicname = ?,artist = ? WHERE musicid = ?",
                    new String[]{music.getTitle(), music.getArtist(), String.valueOf(music.getId())});
        }
    }

    public Audio find(Integer id , int db_type, Context context) {
        SQLiteDatabase db = dateBase.getReadableDatabase();
        if(ALL_MUSIC == db_type) {
            Cursor cursor = db.rawQuery("SELECT * FROM AllMusic WHERE musicid = ?",
                    new String[]{id.toString()});
            //存在数据才返回true
            if (cursor.moveToFirst()) {
                Bundle bundle = new Bundle();
                int music_id = cursor.getInt(cursor.getColumnIndex("musicid"));
                String title = cursor.getString(cursor.getColumnIndex("musicname"));
                String artist = cursor.getString(cursor.getColumnIndex("artist"));
                bundle.putInt(MediaStore.Audio.Media._ID, music_id);
                bundle.putString(MediaStore.Audio.Media.TITLE, title);
                bundle.putString(MediaStore.Audio.Media.ARTIST, artist);
                return new Audio(bundle, context);
            }
            cursor.close();
            return null;
        }
        return null;
    }

    public List<Audio> getScrollData(int offset, int maxResult, int db_type, Context context) {
        if(ALL_MUSIC == db_type) {
            List<Audio> music = new ArrayList<>();
            SQLiteDatabase db = dateBase.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM AllMusic ORDER BY musicid ASC LIMIT = ?,?",
                    new String[]{String.valueOf(offset), String.valueOf(maxResult)});
            while (cursor.moveToNext()) {
                int music_id = cursor.getInt(cursor.getColumnIndex("musicid"));
                String title = cursor.getString(cursor.getColumnIndex("musicname"));
                String artist = cursor.getString(cursor.getColumnIndex("artist"));
                Bundle bundle = new Bundle();
                bundle.putInt(MediaStore.Audio.Media._ID, music_id);
                bundle.putString(MediaStore.Audio.Media.TITLE, title);
                bundle.putString(MediaStore.Audio.Media.ARTIST, artist);
                music.add(new Audio(bundle, context));
            }
            cursor.close();
            return music;
        }
        return null;
    }

    public long getCount(int db_type) {
        SQLiteDatabase db = dateBase.getReadableDatabase();
        if(ALL_MUSIC == db_type) {
            Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM AllMusic", null);
            cursor.moveToFirst();
            long result = cursor.getLong(0);
            cursor.close();
            return result;
        }
        return 0;
    }
}
