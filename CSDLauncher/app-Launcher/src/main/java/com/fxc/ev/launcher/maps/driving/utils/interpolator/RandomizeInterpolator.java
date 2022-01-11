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
package com.fxc.ev.launcher.maps.driving.utils.interpolator;

import android.location.Location;

import com.fxc.ev.launcher.maps.driving.utils.LocationInterpolator;

import java.util.Calendar;
import java.util.Random;

public class RandomizeInterpolator implements LocationInterpolator {

    private static final int DEFAULT_RADIUS_IN_METERS = 35;
    private static final float METERS_TO_DEGREES_FACTOR = 111000f;

    @Override
    public Location interpolate(Location location) {
        Location result = new Location(location);

        Random random = new Random(Calendar.getInstance().getTimeInMillis());

        // Convert radius from meters to degrees
        double radiusInDegrees = DEFAULT_RADIUS_IN_METERS / METERS_TO_DEGREES_FACTOR;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double new_x = Math.abs(w * Math.cos(t));
        double new_y = Math.abs(w * Math.sin(t));
        double yS = Math.sin(Math.toRadians(location.getBearing()));
        double xS = - Math.cos(Math.toRadians(location.getBearing()));

        double foundLongitude = new_x * xS + location.getLongitude();
        double foundLatitude = new_y * yS + location.getLatitude();

        result.setLatitude(foundLatitude);
        result.setLongitude(foundLongitude);
        return result;
    }

}
