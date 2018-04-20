package com.sec.connecttoapilibrary.onlinefm.xmlcheck;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/3.
 */

public class PullLocalInfoParser implements LocalInfoParser {
    private static HashMap<Integer,List<Program>> map= null;
    @Override
    public List<LocalInfo> parse(InputStream is) throws Exception {
        List<Program> programs = null;
        Program program = null;
        map = new HashMap<>();

        List<LocalInfo> localInfos = null;
        LocalInfo localInfo = null;
        String postion = null;

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
                        if(localInfo.gettag() == 0)
                            localInfo.setstationname(localInfo.getpostion() + parser.getAttributeValue(1));
                        else
                            localInfo.setstationname(parser.getAttributeValue(1));
                        localInfo.setchannel(Integer.parseInt(parser.getAttributeValue(2)));
                        localInfos.add(localInfo);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(parser.getName().equals("station")){

                    }
                    break;
            }
            eventType = parser.next();
        }
            return localInfos;
    }

    public HashMap<Integer,List<Program>> getMap(){
        return map;
    }
    @Override
    public String serialize(List<LocalInfo> localInfos) throws Exception {
        return null;
    }
}
