package com.sec.myonlinefm.classificationprogram.data;

import java.util.List;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/4/17.
 */

public class DemandChannelPattern {

    private List<DemandChannel> demandChannelsList;
    private DemandChannel currentDemandChannel;

    public void setDemandChannelsList(List<DemandChannel> demandChannelsList) {
        this.demandChannelsList = demandChannelsList;
    }
    public List<DemandChannel> getDemandChannelsList() {
        return demandChannelsList;
    }

    public void setCurrentDemandChannel(DemandChannel currentDemandChannel) {
        this.currentDemandChannel = currentDemandChannel;
    }

    public DemandChannel getCurrentDemandChannel() {
        return currentDemandChannel;
    }
}
