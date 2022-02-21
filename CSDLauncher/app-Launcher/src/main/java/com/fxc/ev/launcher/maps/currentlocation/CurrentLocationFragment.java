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
package com.fxc.ev.launcher.maps.currentlocation;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.activities.FunctionalExampleFragment;
import com.tomtom.online.sdk.map.MapView;
import com.tomtom.online.sdk.map.TomtomMap;

import timber.log.Timber;

public class CurrentLocationFragment extends Fragment implements FunctionalExampleFragment {

    private final String MAP_RESTORE_KEY = "MAP_RESTORED_ARG";
    public TomtomMap tomtomMap;

    //private ActionBarModel actionBar;

    private boolean isRestored;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.v("onCreateView([inflater, container, savedInstanceState])" + new Object[]{inflater, container, savedInstanceState});
        if (savedInstanceState != null) {
            isRestored = savedInstanceState.getBoolean(MAP_RESTORE_KEY, false);
        }

        return inflater.inflate(R.layout.empty_fragment, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        assignMap();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("metis","CurrentLocationFragment:onResume()");
        /*actionBar = (ActionBarModel) getActivity();
        setActionBarTitle(R.string.app_name);
        setActionBarSubtitle(R.string.empty);*/
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.d("onPause()");
    }

    @Override
    public void onMenuItemSelected() {

    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

    @Override
    public boolean isMapRestored() {
        return isRestored;
    }

    @Override
    public String getFragmentTag() {
        return getClass().getName();
    }

    /*@Override
    public void setActionBarTitle(@StringRes int titleId) {
        actionBar.setActionBarTitle(titleId);
    }

    @Override
    public void setActionBarSubtitle(@StringRes int subtitleId) {
        actionBar.setActionBarSubtitle(subtitleId);
    }*/

    public void assignMap() {
        MapView mapView = getActivity().findViewById(R.id.map_view);
        if (mapView == null) return;
        mapView.addOnMapReadyCallback(map -> {
            Log.v("metis", "assignMap: onMapReady");
            tomtomMap = map;
            alignCurrentLocationButton(getContext(), tomtomMap);
            if (!isMapRestored()) {
                showMyPosition();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(MAP_RESTORE_KEY, true);
        super.onSaveInstanceState(outState);
        Log.v("metis", "onSaveInstanceState");
    }

    void alignCurrentLocationButton(@NonNull Context context, @NonNull TomtomMap tomtomMap) {
        int currentLocationButtonBottomMargin = context.getResources().getDimensionPixelSize(R.dimen.current_location_default_margin_bottom);
        int currentLocationButtonLeftMargin = context.getResources().getDimensionPixelSize(R.dimen.compass_default_margin_start);

        tomtomMap.getUiSettings().getCurrentLocationView().setMargins(
                currentLocationButtonLeftMargin, 0, 0, currentLocationButtonBottomMargin);
    }

    public void showMyPosition() {
        if (tomtomMap == null) {
            return;
        }

        tomtomMap.centerOnMyLocation();
    }

    /*@Override
    public void showInfoText(String text, long duration) {

    }

    @Override
    public void showInfoText(@StringRes int text, long duration) {
    }

    @Override
    public void enableOptionsView() {

    }

    @Override
    public void disableOptionsView() {

    }*/
}
