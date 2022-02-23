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
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import androidx.annotation.Nullable;

import com.fxc.ev.launcher.R;


public final class ZoomBarView extends FrameLayout {

  public interface OnZoomButtonClickedListener {
    void OnZoomIn(ZoomBarView zoomBarView);

    void OnZoomOut(ZoomBarView zoomBarView);
  }

  private OnZoomButtonClickedListener zoomListener;

  public ZoomBarView(Context context) {
    this(context, null);
  }

  public ZoomBarView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, R.attr.zoomBarViewStyle); // TODO: default to 0?
  }

  public ZoomBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    this(context, attrs, defStyleAttr, 0);
  }

  public ZoomBarView(
      Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);

    final View root = inflate(context, R.layout.zoombar, this);
    final ImageButton zoomInButton = (ImageButton) root.findViewById(R.id.zoomin);
    final ImageButton zoomOutButton = (ImageButton) root.findViewById(R.id.zoomout);

    zoomInButton.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            if (zoomListener != null) {
              zoomListener.OnZoomIn(ZoomBarView.this);
            }
          }
        });

    zoomOutButton.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            if (zoomListener != null) {
              zoomListener.OnZoomOut(ZoomBarView.this);
            }
          }
        });
  }

  public void setOnZoomButtonClickedListener(OnZoomButtonClickedListener listener) {
    zoomListener = listener;
  }
}
