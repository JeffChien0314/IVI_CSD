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

import android.graphics.Color;

import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.map.Chevron;
import com.tomtom.online.sdk.map.ChevronPosition;
import com.tomtom.online.sdk.map.CircleBuilder;
import com.tomtom.online.sdk.map.TomtomMap;
import com.tomtom.online.sdk.map.driving.MatchResult;
import com.tomtom.online.sdk.map.driving.MatcherListener;

public class ChevronMatcherUpdater implements MatcherListener {

    private static final int GPS_DOT_COLOR = Color.RED;
    private static final double GPS_DOT_RADIUS = 3.0;
    private static final boolean GPS_DOT_FILL = true;

    private Chevron chevron;
    private TomtomMap tomtomMap;

    public ChevronMatcherUpdater(Chevron chevron, TomtomMap tomtomMap) {
        this.chevron = chevron;
        this.tomtomMap = tomtomMap;
    }

    //tag::doc_process_matcher_result[]
    @Override
    public void onMatched(MatchResult matchResult) {
        ChevronPosition chevronPosition = new ChevronPosition.Builder(matchResult.getMatchedLocation()).build();

        chevron.setDimmed(!matchResult.isMatched());
        chevron.setPosition(chevronPosition);
        chevron.show();

        tomtomMap.getOverlaySettings().removeOverlays();
        tomtomMap.getOverlaySettings().addOverlay(
                CircleBuilder.create()
                        .position(new LatLng(matchResult.originalLocation))
                        .fill(GPS_DOT_FILL)
                        .color(GPS_DOT_COLOR)
                        .radius(GPS_DOT_RADIUS)
                        .build()
        );
    }
    //end::doc_process_matcher_result[]

}