/*
 * Copyright (C) 2019 TomTom NV. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom NV and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * license agreement between you and TomTom NV. If you are the licensee, you are only permitted
 * to use this software in accordance with the terms of your license agreement. If you are
 * not the licensee, you are not authorized to use this software in any manner and should
 * immediately return or destroy it.
 */

package com.fxc.ev.launcher.utils.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.Layout;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.fxc.ev.launcher.R;


// TextView doesn't support outlines out of the box.
// This implementation copes with android:maxlines being set (or not).
// This implementation does not handle 'ellipsizing' due to the fading behaviour that would need
// to be applied to the outline element of the text in order to match the TextView.
public final class OutlineTextView extends AppCompatTextView {

  private final Paint outlinePaint = new Paint();
  private final Path outlinePath = new Path();

  private final float outlineWidth;
  private final int outlineColor;

  public OutlineTextView(Context context) {
    this(context, null);
  }

  public OutlineTextView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, R.attr.outlineTextViewStyle);
  }

  public OutlineTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    if (getEllipsize() != null) {
      throw new IllegalStateException("Ellipsizing is not supported");
    }

    final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.nk2_OutlineTextView);

    outlineWidth = a.getDimension(R.styleable.nk2_OutlineTextView_outlineWidth, 0);
    outlineColor = a.getColor(R.styleable.nk2_OutlineTextView_outlineColor, 0);

    final int paddingToAccountForStroke = (int) Math.max(0, ((outlineWidth / 2) + 0.5f));
    setPadding(
        getPaddingLeft() + paddingToAccountForStroke,
        0,
        getPaddingRight() + paddingToAccountForStroke,
        0);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    final Layout textLayout = getLayout();
    if (textLayout != null) {
      outlinePaint.reset();
      outlinePaint.set(getPaint());
      outlinePaint.setStyle(Paint.Style.STROKE);
      outlinePaint.setStrokeWidth(outlineWidth);
      outlinePaint.setColor(outlineColor);

      final int lineCount = textLayout.getLineCount();
      for (int i = 0; i < lineCount; i++) {
        final int x =
            (int) textLayout.getPrimaryHorizontal(textLayout.getLineStart(i)) + getPaddingLeft();
        // Ensure that we align the outline's baseline correctly and so add getTotalPaddingTop()
        final int y = textLayout.getLineBaseline(i) + getTotalPaddingTop();

        getPaint()
            .getTextPath(
                getText().toString(),
                textLayout.getLineStart(i),
                textLayout.getLineEnd(i),
                x,
                y,
                outlinePath);
        outlinePath.close();

        canvas.drawPath(outlinePath, outlinePaint);
      }
    }

    super.onDraw(canvas);

    if (textLayout == null) {
      // getLayout() can return null if the text or width has recently changed. The layout is
      // reconstructed in super.onDraw. However our outline needs to be drawn first, so invalidate
      // the component to draw again, now with a valid layout.
      invalidate();
    }
  }
}
