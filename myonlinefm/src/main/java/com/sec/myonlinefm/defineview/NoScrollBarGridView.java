package com.sec.myonlinefm.defineview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/4/25.
 */

public class NoScrollBarGridView extends GridView {

    public NoScrollBarGridView(Context context) {
        super(context);
    }

    public NoScrollBarGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoScrollBarGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
