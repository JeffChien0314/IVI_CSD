/*
 * Copyright (C) 2018 TomTom NV. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom NV and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * license agreement between you and TomTom NV. If you are the licensee, you are only permitted
 * to use this software in accordance with the terms of your license agreement. If you are
 * not the licensee, you are not authorized to use this software in any manner and should
 * immediately return or destroy it.
 */

package com.fxc.ev.launcher.utils;

import android.content.Context;
import android.widget.Toast;

public final class Toaster {
  private static Toast toast;

  /**
   * Show a Toast message, cancelling the previous one
   *
   * @param context
   * @param text
   */
  public static void show(final Context context, final String text) {
    if (toast != null) {
      toast.cancel();
    }
    toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
    toast.show();
  }

  /**
   * Show a Toast message, cancelling the previous one
   *
   * @param context
   * @param resid - resource id of text string
   */
  public static void show(final Context context, final int resid) {
    if (toast != null) {
      toast.cancel();
    }
    toast = Toast.makeText(context, resid, Toast.LENGTH_LONG);
    toast.show();
  }
}
