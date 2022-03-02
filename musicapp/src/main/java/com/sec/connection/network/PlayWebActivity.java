package com.sec.connection.network;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

import com.sec.connection.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class PlayWebActivity extends AppCompatActivity {
    private static String ACTION_START_WEB_PLAY = "com.example.action.ACTION_START_WEB_PLAY";
    private static String current_url = null;
//    private WebView playweb;
    private ImageView mWebMusicPic;
    private Bitmap bitmap = null;
    private static final int SET_MAIN_VIEW = 1;

    Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch(msg.what){
                case SET_MAIN_VIEW:
                    mWebMusicPic.setImageBitmap(bitmap);
                    thread.interrupt();
                    thread = null;
                    break;
            }
        }
    };

    /**read the URL**/
    private Runnable dosearchmusic = new Runnable() {

        Document document = null;
        @Override
        public void run() {
            try {
                document = Jsoup.connect(current_url).data("query", "Java").timeout(500).get();
                Elements picture = document.select("div.main-body-cont");

                String picture_url = picture.select("img").first().attr("src");
                Log.d("bin1111.yang", "picture_url : "+picture_url);
                bitmap = getBitmap(picture_url);
                handler.sendEmptyMessage(SET_MAIN_VIEW);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
    private Thread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_web);

        mWebMusicPic = (ImageView)findViewById(R.id.web_music_image_View);

        Intent intent = getIntent();
        if(Objects.equals(intent.getAction(), ACTION_START_WEB_PLAY)){
            current_url = intent.getStringExtra("music_url");
            Log.d("bin1111.yang","url : "+current_url);
        }
//        playweb = (WebView)findViewById(R.id.play_web);
//        //启用支持javascript
//        WebSettings settings = playweb.getSettings();
//        settings.setJavaScriptEnabled(true);
//        playweb.loadUrl(current_url);

        thread = new Thread(dosearchmusic);
        thread.start();
    }

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
