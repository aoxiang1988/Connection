package com.sec.connection.xmlcheck;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/3.
 */

public class PullLocalInfoParser implements LocalInfoParser {

    private List<Program> programs = null;
    private Program program = null;
    private Map<Integer,List<Program>> map = new HashMap<>();

    private List<LocalInfo> localInfos = null;
    private LocalInfo localInfo = null;
    private String postion = null;

    @Override
    public List<LocalInfo> parse(InputStream is) throws Exception {

        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(is, "UTF-8");
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    postion = null;
                    break;
                case XmlPullParser.START_TAG:
                    if(parser.getName().equals("item")){
                        localInfos = new ArrayList<>();
                        postion = parser.getAttributeValue(0);
                    }
                    if(parser.getName().equals("station")) {
                        localInfo = new LocalInfo();
                        programs = new ArrayList<>();
                        localInfo.setpostion(postion);
                        localInfo.settag(Integer.parseInt(parser.getAttributeValue(0)));
                        localInfo.setstationname(parser.getAttributeValue(1));
                        localInfo.setchannel(Integer.parseInt(parser.getAttributeValue(2)));
                        localInfos.add(localInfo);
                    }
//                    if(parser.getName().equals("program")) {
//                        program = new Program();
//                        program.setdata(Integer.parseInt(parser.getAttributeValue(0)));
//                        program.settime(parser.getAttributeValue(1));
//                        program.setcontent(parser.getAttributeValue(2));
//                        programs.add(program);
//                    }
//                    eventType = parser.next();
                    break;
                case XmlPullParser.END_TAG:
//                    if(parser.getName().equals("station")){
//                        map.put(localInfo.getchannel() ,programs);
//                    }
                    break;
            }
            eventType = parser.next();
        }
            return localInfos;
    }

    @Override
    public String serialize(List<LocalInfo> localInfos) throws Exception {
        return null;
    }

    @Override
    public Map<Integer, List<Program>> getmap() throws Exception {
        return map;
    }
}
