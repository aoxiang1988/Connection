package com.sec.myonlinefm.xmlcheck

import java.io.InputStream

/**
 * Created by SRC-TJ-MM-BinYang on 2018/1/3.
 */
interface LocalInfoParser {
    @Throws(Exception::class)
    open fun parse(`is`: InputStream?): MutableList<LocalInfo?>?
    @Throws(Exception::class)
    open fun serialize(books: MutableList<LocalInfo?>?): String?
}