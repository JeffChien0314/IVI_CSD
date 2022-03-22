package com.android.car.carlauncher;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;


/**
 * 带刻度尺的 SeekBar
 */

public class IndicatorVerticalSeekBar extends VerticalSeekBar {

    private int mIndicatorCount;
    private int mColor;
    private float mWidth;

    public IndicatorVerticalSeekBar(Context context) {
        super(context);
    }

    public IndicatorVerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.IndicatorVerticalSeekBar);
        try {
            mIndicatorCount = typedArray.getInt(R.styleable.IndicatorVerticalSeekBar_indicator_count, 12);
            mColor = typedArray.getInt(R.styleable.IndicatorVerticalSeekBar_indicator_color, /*Color.GRAY*/Color.parseColor("#15284A"));
            mWidth=typedArray.getDimension(R.styleable.IndicatorVerticalSeekBar_indicator_width,6);
        } finally {
            typedArray.recycle();
        }

    }

    public IndicatorVerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        float startX;
        float startY;
        float stopX;
        float stopY;
        //Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint linePaint = new Paint(Paint.DITHER_FLAG);
        linePaint.setAntiAlias(true);//锯齿不显示
        linePaint.setStrokeWidth(mWidth);
        linePaint.setColor(/*Color.WHITE*/mColor);
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setAlpha(255);

        Log.i("Tuma0318", "onDraw: "+getMinimumHeight());
        float mTickDiliver = /*getMeasuredHeight()*/690 / mIndicatorCount;
        // 画刻度线
        for (int i = 1; i< mIndicatorCount; i++) {
            stopX = getPaddingLeft()+(i*mTickDiliver);
            startX = stopX;

            c.drawLine(startX,getMeasuredHeight(),stopX,0,linePaint);

        }

    }
}