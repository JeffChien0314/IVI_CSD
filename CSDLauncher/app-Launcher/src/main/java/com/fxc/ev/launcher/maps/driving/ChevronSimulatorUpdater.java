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
package com.fxc.ev.launcher.maps.driving;

import android.location.Location;

import com.fxc.ev.launcher.maps.driving.utils.BaseSimulator;
import com.tomtom.online.sdk.map.Chevron;
import com.tomtom.online.sdk.map.ChevronPosition;

public class ChevronSimulatorUpdater implements BaseSimulator.SimulatorCallback {

    private Chevron chevron;

    public ChevronSimulatorUpdater(Chevron chevron) {
        this.chevron = chevron;
    }

    @Override
    public void onNewRoutePointVisited(Location location) {
        chevron.setPosition(new ChevronPosition.Builder(location).build());
        chevron.setDimmed(false);
        chevron.show();
    }

}