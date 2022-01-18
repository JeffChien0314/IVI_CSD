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
package com.fxc.ev.launcher.activities;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.LauncherApplication;
import com.fxc.ev.launcher.maps.currentlocation.CurrentLocationFragment;
import com.fxc.ev.launcher.utils.BackButtonDelegate;
import com.tomtom.online.sdk.common.permission.AppPermissionHandler;

import com.tomtom.online.sdk.location.LocationUpdateListener;
import com.tomtom.online.sdk.map.MapView;
import com.tomtom.online.sdk.map.OnMapReadyCallback;
import com.tomtom.online.sdk.map.TomtomMap;

import java.util.Locale;
import java.util.zip.Inflater;

import timber.log.Timber;

public class LauncherActivity extends BaseActivity
        implements BackButtonDelegate.BackButtonDelegateCallback/*, ActionBarModel*/, LocationUpdateListener {

    public static final String CURRENT_EXAMPLE_KEY = "CURRENT_EXAMPLE";

    public static final int EMPTY_EXAM = 1000;
    private int currentExampleId = EMPTY_EXAM;

    private BackButtonDelegate backButtonDelegate;
    private MapView mapView;

    //private LocationProvider locationProvider;
    private Location location;


    //tag::doc_implement_on_map_ready_callback[]
    private final OnMapReadyCallback onMapReadyCallback =
            new OnMapReadyCallback() {
                @Override
                public void onMapReady(TomtomMap map) {
                    Log.v("metis", "onCreate: onMapReady");
                    //Map is ready here
                    tomtomMap = map;
                    tomtomMap.setMyLocationEnabled(true);
                    tomtomMap.collectLogsToFile(LauncherApplication.LOG_FILE_PATH);
                }
            };
    //end::doc_implement_on_map_ready_callback[]

    private TomtomMap tomtomMap;
    private FrameLayout mapContainer;
    RelativeLayout mapLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);
        //inflateActivity();

        initManagers();

        mapView = new MapView(getApplicationContext());
        mapView.addOnMapReadyCallback(onMapReadyCallback);
        mapView.addOnMapReadyCallback(tomtomMap -> {
            initLocationPermissions();
        });
        mapView.setId(R.id.map_view);
        mapContainer.addView(mapView);

        //Timber.d("Phone language " + Locale.getDefault().getLanguage());
        restoreState(savedInstanceState);
    }

    @Override
    protected void initContentContainerView() {
        mapLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.activity_home, null);
        mContentContainer.addView(mapLayout);
        mapContainer = mapLayout.findViewById(R.id.map_container);
    }

    private void initLocationPermissions() {
        AppPermissionHandler permissionHandler = new AppPermissionHandler(this);
        permissionHandler.addLocationChecker();
        permissionHandler.askForNotGrantedPermissions();
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            setCurrentExample(new CurrentLocationFragment(), EMPTY_EXAM);
            return;
        }

        currentExampleId = savedInstanceState.getInt(CURRENT_EXAMPLE_KEY);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.functional_example_control_container);
        if (fragment != null) {
            setCurrentExample((FunctionalExampleFragment) fragment, currentExampleId);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        mapView.addOnMapReadyCallback(map -> map.getUiSettings().setCopyrightsViewAdapter(() -> this));
        mapView.addOnMapReadyCallback(map -> map.getUiSettings().getCurrentLocationView().setCurrentLocationViewAdapter(() -> this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_EXAMPLE_KEY, currentExampleId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (!backButtonDelegate.onBackPressed()) {
            // The event was not consumed by the delegate
            // Then proceed with standard procedure.
            super.onBackPressed();
        }
    }

    @Override
    public boolean exitFromFunctionalExample(FunctionalExampleFragment newFragment, int newExampleId) {
        if (!closePreviousFunctionalExample()) {
            return false;
        }

        if (currentExampleId == EMPTY_EXAM) {
            super.onBackPressed();
        }

        setCurrentExample(newFragment, EMPTY_EXAM);
        return true;
    }

    public boolean closePreviousFunctionalExample() {
        FunctionalExampleFragment currentFragment = (FunctionalExampleFragment) getSupportFragmentManager()
                .findFragmentById(R.id.functional_example_control_container);
        return currentFragment.onBackPressed();
    }

    @Override
    public boolean isManeuversOrSearchFragmentOnTop() {
        return false;
    }

    private void initManagers() {
        //functionalExamplesNavigationManager = new FunctionalExamplesNavigationManager(this);
        backButtonDelegate = new BackButtonDelegate(this, getSupportFragmentManager());
    }


    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    public Location getCurrentPosition() {
        return tomtomMap.getUserLocation();
    }


    public void setCurrentExample(FunctionalExampleFragment currentExample, int itemId) {
        currentExampleId = itemId;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.functional_example_control_container, (Fragment) currentExample, currentExample.getFragmentTag())
                .commit();
    }


    //tag::doc_map_permissions[]
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (tomtomMap != null)
            tomtomMap.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    //end::doc_map_permissions[]

}
