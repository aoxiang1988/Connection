package com.sec.myonlinefm.defineview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * Created by SRC-TJ-MM-BinYang on 2018/5/2.
 */
class FocusedTextView : AppCompatTextView {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun isFocused(): Boolean {
        return true
    }
}