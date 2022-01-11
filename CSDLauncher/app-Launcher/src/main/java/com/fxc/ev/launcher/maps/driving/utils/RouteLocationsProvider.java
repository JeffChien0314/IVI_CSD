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
package com.fxc.ev.launcher.maps.driving.utils;

import android.location.Location;

import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.common.util.DistanceCalculator;
import com.tomtom.online.sdk.map.Route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RouteLocationsProvider implements LocationsProvider {

    private static final double NEXT_POINT_TIME_DIFF_IN_MILLIS = 2400L;
    private static final float POINT_DEFAULT_ACCURACY = 0.0f;
    private static final float POINT_DEFAULT_SPEED = 50.0f;
    private static final int MILLIS_IN_HOUR = 1000 * 60 * 60;
    private static final float MAX_SPEED_LIMIT = 120.0f;
    private static final float MIN_SPEED = 0.0f;

    private final List<Route> routes;

    public RouteLocationsProvider(List<Route> routes) {
        this.routes = routes;
    }

    @Override
    public List<Location> getLocations() {

        int pointIdx = 0;
        long pointTime = new Date().getTime();
        List<LatLng> routePoints = routes.get(0).getCoordinates();
        List<Location> routeLocations = new ArrayList<>();

        for (LatLng point : routePoints) {

            float pointBearing = 0.0f;
            float speed = MIN_SPEED;
            if (pointIdx > 0) {
                LatLng prevPoint = routePoints.get(pointIdx - 1);
                pointBearing = getBearingInDegrees(prevPoint, point);
                speed = speedBetweenPoints(prevPoint, point);
            }

            Location location = point.toLocation();
            location.setBearing(pointBearing);
            location.setTime(pointTime);
            location.setAccuracy(POINT_DEFAULT_ACCURACY);
            location.setSpeed(speed/*POINT_DEFAULT_SPEED*/);

            routeLocations.add(location);

            pointTime += NEXT_POINT_TIME_DIFF_IN_MILLIS;
            pointIdx++;
        }

        return routeLocations;
    }

    private static final void normalizeSpeeds(List<Location> routeLocations) {
        List<Float> speeds = new ArrayList<>();
        for (Location location : routeLocations) {
            float speed = location.getSpeed();
            speeds.add(speed);
        }
        Collections.sort(speeds);
        float minSpeed = MIN_SPEED;
        float maxSpeed = MAX_SPEED_LIMIT;
        if (!speeds.isEmpty()) {
            minSpeed = speeds.indexOf(1);
            maxSpeed = speeds.indexOf((speeds.size() - 2));
            for (Location location : routeLocations) {
                float var12 = 120.0F * (location.getSpeed() - minSpeed) / (maxSpeed - minSpeed);
                Math.min(MAX_SPEED_LIMIT, var12);

                location.setSpeed(Math.max(MIN_SPEED, Math.min(MAX_SPEED_LIMIT, var12)));
            }
        }
    }


    private static final float speedBetweenPoints(LatLng first, LatLng second) {
        double distance = DistanceCalculator.calcDistInKilometers(first, second);
        double timeInHours = NEXT_POINT_TIME_DIFF_IN_MILLIS / (MILLIS_IN_HOUR);
        return (float) (distance / timeInHours);
    }

    private float getBearingInDegrees(LatLng from, LatLng to) {
        Location fromLocation = from.toLocation();
        Location toLocation = to.toLocation();
        return fromLocation.bearingTo(toLocation);
    }

}
