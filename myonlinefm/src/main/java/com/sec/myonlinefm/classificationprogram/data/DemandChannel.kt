package com.sec.myonlinefm.classificationprogram.data


import android.graphics.Bitmap
import java.io.Serializable
/**
 * Created by SRC-TJ-MM-BinYang on 2018/4/17.
 */
class DemandChannel : Serializable {
    private var mCategoryId //分类id
            = 0
    private var id //专辑id
            = 0
    private var total //专辑总数
            = 0
    private var mProgramCount //节目数量
            = 0
    private var mSaleType //付费类型 0免费，1已购买，2未购买
            = 0
    private var isFinished = 0
    private var ordered = 0
    private var description //专辑简介
            : String? = null
    private var playcount //播放次数
            : String? = null
    private var title //标题
            : String? = null
    private var type //类型：channel_ondemand表示专辑；channel_live表示电台
            : String? = null
    private var mUpdateTime //更新时间
            : String? = null
    private var mLatestProgram //最后一个节目
            : String? = null
    private var tags: String? = null
    private var thumbs: String? = null
    private var thumbsUrl: String? = null
    fun setCategoryId(categoryId: Int) {
        this.mCategoryId = categoryId
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun setTotal(total: Int) {
        this.total = total
    }

    fun setProgramCount(programCount: Int) {
        this.mProgramCount = programCount
    }

    fun setSaleType(saleType: Int) {
        this.mSaleType = saleType
    }

    fun setIsFinished(isFinished1: Int) {
        this.isFinished = isFinished1
    }

    fun setOrdered(ordered: Int) {
        this.ordered = ordered
    }

    fun getCategoryId(): Int {
        return mCategoryId
    }

    fun getId(): Int {
        return id
    }

    fun getTotal(): Int {
        return total
    }

    fun getProgramCount(): Int {
        return mProgramCount
    }

    fun getSaleType(): Int {
        return mSaleType
    }

    fun getIsFinished(): Int {
        return isFinished
    }

    fun getOrdered(): Int {
        return ordered
    }

    fun setDescription(description: String?) {
        this.description = description
    }

    fun setPlayCount(playCount: String?) {
        this.playcount = playCount
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun setType(type: String?) {
        this.type = type
    }

    fun setUpdateTime(updateTime: String?) {
        this.mUpdateTime = updateTime
    }

    fun setLatestProgram(latestProgram: String?) {
        this.mLatestProgram = latestProgram
    }

    fun setTags(tags: String?) {
        this.tags = tags
    }

    fun setThumbs(thumbs: String?) {
        this.thumbs = thumbs
    }

    fun getDescription(): String? {
        return description
    }

    fun getPlayCount(): String? {
        return playcount
    }

    fun getTitle(): String? {
        return title
    }

    fun getType(): String? {
        return type
    }

    fun getUpdateTime(): String? {
        return mUpdateTime
    }

    fun getLatestProgram(): String? {
        return mLatestProgram
    }

    fun getTags(): String? {
        return tags
    }

    fun getThumbs(): String? {
        return thumbs
    }

    private var purchaseItem: PurchaseItem? = null
    internal var detail: Detail? = null
    private var podCasters: PodCasters? = null
    fun setPodCasters() {
        podCasters = PodCasters()
    }

    fun getPodCasters(): PodCasters? {
        return podCasters
    }

    fun setPurchaseItem() {
        purchaseItem = PurchaseItem()
    }

    fun getPurchaseItem(): PurchaseItem? {
        return purchaseItem
    }

    fun setDetail() {
        detail = Detail()
    }

    fun getDetail(): Detail? {
        return detail
    }

    fun setThumbsUrl(thumbsUrl: String?) {
        this.thumbsUrl = thumbsUrl
    }

    fun getThumbsUrl(): String? {
        return thumbsUrl
    }

    inner class PurchaseItem {
        private var fee = 0f
        private var originalFee = 0f
        fun setFee(fee: Float) {
            this.fee = fee
        }

        fun getFee(): Float {
            return fee
        }

        fun setOriginalFee(originalFee1: Float) {
            this.originalFee = originalFee1
        }

        fun getOriginalFee(): Float {
            return originalFee
        }
    }

    inner class Detail {
        private var programCount = 0
        private var favCount: String? = null
        private var playcount: String? = null
        fun setProgramCount(programCount: Int) {
            this.programCount = programCount
        }

        fun getProgramCount(): Int {
            return programCount
        }

        fun setFavCount(fav_count: String?) {
            this.favCount = fav_count
        }

        fun getFavCount(): String? {
            return favCount
        }

        fun setPlayCount(playcount: String?) {
            this.playcount = playcount
        }

        fun getPlayCount(): String? {
            return playcount
        }

        internal var authors: MutableList<AuthorsBroadcasters?>? = null
        internal var broadcasters: MutableList<AuthorsBroadcasters?>? = null
        internal var podCasters: MutableList<PodCasters?>? = null
        fun setAuthors() {
            authors = ArrayList()
        }

        fun getAuthors(): MutableList<AuthorsBroadcasters?>? {
            return authors
        }

        fun setBroadcasters() {
            broadcasters = ArrayList()
        }

        fun getBroadcasters(): MutableList<AuthorsBroadcasters?>? {
            return broadcasters
        }

        fun setPodCasters() {
            podCasters = ArrayList()
        }

        fun getPodCasters(): MutableList<PodCasters?>? {
            return podCasters
        }
    }

    class AuthorsBroadcasters {
        private var id = 0
        private var username: String? = null
        private var thumb: Bitmap? = null
        private var weibo_name: String? = null
        private var weibo_id: String? = null
        private var qq_name: String? = null
        private var qq_id: String? = null
        fun setId(id: Int) {
            this.id = id
        }

        fun getId(): Int {
            return id
        }

        fun setWeiboName(weibo_name: String?) {
            this.weibo_name = weibo_name
        }

        fun getWeiboName(): String? {
            return weibo_name
        }

        fun setWeiboId(weibo_id: String?) {
            this.weibo_id = weibo_id
        }

        fun getWeiboId(): String? {
            return weibo_id
        }

        fun setQQName(qq_name: String?) {
            this.qq_name = qq_name
        }

        fun getQQName(): String? {
            return qq_name
        }

        fun setQQId(qq_id: String?) {
            this.qq_id = qq_id
        }

        fun getQQId(): String? {
            return qq_id
        }

        fun setUserName(username: String?) {
            this.username = username
        }

        fun getUserName(): String? {
            return username
        }

        fun setThumb(thumb: Bitmap?) {
            this.thumb = thumb
        }

        fun getThumb(): Bitmap? {
            return thumb
        }
    }

    class PodCasters {
        private var id //主播id
                = 0
        private var fan_num //粉丝数。
                = 0
        private var user_id //主播qingting_id。可以用来查询主播信息
                = 0
        private var desc //主播简介
                : String? = null
        private var img_url //主播头像。
                : String? = null
        private var nickname //主播名字
                : String? = null

        fun setId(id: Int) {
            this.id = id
        }

        fun setFanNum(fanNum: Int) {
            this.fan_num = fanNum
        }

        fun setUserId(userId: Int) {
            this.user_id = userId
        }

        fun getId(): Int {
            return id
        }

        fun getFanNum(): Int {
            return fan_num
        }

        fun getUserId(): Int {
            return user_id
        }

        fun setDesc(desc: String?) {
            this.desc = desc
        }

        fun setImgUrl(imgUrl: String?) {
            this.img_url = imgUrl
        }

        fun setNickName(nickname: String?) {
            this.nickname = nickname
        }

        fun getDesc(): String? {
            return desc
        }

        fun getImgUrl(): String? {
            return img_url
        }

        fun getNickName(): String? {
            return nickname
        }
    }

    companion object {
        const val TYPE_CHANNEL_ON_DEMAND: String = "channel_ondemand"
        const val TYPE_CHANNEL_LIVE: String = "channel_live"
        const val FREE_SALE_TYPE = 0
        const val BOUGHT_SALE_TYPE = 1
        const val UN_BOUGHT_SALE_TYPE = 2
    }
}