package com.sec.myonlinefm.data

/**
 * Created by SRC-TJ-MM-BinYang on 2018/2/26.
 */
class SearchType {
    class ChannelLive {
        private var id //电台id
                = 0
        private var title //标题
                : String? = null
        private var type //类型
                : String? = null
        private var freqs //调频，暂时不用
                : String? = null
        private var category_name //所属分类名字
                : String? = null
        private var category_id //所属分类id
                = 0
        private var cover //配图
                : String? = null
        private var keywords //搜索关键字
                : String? = null

        fun setChannelLiveId(id: Int) {
            this.id = id
        }

        fun getChannelLiveId(): Int {
            return id
        }

        fun setChannelLiveTitle(title: String?) {
            this.title = title
        }

        fun getChannelLiveTitle(): String? {
            return title
        }

        fun setChannelLiveType(type: String?) {
            this.type = type
        }

        fun getChannelLiveType(): String? {
            return type
        }

        fun setChannelLiveFreqs(freqs: String?) {
            this.freqs = freqs
        }

        fun getChannelLiveFreqs(): String? {
            return freqs
        }

        fun setChannelLiveCategoryName(category_name: String?) {
            this.category_name = category_name
        }

        fun getChannelLiveCategoryName(): String? {
            return category_name
        }

        fun setChannelLiveCategoryId(category_id: Int) {
            this.category_id = category_id
        }

        fun getChannelLiveCategoryId(): Int {
            return category_id
        }

        fun setChannelLiveCover(cover: String?) {
            this.cover = cover
        }

        fun getChannelLiveCover(): String? {
            return cover
        }

        fun setChannelLiveKeywords(keywords: String?) {
            this.keywords = keywords
        }

        fun getChannelLiveKeywords(): String? {
            return keywords
        }
    }

    class ProgramLive {
        private var id //节目id
                = 0
        private var title //标题
                : String? = null
        private var parent_id //所属电台id
                = 0
        private var parent_type //所属电台类型，channel表示直播电台
                : String? = null
        private var category_name //所属分类名字
                : String? = null
        private var category_id //所属分类id
                = 0
        private var type //类型
                : String? = null
        private var cover //配图
                : String? = null

        fun setProgramLiveId(id: Int) {
            this.id = id
        }

        fun getProgramLiveId(): Int {
            return id
        }

        fun setProgramLiveTitle(title: String?) {
            this.title = title
        }

        fun getProgramLiveTitle(): String? {
            return title
        }

        fun setProgramLiveType(type: String?) {
            this.type = type
        }

        fun getProgramLiveType(): String? {
            return type
        }

        fun setProgramLiveParentId(parent_id: Int) {
            this.parent_id = parent_id
        }

        fun getProgramLiveParentId(): Int {
            return parent_id
        }

        fun setProgramLiveCategoryName(category_name: String?) {
            this.category_name = category_name
        }

        fun getProgramLiveCategoryName(): String? {
            return category_name
        }

        fun setProgramLiveCategoryId(category_id: Int) {
            this.category_id = category_id
        }

        fun getProgramLiveCategoryId(): Int {
            return category_id
        }

        fun setProgramLiveCover(cover: String?) {
            this.cover = cover
        }

        fun getProgramLiveCover(): String? {
            return cover
        }

        fun setProgramLiveParentType(parent_type: String?) {
            this.parent_type = parent_type
        }

        fun getProgramLiveParentType(): String? {
            return parent_type
        }
    }
}