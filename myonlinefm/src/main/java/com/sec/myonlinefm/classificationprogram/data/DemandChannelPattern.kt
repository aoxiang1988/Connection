package com.sec.myonlinefm.classificationprogram.dataimport

import com.sec.myonlinefm.classificationprogram.data.DemandChannel

/**
 * Created by SRC-TJ-MM-BinYang on 2018/4/17.
 */
class DemandChannelPattern {
    internal var demandChannelsList: MutableList<DemandChannel?>? = null
    internal var currentDemandChannel: DemandChannel? = null
    fun setDemandChannelsList(demandChannelsList: MutableList<DemandChannel?>?) {
        this.demandChannelsList = demandChannelsList
    }

    fun getDemandChannelsList(): MutableList<DemandChannel?>? {
        return demandChannelsList
    }

    fun setCurrentDemandChannel(currentDemandChannel: DemandChannel?) {
        this.currentDemandChannel = currentDemandChannel
    }

    fun getCurrentDemandChannel(): DemandChannel? {
        return currentDemandChannel
    }
}