package com.sec.myonlinefm.data

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/16.
 */
class StationProgram {
    private var id //节目id
            = 0
    private var start_time //播放开始时间
            : String? = null
    private var end_time //播放结束时间
            : String? = null
    private var duration //播放时长，单位秒
            = 0
    private var title //标题
            : String? = null
    private var type //节目类型
            : String? = null
    private var channel_id //所属电台id
            = 0
    private var program_id //节目唯一标示符
            = 0
    private var broadcasters: Array<Broadcasters?>? = null
    private var broadcasters_num = 0
    fun newBroadcasters(num: Int) {
        broadcasters_num = num
        broadcasters = arrayOfNulls<Broadcasters?>(num)
    }

    fun getBroadcaster(): Array<Broadcasters?>? {
        return broadcasters
    }

    fun getBroadcastersNum(): Int {
        return broadcasters_num
    }

    fun setProgramId(id: Int) {
        this.id = id
    }

    fun getProgramId(): Int {
        return id
    }

    fun setProgramStart_time(start_time: String?) {
        this.start_time = start_time
    }

    fun getProgramStart_time(): String? {
        return start_time
    }

    fun setProgramEnd_Time(end_time: String?) {
        this.end_time = end_time
    }

    fun getProgramEnd_Time(): String? {
        return end_time
    }

    fun setProgramDuration(duration: Int) {
        this.duration = duration
    }

    fun getProgramDuration(): Int {
        return duration
    }

    fun setProgramTitle(title: String?) {
        this.title = title
    }

    fun getProgramTitle(): String? {
        return title
    }

    fun setProgramType(type: String?) {
        this.type = type
    }

    fun getProgramType(): String? {
        return type
    }

    fun setProgramChannel_Id(channel_id: Int) {
        this.channel_id = channel_id
    }

    fun getProgramChannel_Id(): Int {
        return channel_id
    }

    fun setProgramSelf_Id(program_id: Int) {
        this.program_id = program_id
    }

    fun getProgramSelf_Id(): Int {
        return program_id
    }

    class Broadcasters {
        /*also can get like wei bo or QQ id if has the info*/
        var id = 0
        var username: String? = null
        fun setBroadcastersName(username: String?) {
            this.username = username
        }

        fun getBroadcastersName(): String? {
            return username
        }
    }
}