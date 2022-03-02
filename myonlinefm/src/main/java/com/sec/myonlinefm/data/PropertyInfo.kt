package com.sec.myonlinefm.data


/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/29.
 */
class PropertyInfo {
    private var id = 0
    private var name: String? = null
    fun setPropertyInfoId(id: Int) {
        this.id = id
    }

    fun setPropertyInfoname(name: String?) {
        this.name = name
    }

    fun getPropertyInfoId(): Int {
        return id
    }

    fun getPropertyInfoname(): String? {
        return name
    }

    class values {
        private var id = 0
        private var name: String? = null
        private var sequence: String? = null
        fun setvaluesId(id: Int) {
            this.id = id
        }

        fun setvaluesname(name: String?) {
            this.name = name
        }

        fun setvaluessequence(sequence: String?) {
            this.sequence = sequence
        }

        fun getvaluesId(): Int {
            return id
        }

        fun getvaluesname(): String? {
            return name
        }

        fun getvaluessequence(): String? {
            return sequence
        }
    }
}