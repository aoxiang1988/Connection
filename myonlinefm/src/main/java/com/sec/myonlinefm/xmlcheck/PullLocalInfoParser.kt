package com.sec.myonlinefm.xmlcheck

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/3.
 */
class PullLocalInfoParser : LocalInfoParser {
    @Throws(Exception::class)
    override fun parse(`is`: InputStream?): MutableList<LocalInfo?>? {
        var programs: MutableList<Program?>? = null
        val program: Program? = null
        map = HashMap()
        var localInfos: MutableList<LocalInfo?>? = null
        var localInfo: LocalInfo? = null
        var postion: String? = null
        val parser = Xml.newPullParser()
        parser.setInput(`is`, "UTF-8")
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_DOCUMENT -> postion = null
                XmlPullParser.START_TAG -> {
                    if (parser.name == "item") {
                        localInfos = ArrayList()
                        postion = parser.getAttributeValue(0)
                    }
                    if (parser.name == "station") {
                        localInfo = LocalInfo()
                        programs = ArrayList()
                        localInfo.setpostion(postion)
                        localInfo.settag(parser.getAttributeValue(0).toInt())
                        if (localInfo.gettag() == 0) localInfo.setstationname(localInfo.getpostion() + parser.getAttributeValue(1)) else localInfo.setstationname(parser.getAttributeValue(1))
                        localInfo.setchannel(parser.getAttributeValue(2).toInt())
                        localInfos!!.add(localInfo)
                    }
                }
                XmlPullParser.END_TAG -> if (parser.name == "station") {
                }
            }
            eventType = parser.next()
        }
        return localInfos
    }

    fun getMap(): HashMap<Int?, MutableList<Program?>?>? {
        return map
    }

    @Throws(Exception::class)
    override fun serialize(localInfos: MutableList<LocalInfo?>?): String? {
        return null
    }

    companion object {
        private var map: HashMap<Int?, MutableList<Program?>?>? = null
    }
}