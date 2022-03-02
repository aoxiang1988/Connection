package com.sec.myonlinefm.data

import java.io.Serializable

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/16.
 * this is the OnLine Station info, define the station's content
 */
class Station : Serializable {
    //private int total;//station num
    private var category_id //classify id
            = 0
    private var description //station description
            : String? = null
    private var id //station id
            = 0
    private var playCount //play num
            : String? = null
    private var title //station title
            : String? = null
    private var type //classify：channel_on_demand is album；channel_live is radio
            : String? = null
    private var update_time //update time
            : String? = null
    private var thumbs //thumbnail. if no set null，if has it likes ：small, medium, large，200，400，800(select the needed)
            : String? = null
    private var freq //station frequency
            : String? = null
    private var audience_count //audience count
            = 0
    private var weBoUrl //the URL for share
            : String? = null
    private var currentProgram: StationProgram? = null
    private var currentProgramTime: String? = null
    private var nextProgram: String? = null
    private var item = 0
    fun setWhichItem(item: Int) {
        this.item = item
    }

    fun getWhichItem(): Int {
        return item
    }

    fun setCurrentProgram(currentProgram: StationProgram?) {
        this.currentProgram = currentProgram
    }

    fun getCurrentProgram(): StationProgram? {
        return currentProgram
    }

    fun setCurrentProgramTime(currentProgramTime: String?) {
        this.currentProgramTime = currentProgramTime
    }

    fun getCurrentProgramTime(): String? {
        return currentProgramTime
    }

    fun setNextProgram(nextProgram: String?) {
        this.nextProgram = nextProgram
    }

    fun getNextProgram(): String? {
        return nextProgram
    }

    fun setStationThumbs(thumbs: String?) {
        this.thumbs = thumbs
    }

    fun getStationThumbs(): String? {
        return thumbs
    }

    fun setStationCategory_id(category_id: Int) {
        this.category_id = category_id
    }

    fun getStationCategory_id(): Int {
        return category_id
    }

    fun setStationDescription(description: String?) {
        this.description = description
    }

    fun getStationDescription(): String? {
        return description
    }

    fun setStationId(id: Int) {
        this.id = id
    }

    fun getStationId(): Int {
        return id
    }

    fun setStationPlayCount(playCount: String?) {
        this.playCount = playCount
    }

    fun getStationPlayCount(): String? {
        return playCount
    }

    fun setStationTitle(title: String?) {
        this.title = title
    }

    fun getStationTitle(): String? {
        return title
    }

    fun setStationType(type: String?) {
        this.type = type
    }

    fun getStationType(): String? {
        return type
    }

    fun setStationUpdate_time(update_time: String?) {
        this.update_time = update_time
    }

    fun getStationUpdate_time(): String? {
        return update_time
    }

    fun setStationFreq(freq: String?) {
        this.freq = freq
    }

    fun getStationFreq(): String? {
        return freq
    }

    fun setStationAudience_count(audience_count: Int) {
        this.audience_count = audience_count
    }

    fun getStationAudience_count(): Int {
        return audience_count
    }

    fun setStationWeBoUrl(weBoUrl: String?) {
        this.weBoUrl = weBoUrl
    }

    fun getStationWeBoUrl(): String? {
        return weBoUrl
    }
}