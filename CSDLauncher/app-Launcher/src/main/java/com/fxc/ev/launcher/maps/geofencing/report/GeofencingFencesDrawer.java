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
package com.fxc.ev.launcher.maps.geofencing.report;

import android.graphics.Color;

import com.google.common.collect.ImmutableList;
import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.map.Circle;
import com.tomtom.online.sdk.map.CircleBuilder;
import com.tomtom.online.sdk.map.Polygon;
import com.tomtom.online.sdk.map.PolygonBuilder;
import com.tomtom.online.sdk.map.TomtomMap;

import java.util.List;

public class GeofencingFencesDrawer {

    //Circle over Amsterdam center
    private static final LatLng CIRCLE_CENTER = new LatLng(40.850565, -73.8770483/*52.372144, 4.899115*/);
    private static final double CIRCLE_RADIUS = 1300; //in meters
    private static final int CIRCLE_COLOR = Color.GREEN;

    //Rectangle over Amsterdam
    private static final LatLng RECTANGLE_BOTTOM_RIGHT = new LatLng(40.868876,-73.842328/*52.367172, 4.926724*/);
    private static final LatLng RECTANGLE_BOTTOM_LEFT = new LatLng(40.865548,-73.862235/*52.366650, 4.906364*/);
    private static final LatLng RECTANGLE_TOP_RIGHT = new LatLng(40.877168,-73.846353/*52.371166, 4.913136*/);
    private static final LatLng RECTANGLE_TOP_LEFT = new LatLng(40.865329,-73.843262/*52.363494, 4.916473*/);
    private static final List<LatLng> AMSTERDAM_RECTANGLE_COORDINATES = ImmutableList.of(
            RECTANGLE_BOTTOM_LEFT,
            RECTANGLE_TOP_RIGHT,
            RECTANGLE_BOTTOM_RIGHT,
            RECTANGLE_TOP_LEFT
    );
    private static final int RECTANGLE_COLOR = Color.RED;

    //Fence color opacity
    private static final float FENCE_OPACITY = 0.5f;

    private TomtomMap tomtomMap;

    public GeofencingFencesDrawer(TomtomMap tomtomMap) {
        this.tomtomMap = tomtomMap;
    }

    public void drawPolygonFence() {
        Polygon polygon = PolygonBuilder.create()
                .coordinates(AMSTERDAM_RECTANGLE_COORDINATES)
                .color(RECTANGLE_COLOR)
                .opacity(FENCE_OPACITY)
                .build();

        tomtomMap.getOverlaySettings().addOverlay(polygon);
    }

    public void drawCircularFence() {
        Circle circle = CircleBuilder.create()
                .position(CIRCLE_CENTER)
                .radius(CIRCLE_RADIUS)
                .color(CIRCLE_COLOR)
                .opacity(FENCE_OPACITY)
                .fill(true)
                .build();

        tomtomMap.getOverlaySettings().addOverlay(circle);
    }

}
