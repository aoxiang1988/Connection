package com.sec.connection.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

public class LrcView extends android.support.v7.widget.AppCompatTextView {

	private float width;        //歌词视图宽度  
    private float height;       //歌词视图高度  
    private Paint currentPaint; //当前画笔对象
    private int currentSize = 50;
    private int nocurrentSize = 40;
    private Paint notCurrentPaint;  //非当前画笔对象  
    private float textHeight = 25;  //文本高度  
    private int index = 0;      //list集合下标

    private List<LrcContent> mLrcList = new ArrayList<LrcContent>();

    public void setLrcList(List<LrcContent> mLrcList) {
        this.mLrcList = mLrcList;  
    }  

    public LrcView(Context context) {  
        super(context);  
        init();  
    }  
    public LrcView(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
        init();  
    }  

    public LrcView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        init();  
    }

    private void init() {  
        setFocusable(true);     //设置可对焦  

        //高亮部分  
        currentPaint = new Paint();
        currentPaint.setAntiAlias(true);    //设置抗锯齿，让文字美观饱满  
        currentPaint.setTextAlign(Paint.Align.CENTER);//设置文本对齐方式

        //非高亮部分  
        notCurrentPaint = new Paint();
        notCurrentPaint.setAntiAlias(true);  
        notCurrentPaint.setTextAlign(Paint.Align.CENTER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            currentPaint.setShadowLayer(1, 3, 3, getResources().getColor(android.R.color.darker_gray, null));
            notCurrentPaint.setShadowLayer(1,3,3,getResources().getColor(android.R.color.darker_gray, null));
        } else {
            currentPaint.setShadowLayer(1, 3, 3, getResources().getColor(android.R.color.darker_gray));
            notCurrentPaint.setShadowLayer(1,3,3,getResources().getColor(android.R.color.darker_gray));
        }

    }  

    /** 
     * 绘画歌词 
     */  
    @Override  
    protected void onDraw(Canvas canvas) {  
        super.onDraw(canvas);  
        if(canvas == null) {  
            return;  
        }  

        currentPaint.setColor(Color.argb(180, 251, 20, 20));
//        currentPaint.setColor(Color.argb(140, 150, 200, 14));
        notCurrentPaint.setColor(Color.argb(140, 20, 20, 251));

        currentPaint.setTextSize(currentSize);
        currentPaint.setTypeface(Typeface.SERIF);

        notCurrentPaint.setTextSize(nocurrentSize);
        notCurrentPaint.setTypeface(Typeface.DEFAULT);

        try {
            setText("");
            canvas.drawText(mLrcList.get(index).getlrcstr(), width / 2 - 50, height / 2 - 10, currentPaint);
            float tempY = height / 2;
            tempY = tempY + textHeight;
            if(index+1 <= mLrcList.size() )
            	canvas.drawText(mLrcList.get(index + 1).getlrcstr(), width / 2 + 50, tempY + 10, notCurrentPaint);
        } catch (Exception e) {
            setText("");
        }
    }

    /** 
     * 当view大小改变的时候调用的方法 
     */  
    @Override  
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {  
        super.onSizeChanged(w, h, oldw, oldh);  
        this.width = w;  
        this.height = h;  
    }  

    public void setIndex(int index) {  
        this.index = index;  
    }

    public void setTextSize(int currentSize,int nocurrentSize,int textHeight){
        this.currentSize = currentSize;
        this.nocurrentSize = nocurrentSize;
        this.textHeight = textHeight;
    }

}  
