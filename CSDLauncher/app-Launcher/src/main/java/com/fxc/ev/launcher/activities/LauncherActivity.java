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

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.LauncherApplication;
import com.fxc.ev.launcher.adapter.HomeWidgetAdapter;
import com.fxc.ev.launcher.fragment.Frg_AllApps;
import com.fxc.ev.launcher.fragment.Frg_WidgetsEdit;
import com.fxc.ev.launcher.maps.currentlocation.CurrentLocationFragment;
import com.fxc.ev.launcher.utils.BackButtonDelegate;
import com.tomtom.online.sdk.common.permission.AppPermissionHandler;

import com.tomtom.online.sdk.location.LocationUpdateListener;
import com.tomtom.online.sdk.map.MapView;
import com.tomtom.online.sdk.map.OnMapReadyCallback;
import com.tomtom.online.sdk.map.TomtomMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import timber.log.Timber;

public class LauncherActivity extends BaseActivity
        implements BackButtonDelegate.BackButtonDelegateCallback/*, ActionBarModel*/, LocationUpdateListener {

    public static final String CURRENT_EXAMPLE_KEY = "CURRENT_EXAMPLE";

    public static final int EMPTY_EXAM = 1000;
    private int currentExampleId = EMPTY_EXAM;

    private BackButtonDelegate backButtonDelegate;
    private MapView mapView;
    private ArrayList<View> widgetList = new ArrayList<>();
    public List<String> widgetLabelList = new ArrayList<>();
    private HomeWidgetAdapter homeWidgetAdapter;

    //private LocationProvider locationProvider;
    private Location location;

    private AppWidgetHost mAppWidgetHost;
    private AppWidgetManager mAppWidgetManager;

    public SharedPreferences mRead;
    public SharedPreferences.Editor mEditor;


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
    private GridView gridView;
    private RelativeLayout mapLayout;
    private ImageView widgetEditBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("onCreate()");
        mAppWidgetManager = AppWidgetManager.getInstance(this);
        mAppWidgetHost = new AppWidgetHost(this, 1024);
        mAppWidgetHost.startListening();

        mRead = getSharedPreferences("home_widget", MODE_PRIVATE);
        mEditor = mRead.edit();

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

        Timber.d("Phone language " + Locale.getDefault().getLanguage());
        restoreState(savedInstanceState);
    }

    @Override
    protected void initContentContainerView() {
        mapLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.activity_home, null);
        mContentContainer.addView(mapLayout);
        mapContainer = mapLayout.findViewById(R.id.map_container);
        gridView = mapLayout.findViewById(R.id.grid_view);
        widgetEditBtn = mapLayout.findViewById(R.id.widget_edit_btn);
        initWidgets();
        addListener();
    }

    private void initWidgets() {
        widgetList.clear();
        for (int i = 0; i < 2; i++) {
            widgetList.add(new View(mContext));
        }
        AppWidgetHostView hostView;
        int currentAppWidgetId;
        AppWidgetManager mAppWidgetManager = AppWidgetManager.getInstance(this);
        ArrayList<AppWidgetProviderInfo> mAppwidgetProviderInfos = (ArrayList<AppWidgetProviderInfo>) mAppWidgetManager.getInstalledProviders();
        if (mRead.getString("widget1", "").equals("")) {
            Timber.d("widget1 null ");
            for (int i = 0; i < mAppwidgetProviderInfos.size(); i++) {
                AppWidgetProviderInfo widgetInfo = mAppwidgetProviderInfos.get(i);
                if (widgetInfo.label.equals("Analog clock")) {
                    currentAppWidgetId = mAppWidgetHost.allocateAppWidgetId();
                    hostView = mAppWidgetHost.createView(mContext, currentAppWidgetId, widgetInfo);
                    widgetList.set(0, hostView);
                    mEditor.putString("widget1", widgetInfo.label);
                    mEditor.commit();
                    doBindAppWidgetId(currentAppWidgetId, widgetInfo);

                } else if (widgetInfo.label.equals("my test widget")) {
                    currentAppWidgetId = mAppWidgetHost.allocateAppWidgetId();
                    hostView = mAppWidgetHost.createView(mContext, currentAppWidgetId, widgetInfo);
                    widgetList.set(1, hostView);
                    mEditor.putString("widget2", widgetInfo.label);
                    mEditor.commit();
                    doBindAppWidgetId(currentAppWidgetId, widgetInfo);

                }
            }
        } else {
            Timber.d("widget1 not null");
            for (int i = 0; i < mAppwidgetProviderInfos.size(); i++) {
                AppWidgetProviderInfo widgetInfo = mAppwidgetProviderInfos.get(i);

                if (widgetInfo.label.equals(mRead.getString("widget1", ""))) {
                    currentAppWidgetId = mAppWidgetHost.allocateAppWidgetId();
                    hostView = mAppWidgetHost.createView(mContext, currentAppWidgetId, widgetInfo);
                    widgetList.set(0, hostView);
                    doBindAppWidgetId(currentAppWidgetId, widgetInfo);

                } else if (widgetInfo.label.equals(mRead.getString("widget2", ""))) {
                    currentAppWidgetId = mAppWidgetHost.allocateAppWidgetId();
                    hostView = mAppWidgetHost.createView(mContext, currentAppWidgetId, widgetInfo);
                    widgetList.set(1, hostView);
                    doBindAppWidgetId(currentAppWidgetId, widgetInfo);

                } else if (!mRead.getString("widget3", "").equals("") && widgetInfo.label.equals(mRead.getString("widget3", ""))) {
                    currentAppWidgetId = mAppWidgetHost.allocateAppWidgetId();
                    hostView = mAppWidgetHost.createView(mContext, currentAppWidgetId, widgetInfo);
                    if (widgetList.size() == 2) {
                        widgetList.add(2, hostView);
                    } else {
                        widgetList.set(2, hostView);
                    }
                    doBindAppWidgetId(currentAppWidgetId, widgetInfo);
                } else if (!mRead.getString("widget4", "").equals("") && widgetInfo.label.equals(mRead.getString("widget4", ""))) {
                    currentAppWidgetId = mAppWidgetHost.allocateAppWidgetId();
                    hostView = mAppWidgetHost.createView(mContext, currentAppWidgetId, widgetInfo);
                    if (widgetList.size() == 2) {
                        widgetList.add(new View(mContext));
                        widgetList.add(3, hostView);
                    } else if (widgetList.size() == 3) {
                        widgetList.add(3, hostView);
                    } else {
                        widgetList.set(3, hostView);
                    }
                    doBindAppWidgetId(currentAppWidgetId, widgetInfo);

                }
            }
        }

        widgetLabelList.clear();
        for (int i = 0; i < widgetList.size(); i++) {
            if (widgetList.get(i) instanceof AppWidgetHostView) {
                widgetLabelList.add(((AppWidgetHostView) widgetList.get(i)).getAppWidgetInfo().label);
            }
        }

        if (widgetList.size() == 3) { //add widget4 background
            View widget4 = LayoutInflater.from(mContext).inflate(R.layout.widget4_default_layout, null);
            widgetList.add(3, widget4);
        }

        homeWidgetAdapter = new HomeWidgetAdapter(mContext, widgetList);
        gridView.setAdapter(homeWidgetAdapter);
    }

    private void doBindAppWidgetId(int AppWidgetId, AppWidgetProviderInfo widgetInfo) {
        boolean success = mAppWidgetManager.bindAppWidgetIdIfAllowed(AppWidgetId, widgetInfo.provider);
        if (!success) {
            Timber.d("bindAppWidgetId false");
            addWidgetPermission(mAppWidgetManager);
            boolean bindAllowed = mAppWidgetManager.bindAppWidgetIdIfAllowed(AppWidgetId, widgetInfo.provider);
            if (!bindAllowed) {
                Timber.d("failed to bind widget id : ");
            }
        }
    }

    private void addWidgetPermission(AppWidgetManager appWidgetManager) {
        String methodName = "setBindAppWidgetPermission";
        try {
            Class[] argsClass = new Class[]{String.class, boolean.class};
            Method method = appWidgetManager.getClass().getMethod(methodName, argsClass);
            Object[] args = new Object[]{this.getPackageName(), true};
            try {
                method.invoke(appWidgetManager, args);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void addListener() {
        widgetEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fManager = getSupportFragmentManager();
                FragmentTransaction fTransaction = fManager.beginTransaction();

                fTransaction.replace(R.id.home_layout, new Frg_WidgetsEdit(), "FrgWidgetsEdit");
                fTransaction.addToBackStack(null);
                fTransaction.commitAllowingStateLoss();
            }
        });

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
        initWidgets(); //刷新widget list
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
