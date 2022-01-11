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
package com.fxc.ev.launcher.maps.search;

import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.activities.FunctionalExampleFragment;
import com.tomtom.online.sdk.map.Icon;
import com.tomtom.online.sdk.map.MarkerBuilder;
import com.tomtom.online.sdk.map.SimpleMarkerBalloon;
import com.tomtom.online.sdk.map.TomtomMap;
import com.tomtom.online.sdk.search.api.SearchError;
import com.tomtom.online.sdk.search.api.alongroute.AlongRouteSearchResultListener;
import com.tomtom.online.sdk.search.data.alongroute.AlongRouteSearchResponse;
import com.tomtom.online.sdk.search.data.alongroute.AlongRouteSearchResult;

/**
 * Class to display search result.
 */
class SearchAlongRouteResultDisplay implements AlongRouteSearchResultListener {
    private final TomtomMap tomtomMap;
    private final FunctionalExampleFragment view;

    public SearchAlongRouteResultDisplay(TomtomMap tomtomMap, FunctionalExampleFragment view) {

        this.tomtomMap = tomtomMap;
        this.view = view;
    }

    @Override
    public void onSearchResult(AlongRouteSearchResponse alongRouteSearchResponse) {
        tomtomMap.getMarkerSettings().removeMarkers();
        for (AlongRouteSearchResult result : alongRouteSearchResponse.getResults()) {
            MarkerBuilder markerBuilder = new MarkerBuilder(result.getPosition());
            if (alongRouteSearchResponse.getSummary().getQuery().equals("electric_vehicle_station")) {
                markerBuilder.icon(Icon.Factory.fromResources(view.getContext(), R.drawable.ic_pin_ev_station));
            } else if (alongRouteSearchResponse.getSummary().getQuery().equals("petrol_station")) {
                markerBuilder.icon(Icon.Factory.fromResources(view.getContext(), R.drawable.ic_map_fav));
            } else {
                markerBuilder.icon(Icon.Factory.fromResources(view.getContext(), R.drawable.ic_map_fav));
            }
            markerBuilder.markerBalloon(new SimpleMarkerBalloon(result.getPoi().getName()));
            tomtomMap.addMarker(markerBuilder);
        }
        //view.enableOptionsView();
    }

    @Override
    public void onSearchError(SearchError error) {
        //view.showInfoText(error.getMessage(), Toast.LENGTH_LONG);
        tomtomMap.getMarkerSettings().removeMarkers();
        //view.enableOptionsView();
    }
}
