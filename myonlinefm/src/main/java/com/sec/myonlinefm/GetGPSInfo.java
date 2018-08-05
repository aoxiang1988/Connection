package com.sec.myonlinefm;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.sec.myonlinefm.xmlcheck.CharacterParser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/31.
 */

public class GetGPSInfo {

    final private String UNKNOWN_ADDRESS = "unknown address";
    public static final String KEY_LOCAL_NAME = "local name key";
    public static final String KEY_LOCAL_NAME_FOR_THREAD = "local name key for thread";
    private String mLocalName = null;
    private String mLocalNameForThread = null;
    private Context mContext;
    public GetGPSInfo(Context context){
        mContext= context;
    }

    private LocationManager mLocationManager = null;
    private Handler mListDataHandler = new Handler();

    private LocationListener locationListener = new LocationListener() {
        /**
         * 位置信息变化时触发
         */
        public void onLocationChanged(Location location) {
            getLocalName(location);
        }

        /**
         * GPS状态变化时触发
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                //GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    Log.i("testw-a", "当前GPS状态为可见状态");
                    break;
                //GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    Log.i("testw-a", "当前GPS状态为服务区外状态");
                    break;
                //GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.i("testw-a", "当前GPS状态为暂停服务状态");
                    break;
            }
        }

        /**
         * GPS开启时触发
         */
        public void onProviderEnabled(String provider) {

        }

        /**
         * GPS禁用时触发
         */
        public void onProviderDisabled(String provider) {

        }
    };

    public Location getLocation() {

        String serviceString = Context.LOCATION_SERVICE;
        mLocationManager = (LocationManager) mContext.getSystemService(serviceString);
        Location gpsLocation = null;
        Location networkLocation = null;
        Location mLocation = null;
        gpsLocation = requestUpdatesFromProvider(mLocationManager.GPS_PROVIDER);
        networkLocation = requestUpdatesFromProvider(mLocationManager.NETWORK_PROVIDER);
        mLocation = gpsLocation;
        if (mLocation == null) {
            mLocation = networkLocation;
        }

        return mLocation;
    }

    private Location requestUpdatesFromProvider(String provider) {
        Location location = null;
        if (mLocationManager.isProviderEnabled(provider)) {
            if (ActivityCompat.checkSelfPermission(mContext,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat
                    .checkSelfPermission(mContext,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "不能获取位置权限，该功能无法使用,请手动给与权限", Toast.LENGTH_SHORT).show();
                //     finish();
            } else {
                mLocationManager.requestLocationUpdates(provider, 3600, 10000, locationListener);
                location = mLocationManager.getLastKnownLocation(provider);
            }
        }
        return location;
    }

    public String getLocalName(final Location mLocation) {
        mListDataHandler.post(new Runnable() {
            @Override
            public void run() {
                String addressStr = UNKNOWN_ADDRESS;
                String addressStrForThread = UNKNOWN_ADDRESS;

                if (mLocation != null) {

                    double latitude = mLocation.getLatitude();
                    double longitude = mLocation.getLongitude();

                    Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    StringBuilder sb = new StringBuilder();
                    if (addresses != null && addresses.size() > 0) {
                        Address address = addresses.get(0);
                        sb.append(address.getLocality());
                        addressStr = sb.toString();
                    }
                    addressStrForThread = addressStr.substring(0,2);
                    //zhuan pin yin
                    CharacterParser mCharacterParser = new CharacterParser();
                    addressStr = mCharacterParser.getSelling(addressStr);
                    //       RadioToast.showToast(getApplicationContext(),addressStr, Toast.LENGTH_SHORT);
                } else {
                    Toast.makeText(mContext, "位置获取失败，请手动检查设置", Toast.LENGTH_SHORT).show();
                }
                //      mSendMessage(GET_LOCAL_NAME,addressStr);
                mLocalName = getStringInfo(mContext, KEY_LOCAL_NAME);
                mLocalNameForThread = getStringInfo(mContext, KEY_LOCAL_NAME_FOR_THREAD);
                String newLocalName = addressStr;
                if ((mLocalName == null && mLocalNameForThread == null)
                        || (!mLocalName.equals(newLocalName)) && (!mLocalNameForThread.equals(addressStrForThread)) ) {
                    if (!newLocalName.equals(UNKNOWN_ADDRESS) && !addressStrForThread.equals(UNKNOWN_ADDRESS)) {
                        mLocalName = addressStr;
                        mLocalNameForThread = addressStrForThread;
                        saveStringInfo(mContext, KEY_LOCAL_NAME, mLocalName);
                        saveStringInfo(mContext, KEY_LOCAL_NAME_FOR_THREAD, mLocalNameForThread);
                        if(OnLineFMConnectManager.Companion.getMMainInfoCode() != null) {
                            OnLineFMConnectManager.Companion.getMMainInfoCode().startGetOnLineInfo();
                        }
                    } else {
                        Toast.makeText(mContext, "网络错误，暂时无法更新列表", Toast.LENGTH_SHORT).show();
                    }
                } else {

                }
            }
        });
        return mLocalName;
    }

    public String getStringInfo(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("list_demo", Context.MODE_PRIVATE);
        String datas = sp.getString(key, null);
        return datas;
    }

    public void saveStringInfo(Context context, String key, String datas) {
        SharedPreferences sp = context.getSharedPreferences("list_demo", Context.MODE_PRIVATE);
        sp.edit().putString(key, datas).commit();
    }
}
