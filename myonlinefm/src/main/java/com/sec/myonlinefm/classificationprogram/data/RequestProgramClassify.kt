package com.sec.myonlinefm.classificationprogram.data

import java.io.Serializable

/**
 * Created by SRC-TJ-MM-BinYang on 2018/4/13.
 */
class RequestProgramClassify : Serializable {
    internal var id = 0
    private var name: String? = null
    internal var sectionId = 0
    fun setId(id: Int) {
        this.id = id
    }

    fun getId(): Int {
        return id
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getName(): String? {
        return name
    }

    fun setSectionId(sectionId: Int) {
        this.sectionId = sectionId
    }

    fun getSectionId(): Int {
        return sectionId
    }
}