package com.sec.myonlinefm.classificationprogram.dataimport

import com.sec.myonlinefm.classificationprogram.dataimport.RecommendsData.Recommends.Detail
import java.util.ArrayList

/**
 * Created by SRC-TJ-MM-BinYang on 2018/5/7.
 */
class RecommendsData {
    private var brief_name //简称
            : String? = null
    private var name //推荐类标题，特殊值“banner”表示数据属于banner推荐位
            : String? = null

    fun setBriefName(brief_name: String?) {
        this.brief_name = brief_name
    }

    fun getBriefName(): String? {
        return brief_name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getName(): String? {
        return name
    }

    internal var recommendsList: MutableList<Recommends?>? = null //推荐节目列表
    fun setRecommendsList() {
        recommendsList = ArrayList()
    }

    fun getRecommendsList(): MutableList<Recommends?>? {
        return recommendsList
    }

    class Recommends {
        private var title //名称
                : String? = null
        private var id //推荐电台id
                = 0
        private var thumb //原图地址
                : String? = null
        private var thumbs //节目缩略图。没有时为null，有的时候有三个字段：small, medium, large，分别代表小中大三个分辨率
                : String? = null
        private var update_time //更新时间
                : String? = null
        private var sub_title //副标题
                : String? = null
        private var object_id //recommends.detail的节目id
                = 0

        fun setTitle(title: String?) {
            this.title = title
        }

        fun setId(id: Int) {
            this.id = id
        }

        fun setThumb(thumb: String?) {
            this.thumb = thumb
        }

        fun setThumbs(thumbs: String?) {
            this.thumbs = thumbs
        }

        fun setUpdateTime(update_time: String?) {
            this.update_time = update_time
        }

        fun setSubTitle(sub_title: String?) {
            this.sub_title = sub_title
        }

        fun setObjectID(object_id: Int) {
            this.object_id = object_id
        }

        fun getTitle(): String? {
            return title
        }

        fun getId(): Int {
            return id
        }

        fun getThumb(): String? {
            return thumb
        }

        fun getThumbs(): String? {
            return thumbs
        }

        fun getUpdateTime(): String? {
            return update_time
        }

        fun getSubTitle(): String? {
            return sub_title
        }

        fun getObjectID(): Int {
            return object_id
        }

        internal var detail: Detail? = null //有多重类型，通过type区分：program_ondemand: 点播节目；topic: 专题；activity: 活动
        fun setDetail() {
            detail = Detail()
        }

        fun getDetail(): Detail? {
            return detail
        }

        inner class Detail {
            private var channel_star = 0
            private var chatgroup_id = 0
            private var description: String? = null
            private var duration = 0
            private var id = 0
            private var original_fee = 0
            private var price = 0
            private var redirect_url: String? = null
            private var sale_status: String? = null
            private var thumbs: String? = null
            private var title: String? = null
            private var sequence = 0
            private var type: String? = null
            private var update_time: String? = null
            fun setType(type: String?) {
                this.type = type
            }

            fun getType(): String? {
                return type
            }

            fun setUpdateTime(update_time: String?) {
                this.update_time = update_time
            }

            fun getUpdateTime(): String? {
                return update_time
            }

            fun setSaleStatus(sale_status: String?) {
                this.sale_status = sale_status
            }

            fun getSaleStatus(): String? {
                return sale_status
            }

            fun setThumbs(thumbs: String?) {
                this.thumbs = thumbs
            }

            fun getThumbs(): String? {
                return thumbs
            }

            fun setTitle(title: String?) {
                this.title = title
            }

            fun getTitle(): String? {
                return title
            }

            fun setSequence(sequence: Int) {
                this.sequence = sequence
            }

            fun getSequence(): Int {
                return sequence
            }

            fun setPrice(price: Int) {
                this.price = price
            }

            fun getPrice(): Int {
                return price
            }

            fun setRedirectUrl(redirect_url: String?) {
                this.redirect_url = redirect_url
            }

            fun getRedirectUrl(): String? {
                return redirect_url
            }

            fun setId(id: Int) {
                this.id = id
            }

            fun getId(): Int {
                return id
            }

            fun setOriginalFee(original_fee: Int) {
                this.original_fee = original_fee
            }

            fun getOriginalFee(): Int {
                return original_fee
            }

            fun setChannelStar(channel_star: Int) {
                this.channel_star = channel_star
            }

            fun getChannelStar(): Int {
                return channel_star
            }

            fun setChatGroupID(chatgroup_id: Int) {
                this.chatgroup_id = chatgroup_id
            }

            fun getChatGroupID(): Int {
                return chatgroup_id
            }

            fun setDescription(description: String?) {
                this.description = description
            }

            fun getDescription(): String? {
                return description
            }

            fun setDuration(duration: Int) {
                this.duration = duration
            }

            fun getDuration(): Int {
                return duration
            }
        }

        private var parent_info: ParentInfo? = null //推荐节目的父类数据
        fun setParentInfo() {
            parent_info = ParentInfo()
        }

        fun getParentInfo(): ParentInfo? {
            return parent_info
        }

        inner class ParentInfo {
            private var parent_id = 0
            private var parent_name: String? = null
            fun setParentID(parent_id: Int) {
                this.parent_id = parent_id
            }

            fun setParentName(parent_name: String?) {
                this.parent_name = parent_name
            }

            fun getParentID(): Int {
                return parent_id
            }

            fun getParentName(): String? {
                return parent_name
            }

            internal var parentExtra: ParentExtra? = null
            fun setParentExtra() {
                parentExtra = ParentExtra()
            }

            fun getParentExtra(): ParentExtra? {
                return parentExtra
            }

            inner class ParentExtra {
                private var category_id = 0
                private var tag: String? = null
                fun setCategoryID(category_id: Int) {
                    this.category_id = category_id
                }

                fun setTag(tag: String?) {
                    this.tag = tag
                }

                fun getCategoryID(): Int {
                    return category_id
                }

                fun getTag(): String? {
                    return tag
                }
            }
        }
    }
}