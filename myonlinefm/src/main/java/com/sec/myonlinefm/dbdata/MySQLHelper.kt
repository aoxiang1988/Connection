package com.sec.myonlinefm.dbdata

import android.content.Context
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by SRC-TJ-MM-BinYang on 2018/5/10.
 */
class MySQLHelper : SQLiteOpenHelper {
    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(context, name, factory, version) {
        mMySQLHelper = this
    }

    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int, errorHandler: DatabaseErrorHandler?) : super(context, name, factory, version, errorHandler) {
        mMySQLHelper = this
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("CREATE TABLE fav_channels (" +
                "channel_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "category_id INTEGER, " +
                "channel_name VARCHAR(20), " +
                "channel_them_url VARCHAR(100), " +
                "pod_caster_name VARCHAR(100))"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    companion object {
        private var mMySQLHelper: MySQLHelper? = null
        fun getInstances(): MySQLHelper? {
            return mMySQLHelper
        }
    }
}