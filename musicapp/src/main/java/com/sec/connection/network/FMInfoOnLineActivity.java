package com.sec.connection.network;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.sec.connection.R;
import com.sec.connection.xmlcheck.LocalInfo;
import com.sec.connection.xmlcheck.Program;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FMInfoOnLineActivity extends AppCompatActivity {

    private static final int DRAW_LIST = 1;
    Thread thread;
    private List<LocalInfo> mOnlineInfo = new ArrayList<>();
    private Map<String,List<Program>> mMap = new HashMap<>();
    private List<Program> mPrograms = null;

    Bundle bundle = new Bundle();

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case DRAW_LIST:
//                    for(int i=0;i<onlineinfo.size();i++){
//                        Log.d("bin1111.yang","info : "+"\n"
//                                +onlineinfo.get(i).getstationname()+"\n"
//                                +onlineinfo.get(i).getsrc()+"\n"
//                                +onlineinfo.get(i).getradio_ID());
//                    }
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fminfo_on_line);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        /* ************************************************************************************* *
         * simple use the function of connect manager..
         * ************************************************************************************* */
        /*ConnectMainManager.getInstance().getLocalList(new RequestCallBack<OnLineRadioPattern>() {
            @Override
            public void onSuccess(OnLineRadioPattern val) {
                String a = val.getCurrentStationList().get(0).getStationTitle();
                Log.d("bin1111.yang","title : "+a);
            }

            @Override
            public void onFail(String errorMessage) {
                Log.d("bin1111.yang","error : "+errorMessage);
            }
        });*/
        /*
         * *******************************************************************************************/
        thread = new Thread(doSearchMusic);
        thread.start();
    }

    static boolean find = false;

    Runnable doSearchMusic = new Runnable() {
        Document document = null;
        public void run() {
            // TODO Auto-generated method stub
            try {
                find = false;
                String searchfm = "http://www.qingting.fm/radiopage/5/1";
                if(!find){
                    document = Jsoup.connect(searchfm).data("query", "Java").timeout(5000).get();
                    Elements FMTitles = document.select("a.nVFQ");
                    System.out.println(" " + FMTitles);
                    for (Element fm : FMTitles){
                        String fmtitle = fm.select("img").attr("alt");
                        String src = fm.select("img").attr("src");
                        String radio_ID = fm.attr("href");
                        bundle.putString("TITLE", fmtitle);
                        bundle.putString("PICTURE", src);
                        bundle.putString("NETURL", radio_ID);
                        Log.d("bin1111.yang","info : "+fmtitle);
                        getProgram(fmtitle, radio_ID);

                        LocalInfo localInfo = new LocalInfo(bundle,getApplicationContext());
                        mOnlineInfo.add(localInfo);
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

    private void getProgram(String fmtitle, String radio_ID) throws IOException {
        
        mPrograms = new ArrayList<>();
        Document document_program;
        String search_program = "http://www.qingting.fm"+radio_ID;
        document_program = Jsoup.connect(search_program).data("query", "Java").timeout(5000).get();
        Elements program_info = document_program.select("li._3w3t");
//        Elements progrem_title = document_program.select("span._1dQW");
//        Elements progrem_time = document_program.select("span._1DvZ");
        for (Element p : program_info){
            Program program = new Program();
            String content = p.select("div").text();
            String time = p.select("span._1DvZ").text();
            String new_path = time.replace(" - ", "@");
            String splitPath[] = new_path.split("@");
            program.setcontent(content);
            program.settime(time);
            mPrograms.add(program);
            Log.d("bin1111.yang","program : "+content+" "+splitPath[0]+"~"+splitPath[1]);
        }
        mMap.put(fmtitle , mPrograms);
    }

}
