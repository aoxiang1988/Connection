package com.sec.myonlinefm.classificationprogram.data

/**
 * Created by SRC-TJ-MM-BinYang on 2018/4/28.
 */

class ChannelProgramPattern {

    private var channelProgramList : MutableList<ChannelProgram?>? = null

    private var total = 0

    fun setChannelProgramList(channelProgramList: MutableList<ChannelProgram?>) {
        this.channelProgramList = channelProgramList
    }

    fun getChannelProgramList() : MutableList<ChannelProgram?>? {
        return channelProgramList
    }

    fun setTotal(total : Int) {
        this.total = total
    }

    fun getTotal() : Int {
        return total
    }
}
