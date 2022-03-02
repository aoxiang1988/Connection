package com.sec.myonlinefm.classificationprogram.data


/**
 * Created by SRC-TJ-MM-BinYang on 2018/5/7.
 */
class WaPiData {
    private var category_id //分类id
            = 0
    private var cover //专辑头像
            : String? = null
    private var id //专辑id
            = 0
    private var rank //排名
            = 0
    private var update_time //更新时间
            : String? = null
    private var desc //专辑简介
            : String? = null
    private var name //专辑名字
            : String? = null

    fun setCategoryID(category_id: Int) {
        this.category_id = category_id
    }

    fun setUpdateTime(update_time: String?) {
        this.update_time = update_time
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun setCover(cover: String?) {
        this.cover = cover
    }

    fun setDesc(desc: String?) {
        this.desc = desc
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun setRank(rank: Int) {
        this.rank = rank
    }

    fun getId(): Int {
        return id
    }

    fun getCategoryID(): Int {
        return category_id
    }

    fun getRank(): Int {
        return rank
    }

    fun getCover(): String? {
        return cover
    }

    fun getDesc(): String? {
        return desc
    }

    fun getName(): String? {
        return name
    }

    fun getUpdateTime(): String? {
        return update_time
    }
}