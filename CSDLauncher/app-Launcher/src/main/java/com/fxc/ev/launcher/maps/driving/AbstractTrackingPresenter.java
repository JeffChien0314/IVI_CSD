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

import android.os.Bundle;
import android.util.Log;

import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.activities.BaseFunctionalExamplePresenter;
import com.fxc.ev.launcher.maps.driving.utils.BaseSimulator;
import com.fxc.ev.launcher.maps.driving.utils.Simulator;
import com.fxc.ev.launcher.activities.FunctionalExampleFragment;
import com.tomtom.online.sdk.common.location.LatLng;

import com.tomtom.online.sdk.map.CameraPosition;
import com.tomtom.online.sdk.map.Chevron;
import com.tomtom.online.sdk.map.ChevronBuilder;
import com.tomtom.online.sdk.map.Icon;
import com.tomtom.online.sdk.map.MapConstants;
import com.tomtom.online.sdk.map.TomtomMap;
import com.tomtom.online.sdk.map.gestures.GesturesConfiguration;

import timber.log.Timber;

public abstract class AbstractTrackingPresenter extends BaseFunctionalExamplePresenter {

    private static final String LAST_PLAYED_LOCATION_KEY = "LAST_PLAYED_LOCATION";

    public final static int NO_ANIMATION_TIME = 0;
    public final static double CHEVRON_ICON_SCALE = 2.5;
    public final static double MIN_MAP_ZOOM_LEVEL_FOR_EXAMPLE = 16.5;
    public final static double MAX_MAP_ZOOM_LEVEL_FOR_EXAMPLE = 17.5;
    public final static double DEFAULT_MAP_ZOOM_LEVEL_FOR_EXAMPLE = 17.0;

    private Chevron chevron;
    private Simulator simulator;
    private int lastPlayedLocationIdx;
    private FunctionalExampleFragment fragment;

    @Override
    public void bind(final FunctionalExampleFragment view, final TomtomMap map) {
        super.bind(view, map);
        fragment = view;
        if (view.isMapRestored() || hasChevrons()) {
            onPresenterRestored();
        } else {
            onPresenterCreated();
        }
        onPresenterReady();
    }

    public FunctionalExampleFragment getFragment(){
        return fragment;
    }

    @Override
    public void cleanup() {
        super.cleanup();
        stopSimulator();
        stopTracking();
        clearMap();
    }

    @Override
    public void onPause() {
        super.onPause();
        lastPlayedLocationIdx = stopSimulator();
    }

    public void saveState(Bundle outState) {
        outState.putInt(LAST_PLAYED_LOCATION_KEY, stopSimulator());
    }

    public void restoreState(Bundle bundle) {
        lastPlayedLocationIdx = bundle.getInt(LAST_PLAYED_LOCATION_KEY, 0);
    }

    protected void onPresenterCreated() {
        Log.v("metis","onPresenterCreated");
        centerOnDefaultPosition();
        createChevron();
    }

    protected void onPresenterRestored() {
        Log.v("metis","onPresenterRestored");
        restoreChevron();
        restoreSimulator();
    }

    protected void onPresenterReady() {

    }

    public void startTracking() {
        //tag::doc_start_chevron_tracking[]
        tomtomMap.getDrivingSettings().startTracking(getChevron());
        //end::doc_start_chevron_tracking[]
    }

    public void stopTracking() {
        //tag::doc_stop_chevron_tracking[]
        tomtomMap.getDrivingSettings().stopTracking();
        //end::doc_stop_chevron_tracking[]
        tomtomMap.getRouteSettings().displayRoutesOverview();
    }

    protected void restoreSimulator() {
        simulator = createSimulator();
        simulator.resume(getSimulatorCallback(), lastPlayedLocationIdx);
    }

    protected void blockZoomGestures() {
        tomtomMap.updateGesturesConfiguration(new GesturesConfiguration.Builder()
                .minZoom(MIN_MAP_ZOOM_LEVEL_FOR_EXAMPLE)
                .maxZoom(MAX_MAP_ZOOM_LEVEL_FOR_EXAMPLE)
                .build()
        );
    }

    protected void centerOnDefaultPosition() {
        tomtomMap.centerOn(CameraPosition.builder()
                .focusPosition(getDefaultMapPosition())
                .zoom(DEFAULT_MAP_ZOOM_LEVEL_FOR_EXAMPLE)
                .bearing(MapConstants.ORIENTATION_NORTH)
                .animationDuration(NO_ANIMATION_TIME)
                .build());
    }

    private boolean hasChevrons() {
        return tomtomMap.getDrivingSettings().getChevrons().size() > 0;
    }

    private int stopSimulator() {
        if (getSimulator() != null) {
            return getSimulator().stop();
        } else {
            Timber.d("Cannot stop routeSimulator");
            return 0;
        }
    }

    private void clearMap() {
        tomtomMap.updateGesturesConfiguration(new GesturesConfiguration.Builder().build());
        tomtomMap.getDrivingSettings().removeChevrons();
        tomtomMap.clear();
    }

    public void createChevron() {
        Icon activeIcon = Icon.Factory.fromResources(getContext(), R.drawable.chevron_color);
        Icon inactiveIcon = Icon.Factory.fromResources(getContext(), R.drawable.chevron_shadow);
        //tag::doc_create_chevron[]
        ChevronBuilder chevronBuilder = ChevronBuilder.create(activeIcon, inactiveIcon);
        chevron = tomtomMap.getDrivingSettings().addChevron(chevronBuilder);
        //end::doc_create_chevron[]
    }

    private void restoreChevron() {
        //Chevron is stored inside Maps SDK
        chevron = tomtomMap.getDrivingSettings().getChevrons().get(0);
    }

    public Chevron getChevron() {
        return chevron;
    }

    protected Simulator getSimulator() {
        return simulator;
    }

    protected abstract BaseSimulator.SimulatorCallback getSimulatorCallback();

    protected abstract BaseSimulator createSimulator();

    protected abstract LatLng getDefaultMapPosition();

}
