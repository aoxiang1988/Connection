package com.sec.myonlinefm.classificationprogram.data

import java.io.Serializable
import java.util.ArrayList

/**
 * Created by SRC-TJ-MM-BinYang on 2018/4/28.
 */

class ChannelProgram : Serializable {

    private var channelID : String? = null
    private var description : String? = null
    private var duration : Float? = 0.0f
    private var id : Int? = 0
    private var updateTime : String? = null
    private var title : String? = null
    private var type : String? = null
//    private String thumbs;

    fun setChannelID(channelID : String) {
        this.channelID = channelID
    }
    fun setDescription(description : String) {
        this.description = description
    }
    fun setDuration(duration : Float) {
        this.duration = duration
    }
    fun setId(id : Int) {
        this.id = id
    }
    fun setUpdateTime(updateTime : String) {
        this.updateTime = updateTime
    }
    fun setTitle(title : String) {
        this.title = title
    }
    fun setType(type : String) {
        this.type = type
    }

    fun getChannelID(): String? {
        return channelID
    }

    fun getDescription(): String? {
        return description
    }

    fun getUpdateTime(): String? {
        return updateTime
    }

    fun getTitle(): String? {
        return title
    }

    fun getType(): String? {
        return type
    }

    fun getDuration(): Float? {
        return duration
    }

    fun getId(): Int? {
        return id
    }

    private var mediaInfo : ProgramMediaInfo ? = null

    fun setMediaInfo() {
        mediaInfo = ProgramMediaInfo()
    }

    fun getMediaInfo(): ProgramMediaInfo? {
        return mediaInfo
    }


    class ProgramMediaInfo {

        private var id : Int? = 0
        private var duration : Float? = 0.0f
        private var bitrateUrlList : MutableList<BitrateUrl>? = null

        fun setId(id : Int) {
            this.id = id
        }

        fun setDuration(duration : Float) {
            this.duration = duration
        }

        fun setBitrateUrl() {
            bitrateUrlList = ArrayList()
        }

        fun getBitrateUrlList() : MutableList<BitrateUrl>? {
            return bitrateUrlList
        }

        class BitrateUrl {
            private var filePath : String? = null
            private var qetag : String? = null//无实际意义
            private var bitrate : Int? = 0

            fun setFilePath(filePath : String) {
                this.filePath = filePath
            }

            fun setQetag(qetag : String) {
                this.qetag = qetag
            }

            fun setBitrate(bitrate : Int) {
                this.bitrate = bitrate
            }

            fun getFilePath(): String? {
                return filePath
            }

            fun getQetag(): String? {
                return qetag
            }

            fun getBitrate(): Int? {
                return bitrate
            }
        }
    }
}
