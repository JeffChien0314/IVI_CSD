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
package com.fxc.ev.launcher.maps.uiextensions;

import android.content.Context;

import androidx.annotation.NonNull;

import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.utils.Locations;
import com.fxc.ev.launcher.utils.formatter.LatLngFormatter;
import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.map.BaseMarkerBalloon;
import com.tomtom.online.sdk.map.Icon;
import com.tomtom.online.sdk.map.Marker;
import com.tomtom.online.sdk.map.MarkerAnchor;
import com.tomtom.online.sdk.map.MarkerBuilder;
import com.tomtom.online.sdk.map.SimpleMarkerBalloon;
import com.tomtom.online.sdk.map.SingleLayoutBalloonViewAdapter;
import com.tomtom.online.sdk.map.TextBalloonViewAdapter;
import com.tomtom.online.sdk.map.TomtomMap;

import java.util.List;

public class MarkerDrawer {

    private static final String SAMPLE_GIF_ASSET_PATH = "images/racing_car.gif";

    private Context context;
    private TomtomMap tomtomMap;

    public MarkerDrawer(Context context, TomtomMap tomtomMap) {
        this.context = context;
        this.tomtomMap = tomtomMap;
    }

    public void createRandomAnimatedMarkers(int number) {
        List<LatLng> list = Locations.randomLocation(Locations.AMSTERDAM_LOCATION, number, 0.2f);
        for (LatLng position : list) {
            //tag::doc_create_animated_marker[]
            MarkerBuilder markerBuilder = new MarkerBuilder(position)
                    .icon(createAnimatedIcon());
            tomtomMap.addMarker(markerBuilder);
            //end::doc_create_animated_marker[]
        }
    }

    public void createRandomDraggableMarkers(int number) {
        List<LatLng> list = Locations.randomLocation(Locations.AMSTERDAM_LOCATION, number, 0.2f);
        for (LatLng position : list) {
            //tag::doc_create_simple_draggable_marker[]
            MarkerBuilder markerBuilder = new MarkerBuilder(position)
                    .markerBalloon(new SimpleMarkerBalloon(positionToText(position)))
                    .draggable(true);
            tomtomMap.addMarker(markerBuilder);
            //end::doc_create_simple_draggable_marker[]
        }
    }

    public void createRandomMarkersWithClustering(LatLng centerLocation, int number, float distanceInDegree) {
        List<LatLng> list = Locations.randomLocation(centerLocation, number, distanceInDegree);
        for (LatLng position : list) {
            //tag::doc_add_marker_to_cluster[]
            MarkerBuilder markerBuilder = new MarkerBuilder(position)
                    .shouldCluster(true)
                    .markerBalloon(new SimpleMarkerBalloon(positionToText(position)));
            //end::doc_add_marker_to_cluster[]
            tomtomMap.addMarker(markerBuilder);
        }
    }

    public void createMarkerWithSimpleBalloon(LatLng position) {
        //tag::doc_init_popup_layout[]
        tomtomMap.getMarkerSettings().setMarkerBalloonViewAdapter(new TextBalloonViewAdapter());
        SimpleMarkerBalloon balloon = new SimpleMarkerBalloon("Welcome to TomTom");
        MarkerBuilder markerBuilder = new MarkerBuilder(position)
                .markerBalloon(balloon);

        Marker m = tomtomMap.addMarker(markerBuilder);
        //end::doc_init_popup_layout[]
        m.select();
    }

    public void createMarkerWithSimpleMarkerBalloon(Context context,LatLng position,String name) {
        //tag::doc_init_popup_layout[]
        tomtomMap.getMarkerSettings().setMarkerBalloonViewAdapter(new TextBalloonViewAdapter());
        //SimpleMarkerBalloon balloon = new SimpleMarkerBalloon("Welcome to TomTom");
        MarkerBuilder markerBuilder = new MarkerBuilder(position)
                //.icon(Icon.Factory.fromResources(context, R.drawable.ic_marker))
                .markerBalloon(new SimpleMarkerBalloon(name));

        Marker m = tomtomMap.addMarker(markerBuilder);
        //end::doc_init_popup_layout[]
        //m.select();  //metis@211119 mark
    }

    public void createMarkerWithCustomBalloon(LatLng position) {
        //tag::doc_show_popup_layout[]
        tomtomMap.getMarkerSettings().setMarkerBalloonViewAdapter(new SingleLayoutBalloonViewAdapter(R.layout.custom_balloon_layout));
        MarkerBuilder markerBuilder = new MarkerBuilder(position)
                .markerBalloon(new BaseMarkerBalloon());
        Marker m = tomtomMap.addMarker(markerBuilder);
        //end::doc_show_popup_layout[]
        m.select();
    }

    void createSimpleMarkers(LatLng location, int number, float distanceInDegree) {
        List<LatLng> list = Locations.randomLocation(location, number, distanceInDegree);
        for (LatLng position : list) {
            //tag::doc_create_simple_marker[]
            MarkerBuilder markerBuilder = new MarkerBuilder(position)
                    .markerBalloon(new SimpleMarkerBalloon(positionToText(position)));
            tomtomMap.addMarker(markerBuilder);
            //end::doc_create_simple_marker[]
        }
    }

    void createDecalMarkers(LatLng location, int number, float distanceInDegree) {
        List<LatLng> list = Locations.randomLocation(location, number, distanceInDegree);
        for (LatLng position : list) {
            //tag::doc_create_decal_marker[]
            MarkerBuilder markerBuilder = new MarkerBuilder(position)
                    .icon(Icon.Factory.fromResources(context, R.drawable.ic_favourites))
                    .markerBalloon(new SimpleMarkerBalloon(positionToText(position)))
                    .tag("more information in tag").iconAnchor(MarkerAnchor.Bottom)
                    .decal(true); //By default is false
            tomtomMap.addMarker(markerBuilder);
            //end::doc_create_decal_marker[]
        }
    }

    @NonNull
    private String positionToText(LatLng position) {
        return LatLngFormatter.toSimpleString(position);
    }

    private Icon createAnimatedIcon() {
        return Icon.Factory.fromAssets(context, SAMPLE_GIF_ASSET_PATH);
    }
}
