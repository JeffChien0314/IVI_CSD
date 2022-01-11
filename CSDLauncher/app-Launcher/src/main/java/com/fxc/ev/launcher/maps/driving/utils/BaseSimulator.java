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
package com.fxc.ev.launcher.maps.driving.utils;

import android.location.Location;
import android.os.Handler;

import java.util.List;

public abstract class BaseSimulator implements Simulator {

    private final static int DEFAULT_DELAY_TIME_IN_MS = 1200;

    private final Handler dispatcher;
    private boolean isActive = false;
    private int gpsActiveLocationIdx = 0;
    private SimulatorCallback simulatorCallback;
    private LocationInterpolator locationInterpolator;

    public BaseSimulator(LocationInterpolator locationInterpolator) {
        this.dispatcher = new Handler();
        this.locationInterpolator = locationInterpolator;
    }

    protected abstract List<Location> getLocations();

    @Override
    public void play(BaseSimulator.SimulatorCallback callback) {
        if (!isActive) {
            isActive = true;
            simulatorCallback = callback;
            dispatcher.post(this);
        }
    }

    @Override
    public int stop() {
        if (isActive) {
            isActive = false;
            simulatorCallback = null;
        }
        return gpsActiveLocationIdx;
    }

    @Override
    public void resume(BaseSimulator.SimulatorCallback callback, int start) {
        gpsActiveLocationIdx = start;
        play(callback);
    }

    @Override
    public void run() {
        // Process only when active
        if (isActive) {

            // Get location info
            Location location = getLocations().get(gpsActiveLocationIdx);
            Location interpolatedLocation = locationInterpolator != null ?
                    locationInterpolator.interpolate(location) : location;

            //Next location calculated based on route points
            //If we are visiting last location, wait 1 second
            //Before starting this simulation again
            long nextLocationDelay = DEFAULT_DELAY_TIME_IN_MS;
            if (gpsActiveLocationIdx < getLocations().size() - 1) {
                //Get next update time
                Location nextLocation = getLocations().get(gpsActiveLocationIdx + 1);
                nextLocationDelay = nextLocation.getTime() - location.getTime();
                // Increase active location idx
                gpsActiveLocationIdx++;
            } else {
                //Reset for new loop
                gpsActiveLocationIdx = 0;
            }

            // Notify about new location
            if (simulatorCallback != null) {
                simulatorCallback.onNewRoutePointVisited(interpolatedLocation);
            }

            //Schedule next update
            if (gpsActiveLocationIdx < getLocations().size() - 1) {
                dispatcher.postDelayed(this, nextLocationDelay);
            }
        }
    }

    public interface SimulatorCallback {
        void onNewRoutePointVisited(Location location);
    }
}
