package com.sec.myonlinefm.dbdata;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/5/10.
 */

public class MySQLHelper extends SQLiteOpenHelper {

    private static MySQLHelper mMySQLHelper;

    public MySQLHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mMySQLHelper = this;
    }

    public MySQLHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        mMySQLHelper = this;
    }

    public static MySQLHelper getInstances() {
        return mMySQLHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE fav_channels (" +
                "channel_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "category_id INTEGER, " +
                "channel_name VARCHAR(20), " +
                "channel_them_url VARCHAR(100), " +
                "pod_caster_name VARCHAR(100))"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
