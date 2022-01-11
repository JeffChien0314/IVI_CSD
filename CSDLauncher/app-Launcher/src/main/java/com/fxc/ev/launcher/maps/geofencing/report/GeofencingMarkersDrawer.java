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
package com.fxc.ev.launcher.maps.geofencing.report;

import android.content.Context;

import com.fxc.ev.launcher.R;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.fxc.ev.launcher.maps.geofencing.report.utils.FencesDescriptionProcessor;
import com.fxc.ev.launcher.maps.geofencing.report.utils.InsideFencesDescriptionProcessor;
import com.fxc.ev.launcher.maps.geofencing.report.utils.InsideOutsideFencesDescriptionProcessor;
import com.fxc.ev.launcher.maps.geofencing.report.utils.OutsideFencesDescriptionProcessor;
import com.tomtom.online.sdk.common.func.FuncUtils;
import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.geofencing.report.FenceDetails;
import com.tomtom.online.sdk.geofencing.report.Report;
import com.tomtom.online.sdk.map.BaseMarkerBalloon;
import com.tomtom.online.sdk.map.Icon;
import com.tomtom.online.sdk.map.Marker;
import com.tomtom.online.sdk.map.MarkerBuilder;
import com.tomtom.online.sdk.map.SimpleMarkerBalloon;
import com.tomtom.online.sdk.map.TomtomMap;

import java.util.List;

public class GeofencingMarkersDrawer {

    private static final LatLng DEFAULT_MARKER_POSITION = new LatLng(40.86224, -73.85745/*52.372144, 4.899115*/);
    private static final String DRAGGABLE_MARKER_TAG = "DRAGGABLE_MARKER";
    private static final String FENCE_MARKERS_TAG = "FENCE_MARKERS";

    private TomtomMap tomtomMap;
    private Context context;

    public GeofencingMarkersDrawer(Context context, TomtomMap tomtomMap) {
        this.context = context;
        this.tomtomMap = tomtomMap;
    }

    public void drawDraggableMarkerAtDefaultPosition(LatLng latLng) {
        MarkerBuilder markerBuilder = new MarkerBuilder(latLng)
                .markerBalloon(new BaseMarkerBalloon())
                .tag(DRAGGABLE_MARKER_TAG)
                .draggable(true);

        tomtomMap.addMarker(markerBuilder);
    }

   public void drawDraggableMarkerAtDefaultPosition() {
        MarkerBuilder markerBuilder = new MarkerBuilder(DEFAULT_MARKER_POSITION)
                .markerBalloon(new BaseMarkerBalloon())
                .tag(DRAGGABLE_MARKER_TAG)
                .draggable(true);

        tomtomMap.addMarker(markerBuilder);
    }

    public void deselectDraggableMarker() {
        FuncUtils.apply(findDraggableMarker(), marker -> {
            marker.deselect();
        });
    }

    public void updateMarkersFromResponse(Report report) {
        //Add fence markers
        addFenceMarkers(report.getInside());
        addFenceMarkers(report.getOutside());

        //Update marker text if it is available
        FuncUtils.apply(findDraggableMarker(), marker -> {
            updateDraggableMarkerText(marker, report);
            marker.select();
        });
    }

    private void addFenceMarkers(List<FenceDetails> fences) {
        FuncUtils.forEach(fences, fenceDetails -> {
            String fenceName = String.format("\"%1$s\"", fenceDetails.getFence().getName());
            drawMarkerForFence(fenceDetails.getClosestPoint(), fenceName);
        });
    }

    private Optional<Marker> findDraggableMarker() {
        return tomtomMap.getMarkerSettings().findMarkerByTag(DRAGGABLE_MARKER_TAG);
    }

    private void updateDraggableMarkerText(Marker marker, Report report) {
        List<FencesDescriptionProcessor> descriptionProcessors = ImmutableList.of(
                new InsideFencesDescriptionProcessor(),
                new OutsideFencesDescriptionProcessor(),
                new InsideOutsideFencesDescriptionProcessor()
        );

        BaseMarkerBalloon baseMarkerBalloon = (BaseMarkerBalloon) marker.getMarkerBalloon().get();

        for (FencesDescriptionProcessor descriptionProcessor : descriptionProcessors) {
            if (descriptionProcessor.isValid(report)) {
                baseMarkerBalloon.setText(descriptionProcessor.getText(context, report));
            }
        }
    }

    private void drawMarkerForFence(LatLng fencePos, String fenceName) {
        MarkerBuilder markerBuilder = new MarkerBuilder(fencePos)
                .markerBalloon(new SimpleMarkerBalloon(context.getResources().
                        getString(R.string.report_service_fence_marker_balloon, fenceName)))
                .tag(FENCE_MARKERS_TAG)
                .icon(createMarkerIcon());

        tomtomMap.addMarker(markerBuilder);
    }

    public void removeFenceMarkers() {
        FuncUtils.forEach(tomtomMap.getMarkers(), marker ->
                tomtomMap.removeMarkerByTag(FENCE_MARKERS_TAG));
    }

    private Icon createMarkerIcon() {
        return Icon.Factory.fromResources(context, R.drawable.ic_marker_entry_point);
    }

}
