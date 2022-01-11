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
package com.fxc.ev.launcher.maps.driving.tracking;

import android.location.Location;

import com.fxc.ev.launcher.activities.FunctionalExampleModel;
import com.fxc.ev.launcher.maps.driving.AbstractRoutingPresenter;
import com.fxc.ev.launcher.maps.driving.ChevronSimulatorUpdater;
import com.fxc.ev.launcher.maps.driving.utils.BaseSimulator;
import com.fxc.ev.launcher.maps.driving.utils.RouteSimulator;
import com.fxc.ev.launcher.maps.driving.utils.interpolator.BasicInterpolator;
import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.map.CameraPosition;
import com.tomtom.online.sdk.map.Chevron;
import com.tomtom.online.sdk.map.MapConstants;
import com.tomtom.online.sdk.map.Route;
import com.tomtom.online.sdk.map.TomtomMap;
import com.tomtom.online.sdk.map.camera.OnMapCenteredCallback;

import java.util.List;

public class ChevronTrackingPresenter extends AbstractRoutingPresenter {
    ChevronTrackingFragment chevronTrackingFragment;
    boolean isZooming = false;
    private final int ZOOM_CHANGE_ANIMATION_MILLIS = 300;
    private double SMALL_SPEED_ZOOM_LEVEL = 19.0;
    private double MEDIUM_SPEED_ZOOM_LEVEL = 18.0;
    private double GREATER_SPEED_ZOOM_LEVEL = 17.0;
    private double BIG_SPEED_ZOOM_LEVEL = 16.0;
    private double HUGE_SPEED_ZOOM_LEVEL = 14.0;


    class ChevronUpdater extends ChevronSimulatorUpdater {

        public ChevronUpdater(Chevron chevron) {
            super(chevron);
        }

        @Override
        public void onNewRoutePointVisited(Location location) {
            super.onNewRoutePointVisited(location);
            updateZoomLevelBaseOnNewLocation(tomtomMap, location);
        }
    }

    private double getZoomLevel(TomtomMap tomtomMap, Location location) {
        double zoomLevel;
        float f = location.getSpeed();
        if (f > 0.0 && f <= 10.0) {
            zoomLevel = SMALL_SPEED_ZOOM_LEVEL;
        } else if (f > 10.0 && f <= 20.0) {
            zoomLevel = MEDIUM_SPEED_ZOOM_LEVEL;
        } else if (f > 20.0 && f <= 40.0) {
            zoomLevel = GREATER_SPEED_ZOOM_LEVEL;
        } else if (f > 40.0 && f <= 70.0) {
            zoomLevel = BIG_SPEED_ZOOM_LEVEL;
        } else if (f > 70.0 && f <= 120.0) {
            zoomLevel = HUGE_SPEED_ZOOM_LEVEL;
        } else {
            zoomLevel = tomtomMap.getZoomLevel();
        }
        return zoomLevel;
    }

    private void updateZoomLevelBaseOnNewLocation(TomtomMap tomtomMap, Location location) {

        /*val zoomLevel = when (location.speed) {
            in SMALL_SPEED_RANGE_IN_KMH -> SMALL_SPEED_ZOOM_LEVEL
            in MEDIUM_SPEED_RANGE_IN_KMH -> MEDIUM_SPEED_ZOOM_LEVEL
            in GREATER_SPEED_RANGE_IN_KMH -> GREATER_SPEED_ZOOM_LEVEL
            in BIG_SPEED_RANGE_IN_KMH -> BIG_SPEED_ZOOM_LEVEL
            in HUGE_SPEED_RANGE_IN_KMH -> HUGE_SPEED_ZOOM_LEVEL
            else -> tomtomMap.zoomLevel
        }*/
        double zoomLevel = getZoomLevel(tomtomMap, location);
        if (tomtomMap.getZoomLevel() != zoomLevel) {
            setCameraPosition(tomtomMap, zoomLevel);
        }
    }

    private void setCameraPosition(TomtomMap tomtomMap, Double zoomLevel) {
        if (!isZooming) {
            isZooming = true;
            tomtomMap.centerOn(CameraPosition.builder()
                            .zoom(zoomLevel)
                            .animationDuration(ZOOM_CHANGE_ANIMATION_MILLIS)
                            .build(),
                    onMapCenteredCallback);
        }
    }

    private OnMapCenteredCallback onMapCenteredCallback = new OnMapCenteredCallback() {
        @Override
        public void onFinish() {
            isZooming = false;
        }

        @Override
        public void onCancel() {
            isZooming = false;
        }
    };

    @Override
    protected BaseSimulator.SimulatorCallback getSimulatorCallback() {
        //ChevronSimulatorUpdater chevronSimulatorUpdater = new ChevronSimulatorUpdater(getChevron());
        return new ChevronUpdater(getChevron());
        //return new RouteProgressSimulatorUpdater(getChevron(),tomtomMap.getRouteSettings(),tomtomMap.getRoutes().get(0).getId());
    }

    @Override
    public FunctionalExampleModel getModel() {
        return new ChevronTrackingFunctionalExample();
    }

    @Override
    protected BaseSimulator createSimulator() {
        //Route is stored within Maps SDK
        List<Route> routes = tomtomMap.getRoutes();
        return new RouteSimulator(routes, new BasicInterpolator());
    }

    @Override
    protected LatLng getDefaultMapPosition() {
        return ROUTE_CONFIG.getOrigin();
    }

    @Override
    protected void onPresenterCreated() {
        cleanup();
        chevronTrackingFragment = (ChevronTrackingFragment) getFragment();

        if (chevronTrackingFragment.getOrigin() != null && chevronTrackingFragment.getDestination() != null) {
            centerOnDefaultPosition();
            createChevron();
            planRoute(chevronTrackingFragment.getOrigin(), chevronTrackingFragment.getDestination());
        } else {
            super.onPresenterCreated();
        }
    }

    //metis@211122 add
    @Override
    protected void centerOnDefaultPosition() {
        //super.centerOnDefaultPosition();
        if (chevronTrackingFragment.getOrigin() != null && chevronTrackingFragment.getDestination() != null) {
            tomtomMap.centerOn(CameraPosition.builder()
                    .focusPosition(chevronTrackingFragment.getOrigin()) //当前位置
                    .zoom(DEFAULT_MAP_ZOOM_LEVEL_FOR_EXAMPLE)
                    .bearing(MapConstants.ORIENTATION_NORTH)
                    .animationDuration(NO_ANIMATION_TIME)
                    .build());
        } else {
            super.centerOnDefaultPosition();  //
        }
    }

    @Override
    public void cleanup() {
        if (tomtomMap != null) {
            tomtomMap.removeMarkers();
            tomtomMap.set2DMode();
            //tomtomMap.getRouteSettings().deactivateProgressAlongRoute(tomtomMap.getRoutes().get(0).getId());
        }
    }
}
