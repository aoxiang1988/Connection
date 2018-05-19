package com.sec.myonlinefm.defineview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatTextView;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/5/2.
 */

public class FocusedTextView extends AppCompatTextView {
    public FocusedTextView(Context context) {
        super(context);
    }

    public FocusedTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusedTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
