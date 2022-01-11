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

import android.content.Context;

import com.fxc.ev.launcher.activities.FunctionalExampleModel;
import com.fxc.ev.launcher.maps.RoutePlannerPresenter;
import com.fxc.ev.launcher.maps.RouteSpecificationFactory;
import com.fxc.ev.launcher.activities.FunctionalExampleFragment;
import com.fxc.ev.launcher.maps.routeconfig.AmsterdamToHaarlemRouteConfig;
import com.fxc.ev.launcher.maps.RoutingUiListener;
import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.map.TomtomMap;
import com.tomtom.online.sdk.routing.route.RouteSpecification;
import com.tomtom.online.sdk.routing.route.information.FullRoute;

import timber.log.Timber;

public class SearchAlongRoutePresenter extends RoutePlannerPresenter {

    protected Context context;
    protected SearchAlongRouteFragment fragment;
    private SearchAlongRouteRequester searchRequester;
    /*private LatLng origin;
    private LatLng destination;*/

    public SearchAlongRoutePresenter(RoutingUiListener viewModel) {
        super(viewModel);
    }

    @Override
    public void bind(FunctionalExampleFragment view, TomtomMap map) {
        super.bind(view, map);
        context = view.getContext();
        fragment = (SearchAlongRouteFragment) view;
        cleanup();

        searchRequester = new SearchAlongRouteRequester(view.getContext(), new SearchAlongRouteResultDisplay(tomtomMap, fragment));
        displayRoute();
    }

    @Override
    public FunctionalExampleModel getModel() {
        return new SearchAlongRouteFunctionalExample();
    }

    void displayRoute() {
        viewModel.showRoutingInProgressDialog();
        if (fragment.getOrigin() != null && fragment.getDestination() != null) {
            showRoute(getRouteQuery(fragment.getOrigin(), fragment.getDestination()));
        } else {
            showRoute(getRouteQuery());
        }
        tomtomMap.clearRoute();
    }

    protected RouteSpecification getRouteQuery() {
        return RouteSpecificationFactory.createRouteForAlongRouteSearch(new AmsterdamToHaarlemRouteConfig());
    }

    //metis@210018 add
    protected RouteSpecification getRouteQuery(LatLng origin, LatLng destination) {
        return RouteSpecificationFactory.createRouteForAlongRouteSearch(origin, destination);
    }

    public void performSearch(String term) {

        //fragment.disableOptionsView();

        if (routesMap.isEmpty()) {
            Timber.d("performSearch(): no routes available for term " + term);
            return;
        }

        FullRoute route = (FullRoute) routesMap.values().toArray()[0];

        searchRequester.performSearch(term, route);
    }

    @Override
    public void cleanup() {
        if (tomtomMap != null) {
            tomtomMap.removeMarkers();
            tomtomMap.getUiSettings().getCompassView().show(); //指南针
            tomtomMap.getUiSettings().getCurrentLocationView().show(); //定位
            tomtomMap.getUiSettings().getPanningControlsView().hide(); //平移控制
            tomtomMap.getUiSettings().getZoomingControlsView().hide(); //缩放控制
            tomtomMap.clearRoute();
            tomtomMap.getDrivingSettings().removeChevrons();
            tomtomMap.getOverlaySettings().removeOverlays();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (tomtomMap != null){
            tomtomMap.hideTrafficFromRoute(tomtomMap.getRoutes().get(0).getId());
        }
    }
}
