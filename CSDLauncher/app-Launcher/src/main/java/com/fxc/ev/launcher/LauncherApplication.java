/**
 * Copyright (c) 2015-2021 TomTom N.V. All rights reserved.
 * <p>
 * This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be used
 * for internal evaluation purposes or commercial use strictly subject to separate licensee
 * agreement between you and TomTom. If you are the licensee, you are only permitted to use
 * this Software in accordance with the terms of your license agreement. If you are not the
 * licensee then you are not authorised to use this software in any manner and should
 * immediately return it to TomTom N.V.
 */
package com.fxc.ev.launcher;

import android.content.Context;
import android.os.Environment;

import androidx.multidex.MultiDexApplication;

import com.fxc.ev.launcher.utils.NetworkStats;
import com.tomtom.navkit2.analytics.AnalyticsCaptureInterface;
import com.tomtom.navkit2.analytics.AnalyticsProxy;
import com.tomtom.navkit2.http.Core;

import java.io.File;
import java.util.Map;

public class LauncherApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        new Core.Initializer(getApplicationContext()).setCacheSize(5 * 1024 * 1024).initialize();

        NetworkStats.sharedInstance().init(getApplicationContext());

        AnalyticsProxy.setCaptureInterface(new AnalyticsCaptureInterface() {
            @Override
            public void logEvent(String key, Map<String, String> attributes) {
                NetworkStats.sharedInstance().processEvent(key, attributes);
            }
        });
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
