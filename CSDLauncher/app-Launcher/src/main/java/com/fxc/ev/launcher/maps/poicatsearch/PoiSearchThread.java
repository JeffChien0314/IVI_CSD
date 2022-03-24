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
    private FtsResultsListener myFtsResultsListener;
    private PoiSuggestionsListener myPoiSuggestionsListener;
    private Input input;
    private Context context;
    private Bundle bundle;

    public PoiSearchThread(Context context, FtsResultsListener myFtsResultsListener, PoiSuggestionsListener myPoiSuggestionsListener,Input input) {
        this.context = context;
        bundle = new BundleConfig(context).createBundleConfiguration();
        this.myFtsResultsListener = myFtsResultsListener;
        this.myPoiSuggestionsListener = myPoiSuggestionsListener;
        this.input = input;
    }

    @Override
    public void run() {
        Search search = new Search(context, bundle);
        search.query(input, myFtsResultsListener, myPoiSuggestionsListener);
    }

    /*private Input getInput(String query, Coordinate coordinate) {
        double scale = map.getCamera().getProperties().getScale();
        int numberLimit = getNumberOfCategory(scale,index);
        FilterByGeoRadius filter = new FilterByGeoRadius(coordinate, (int) scale);
        Input.Builder builder = new Input.Builder();
        builder.setSearchString(query)
                .setLanguage("zh-TW")
                .setLimit(numberLimit)
                .setExecutionMode(ExecutionMode.kOnline)
                .setFilterByGeoRadius(filter);
        Input input = builder.build();
        return input;
    }

    //Jerry@20220324 add:get category number
    private int getNumberOfCategory(double scale,int index){
        int number = 0;
        if(Constants.TWO_KM >= scale){
            number = Constants.SCALE_TYPE_1KM_2KM[index];
        }else if(scale > Constants.TWO_KM && scale <= Constants.FOUR_KM){
            number = Constants.SCALE_TYPE_2KM_4KM[index];
        }else if(scale > Constants.FOUR_KM && scale <= Constants.EIGHT_KM){
            number = Constants.SCALE_TYPE_4KM_8KM[index];
        }else if(scale > Constants.EIGHT_KM && scale <= Constants.TEN_KM){
            number = Constants.SCALE_TYPE_8KM_10KM[index];
        }else if(scale > Constants.TEN_KM && scale <= Constants.FIFTEEN_KM){
            number = Constants.SCALE_TYPE_10KM_15KM[index];
        }else if(scale > Constants.FIFTEEN_KM && scale <= Constants.TWENTY_FIVW_KM){
            number = Constants.SCALE_TYPE_15KM_25KM[index];
        }else if(scale > Constants.TWENTY_FIVW_KM && scale <= Constants.THIRTY_FIVE_KM){
            number = Constants.SCALE_TYPE_25KM_35KM[index];
        }
        return number;
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
                        ftsResult.getPoiName());
                *//*PoiCategorySet poiCategorySet = ftsResult.toPlace().poi().categories();
                for (PoiCategory poiCategory : poiCategorySet) {
                    Log.i("Jerry", "*FtsResults**poiCategory:" + poiCategory.id());
                }*//*
                //Log.i(TAG, "*****************************************");
            }
        }
    }

    private class MyPoiSuggestionsListener implements PoiSuggestionsListener {

        @Override
        public void onPoiSuggestionResults(@NonNull PoiSuggestionResults poiSuggestionResults) {
            
        }
    }*/
}
