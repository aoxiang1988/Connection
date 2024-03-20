package com.sec.myonlinefm.xmlcheck

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/8.
 */
class Program {
    private var time: String? = null
    private var data = 0
    private var content: String? = null
    private var end: String? = null
    fun settime(time: String?) {
        this.time = time
    }

    fun setend(end: String?) {
        this.end = end
    }

    fun setdata(data: Int) {
        this.data = data
    }

    fun setcontent(content: String?) {
        this.content = content
    }

    fun getend(): String? {
        return end
    }

    fun gettime(): String? {
        return time
    }

    fun getdata(): Int {
        return data
    }

    fun getcontent(): String? {
        return content
    }
}