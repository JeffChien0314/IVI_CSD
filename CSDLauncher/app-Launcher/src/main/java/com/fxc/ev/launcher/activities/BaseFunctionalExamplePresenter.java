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
package com.fxc.ev.launcher.activities;

import android.content.Context;
import android.content.res.Resources;

import com.fxc.ev.launcher.R;
import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.common.util.Contextable;
import com.tomtom.online.sdk.map.CameraPosition;
import com.tomtom.online.sdk.map.MapConstants;
import com.tomtom.online.sdk.map.MapPadding;
import com.tomtom.online.sdk.map.TomtomMap;
import com.tomtom.online.sdk.map.ui.MapComponentView;

import java.util.concurrent.Executors;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseFunctionalExamplePresenter implements FunctionalExamplePresenter, Contextable {

    private static final int NETWORK_THREADS_NUMBER = 4;
    public static final float DEFAULT_ZOOM_TRAFFIC_LEVEL = 12.0f; //works from 11.1
    protected static final int DEFAULT_MAP_PADDING = 0;

    protected TomtomMap tomtomMap;
    protected FunctionalExampleFragment view;
    protected Scheduler networkScheduler = Schedulers.from(Executors.newFixedThreadPool(NETWORK_THREADS_NUMBER));

    @Override
    public void bind(FunctionalExampleFragment view, TomtomMap map) {

        this.view = view;
        tomtomMap = map;

        setupUIModel(view);

        confMapPadding();
    }

    public void setupUIModel(FunctionalExampleFragment view) {
        setComponentMargins(tomtomMap.getUiSettings().getCurrentLocationView(), getModel().getCurrentLocationMargins());
        setComponentMargins(tomtomMap.getUiSettings().getCompassView(), getModel().getCompassMargins());

        /*view.setActionBarTitle(getModel().getPlayableTitle());
        view.setActionBarSubtitle(getModel().getPlayableSubtitle());*/
    }

    public void setComponentMargins(MapComponentView componentView, int[] margins) {
        Resources resources = view.getContext().getResources();
        componentView.setMargins(
                resources.getDimensionPixelSize(margins[0]),
                resources.getDimensionPixelSize(margins[1]),
                resources.getDimensionPixelSize(margins[2]),
                resources.getDimensionPixelSize(margins[3]));
    }

    @Override
    public void cleanup() {
        setComponentMargins(tomtomMap.getUiSettings().getCompassView(), BaseFunctionalExampleModel.COMPASS_CLOSE_TO_BAR);
        resetMapPadding();
    }

    public abstract FunctionalExampleModel getModel();

    @Override
    public void onPause() {
    }

    public void centerOn(LatLng location) {
        tomtomMap.centerOn(CameraPosition.builder()
                .focusPosition(location)
                .zoom(DEFAULT_ZOOM_LEVEL)
                .bearing(MapConstants.ORIENTATION_NORTH)
                .build());
    }

    public void centerOn(LatLng location, double zoom) {
        tomtomMap.centerOn(CameraPosition.builder()
                .focusPosition(location)
                .zoom(zoom)
                .bearing(MapConstants.ORIENTATION_NORTH)
                .build());
    }

    protected void confMapPadding() {
        int padding = getContext().getResources().getDimensionPixelSize(R.dimen.offset_extra_big);
        tomtomMap.setPadding(new MapPadding(padding, padding, padding, padding));
    }

    protected void resetMapPadding() {
        tomtomMap.setPadding(new MapPadding(DEFAULT_MAP_PADDING, DEFAULT_MAP_PADDING, DEFAULT_MAP_PADDING, DEFAULT_MAP_PADDING));
    }

    @Override
    public Context getContext() {
        return view.getContext();
    }


}
