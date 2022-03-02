package com.sec.myonlinefm

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.sec.myonlinefm.OnLineFMConnectManager.Companion.mMainInfoCode
import com.sec.myonlinefm.xmlcheck.CharacterParser
import java.io.IOException
import java.util.*

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/31.
 */
class GetGPSInfo(private val mContext: Context?) {
    private val UNKNOWN_ADDRESS: String = "unknown address"
    private var mLocalName: String? = null
    private var mLocalNameForThread: String? = null
    private var mLocationManager: LocationManager? = null
    private val mListDataHandler: Handler = Handler()
    private val locationListener: LocationListener = object : LocationListener {
        /**
         * 位置信息变化时触发
         */
        override fun onLocationChanged(location: Location) {
            getLocalName(location)
        }

        /**
         * GPS开启时触发
         */
        override fun onProviderEnabled(provider: String) {
        }

        /**
         * GPS禁用时触发
         */
        override fun onProviderDisabled(provider: String) {

        }
    }

    fun getLocation(): Location? {
        val serviceString = Context.LOCATION_SERVICE
        mLocationManager = mContext!!.getSystemService(serviceString) as LocationManager
        var gpsLocation: Location? = null
        var networkLocation: Location? = null
        var mLocation: Location? = null
        gpsLocation = requestUpdatesFromProvider(LocationManager.GPS_PROVIDER)
        networkLocation = requestUpdatesFromProvider(LocationManager.NETWORK_PROVIDER)
        mLocation = gpsLocation
        if (mLocation == null) {
            mLocation = networkLocation
        }
        return mLocation
    }

    private fun requestUpdatesFromProvider(provider: String?): Location? {
        var location: Location? = null
        if (mLocationManager!!.isProviderEnabled(provider!!)) {
            if (ActivityCompat.checkSelfPermission(mContext!!,
                            Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat
                            .checkSelfPermission(mContext,
                                    Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "不能获取位置权限，该功能无法使用,请手动给与权限", Toast.LENGTH_SHORT).show()
                //     finish();
            } else {
                mLocationManager!!.requestLocationUpdates(provider, 3600, 10000f, locationListener!!)
                location = mLocationManager!!.getLastKnownLocation(provider)
            }
        }
        return location
    }

    fun getLocalName(mLocation: Location?): String? {
        mListDataHandler!!.post(Runnable {
            var addressStr = UNKNOWN_ADDRESS
            var addressStrForThread = UNKNOWN_ADDRESS
            if (mLocation != null) {
                val latitude = mLocation.latitude
                val longitude = mLocation.longitude
                val geocoder = Geocoder(mContext, Locale.getDefault())
                var addresses: MutableList<Address?>? = null
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                val sb = StringBuilder()
                if (addresses != null && addresses.size > 0) {
                    val address = addresses[0]
                    sb.append(address!!.getLocality())
                    addressStr = sb.toString()
                }
                addressStrForThread = addressStr!!.substring(0, 2)
                //zhuan pin yin
                val mCharacterParser = CharacterParser()
                addressStr = mCharacterParser.getSelling(addressStr)!!
                //       RadioToast.showToast(getApplicationContext(),addressStr, Toast.LENGTH_SHORT);
            } else {
                Toast.makeText(mContext, "位置获取失败，请手动检查设置", Toast.LENGTH_SHORT).show()
            }
            //      mSendMessage(GET_LOCAL_NAME,addressStr);
            mLocalName = getStringInfo(mContext, KEY_LOCAL_NAME)
            mLocalNameForThread = getStringInfo(mContext, KEY_LOCAL_NAME_FOR_THREAD)
            val newLocalName = addressStr
            if (mLocalName == null && mLocalNameForThread == null
                    || mLocalName != newLocalName && mLocalNameForThread != addressStrForThread) {
                if (newLocalName != UNKNOWN_ADDRESS && addressStrForThread != UNKNOWN_ADDRESS) {
                    mLocalName = addressStr
                    mLocalNameForThread = addressStrForThread
                    saveStringInfo(mContext, KEY_LOCAL_NAME, mLocalName)
                    saveStringInfo(mContext, KEY_LOCAL_NAME_FOR_THREAD, mLocalNameForThread)
                    if (mMainInfoCode != null) {
                        mMainInfoCode!!.startGetOnLineInfo()
                    }
                } else {
                    Toast.makeText(mContext, "网络错误，暂时无法更新列表", Toast.LENGTH_SHORT).show()
                }
            } else {
            }
        })
        return mLocalName
    }

    fun getStringInfo(context: Context?, key: String?): String? {
        val sp = context!!.getSharedPreferences("list_demo", Context.MODE_PRIVATE)
        return sp.getString(key, null)
    }

    fun saveStringInfo(context: Context?, key: String?, datas: String?) {
        val sp = context!!.getSharedPreferences("list_demo", Context.MODE_PRIVATE)
        sp.edit().putString(key, datas).commit()
    }

    companion object {
        val KEY_LOCAL_NAME: String? = "local name key"
        val KEY_LOCAL_NAME_FOR_THREAD: String? = "local name key for thread"
    }
}