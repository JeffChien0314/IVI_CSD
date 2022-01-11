package com.fxc.ev.launcher.maps.driving.tracking;

import android.graphics.Color;
import android.location.Location;
import android.os.Handler;

import com.fxc.ev.launcher.maps.driving.ChevronSimulatorUpdater;
import com.tomtom.online.sdk.map.Chevron;
import com.tomtom.online.sdk.map.RouteSettings;
import com.tomtom.online.sdk.map.route.RouteLayerStyle;

public class RouteProgressSimulatorUpdater extends ChevronSimulatorUpdater {
    private final Handler handler = new Handler();
    private final RouteSettings routeSettings;
    private final long routeId;
    private static final int DARK_BLUE = 0x99006699;
    private static final long ONE_SECOND_IN_MILLIS = 1000L;

    public RouteProgressSimulatorUpdater(Chevron chevron, RouteSettings routeSettings, long routeId) {
        super(chevron);
        this.routeSettings = routeSettings;
        this.routeId = routeId;
        RouteLayerStyle routeLayerStyle = new RouteLayerStyle.Builder()
                .color(Color.parseColor("#006699"))
                .build();

        routeSettings.activateProgressAlongRoute(routeId, routeLayerStyle);
    }

    @Override
    public void onNewRoutePointVisited(Location location) {
        super.onNewRoutePointVisited(location);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                routeSettings.updateProgressAlongRoute(routeId, location);
            }
        }, ONE_SECOND_IN_MILLIS);
    }
}
