package com.fxc.ev.launcher.maps.poicatsearch;

import android.content.Context;
import android.util.Log;

import com.fxc.ev.launcher.R;
import com.tomtom.navkit.map.Color;
import com.tomtom.navkit.map.Layer;
import com.tomtom.navkit.map.Marker;
import com.tomtom.navkit.map.MarkerBuilder;
import com.tomtom.navkit.map.MarkerLabelBuilder;

import java.util.ArrayList;
import java.util.List;

public class WaypointMarker {
    private Layer markerLayer;
    private Context context;

    private Marker destinationMarker;
    public List<Marker> waypointMarkers = new ArrayList<>();

    public WaypointMarker(Layer markerLayer, Context context) {
        this.markerLayer = markerLayer;
        this.context = context;
    }

    public void addWaypointMarker(com.tomtom.navkit.map.Coordinate waypoint, String url, String poiName) {
        final MarkerBuilder markerBuilder = new MarkerBuilder();
        MarkerLabelBuilder markerLabelBuilder = null;
        markerBuilder
                .setCoordinate(waypoint)
                .setPinUri(url)
                .setIconUri(url);
        try {
            markerLabelBuilder = markerBuilder.addLabel();
            markerLabelBuilder
                    .setFontUri(context.getString(R.string.font_style))
                    .setTextAnchoring(MarkerLabelBuilder.TextAnchoring.kLeft)
                    .setTextSize(20)
                    .setWrapText(false)
                    //.setMaximumNumberOfLines(1)
                    .setOffset(15, 5)
                    .setText(poiName)
                    .setTextColor(new Color(255, 0, 0));

        } catch (MarkerBuilder.AlreadyHasLabel alreadyHasLabel) {
            alreadyHasLabel.printStackTrace();
        }
        Marker marker = markerLayer.addMarker(markerBuilder);
        waypointMarkers.add(marker);
    }

    public Marker addWaypointMarker(com.tomtom.navkit.map.Coordinate waypoint) {
        final MarkerBuilder markerBuilder = new MarkerBuilder();
        markerBuilder
                .setCoordinate(waypoint)
                .setPinUri(context.getString(R.string.poi_search_position_marker_path))
                .setIconUri(context.getString(R.string.poi_search_position_marker_path));

        destinationMarker = markerLayer.addMarker(markerBuilder);
        return destinationMarker;
    }

    public void removeMarker(Marker destinationMarker){
        if(destinationMarker!=null) {
            markerLayer.removeMarker(destinationMarker);
        }
    }

    public void removeAllMarkers() {
        for (Marker waypointMarker : waypointMarkers) {
            markerLayer.removeMarker(waypointMarker);
        }
        waypointMarkers.clear();
    }
}
