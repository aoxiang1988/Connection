package com.sec.connection.network;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.LayoutInflater;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SearchOnNetWork extends Activity {

	/*****
	 * http://blog.csdn.net/kieven2008/article/details/8210737 maybe need
	 * http://www.2cto.com/kf/201605/504920.html   ***************/

	private ListView searchresult = null;
	private static AutoCompleteTextView textviewmusicname;
	private static String ACTION_START_WEB_PLAY = "com.example.action.ACTION_START_WEB_PLAY";
	private static Thread thread;
	private static final int DRAW_LIST = 1;
	private int start = 0;//伪翻页
	private String presearchmusci = null;
	private String getsearchmusicname = null;

	public static MainService mService = null;

	public static void initservice(MainService service) {
		mService = service;
	}

	int netpostiton;
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
					searchresult.setAdapter(adapter);
					searchresult.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
												int arg2, long arg3) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(ACTION_START_WEB_PLAY);
							netpostiton = arg2;
							url = list.get(netpostiton).getNetUrl();
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
		String context = Context.LOCATION_SERVICE;
		locationManager = (LocationManager) getSystemService(context);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(false);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
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


		Button searchbutton = (Button) findViewById(R.id.search);
		searchresult = (ListView)findViewById(R.id.serachresult);
		textviewmusicname = (AutoCompleteTextView)findViewById(R.id.getsearchmusicname);
		searchbutton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


				try {
					getsearchmusicname = URLEncoder.encode(textviewmusicname.getText().toString(), "utf8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				if(!Objects.equals(presearchmusci, getsearchmusicname)){
					start = 0;
					if(list != null)
						list.clear();
					presearchmusci = getsearchmusicname;
					searchmusic();
				} else
					searchmusic();
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
		thread = new Thread(dosearchmusic);
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

	Runnable dosearchmusic = new Runnable() {
		Document document = null;
		public void run() {
			// TODO Auto-generated method stub
			try {
				find = false;
				String searchmusic = "http://music.baidu.com/search?key="+ getsearchmusicname +"&start=" + start + "&size=20&third_type=0";
				if(!find){
					document = Jsoup.connect(searchmusic).data("query", "Java").timeout(5000).get();
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
}
