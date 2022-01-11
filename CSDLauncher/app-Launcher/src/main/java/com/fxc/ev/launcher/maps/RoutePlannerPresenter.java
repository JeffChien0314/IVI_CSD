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
package com.fxc.ev.launcher.maps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.fxc.ev.launcher.BuildConfig;
import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.utils.CheckedButtonCleaner;
import com.fxc.ev.launcher.utils.RouteUtils;
import com.fxc.ev.launcher.activities.BaseFunctionalExamplePresenter;
import com.fxc.ev.launcher.activities.FunctionalExampleFragment;
import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.location.Locations;
import com.tomtom.online.sdk.map.Icon;
import com.tomtom.online.sdk.map.Route;
import com.tomtom.online.sdk.map.RouteBuilder;
import com.tomtom.online.sdk.map.RouteSettings;
import com.tomtom.online.sdk.map.RouteStyle;
import com.tomtom.online.sdk.map.TomtomMap;
import com.tomtom.online.sdk.map.model.MapModeType;
import com.tomtom.online.sdk.map.route.RouteLayerStyle;
import com.tomtom.online.sdk.map.route.traffic.TrafficData;
import com.tomtom.online.sdk.map.rx.RxContext;
import com.tomtom.online.sdk.routing.OnlineRoutingApi;
import com.tomtom.online.sdk.routing.RoutingApi;
import com.tomtom.online.sdk.routing.RoutingException;
import com.tomtom.online.sdk.routing.route.RouteCallback;
import com.tomtom.online.sdk.routing.route.RoutePlan;
import com.tomtom.online.sdk.routing.route.RouteSpecification;
import com.tomtom.online.sdk.routing.route.information.FullRoute;
import com.tomtom.online.sdk.routing.route.information.RouteSection;
import com.tomtom.online.sdk.routing.rx.RxRoutingApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class RoutePlannerPresenter extends BaseFunctionalExamplePresenter
        implements RxContext {

    protected CheckedButtonCleaner checkedButtonCleaner;
    protected RoutingUiListener viewModel;

    protected RoutingApi routingApi;

    private Long DENSITY_LEVEL_0_COLOR = 4291598592L;
    private Long DENSITY_LEVEL_1_COLOR = 4294967040L;
    private Long DENSITY_LEVEL_2_COLOR = 4294940928L;
    private Long DENSITY_LEVEL_3_COLOR = 4294914816L;
    private Long DENSITY_LEVEL_4_COLOR = 4288230144L;
    private Long DENSITY_LEVEL_5_COLOR = 4294967295L;

    private List<RouteLayerStyle> routeTrafficStyles = new ArrayList<>();

    /**
     * Choose map which keep orders. To match queries with the order.
     */
    protected Map<Long, FullRoute> routesMap = new LinkedHashMap<>();
    protected CompositeDisposable compositeDisposable = new CompositeDisposable();
    protected Icon defaultStartIcon;
    protected Icon defaultEndIcon;

    public void setRoutesMap(Map<Long, FullRoute> routesMap) {
        this.routesMap = routesMap;
    }

    public Map<Long, FullRoute> getRoutesMap() {
        return routesMap;
    }

    public RoutePlannerPresenter(RoutingUiListener viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void onPause() {
        compositeDisposable.clear();
    }

    @NonNull
    @Override
    public Scheduler getWorkingScheduler() {
        return networkScheduler;
    }

    @NonNull
    @Override
    public Scheduler getResultScheduler() {
        return AndroidSchedulers.mainThread();
    }

    @Override
    public void cleanup() {
        compositeDisposable.clear();
        super.cleanup();
        tomtomMap.clear();
        tomtomMap.setPadding(DEFAULT_MAP_PADDING, DEFAULT_MAP_PADDING,
                DEFAULT_MAP_PADDING, DEFAULT_MAP_PADDING);

    }

    @Override
    public void bind(FunctionalExampleFragment view, TomtomMap map) {
        super.bind(view, map);

        createOnlineRoutingApi(view.getContext().getApplicationContext());

        confMapPadding();
        confRouteIcons();
        createTrafficStyles();

        if (!view.isMapRestored()) {
            Log.v("metis", "!view.isMapRestored()");
            tomtomMap.getUiSettings().setMapModeType(MapModeType.MODE_2D);
            centerOnDefaultLocation();
        } else {
            Log.v("metis", "view.isMapRestored()");
            tomtomMap.displayRoutesOverview();
        }

        viewModel.repeatRequestWhenNotFinished();
    }

    private void createTrafficStyles() {
        List<Long> colorLevels = Arrays.asList(DENSITY_LEVEL_0_COLOR, DENSITY_LEVEL_1_COLOR, DENSITY_LEVEL_2_COLOR
                , DENSITY_LEVEL_3_COLOR, DENSITY_LEVEL_4_COLOR, DENSITY_LEVEL_5_COLOR);
        for (Long colorLevel : colorLevels) {
            RouteLayerStyle routeLayerStyle = new RouteLayerStyle.Builder()
                    .color(colorLevel.intValue())
                    .build();
            routeTrafficStyles.add(routeLayerStyle);
        }
    }

    public void createOnlineRoutingApi(Context context) {
        //tag::doc_initialise_routing[]
        routingApi = OnlineRoutingApi.create(context, BuildConfig.ROUTING_API_KEY);
        //end::doc_initialise_routing[]
    }

    @SuppressWarnings("unused")
    public void planRxRoute(Context context, RouteSpecification routeSpecification) {
        //tag::doc_initialise_rx_routing[]
        RxRoutingApi routingApi = new RxRoutingApi(context, BuildConfig.ROUTING_API_KEY);
        //end::doc_initialise_rx_routing[]

        //tag::doc_execute_rx_routing[]
        Disposable disposable = routingApi.planRoute(routeSpecification)
                .subscribeOn(getWorkingScheduler())
                .observeOn(getResultScheduler())
                .subscribe(
                        routePlan -> displayFullRoutes(routePlan),
                        throwable -> proceedWithError(throwable.getMessage())
                );
        //end::doc_execute_rx_routing[]
    }

    public void confRouteIcons() {
        defaultStartIcon = Icon.Factory.fromResources(view.getContext(), R.drawable.ic_map_route_departure);
        defaultEndIcon = Icon.Factory.fromResources(view.getContext(), R.drawable.ic_map_route_destination);
    }

    @Override
    protected void confMapPadding() {
        int offsetBig = view.getContext().getResources().getDimensionPixelSize(R.dimen.offset_super_big);

        int actionBarHeight = view.getContext().getResources().getDimensionPixelSize(
                R.dimen.abc_action_bar_default_height_material);

        int etaPanelHeight = view.getContext().getResources().getDimensionPixelSize(
                R.dimen.eta_panel_height);

        int buttonPadding = view.getContext().getResources().getDimensionPixelSize(R.dimen.button_offset);

        int topPadding = actionBarHeight + etaPanelHeight + offsetBig;
        int bottomPadding = actionBarHeight + buttonPadding;

        tomtomMap.setPadding(topPadding, offsetBig, bottomPadding, offsetBig);
    }

    @SuppressLint("CheckResult")
    public void showRoute(RouteSpecification routeSpecification, RouteCallback routeCallback) {
        //tag::doc_execute_routing[]
        routingApi.planRoute(routeSpecification, routeCallback);
        //end::doc_execute_routing[]
    }

    @SuppressLint("CheckResult")
    public void showRoute(RouteSpecification routeSpecification) {
        routingApi.planRoute(routeSpecification, routeCallback);
    }

    protected void displayRoutes(RoutePlan routePlan) {
        displayRoutes(routePlan, RouteStyle.DEFAULT_ROUTE_STYLE, defaultStartIcon, defaultEndIcon);
    }

    protected void displayRoutes(RoutePlan routePlan, RouteStyle routeStyle, Icon startIcon, Icon endIcon) {

        routesMap.clear();

        displayFullRoutes(routePlan, routeStyle, startIcon, endIcon);

        tomtomMap.displayRoutesOverview();
    }

    protected void displayFullRoutes(RoutePlan routePlan) {
        displayFullRoutes(routePlan, RouteStyle.DEFAULT_ROUTE_STYLE, defaultStartIcon, defaultEndIcon);
    }

    protected void displayFullRoutes(RoutePlan routePlan, RouteStyle routeStyle, Icon startIcon, Icon endIcon) {
        List<FullRoute> routes = routePlan.getRoutes();

        //metis@211208
        int routeIndex = 0;
        for (FullRoute route : routes) {
            routeIndex++;
            addMapRoute(routeStyle, startIcon, endIcon, route, routeIndex);
        }

        processAddedRoutes(routeStyle, routes);
    }

    protected void addMapRoute(RouteStyle routeStyle, Icon startIcon, Icon endIcon, FullRoute route, int routeIndex) {
        //tag::doc_display_route[]
        RouteBuilder routeBuilder = new RouteBuilder(route.getCoordinates())
                .endIcon(endIcon)
                .startIcon(startIcon)
                .style(routeStyle)
                .tag(String.valueOf(routeIndex));
        final Route mapRoute = tomtomMap.addRoute(routeBuilder);
        //end::doc_display_route[]

        showTrafficOnRoute(route);

        routesMap.put(mapRoute.getId(), route);
    }

    private void showTrafficOnRoute(FullRoute route) {
        List<RouteSection> routeSections = route.getSections();
        LinkedHashMap<RouteLayerStyle, List<TrafficData>> trafficStyle = new LinkedHashMap<>();
        List<TrafficData> trafficData = new ArrayList<>();
        for (RouteSection routeSection : routeSections) {
            int styleIndex = 5;
            if (routeSection.getMagnitudeOfDelay() >= 0) {
                styleIndex = routeSection.getMagnitudeOfDelay();
            }

            RouteLayerStyle routeLayerStyle = routeTrafficStyles.get(styleIndex);

            trafficData.add(new TrafficData(routeSection.getStartPointIndex(), routeSection.getEndPointIndex()));
            trafficStyle.put(routeLayerStyle, trafficData);
        }

        tomtomMap.showTrafficOnRoute(tomtomMap.getRoutes().get(0).getId(), trafficStyle);
    }

    protected void processAddedRoutes(RouteStyle routeStyle, List<FullRoute> routes) {
        selectFirstRouteAsActive(routeStyle);
        if (!routes.isEmpty()) {
            displayInfoAboutRoute(routes.get(0));
        }
    }

    protected void selectFirstRouteAsActive(RouteStyle routeStyle) {
        if (!tomtomMap.getRouteSettings().getRoutes().isEmpty() && routeStyle.equals(RouteStyle.DEFAULT_ROUTE_STYLE)) {
            RouteSettings routeSettings = tomtomMap.getRouteSettings();
            long routeId = tomtomMap.getRouteSettings().getRoutes().get(0).getId();
            RouteUtils.setRoutesInactive(routeSettings);
            RouteUtils.setRouteActive(routeId, routeSettings);
        }
    }

    protected void displayInfoAboutRoute(FullRoute routeResult) {
        viewModel.hideRoutingInProgressDialog();
        viewModel.routeUpdated(routeResult);
    }

    protected void proceedWithError(String message) {
        viewModel.showError(view.getContext().getString(R.string.msg_error_general_route_processing, message));
        viewModel.hideRoutingInProgressDialog();
    }

    public void centerOnDefaultLocation() {
        centerOn(new LatLng(Locations.AMSTERDAM.getLatitude(), Locations.AMSTERDAM.getLongitude()));
    }

    void setCheckedButtonCleaner(CheckedButtonCleaner checkedButtonCleaner) {
        this.checkedButtonCleaner = checkedButtonCleaner;
    }

    public float getConsumption(FullRoute route) {
        //One of those two items is always 0, so max will select the proper value
        float fuelConsumption = route.getSummary().getFuelConsumptionInLiters();
        float batteryConsumption = route.getSummary().getBatteryConsumptionInkWh();
        return Math.max(fuelConsumption, batteryConsumption);
    }

    private RouteCallback routeCallback = new RouteCallback() {
        @Override
        public void onSuccess(@NonNull RoutePlan routePlan) {
            displayRoutes(routePlan);
        }

        @Override
        public void onError(@NonNull RoutingException error) {
            proceedWithError(error.getMessage());
        }
    };
}