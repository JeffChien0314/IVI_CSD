package com.fxc.ev.launcher.maps.poicatsearch;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.fxc.ev.launcher.R;
import com.tomtom.navkit.map.Layer;
import com.tomtom.navkit.map.Map;
import com.tomtom.navkit.map.Marker;
import com.tomtom.navkit2.place.Coordinate;
import com.tomtom.navkit2.search.ExecutionMode;
import com.tomtom.navkit2.search.FilterByGeoRadius;
import com.tomtom.navkit2.search.FtsResult;
import com.tomtom.navkit2.search.FtsResultVector;
import com.tomtom.navkit2.search.FtsResults;
import com.tomtom.navkit2.search.FtsResultsListener;
import com.tomtom.navkit2.search.Input;
import com.tomtom.navkit2.search.PoiSuggestionResults;
import com.tomtom.navkit2.search.PoiSuggestionsListener;
import com.tomtom.navkit2.search.Search;

import java.util.List;

public class PoiSearchThread extends Thread {
    private final String TAG = PoiSearchThread.class.getSimpleName();
    private String query;
    private Context context;
    private com.tomtom.navkit2.place.Coordinate coordinate;
    private Bundle bundle;
    private Map map;
    private Layer layer;
    private List<Marker> waypointMarkers;

    public PoiSearchThread(Context context, String query, Coordinate coordinate, Map map,List<Marker> waypointMarkers) {
        this.context = context;
        this.query = query;
        this.coordinate = coordinate;
        this.map = map;
        this.waypointMarkers = waypointMarkers;
        layer = map.addLayer();
        bundle = new BundleConfig(context).createBundleConfiguration();
    }

    @Override
    public void run() {
        Search search = new Search(context, bundle);
        search.query(getInput(query, coordinate), new MyFtsResultsListener(), new MyPoiSuggestionsListener());
    }

    private Input getInput(String query, Coordinate coordinate) {
        FilterByGeoRadius filter = new FilterByGeoRadius(coordinate, Constants.RADIUS_IN_METERS);
        Input.Builder builder = new Input.Builder();
        builder.setSearchString(query)
                .setLanguage("zh-TW")
                .setLimit(Constants.LIMIT)
                .setExecutionMode(ExecutionMode.kOnline)
                .setFilterByGeoRadius(filter);
        Input input = builder.build();
        return input;
    }

    private class MyFtsResultsListener implements FtsResultsListener {

        @Override
        public void onFtsResults(@NonNull FtsResults ftsResults) {
            FtsResultVector resultVector = ftsResults.getResults();
            for (FtsResult ftsResult : resultVector) {
                Log.i(TAG, "*FtsResults**Place:" + ftsResult.toPlace().toString());

                double latitude = ftsResult.getLocation().coordinate().latitude();
                double longitude = ftsResult.getLocation().coordinate().longitude();
                com.tomtom.navkit.map.Coordinate coordinate = new com.tomtom.navkit.map.Coordinate(latitude, longitude);
                new WaypointMarker(layer, context).addWaypointMarker(coordinate,context.getString(R.string.shop_poi_search_position_marker_path),
                        ftsResult.getPoiName(),waypointMarkers);

                /*PoiCategorySet poiCategorySet = ftsResult.toPlace().poi().categories();
                for (PoiCategory poiCategory : poiCategorySet) {
                    Log.i("Jerry", "*FtsResults**poiCategory:" + poiCategory.id());
                }*/
                //Log.i(TAG, "*****************************************");
            }
        }
    }

    private class MyPoiSuggestionsListener implements PoiSuggestionsListener {

        @Override
        public void onPoiSuggestionResults(@NonNull PoiSuggestionResults poiSuggestionResults) {
            
        }
    }
}
