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
package com.fxc.ev.launcher.maps.driving;

import android.widget.Toast;

import com.fxc.ev.launcher.BuildConfig;
import com.fxc.ev.launcher.maps.routeconfig.RouteConfigExample;
import com.fxc.ev.launcher.maps.routeconfig.LodzCityCenterRouteConfig;
import com.tomtom.online.sdk.common.location.LatLng;

import com.tomtom.online.sdk.map.ChevronPosition;
import com.tomtom.online.sdk.map.Route;
import com.tomtom.online.sdk.map.RouteBuilder;
import com.tomtom.online.sdk.routing.OnlineRoutingApi;
import com.tomtom.online.sdk.routing.RoutingApi;
import com.tomtom.online.sdk.routing.RoutingException;
import com.tomtom.online.sdk.routing.route.RouteCalculationDescriptor;
import com.tomtom.online.sdk.routing.route.RouteCallback;
import com.tomtom.online.sdk.routing.route.RouteDescriptor;
import com.tomtom.online.sdk.routing.route.RoutePlan;
import com.tomtom.online.sdk.routing.route.RouteSpecification;
import com.tomtom.online.sdk.routing.route.information.FullRoute;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractRoutingPresenter extends AbstractTrackingPresenter {

    protected static final RouteConfigExample ROUTE_CONFIG = new LodzCityCenterRouteConfig();

    @Override
    protected void onPresenterCreated() {
        super.onPresenterCreated();
        planRoute();
    }

    @Override
    protected void onPresenterRestored() {
        super.onPresenterRestored();
        restoreRouteOverview();
    }

    private void planRoute() {
        RoutingApi routingApi = OnlineRoutingApi.create(getContext(), BuildConfig.ROUTING_API_KEY);
        routingApi.planRoute(getRouteSpecification(), routeCallback);
    }

    //metis@211122 add
    public void planRoute(LatLng origin, LatLng destination) {
        RoutingApi routingApi = OnlineRoutingApi.create(getContext(), BuildConfig.ROUTING_API_KEY);
        routingApi.planRoute(getRouteSpecification(origin, destination), routeCallback);
    }

    private RouteSpecification getRouteSpecification() {
            RouteDescriptor descriptor = new RouteDescriptor.Builder()
                    .considerTraffic(false)
                    .build();
        RouteCalculationDescriptor calculationDescriptor = new RouteCalculationDescriptor.Builder()
                .routeDescription(descriptor)
                .waypoints(ROUTE_CONFIG.getWaypoints())
                .build();
        return new RouteSpecification.Builder(ROUTE_CONFIG.getOrigin(), ROUTE_CONFIG.getDestination())
                .routeCalculationDescriptor(calculationDescriptor)
                .build();
    }

    //metis@211122 add
    private RouteSpecification getRouteSpecification(LatLng origin, LatLng destination) {
        RouteDescriptor descriptor = new RouteDescriptor.Builder()
                .considerTraffic(false)
                .build();
        RouteCalculationDescriptor calculationDescriptor = new RouteCalculationDescriptor.Builder()
                .routeDescription(descriptor)
                //.waypoints(ROUTE_CONFIG.getWaypoints())
                .build();
        return new RouteSpecification.Builder(origin, destination)
                .routeCalculationDescriptor(calculationDescriptor)
                .build();
    }

    private FullRoute getFirstRouteFromResponse(RoutePlan routePlan) {
        return routePlan.getRoutes().get(0);
    }

    private LatLng getRouteOrigin(FullRoute fullRoute) {
        return fullRoute.getCoordinates().get(0);
    }

    protected List<LatLng> getFirstRouteFromMap() {
        return tomtomMap.getRoutes().get(0).getCoordinates();
    }

    protected List<Route> getRoutesFromMap() {
        return tomtomMap.getRoutes();
    }

    protected void restoreRouteOverview() {
        if (tomtomMap.getDrivingSettings().isTracking()) {
            tomtomMap.getRouteSettings().displayRoutesOverview();
        }
    }

    protected void showRoute(FullRoute route) {
        tomtomMap.addRoute(new RouteBuilder(route.getCoordinates()));
        tomtomMap.getRouteSettings().displayRoutesOverview();
        tomtomMap.set3DMode();
    }

    private RouteCallback routeCallback = new RouteCallback() {
        @Override
        public void onSuccess(@NotNull RoutePlan routePlan) {
            FullRoute route = getFirstRouteFromResponse(routePlan);
            onRouteReady(route);
        }

        @Override
        public void onError(@NotNull RoutingException error) {
            onRouteError(error);
        }
    };

    protected void onRouteReady(FullRoute fullRoute) {
        ChevronPosition chevronPosition = new ChevronPosition.Builder(getRouteOrigin(fullRoute).toLocation()).build();
        getChevron().setPosition(chevronPosition);
        showRoute(fullRoute);
        restoreSimulator();
    }

    protected void onRouteError(RoutingException exception) {
        Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
    }

}
