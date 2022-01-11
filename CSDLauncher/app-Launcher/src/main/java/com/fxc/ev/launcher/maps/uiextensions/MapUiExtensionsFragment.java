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
package com.fxc.ev.launcher.maps.uiextensions;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.maps.ExampleFragment;
import com.fxc.ev.launcher.maps.search.SearchAlongRouteFragment;
import com.fxc.ev.launcher.utils.views.OptionsButtonsView;
import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.search.fuzzy.FuzzySearchDetails;

import java.util.UUID;

public class MapUiExtensionsFragment extends ExampleFragment<MapUiExtensionsPresenter> {

    private static final String PROJECT_UUID_BUNDLE_KEY = "PROJECT_UUID_BUNDLE_KEY";

    private static final int NUMBER_OF_ACTIONS = 3;
    private LatLng latLng;
    //private LatLng curPosition;
    private FuzzySearchDetails fuzzySearchDetails;
    //private FunctionalExamplesActivity mActivity;
    private Boolean isShowMarker;
    private double zoomLevel;
    private static final int DEFAULT_ZOOM_LEVEL = 10;
    private Boolean isFromSearchFragment;

    @Override
    protected MapUiExtensionsPresenter createPresenter() {
        return new MapUiExtensionsPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v("metis", "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v("metis", "onViewCreated");

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(PROJECT_UUID_BUNDLE_KEY, presenter.getCurrentProjectId());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("metis", "onCreate");
        /*mActivity = (FunctionalExamplesActivity) getActivity();
        curPosition = new LatLng(mActivity.getCurrentPosition());*/
        if (savedInstanceState != null) {
            presenter.setCurrentProjectId((UUID) savedInstanceState.getSerializable(PROJECT_UUID_BUNDLE_KEY));
        }

        Bundle bundle = getArguments();
        if (bundle != null) {
            isFromSearchFragment = true;
            fuzzySearchDetails = (FuzzySearchDetails) bundle.getSerializable("searchDetail");
            latLng = fuzzySearchDetails.getPosition(); //搜索位置
            isShowMarker = bundle.getBoolean("showMarker");
            zoomLevel = bundle.getDouble("zoomLevel");
        } else {
            latLng = getCurPosition();
            isShowMarker = false;
            zoomLevel = DEFAULT_ZOOM_LEVEL;
            isFromSearchFragment = false;
        }
    }

    @Override
    public void onResume() {
        Log.v("metis", "onResume");
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.v("metis", "onOptionsItemSelected");
        return super.onOptionsItemSelected(item);
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public Boolean isShowMarker() {
        return isShowMarker;
    }

    /*public LatLng getCurPosition() {
        return curPosition;
    }*/

    public FuzzySearchDetails getFuzzySearchDetails() {
        return fuzzySearchDetails;
    }

    public Double getZoomLevel() {
        return zoomLevel;
    }

    @Override
    protected void onOptionsButtonsView(final OptionsButtonsView view) {
        Log.v("metis", "onOptionsButtonsView");
        if (isFromSearchFragment) {
            view.addOption("Go to here");
            view.addOption("Fences");
        }

        /*view.addOption(R.string.map_ui_extensions_default);
        view.addOption(R.string.map_ui_extensions_custom);
        view.addOption(R.string.map_ui_extensions_hide);
        optionsView.selectItem(0, true);*/
    }

    @Override
    public boolean isMapRestored() {
        /*final boolean[] previousState = new boolean[]{
                optionsView.isSelected(0),
                optionsView.isSelected(1),
                optionsView.isSelected(2)
        };
        Log.v("metis", "isMapRestored: " + executeActions(previousState));*/
        return super.isMapRestored()/*executeActions(previousState)*/;
    }

    @Override
    public void onChange(boolean[] oldValues, boolean[] newValues) {
        Log.v("metis", "onChange");
        //executeActions(newValues);
        if (isFromSearchFragment) {
            if(newValues[0]) {
                toSearchAlongRouteFragment(fuzzySearchDetails);
            } else if(newValues[1]){
                presenter.drawTwoFences(fuzzySearchDetails.getPosition());
            }
        }
    }

    private void toSearchAlongRouteFragment(FuzzySearchDetails fuzzySearchDetails) {
        SearchAlongRouteFragment searchAlongRouteFragment = new SearchAlongRouteFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("origin", getCurPosition());
        bundle.putSerializable("destination", fuzzySearchDetails.getPosition());
        searchAlongRouteFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.functional_example_control_container, searchAlongRouteFragment, searchAlongRouteFragment.getFragmentTag())
                .commit();
    }


    private boolean executeActions(boolean[] newValues) {
        Log.v("metis", "executeActions");
        for (int i = 0; i < NUMBER_OF_ACTIONS; i++) {
            if (newValues[i]) {
                executeActionForSelection(i);
                return true;
            }
        }
        return false;
    }

    private void executeActionForSelection(int selectionId) {
        Log.v("metis", "executeActionForSelection():" + selectionId);

        switch (selectionId) {
            case 0:
                presenter.show(isShowMarker, latLng, zoomLevel);
                presenter.defaultMapComponentIcons(); //设置缩放，平移，定位 icons
                break;
            case 1:
                presenter.show(isShowMarker, latLng, zoomLevel);
                presenter.customMapComponentIcons(isShowMarker, latLng, zoomLevel);
                break;
            case 2:
                presenter.hide(isShowMarker, latLng, zoomLevel);
                presenter.defaultMapComponentIcons(); //设置缩放，平移，定位 icons
                break;
        }
    }

}
