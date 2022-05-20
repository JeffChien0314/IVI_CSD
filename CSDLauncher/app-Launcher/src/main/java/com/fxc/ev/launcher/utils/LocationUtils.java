package com.fxc.ev.launcher.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

public class LocationUtils {
    public static void setMock(Context context, double latitude, double longitude, float accuracy) {
        Log.v("LauncherActivity", "mocklocation: " + SystemPropUtil.getprop("mocklocation.enable", "0"));
        if (SystemPropUtil.getprop("mocklocation.enable", "0").equals("1")) {
            LocationManager locMgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            Location newLocation = new Location(LocationManager.GPS_PROVIDER);
            newLocation.setLatitude(latitude);
            newLocation.setLongitude(longitude);
            newLocation.setAccuracy(accuracy);
            newLocation.setAltitude(0);
            newLocation.setAccuracy(500);
            newLocation.setTime(System.currentTimeMillis());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                newLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
            }

            try {
                locMgr.addTestProvider(LocationManager.GPS_PROVIDER, false, false, false, false, false, false, false, android.location.Criteria.POWER_LOW, android.location.Criteria.ACCURACY_FINE);
            } catch (IllegalArgumentException ignored) {
            }

            try {
                locMgr.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
            } catch (IllegalArgumentException ignored) {
                locMgr.addTestProvider(LocationManager.GPS_PROVIDER, false, false, false, false, false, false, false, android.location.Criteria.POWER_LOW, android.location.Criteria.ACCURACY_FINE);
            }

            locMgr.setTestProviderStatus(LocationManager.GPS_PROVIDER, LocationProvider.AVAILABLE, null, System.currentTimeMillis());
            try {
                locMgr.setTestProviderLocation(LocationManager.GPS_PROVIDER, newLocation);
            } catch (IllegalArgumentException ignored) {
                locMgr.addTestProvider(LocationManager.GPS_PROVIDER, false, false, false, false, false, false, false, android.location.Criteria.POWER_LOW, android.location.Criteria.ACCURACY_FINE);
                locMgr.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
                locMgr.setTestProviderLocation(LocationManager.GPS_PROVIDER, newLocation);
            }
            Toaster.show(context, "Set Location!!!");
        }
    }

}
