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

package com.fxc.ev.launcher.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.fxc.ev.launcher.BuildConfig;

import java.io.File;

public class ApplicationPreferences {
  private static final String TAG = "ApplicationPreferences";
  private static final String KEYSTORE_FILE = "keystore.sqlite";

  public static final String NDS_MAP_ROOT_RELATIVE_PATH = "map" + File.separator + "map";
  public static final String NDS_MAP_KEYSTORE_RELATIVE_PATH =
      "map" + File.separator + KEYSTORE_FILE;

  /**
   * Check if onboard map is available.
   *
   * @param context Android context
   * @return true if onboard map was found, false otherwise
   */
  public static boolean isOnboardMapAvailable(@NonNull Context context) {
    final File externalFilesDirPath = context.getApplicationContext().getExternalFilesDir(null);
    if (externalFilesDirPath == null || !externalFilesDirPath.exists()) {
      Log.e(TAG, "External files dir does not exist");
      return false;
    }
    final File mapPath = new File(externalFilesDirPath, "map");
    if (!mapPath.exists()) {
      Log.e(TAG, "Map directory does not exist: " + mapPath);
      return false;
    }
    final File ndsMapRootFolder = new File(mapPath, "map");
    final File root = new File(ndsMapRootFolder, "ROOT.NDS");
    if (!root.exists()) {
      Log.e(TAG, "ROOT.NDS does not exist for path: " + ndsMapRootFolder.toString());
      return false;
    }
    return true;
  }

  public static String getKeystorePassword() {
    //noinspection ConstantConditions
    if (BuildConfig.KEYSTORE_PASSWORD == null) {
      Log.w(
          TAG,
          "Keystore password is not set (`keyStorePassword` project property). In most cases this "
              + "is required in order to access encrypted onboard maps");
    }
    return BuildConfig.KEYSTORE_PASSWORD;
  }

  public static String getMapUpdateServerApiKey() {
    //noinspection ConstantConditions
    if (BuildConfig.MAP_UPDATE_SERVER_API_KEY == null) {
      Log.w(
          TAG,
          "Map update server API key isn't set (`mapUpdateServerApiKey` project property). In most "
              + "cases this is required in order for map update examples to work");
    }
    return BuildConfig.MAP_UPDATE_SERVER_API_KEY;
  }
}
