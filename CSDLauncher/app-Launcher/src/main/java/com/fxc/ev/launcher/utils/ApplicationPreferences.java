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
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.fxc.ev.launcher.BuildConfig;
import com.fxc.ev.launcher.maps.poicatsearch.Constants;

import java.io.File;

public class ApplicationPreferences {
    private static final String TAG = "ApplicationPreferences";
    private static final String KEYSTORE_FILE = "nk_auto_dev.nks";//"keystore.sqlite";

    public static final String NDS_MAP_ROOT_RELATIVE_PATH = "map" + File.separator + "map";
    /*public static final String NDS_MAP_KEYSTORE_RELATIVE_PATH =
        "map" + File.separator + KEYSTORE_FILE;*/
    public static final String NDS_MAP_KEYSTORE_RELATIVE_PATH =
            "map" + File.separator + KEYSTORE_FILE;//Jerry@20220408 add:for onboard map

    /**
     * Check if onboard map is available.
     *
     * @param context Android context
     * @return true if onboard map was found, false otherwise
     */
    public static boolean isOnboardMapAvailable(@NonNull Context context) {
        //Jerry@20220408 modify:for onboard map
        File externalFilesDirPath;
        File externalFilesDirPathData = context.getApplicationContext().getExternalFilesDir(null);
        File externalFilesDirPathDownload = Environment.getExternalStoragePublicDirectory(Constants.NDS_MAP_STORAGE_PATH);

        externalFilesDirPath = externalFilesDirPathDownload;
        if (externalFilesDirPath == null || !externalFilesDirPath.exists()) {
            Log.e(TAG, "External download files dir does not exist");
            return false;
        }
        final File mapPath = new File(externalFilesDirPath, "map");
        if (!mapPath.exists()) {
            Log.e(TAG, "Map download directory does not exist: " + mapPath);
            mapPath.mkdir();
        }
        final File mapPathDate = new File(externalFilesDirPathData, "map");
        if (!mapPathDate.exists()) {
            Log.e(TAG, "Map date directory does not exist: " + mapPathDate);
            mapPathDate.mkdir();
        }
        final File ndsMapRootFolder = new File(mapPath, "map");
        if (!ndsMapRootFolder.exists()) {
            Log.e(TAG, "Map ndsMapRootFolder directory does not exist: " + mapPath);
            ndsMapRootFolder.mkdir();
        }
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

    //Jerry@20220408 add for onboard map
    public static String getNdsMapRootFolder(Context context) {
        if (Constants.IS_STORAGE_DOWNLOAD) {
            return new File(
                    Environment.getExternalStoragePublicDirectory(Constants.NDS_MAP_STORAGE_PATH),
                    ApplicationPreferences.NDS_MAP_ROOT_RELATIVE_PATH)
                    .getPath();
        } else {
            return new File(
                    context.getExternalFilesDir(null),
                    ApplicationPreferences.NDS_MAP_ROOT_RELATIVE_PATH)
                    .getPath();
        }
    }

    //Jerry@20220408 add for onboard map
    public static File getKeystore(Context context) {
        if (Constants.IS_STORAGE_DOWNLOAD) {
            return new File(
                    Environment.getExternalStoragePublicDirectory(Constants.NDS_MAP_STORAGE_PATH),
                    ApplicationPreferences.NDS_MAP_KEYSTORE_RELATIVE_PATH);
        } else {
            return new File(
                    context.getExternalFilesDir(null),
                    ApplicationPreferences.NDS_MAP_KEYSTORE_RELATIVE_PATH);
        }
    }
}
