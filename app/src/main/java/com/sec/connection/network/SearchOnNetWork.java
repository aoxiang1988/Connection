package com.sec.connection.network;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sec.connection.R;
import com.sec.connection.data.Audio;
import com.sec.connection.data.CharacterParser;
import com.sec.connection.MainService;
import com.sec.connection.UserAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SearchOnNetWork extends Activity {

	/*****
	 * http://blog.csdn.net/kieven2008/article/details/8210737 maybe need
	 * http://www.2cto.com/kf/201605/504920.html   ***************/

	private ListView mSearchResult = null;
	private AutoCompleteTextView mTextViewMusicName;
	private static String ACTION_START_WEB_PLAY = "com.example.action.ACTION_START_WEB_PLAY";
	private static Thread thread;
	private static final int DRAW_LIST = 1;
	private int start = 0;//伪翻页
	private String mPreSearchMusic = null;
	private String mSearchMusicName = null;

	public static MainService mService = null;

	public static void initservice(MainService service) {
		mService = service;
	}

	int mNetPosition;
	String url;
	List<Audio> list = new ArrayList<>();
	Bundle bundle = new Bundle();

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
				case DRAW_LIST:
					//if(list == null){
					UserAdapter adapter = new UserAdapter(getApplicationContext(), R.layout.listitem, list, 1);
					adapter.notifyDataSetChanged();
					start = start + 20;
					mSearchResult.setAdapter(adapter);
					mSearchResult.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
												int arg2, long arg3) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(ACTION_START_WEB_PLAY);
							mNetPosition = arg2;
							url = list.get(mNetPosition).getNetUrl();
							intent.putExtra("music_url", url);
							intent.setClass(SearchOnNetWork.this, PlayWebActivity.class);
							startActivity(intent);
						}
					});
					break;
			}
		}
	};

	static boolean find = false;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchnetwork);
//		View view_1 = LayoutInflater.from(this).inflate(R.layout.mediasetting,null);
		TextView mActionTitle = findViewById(R.id.media_setting_text);
		mActionTitle.setText(getResources().getString(R.string.network_search));
		ImageView mActionBut = findViewById(R.id.media_back);
		mActionBut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		/*
		* */
		LocationManager locationManager;
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(false);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		assert locationManager != null;
		String provider = locationManager.getBestProvider(criteria,
				true);
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		Location location = locationManager
				.getLastKnownLocation(provider);
		updateWithNewLocation(location);


		Button mSearchButton = findViewById(R.id.search);
		mSearchResult = findViewById(R.id.serachresult);
		mTextViewMusicName = findViewById(R.id.getsearchmusicname);
		mSearchButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				assert inputMethodManager != null;
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				try {
					mSearchMusicName = URLEncoder.encode(mTextViewMusicName.getText().toString(), "utf8");
					if(!Objects.equals(mPreSearchMusic, mSearchMusicName)){
						start = 0;
						if(list != null)
							list.clear();
						mPreSearchMusic = mSearchMusicName;
						searchmusic();
					} else
						searchmusic();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		});
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	protected void searchmusic() {
		// TODO Auto-generated method stub
		thread = new Thread(DoSearchMusic);
		thread.start();
	}

	private void updateWithNewLocation(Location location) {
		String coordinate;
		//TextView loc = (TextView) findViewById(R.id.loc);
		String addressStr = "no address \n";
		CharacterParser characterParser = CharacterParser.getInstance();

		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			coordinate = "Latitude：" + lat + "\nLongitude：" + lng;
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			Geocoder geocoder = new Geocoder(this, Locale.getDefault());
			try {
				List<Address> addresses = geocoder.getFromLocation(latitude,
						longitude, 1);
				StringBuilder sb = new StringBuilder();
				if (addresses.size() > 0) {
					Address address = addresses.get(0);
//					for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
//						sb.append(address.getAddressLine(i)).append("\n");
//					}
					Log.d("bin1111.yang","pin yin : "+ characterParser.getSelling(address.getLocality()));
					sb.append(address.getLocality()).append("\n");
					sb.append(address.getCountryName());
					addressStr = sb.toString();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			coordinate = "no coordinate!\n";
		}

		Log.d("bin1111.yang","GPS : "+"your coordinate：\n" + coordinate + "\n"
				+ addressStr);
	}

	Runnable DoSearchMusic = new Runnable() {
		Document document = null;
		public void run() {
			// TODO Auto-generated method stub
			find = false;
			try {
				String search_music = "http://music.baidu.com/search?key="+ mSearchMusicName +"&start=" + start + "&size=20&third_type=0";
				if(!find){
					document = Jsoup.connect(search_music).data("query", "Java").timeout(5000).get();
					Elements songTitles = document.select("span.song-title");
					Elements songArtisters = document.select("span.singer");
		            Elements songInfos;
		            System.out.println(" " + songTitles);
		            int xyz = 0;
		            for (Element song : songTitles){
						String musicname = song.text();
						String artist = songArtisters.get(xyz).text();
						String url = "http://music.baidu.com" + song.select("a").attr("href");
						bundle.putString("TITLE", musicname);
						bundle.putString("ARTIST", artist);
						bundle.putString("NETURL", url);
						Audio audio = new Audio(bundle,getApplicationContext());
						list.add(audio);
						xyz = xyz + 1;
		            }
					find = true;
					handler.sendEmptyMessage(DRAW_LIST);
				}else{
					thread.join();
				}
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	/**url 转 bitmap**/
	private Bitmap getBitmap(String url) {
		Bitmap bm = null;
		try {
			URL iconUrl = new URL(url);
			URLConnection conn = iconUrl.openConnection();
			HttpURLConnection http = (HttpURLConnection) conn;

			int length = http.getContentLength();

			conn.connect();
			// 获得图像的字符流
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is, length);
			bm = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();// 关闭流
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return bm;
	}
}
