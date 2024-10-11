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

    private final Program program = null;
    private final Map<Integer,List<Program>> map = new HashMap<>();

    private List<LocalInfo> localAllInfo = null;
    private String position = null;

    @Override
    public List<LocalInfo> parse(InputStream is) throws Exception {

        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(is, "UTF-8");
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    position = null;
                    break;
                case XmlPullParser.START_TAG:
                    if(parser.getName().equals("item")){
                        localAllInfo = new ArrayList<>();
                        position = parser.getAttributeValue(0);
                    }
                    if(parser.getName().equals("station")) {
                        LocalInfo localInfo = new LocalInfo();
                        List<Program> programs = new ArrayList<>();
                        localInfo.setPosition(position);
                        localInfo.setTag(Integer.parseInt(parser.getAttributeValue(0)));
                        localInfo.setStationName(parser.getAttributeValue(1));
                        localInfo.setChannel(Integer.parseInt(parser.getAttributeValue(2)));
                        localAllInfo.add(localInfo);
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
            return localAllInfo;
    }

    @Override
    public String serialize(List<LocalInfo> localAllInfo) throws Exception {
        return null;
    }

    @Override
    public Map<Integer, List<Program>> getmap() throws Exception {
        return map;
    }
}
