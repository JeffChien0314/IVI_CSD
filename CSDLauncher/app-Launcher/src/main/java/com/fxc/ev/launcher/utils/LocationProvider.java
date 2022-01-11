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
package com.fxc.ev.launcher.utils;

import android.content.Context;
import android.location.Location;

import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.location.BasicLocationSource;
import com.tomtom.online.sdk.location.LocationSource;
import com.tomtom.online.sdk.location.LocationUpdateListener;
import com.tomtom.online.sdk.location.Locations;

public class LocationProvider {

    private LocationSource locationSource;

    public LocationProvider(Context context) {
        this.locationSource = createLocationSource(context);
    }

    public LatLng getLastKnownPosition() {
        Location location = locationSource.getLastKnownLocation();
        if (location == null) {
            location = Locations.AMSTERDAM;
        }
        return new LatLng(location);
    }

    public void activateLocationSource() {
        locationSource.activate();
    }

    public void deactivateLocationSource() {
        locationSource.deactivate();
    }

    public void addLocationUpdateListener(LocationUpdateListener locationUpdateListener) {
        locationSource.addLocationUpdateListener(locationUpdateListener);
    }

    private LocationSource createLocationSource(Context context) {
        return new BasicLocationSource(context);
    }
}
