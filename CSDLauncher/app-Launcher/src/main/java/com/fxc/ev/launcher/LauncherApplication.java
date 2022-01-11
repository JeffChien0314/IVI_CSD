/**
 * Copyright (c) 2015-2021 TomTom N.V. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be used
 * for internal evaluation purposes or commercial use strictly subject to separate licensee
 * agreement between you and TomTom. If you are the licensee, you are only permitted to use
 * this Software in accordance with the terms of your license agreement. If you are not the
 * licensee then you are not authorised to use this software in any manner and should
 * immediately return it to TomTom N.V.
 */
package com.fxc.ev.launcher;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.multidex.MultiDexApplication;

import com.squareup.leakcanary.LeakCanary;
import com.tomtom.online.sdk.common.util.LogUtils;

import java.io.File;

public class LauncherApplication extends MultiDexApplication {

    private static final String ROBO_ELECTRIC_FINGERPRINT = "robolectric";
    public static final String LOG_FILE_PATH = Environment.getExternalStorageDirectory() + File.separator;

    //tag::doc_log[]
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.enableLogs(Log.VERBOSE);
        //end::doc_log[]
        //initStrictMode();
        if (!isRoboElectricUnitTest()) {
            LeakCanary.install(this);
        }
    }

    public boolean isRoboElectricUnitTest() {
        return ROBO_ELECTRIC_FINGERPRINT.equals(Build.FINGERPRINT);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
    private void initStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    //.penaltyDeath() TODO RoomDatabase closable
                    .build());
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        try {
            super.attachBaseContext(base);
        } catch (Exception e) {
            String vmName = System.getProperty("java.vm.name");
            if (!vmName.startsWith("Java")) {
                throw e;
            }
        }
    }

}
