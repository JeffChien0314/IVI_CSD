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

import static com.tomtom.navkit2.guidance.VoiceGuidanceModeListener.VoiceGuidanceMode.COMPACT;
import static com.tomtom.navkit2.guidance.VoiceGuidanceModeListener.VoiceGuidanceMode.COMPREHENSIVE;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;

import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.fxc.ev.launcher.BuildConfig;
import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.adapter.HomeWidgetAdapter;
import com.fxc.ev.launcher.fragment.Frg_WidgetsEdit;
import com.fxc.ev.launcher.maps.poicatsearch.Constants;
import com.fxc.ev.launcher.maps.poicatsearch.PoiSearchThread;
import com.fxc.ev.launcher.maps.route.RoutePlanningPreferencesActivity;
import com.fxc.ev.launcher.maps.search.SearchFragment;
import com.fxc.ev.launcher.utils.CameraStackController;
import com.fxc.ev.launcher.utils.CameraStackController.CameraType;
import com.fxc.ev.launcher.utils.DistanceConversions;
import com.fxc.ev.launcher.utils.EtaFormatter;
import com.fxc.ev.launcher.utils.PermissionsManager;
import com.fxc.ev.launcher.utils.SharedPreferenceUtils;
import com.fxc.ev.launcher.utils.Toaster;
import com.tomtom.navkit.map.ClickCoordinates;
import com.tomtom.navkit.map.InvalidExtensionId;
import com.tomtom.navkit.map.Layer;
import com.tomtom.navkit.map.Map;
import com.tomtom.navkit.map.MapClickEvent;
import com.tomtom.navkit.map.MapClickListener;
import com.tomtom.navkit.map.MapHolder;
import com.tomtom.navkit.map.MapLongClickEvent;
import com.tomtom.navkit.map.MapLongClickListener;
import com.tomtom.navkit.map.Marker;
import com.tomtom.navkit.map.MarkerBuilder;
import com.tomtom.navkit.map.MarkerLabelBuilder;
import com.tomtom.navkit.map.Point;
import com.tomtom.navkit.map.camera.Camera;
import com.tomtom.navkit.map.camera.CameraListener;
import com.tomtom.navkit.map.extension.positioning.PositioningExtension;
import com.tomtom.navkit2.TrafficRenderer;
import com.tomtom.navkit2.TripRenderer;
import com.tomtom.navkit2.drivingassistance.DrivingContextApi;
import com.tomtom.navkit2.drivingassistance.Position;
import com.tomtom.navkit2.drivingassistance.PositionUpdateListener;
import com.tomtom.navkit2.guidance.AudioInstructionListener;
import com.tomtom.navkit2.guidance.Instruction;
import com.tomtom.navkit2.guidance.NextInstructionListener;
import com.tomtom.navkit2.guidance.StockTextToSpeechEngine;
import com.tomtom.navkit2.mapdisplay.trip.TripRendererClickListener;
import com.tomtom.navkit2.navigation.Navigation;
import com.tomtom.navkit2.navigation.NavigationService;
import com.tomtom.navkit2.navigation.Route;
import com.tomtom.navkit2.navigation.RouteDeviatedListener;
import com.tomtom.navkit2.navigation.RouteProgress;
import com.tomtom.navkit2.navigation.RouteSnapshot;
import com.tomtom.navkit2.navigation.RouteStop;
import com.tomtom.navkit2.navigation.RouteStopVector;
import com.tomtom.navkit2.navigation.RouteUpdateListener;
import com.tomtom.navkit2.navigation.Trip;
import com.tomtom.navkit2.navigation.TripPlan;
import com.tomtom.navkit2.navigation.TripPlanCallback;
import com.tomtom.navkit2.navigation.TripPlanError;
import com.tomtom.navkit2.navigation.TripPlanResult;
import com.tomtom.navkit2.navigation.TripUpdateListener;
import com.tomtom.navkit2.navigation.common.Preference;
import com.tomtom.navkit2.place.Coordinate;
import com.tomtom.navkit2.place.Location;
import com.tomtom.navkit2.search.ExecutionMode;
import com.tomtom.navkit2.search.FilterByGeoRadius;
import com.tomtom.navkit2.search.FtsResult;
import com.tomtom.navkit2.search.FtsResultVector;
import com.tomtom.navkit2.search.FtsResults;
import com.tomtom.navkit2.search.FtsResultsListener;
import com.tomtom.navkit2.search.Input;
import com.tomtom.navkit2.search.PoiSuggestionResults;
import com.tomtom.navkit2.search.PoiSuggestionsListener;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;


public class LauncherActivity extends InteractiveMapActivity implements SearchFragment.OnMarkerChangedListener {
    public static final String TAG = "LauncherActivity";

    private ArrayList<View> widgetList = new ArrayList<>();
    public List<String> widgetLabelList = new ArrayList<>();
    private HomeWidgetAdapter homeWidgetAdapter;
    private AppWidgetHost mAppWidgetHost;
    private AppWidgetManager mAppWidgetManager;
    public SharedPreferences mRead;
    public SharedPreferences.Editor mEditor;
    private GridView gridView;
    private ImageView widgetEditBtn;


    private static final long COMPASS_ROTATION_ANIMATION_DURATION = 500l;
    private static final long TRAFFIC_LISTENER_SEARCH_DISTANCE_IN_METERS = 1000L;
    private static final long TRAFFIC_LISTENER_UPDATE_PERIOD_IN_SECONDS = 1L;
    private static final long CENTIMETERS_PER_METER = 100L;
    private static final double SHIELD_ANCHOR_X = 0.5;
    private static final double SHIELD_ANCHOR_Y = 0.41;
    private static final double DESTINATION_ICON_ANCHOR_X = 0.5;
    private static final double DESTINATION_ICON_ANCHOR_Y = 0.41;
    private static final double WAYPOINT_ICON_ANCHOR_X = 0.5;
    private static final double WAYPOINT_ICON_ANCHOR_Y = 0.55;

    // route planning settings
    private static final String PREFER_ROUTE_PLANNING_ALTERNATIVES = "pref_route_planning_alternatives";
    private static final String PREFER_ROUTE_PLANNING_BORDERS = "pref_route_planning_borders";
    private static final String PREFER_ROUTE_PLANNING_FERRIES = "pref_route_planning_ferry";
    private static final String PREFER_ROUTE_PLANNING_MOTORWAYS = "pref_route_planning_motorway";
    private static final String PREFER_ROUTE_PLANNING_TOLLS = "pref_route_planning_tolls";
    private static final String PREFER_ROUTE_PLANNING_UNPAVED = "pref_route_planning_unpaved";
    private static final String PREFER_ROUTE_PLANNING_CARPOOLS = "pref_route_planning_carpools";

    private String trafficConfiguration;
    private TrafficRenderer trafficRenderer;
    private Navigation navigation;
    private Trip trip;
    private TripUpdateListener tripUpdateListener;
    private TripRenderer tripRenderer;
    private Layer markerLayer;
    private Marker destinationMarker;
    private List<Marker> waypointMarkers;
    //metis@0309 add -->
    private MarkerBuilder searchMarkerBuilder;
    private List<Marker> searchMarkerList = new ArrayList<>();
    private FragmentManager fragmentManager;
    private boolean isDisappearance = false;
    //metis@0309 add <--
    //private NetworkDrawer networkDrawer;

    // trip planning preferences
    private Preference bordersPreference = Preference.ALLOW;
    private Preference ferriesPreference = Preference.ALLOW;
    private Preference motorwaysPreference = Preference.ALLOW;
    private Preference tollsPreference = Preference.ALLOW;
    private Preference unpavedPreference = Preference.ALLOW;
    private Preference carpoolsPreference = Preference.ALLOW;
    private int numberOfAlternatives = 0;

    private PermissionsManager permissionsManager = new PermissionsManager(this);
    private PositioningExtension positioningExtension;
    private DrivingContextApi drivingContextApi;
    private Position lastKnownPosition;

    private ImageButton mapModeButton;
    private ImageButton planningSettingsButton;
    private ImageButton voiceGuidanceButton;
    private Button searchButton; //metis@ add
    private Button favHomeBtn; //metis@0314 add
    private Button favOfficeBtn; //metis@0314 add
    private FrameLayout searchContainer;

    private boolean fullVoiceGuidanceMode = true;
    private boolean navigationServiceBound = false;

    private double mLatitude = 0;
    private double mLongitude = 0;
    private MyCameraListener myCameraListener;
    private Map map;
    boolean isNavigationTTSMute = false;//Jerry@20220321 add
    private TripPlan tripPlan;
    private RouteStopVector waypoints;
    private List<Marker> poiCatMarkers;//Jerry@20220324 add
    private List<MarkerClass> poiMarkerClassList;//Jerry@20220406 add
    private boolean isFragmentShow = false;//Jerry@20220401 add
    private boolean isFragmentHide = false;//Jerry@20220406 add:click map not add marker
    private boolean isAlreadyTranslation = false;//Jerry@20220401 add

    //Jerry@20220317 add for stop navigation
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Jerry", "intent.getAction():" + intent.getAction());
            if (Constants.STOP_NAVIGATION.equals(intent.getAction())) {
                if (tripRenderer != null) {
                    getCameraStackController().removeTripFromOverviewCamera(tripRenderer);
                    tripRenderer.stop();
                    tripRenderer = null;
                }
                if (trip != null) {
                    trip.removeListener(tripUpdateListener);
                    tripUpdateListener = null;
                    hideEtaPanel();
                    hideNextInstructionPanel();
                    setMapWidgetVisibility2(View.VISIBLE);
                    stopPreview();
                    navigation.deleteTrip(trip);
                    trip = null;
                }
            } else if (Constants.TTS_CONTROL_TOGGLE.equals(intent.getAction())) {//Jerry@20220321 add
                isNavigationTTSMute = intent.getBooleanExtra(Constants.TTS_CONTROL_TOGGLE, false);
            }
        }
    };

    private ServiceConnection navigationConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d(TAG, "onServiceConnected(Navigation)");
            navigationServiceBound = true;

            // Map positioning extension (chevron location). MICHI-10715
            try {
                positioningExtension = PositioningExtension.create(getMapHolder(), "positioningExtension");
            } catch (InvalidExtensionId e) {
                Log.e(TAG, "No extension with id `positioningExtension` in style file", e);
            } catch (MapHolder.MapHolderEmpty e) {
                Log.e(TAG, "Map view has not yet been created", e);
            }

            getCameraStackController().setCurrentCamera(CameraType.kFollowRouteCamera);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected(Navigation)");
            navigationServiceBound = false;
        }
    };

    //metis@0309 获取当前位置
    public Position getLastKnownPosition() {
        return lastKnownPosition;
    }

    public void setCurrentFragment(Fragment currentFragment) {
        setMapWidgetVisibility(View.GONE);
        FragmentTransaction fTransaction = fragmentManager.beginTransaction();
        fTransaction.replace(R.id.search_container, currentFragment, "");
        fTransaction.addToBackStack(null);
        fTransaction.commitAllowingStateLoss();
    }

    public void setDisappearance(boolean isDisappearance) {
        this.isDisappearance = isDisappearance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAppWidgetManager = AppWidgetManager.getInstance(this);
        mAppWidgetHost = new AppWidgetHost(this, 1024);
        mAppWidgetHost.startListening();

        mRead = getSharedPreferences("home_widget", MODE_PRIVATE);
        mEditor = mRead.edit();

        fragmentManager = getSupportFragmentManager(); //metis@0309 add

        super.onCreate(savedInstanceState);

        initMap();

    }

    @Override
    protected void initContentContainerView() {
        super.initContentContainerView();
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
        if (mAppWidgetManager == null) return;
        ArrayList<AppWidgetProviderInfo> mAppwidgetProviderInfos = (ArrayList<AppWidgetProviderInfo>) mAppWidgetManager.getInstalledProviders();
        if (mRead.getString("widget1", "").equals("")) {
            Log.d(TAG, "widget1 null ");
            for (int i = 0; i < mAppwidgetProviderInfos.size(); i++) {
                AppWidgetProviderInfo widgetInfo = mAppwidgetProviderInfos.get(i);
                if (widgetInfo.label.equals("Music")) {
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
            Log.d(TAG, "widget1 not null");
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
            Log.d(TAG, "bindAppWidgetId false");
            addWidgetPermission(mAppWidgetManager);
            boolean bindAllowed = mAppWidgetManager.bindAppWidgetIdIfAllowed(AppWidgetId, widgetInfo.provider);
            if (!bindAllowed) {
                Log.d(TAG, "failed to bind widget id : ");
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

    private void initMap() {
        //Jerry@20220317 add for stopping navigation-->
        isNavigationTTSMute = (boolean) SharedPreferenceUtils.get(LauncherActivity.this, Constants.TTS_CONTROL_TOGGLE, Constants.TTS_CONTROL_TOGGLE, false);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.STOP_NAVIGATION);
        intentFilter.addAction(Constants.TTS_CONTROL_TOGGLE);
        registerReceiver(broadcastReceiver, intentFilter);
        //<--Jerry@20220317 add for stopping navigation
        // Limit power consumption with a 20 FPS cap
        getMapHolder().getSurfaceAdapter().setFrameRateCap(20l);
        map = getMapHolder().getMap();

        markerLayer = map.addLayer();
        waypointMarkers = new ArrayList<>();
        //poiCatMarkers = new ArrayList<>();//Jerry@20220324 add
        poiMarkerClassList = new ArrayList<>();//Jerry@202200406 add
        searchMarkerBuilder = new MarkerBuilder(); //metis@0309 add

        tripPlan = new TripPlan();
        waypoints = new RouteStopVector();

        myCameraListener = new MyCameraListener();
        map.getCamera().registerListener(myCameraListener);

        map.setMapClickListener(new MapClickListener() {
            @Override
            public void onMapClick(MapClickEvent e) {
                Log.v("metis", "onMapClick");
                if (isFragmentShow || isFragmentHide) {//Jerry@20220401 add:Search fragment show,can't click
                    isFragmentHide = false;
                    return;
                }
                com.tomtom.navkit.map.Coordinate waypoint = e.getClickCoordinates().getCoordinate();

                addWaypointMarker(waypoint);

                RouteStop wp = RouteStop.from(new Coordinate(waypoint.getLatitude(), waypoint.getLongitude()));
                waypoints.add(wp);
                tripPlan.setWaypoints(waypoints);

            }
        });

        map.setMapLongClickListener(new MapLongClickListener() {
            @Override
            public void onMapLongClick(MapLongClickEvent e) {
                Log.v("metis", "onMapLongClick");
                if (isFragmentShow || isFragmentHide) {//Jerry@20220401 add:Search fragment show,can't click
                    isFragmentHide = false;
                    return;
                }
                // Use the existence of marker to check we don't attempt multiple route plans
                // concurrently
                if (destinationMarker == null) {
                    com.tomtom.navkit.map.Coordinate destination = e.getClickCoordinates().getCoordinate();
                    startNavigation(destination);//Jerry@20220322 add
                }
            }
        });

        // Traffic
        AssetManager assetManager = getAssets();
        StringBuilder jsonBuilder = new StringBuilder();
        try (Scanner scanner = new Scanner(assetManager.open("traffic_config.json"), "UTF-8").useDelimiter("\\Z")) {
            while (scanner.hasNext()) {
                jsonBuilder.append(scanner.next());
            }
            trafficConfiguration = jsonBuilder.toString();
            trafficRenderer = TrafficRenderer.createIncidentsRenderer(this, map, BuildConfig.API_KEY, trafficConfiguration);
            if (trafficRenderer != null) {
                Log.d(TAG, "TrafficRenderer is created");
            } else {
                Log.e(TAG, "TrafficRenderer failed to start");
                finish();
            }
        } catch (IOException io) {
            Log.e(TAG, "Failed to read traffic config - " + io);
            finish();
        }

        // Driving Context
        drivingContextApi = new DrivingContextApi(this);
        drivingContextApi.addPositionUpdateListener(new PositionUpdateListener() {
            @Override
            public void onPositionUpdate() {
                lastKnownPosition = drivingContextApi.currentPosition();
                getSpeedBubbleView().update(drivingContextApi.currentPosition(), drivingContextApi.roadElement());
                getNextInstructionPanelView().updatePosition(drivingContextApi.currentPosition());
            }
        });

        mapModeButton = findViewById(R.id.mapModeButton);
        mapModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCameraStackController().nextCamera();
            }
        });

        planningSettingsButton = findViewById(R.id.planningMenu_btn);
        planningSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(LauncherActivity.this, RoutePlanningPreferencesActivity.class);
                startActivity(myIntent);
            }
        });

        getCameraStackController().addCameraChangedListener(new CameraStackController.OnCameraChangedListener() {
            @Override
            public void onChange(
                    @SuppressWarnings("unused") CameraStackController.CameraType oldCameraType,
                    CameraStackController.CameraType newCameraType) {
                doMapModeButtonAnimation(newCameraType);
            }
        });

        voiceGuidanceButton = findViewById(R.id.voiceGuidanceMode);
        voiceGuidanceButton.setVisibility(View.GONE);//Jerry@20220321 add:not display
        voiceGuidanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullVoiceGuidanceMode = !fullVoiceGuidanceMode;
                int resId = fullVoiceGuidanceMode ? R.drawable.nk2_ic_full_voice_guidance_mode : R.drawable.nk2_ic_compact_voice_guidance_mode;
                voiceGuidanceButton.setImageResource(resId);
                navigation.setVoiceGuidanceMode(fullVoiceGuidanceMode ? COMPREHENSIVE : COMPACT);
            }
        });

        //Jerry@20220321 add:tts control-->
        StockTextToSpeechEngine engine = new StockTextToSpeechEngine(LauncherActivity.this) {

            @Override
            public void onAudioPrepare(AudioMessage message, MessageKind kind, String id) {
                if (isNavigationTTSMute) {
                    message.setMessage("");
                }
                super.onAudioPrepare(message, kind, id);
            }

            @Override
            public void onNextTriggerTimeChanged(int expected_time_sec) {
                super.onNextTriggerTimeChanged(expected_time_sec);
            }

            @Override
            public void onInit(int status) {
                super.onInit(status);
            }
        };
        navigation = new Navigation(mContext, (AudioInstructionListener) engine);
        //<--Jerry@20220321 add:tts control
        navigation.addNextInstructionListener(new NextInstructionListener() {
            @Override
            public void onNextInstructionChange(int distanceToInstructionInMeters, List<Instruction> instructions) {
                int visibility = View.GONE;
                if (!instructions.isEmpty()) {
                    getNextInstructionPanelView().update(distanceToInstructionInMeters, instructions);
                    visibility = View.VISIBLE;
                    if (View.VISIBLE == searchButton.getVisibility()) {//Jerry@20220314
                        setMapWidgetVisibility2(View.GONE);
                    }
                }
                getNextInstructionPanelView().setVisibility(visibility);
            }

            @Override
            public void onDistanceToNextInstructionChange(int distanceToInstructionInMeters) {
                getNextInstructionPanelView().updateDistance(distanceToInstructionInMeters);
            }
        });

        // Services
        if (permissionsManager.acquirePermissions()) {
            setupNavigationServices();
        }

        navigation.addRouteDeviatedListener(new RouteDeviatedListener() {
            @Override
            public void onRouteDeviated(Route route) {
                int metersFromStart = Math.round(route.snapshot().progress().offsetFromStartOfRouteInCm() / 100f);
                Toast.makeText(getApplicationContext(), getString(R.string.navigation_experience_deviated_from_route, metersFromStart), Toast.LENGTH_LONG).show();
            }
        });

        //metis@0309 显示搜索页面 -->
        favHomeBtn = findViewById(R.id.favorites_home);
        favOfficeBtn = findViewById(R.id.favorites_office);
        searchContainer = findViewById(R.id.search_container);
        searchButton = findViewById(R.id.search_btn);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentFragment(new SearchFragment());
                //Jerry@20220401 add:Search fragment show,can't click
                isFragmentShow = true;
                isFragmentHide = false;
                if (!isAlreadyTranslation) {
                    setMapViewMove(Constants.MOVE_RIGHT);
                    isAlreadyTranslation = true;
                }
            }
        });
        //metis@0309 显示搜索页面 <--
    }

    //Jerry@20220322 add
    public void startNavigation(com.tomtom.navkit.map.Coordinate destination) {
        MarkerBuilder markerBuilder = new MarkerBuilder();
        markerBuilder.setCoordinate(destination)
                // Equivalent for Michi default route markers configuration
                .setPinUri(getString(R.string.navigation_route_marker_pin_path))
                .setShieldUri(getString(R.string.navigation_route_marker_shield_path))
                .setShieldColor(toMapColor(ContextCompat.getColor(getApplicationContext(), R.color.route_marker_default_shield)))
                .setShieldAnchor(SHIELD_ANCHOR_X, SHIELD_ANCHOR_Y)
                .setIconUri(getString(R.string.navigation_route_marker_destination_icon_path))
                .setIconAnchor(DESTINATION_ICON_ANCHOR_X, DESTINATION_ICON_ANCHOR_Y);
        destinationMarker = markerLayer.addMarker(markerBuilder);

        tripPlan.setDestination(toPlaceCoordinate(destination));

        doReadSettings();

        tripPlan.setNumberOfAlternatives(numberOfAlternatives);
        tripPlan.getRoutingParameters().setBorderCrossingsPreference(bordersPreference);
        tripPlan.getRoutingParameters().setFerriesPreference(ferriesPreference);
        tripPlan.getRoutingParameters().setMotorwaysPreference(motorwaysPreference);
        tripPlan.getRoutingParameters().setTollsPreference(tollsPreference);
        tripPlan.getRoutingParameters().setUnpavedRoadsPreference(unpavedPreference);
        tripPlan.getRoutingParameters().setCarpoolsPreference(carpoolsPreference);

        navigation.planTrip(tripPlan, new TripPlanCallback() {
            @Override
            public void onTripPlanned(TripPlanResult result) {
                waypoints.clear();
                removeAllMarkers();

                if (tripRenderer != null) {
                    getCameraStackController().removeTripFromOverviewCamera(tripRenderer);
                    tripRenderer.stop();
                    tripRenderer = null;
                }
                if (trip != null) {
                    trip.removeListener(tripUpdateListener);
                    tripUpdateListener = null;
                    hideEtaPanel();
                    hideNextInstructionPanel();
                    stopPreview();
                    navigation.deleteTrip(trip);
                    trip = null;
                }
                trip = result.trip();

                tripUpdateListener = new TripUpdateListener() {
                    @Override
                    public void onRoutesChange(Trip trip) {
                        updateEtaPanelForRoutesOnTrip(trip);
                    }

                    @Override
                    public void onStateChange(Trip trip) {
                    }

                    @Override
                    public void onTripArrival(Trip trip) {
                        setMapWidgetVisibility2(View.VISIBLE);//Jerry@20220314
                        Toast.makeText(mContext, getString(R.string.navigation_experience_destination_reached_message), Toast.LENGTH_LONG).show();
                    }
                };
                trip.addListener(tripUpdateListener);

                updateEtaPanelForRoutesOnTrip(trip);
                trafficRenderer.setTrafficMarkerVisibility(false);
                tripRenderer = TripRenderer.create(trip, getMapHolder().getMap(), trafficConfiguration);

                getCameraStackController().addTripToOverviewCamera(tripRenderer);
                if (trip.routeList().size() <= 1) {
                    navigation.startNavigation(trip);
                    if (Constants.IS_DEMO) {//Jerry@20220314 add
                        startDemo();
                    }
                } else {
                    Toaster.show(getApplicationContext(), R.string.navigation_experience_select_route_message);
                    tripRenderer.addClickListener(new TripRendererClickListener() {
                        @Override
                        public void onRouteClicked(Route route, ClickCoordinates clickCoordinates) {
                            trip.setPreferredRoute(route);
                            navigation.startNavigation(trip);
                            if (Constants.IS_DEMO) {//Jerry@20220314 add
                                startDemo();
                            }
                        }
                    });
                }
            }

            @Override
            public void onTripPlanFailed(TripPlanError error) {
                trafficRenderer.setTrafficMarkerVisibility(true);
                Toast.makeText(mContext, getString(R.string.navigation_experience_route_planning_error), Toast.LENGTH_LONG).show();
                waypoints.clear();
                removeAllMarkers();
            }
        });
    }

    private void setMapWidgetVisibility(int visibility) {
        searchButton.setVisibility(visibility);
        favHomeBtn.setVisibility(visibility);
        favOfficeBtn.setVisibility(visibility);
        //voiceGuidanceButton.setVisibility(visibility);//Jerry@20220318 add:not display
        planningSettingsButton.setVisibility(visibility);
        mapModeButton.setVisibility(visibility);
        getZoomBarView().setVisibility(visibility);
    }

    private void setMapWidgetVisibility2(int visibility) {
        searchButton.setVisibility(visibility);
        favHomeBtn.setVisibility(visibility);
        favOfficeBtn.setVisibility(visibility);
        //voiceGuidanceButton.setVisibility(visibility);//Jerry@20220318 add:not display
    }

    //Jerry@0308 add CameraListener
    private class MyCameraListener extends CameraListener {
        @Override
        public void onCameraPropertiesChange(Camera camera) {
            //super.onCameraPropertiesChange(camera);
        }

        @Override
        public void onCameraPropertiesSteady(Camera camera) {
            //super.onCameraPropertiesSteady(camera);
            double scale = camera.getProperties().getScale();
            if (scale > Constants.THIRTY_FIVE_KM) {
                removeAllPoiMarkers(null);
                return;
            }
            mLatitude = camera.getProperties().getLookAt().getLatitude();
            mLongitude = camera.getProperties().getLookAt().getLongitude();
            Coordinate coordinate = new Coordinate(mLatitude, mLongitude);
            for (int index = 0; index < Constants.ALL_CATEGORY.length; index++) {
                MyFtsResultsListener myFtsResultsListener = new MyFtsResultsListener(Constants.ALL_CATEGORY[index]);
                MyPoiSuggestionsListener myPoiSuggestionsListener = new MyPoiSuggestionsListener();
                Input input = getInput(Constants.ALL_CATEGORY[index], coordinate, scale, index);
                PoiSearchThread thread = new PoiSearchThread(LauncherActivity.this, myFtsResultsListener, myPoiSuggestionsListener, input);
                thread.start();
            }
        }

        @Override
        public void onCameraPropertiesSignificantChange(Camera camera) {
            //super.onCameraPropertiesSignificantChange(camera);
        }
    }

    private void addWaypointMarker(com.tomtom.navkit.map.Coordinate waypoint) {
        MarkerBuilder markerBuilder = new MarkerBuilder();
        markerBuilder.setCoordinate(waypoint)
                .setPinUri(getString(R.string.navigation_route_marker_pin_path))
                .setShieldUri(getString(R.string.navigation_route_marker_shield_path))
                .setShieldColor(toMapColor(ContextCompat.getColor(getApplicationContext(), R.color.route_marker_default_shield)))
                .setShieldAnchor(SHIELD_ANCHOR_X, SHIELD_ANCHOR_Y)
                .setIconUri(getString(R.string.navigation_route_marker_waypoint_icon_path))
                .setIconAnchor(WAYPOINT_ICON_ANCHOR_X, WAYPOINT_ICON_ANCHOR_Y);
        waypointMarkers.add(markerLayer.addMarker(markerBuilder));
    }

    private static com.tomtom.navkit.map.Color toMapColor(@ColorInt int color) {
        int rgb = color & 0xffffff;
        float alpha = Color.alpha(color) / 255.0f;
        return new com.tomtom.navkit.map.Color(rgb, alpha);
    }

    private static com.tomtom.navkit2.place.Coordinate toPlaceCoordinate(com.tomtom.navkit.map.Coordinate coordinate) {
        return new com.tomtom.navkit2.place.Coordinate(coordinate.getLatitude(), coordinate.getLongitude());
    }

    private static com.tomtom.navkit.map.Coordinate toMapCoordinate(com.tomtom.navkit2.place.Coordinate coordinate) {
        return new com.tomtom.navkit.map.Coordinate(coordinate.latitude(), coordinate.longitude());
    }

    public void doReadSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        numberOfAlternatives = Integer.valueOf(prefs.getString(PREFER_ROUTE_PLANNING_ALTERNATIVES, "0"));
        bordersPreference = prefs.getBoolean(PREFER_ROUTE_PLANNING_BORDERS, false)
                ? Preference.AVOID : Preference.ALLOW;
        ferriesPreference = prefs.getBoolean(PREFER_ROUTE_PLANNING_FERRIES, false)
                ? Preference.AVOID : Preference.ALLOW;
        motorwaysPreference = prefs.getBoolean(PREFER_ROUTE_PLANNING_MOTORWAYS, false)
                ? Preference.AVOID : Preference.ALLOW;
        tollsPreference = prefs.getBoolean(PREFER_ROUTE_PLANNING_TOLLS, false)
                ? Preference.AVOID : Preference.ALLOW;
        unpavedPreference = prefs.getBoolean(PREFER_ROUTE_PLANNING_UNPAVED, false)
                ? Preference.AVOID : Preference.ALLOW;
        carpoolsPreference = prefs.getBoolean(PREFER_ROUTE_PLANNING_CARPOOLS, false)
                ? Preference.AVOID : Preference.ALLOW;
    }

    private void updateEtaPanelForRoutesOnTrip(Trip trip) {
        Route navigatedRoute = trip.routeList().get(0);
        showEtaPanel(navigatedRoute.snapshot().progress());

        navigatedRoute.addListener(new RouteUpdateListener() {
            @Override
            public void onPathChange(RouteSnapshot snapshot) {
                // not interested in path changes in this example
            }

            @Override
            public void onProgressChange(RouteSnapshot snapshot) {
                showEtaPanel(snapshot.progress());
            }
        });
    }

    private void showEtaPanel(RouteProgress progress) {
        String countryCode = lastKnownPosition != null ? lastKnownPosition.place().address().countryCode() : "";

        GregorianCalendar eta = progress.eta();

        DistanceConversions.FormattedDistance fd = DistanceConversions.convert((int) (progress.remainingLengthInCm() / 100), countryCode);
        String etaAndDistanceText =
                EtaFormatter.toString(eta) + "\n" + fd.distance + " " + fd.unit + "\n";

        int trafficDelayInMins = Math.round(progress.remainingTrafficDelayInSeconds() / 60.0f);
        String trafficDelayText = trafficDelayInMins > 0 ? "\u26a0 +" + trafficDelayInMins + "min" : "";

        //Jerry@20220317 add:get navigation remaining time
        int remainingTimeInSeconds = progress.remainingTimeInSeconds() + progress.remainingTrafficDelayInSeconds();

        String completeText = etaAndDistanceText + trafficDelayText;
        SpannableString completeSpannable = new SpannableString(completeText);
        int trafficTextColor = Color.rgb(0xFF, 0xAA, 0xAA);
        completeSpannable.setSpan(
                new ForegroundColorSpan(trafficTextColor),
                etaAndDistanceText.length(),
                completeText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //Jerry@20220317 modify-->
        //getEtaTextView().setText(completeSpannable);//Jerry@20220314 mark:invisible
        //getEtaTextView().setVisibility(View.VISIBLE);//Jerry@20220314 mark:invisible
        getNextInstructionPanelView().updateTripOutlineView(fd.distance.concat(" " + fd.unit), remainingTimeInSeconds, eta.getTime());
        //<--Jerry@20220317 modify
    }

    private void hideEtaPanel() {
        getEtaTextView().setVisibility(View.GONE);
    }

    private void hideNextInstructionPanel() {
        getNextInstructionPanelView().setVisibility(View.GONE);
        searchButton.setVisibility(View.VISIBLE);//Jerry@20220314 add:visible
    }

    private void doMapModeButtonAnimation(CameraType newCameraType) {
        final ColorStateList compassIconTint;
        final float mapModeRotationInDegrees;

        switch (newCameraType) {
            case kManualCamera:
                compassIconTint = getColorStateListFromResources(getResources(), R.color.nk2_compass_manual_mode_tint);
                mapModeRotationInDegrees = 0.0f;
                break;
            case kFollowRouteCamera:
                compassIconTint = null;
                mapModeRotationInDegrees = 45.0f;
                break;
            case kNorthUpCamera:
                // fall through
            case kOverviewCamera:
                compassIconTint = null;
                mapModeRotationInDegrees = 0.0f;
                break;
            default:
                throw new IllegalArgumentException("Unexpected CameraType");
        }

        mapModeButton.setImageTintList(compassIconTint);
        mapModeButton.animate()
                .setDuration(COMPASS_ROTATION_ANIMATION_DURATION)
                .rotation(mapModeRotationInDegrees);
    }

    @SuppressWarnings("deprecation") // getColorStateList(int)
    private static ColorStateList getColorStateListFromResources(Resources resources, int resId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return resources.getColorStateList(resId);
        }
        return resources.getColorStateList(resId, null);
    }

    private void setupNavigationServices() {
        // connect to the navigation service which provides trip planning and driving assistance
        final Bundle serviceBundle = makeNavigationServiceBundle();
        final Intent intent = new Intent(this, NavigationService.class);
        serviceBundle.putString(NavigationService.INITIAL_LANGUAGE_KEY, "zh-TW");
        intent.putExtra(NavigationService.CONFIGURATION, serviceBundle);
        if (!bindService(intent, navigationConnection, Context.BIND_AUTO_CREATE)) {
            Log.e(TAG, "Couldn't bind navigation service");
            finish();
        }
    }

    private Bundle makeNavigationServiceBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(NavigationService.ROUTING_API_AUTH_TOKEN_KEY, BuildConfig.API_KEY);
        bundle.putString(NavigationService.NAVIGATION_AUTH_TOKEN_KEY, BuildConfig.API_KEY);

        return bundle;
    }

    private void removeAllMarkers() {
        markerLayer.removeMarker(destinationMarker);
        destinationMarker = null;
        for (Marker waypointMarker : waypointMarkers) {
            markerLayer.removeMarker(waypointMarker);
        }
        waypointMarkers.clear();
    }

    @Override
    public void onBackPressed() {
        Log.v(TAG, "getBackStackEntryCount:" + fragmentManager.getBackStackEntryCount());
        if (fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStack();
            if (fragmentManager.getBackStackEntryCount() == 1) {
                setMapWidgetVisibility(View.VISIBLE);
                //Jerry@20220401 add:Search fragment show,can't click
                isFragmentShow = false;
                isFragmentHide = false;
                setMapViewMove(Constants.MOVE_LEFT);
                isAlreadyTranslation = false;
            }
        } else {
            super.onBackPressed();
        }
        initWidgets(); //刷新widget list
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionsManager.processRequestPermissionsResult(requestCode, permissions, grantResults)) {
            setupNavigationServices();
        } else {
            final String errorText = permissionsManager.getErrorText();
            Log.e(TAG, errorText);
            Toast.makeText(getApplicationContext(), errorText, Toast.LENGTH_LONG).show();

            finish();
        }
    }

    @Override
    protected void onDestroy() {
        stopPreview();
        if (tripRenderer != null) {
            getCameraStackController().removeTripFromOverviewCamera(tripRenderer);
            tripRenderer.stop();
            tripRenderer = null;
        }

        if (trafficRenderer != null) {
            trafficRenderer.stop();
            trafficRenderer = null;
        }

        if (trip != null) {
            trip.removeListener(tripUpdateListener);
            tripUpdateListener = null;
            navigation.deleteTrip(trip);
            trip = null;
        }

        if (navigation != null) {
            navigation.close();
            navigation = null;
        }

        if (positioningExtension != null) {
            positioningExtension.stop();
            positioningExtension = null;
        }

        if (drivingContextApi != null) {
            drivingContextApi.close();
        }

        if (navigationServiceBound) {
            unbindService(navigationConnection);
        }

        if (myCameraListener != null) {
            map.getCamera().unregisterListener(myCameraListener);
        }

        //Jerry@20220317 add
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }

        super.onDestroy();
    }

    private void removeSearchMarkers() {
        for (Marker marker : searchMarkerList) {
            markerLayer.removeMarker(marker);
        }
        searchMarkerList.clear();
    }

    @Override
    public void onMarkerChange(List<Location> locations) {
        removeSearchMarkers();
        for (Location location : locations) {
            com.tomtom.navkit.map.Coordinate coordinate = toMapCoordinate(location.coordinate());
            searchMarkerBuilder.setCoordinate(coordinate)
                    // Equivalent for Michi default route markers configuration
                    .setPinUri(getString(R.string.search_marker_pin_path));
            searchMarkerList.add(markerLayer.addMarker(searchMarkerBuilder));
        }
    }

    //metis@0401 解决点击非搜索页区域搜索页不消失问题 -->
    private void finishFragment() {
        Fragment curFragment = fragmentManager.findFragmentById(R.id.search_container);
        if (curFragment != null && curFragment instanceof SearchFragment
                && !isDisappearance && fragmentManager.getBackStackEntryCount() == 1) {
            fragmentManager.popBackStack();
            setMapWidgetVisibility(View.VISIBLE);
            //Jerry@20220401 add:Search fragment show,can't click
            isFragmentShow = false;
            isFragmentHide = true;
            setMapViewMove(Constants.MOVE_LEFT);
            isAlreadyTranslation = false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        for (int i = 0; i < event.getPointerCount(); i++) {
            float x = event.getX(i);
            float y = event.getY(i);

            if (!touchEventInView(searchContainer, x, y)) {
                finishFragment(); //点击非搜索页区域时关闭搜索页

            } else {

            }

        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 该方法检测一个点击事件是否落入在一个View内，换句话说，检测这个点击事件是否发生在该View上。
     *
     * @param view
     * @param x
     * @param y
     * @return
     */
    private boolean touchEventInView(View view, float x, float y) {
        if (view == null) {
            return false;
        }

        int[] location = new int[2];
        view.getLocationOnScreen(location);

        int left = location[0];
        int top = location[1];

        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();

        if (y >= top && y <= bottom && x >= left && x <= right) {
            return true;
        }

        return false;
    }
    //metis@0401 解决点击非搜索页区域搜索页不消失问题 <--

    //Jerry@20220314 add
    private void stopPreview() {
        if (trip != null) {
            trip.stopPreview();
        }
    }

    //Jerry@20220314 add
    private void startDemo() {
        Log.i(TAG, "Starting trip preview");
        if (trip != null) {
            trip.startPreview(Constants.SPEED_MULTIPLIER);
        }
    }

    //Jerry@20220324 add:getInput
    private Input getInput(String query, Coordinate coordinate, double scale, int index) {
        int numberLimit = getNumberOfCategory(scale, index);
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
    private int getNumberOfCategory(double scale, int index) {
        int number = 0;
        if (Constants.TWO_KM >= scale) {
            number = Constants.SCALE_TYPE_1KM_2KM[index];
        } else if (scale > Constants.TWO_KM && scale <= Constants.FOUR_KM) {
            number = Constants.SCALE_TYPE_2KM_4KM[index];
        } else if (scale > Constants.FOUR_KM && scale <= Constants.EIGHT_KM) {
            number = Constants.SCALE_TYPE_4KM_8KM[index];
        } else if (scale > Constants.EIGHT_KM && scale <= Constants.TEN_KM) {
            number = Constants.SCALE_TYPE_8KM_10KM[index];
        } else if (scale > Constants.TEN_KM && scale <= Constants.FIFTEEN_KM) {
            number = Constants.SCALE_TYPE_10KM_15KM[index];
        } else if (scale > Constants.FIFTEEN_KM && scale <= Constants.TWENTY_FIVW_KM) {
            number = Constants.SCALE_TYPE_15KM_25KM[index];
        } else if (scale > Constants.TWENTY_FIVW_KM && scale <= Constants.THIRTY_FIVE_KM) {
            number = Constants.SCALE_TYPE_25KM_35KM[index];
        }
        return number;
    }

    //Jerry@20220324 add:FtsResultsListener
    private class MyFtsResultsListener implements FtsResultsListener {
        String category;

        public MyFtsResultsListener(String category) {
            this.category = category;
        }

        @Override
        public void onFtsResults(@NonNull FtsResults ftsResults) {
            FtsResultVector resultVector = ftsResults.getResults();
            for (FtsResult ftsResult : resultVector) {
                Log.i(TAG, "*FtsResults**Place:" + ftsResult.toPlace().toString());

                double latitude = ftsResult.getLocation().coordinate().latitude();
                double longitude = ftsResult.getLocation().coordinate().longitude();
                com.tomtom.navkit.map.Coordinate coordinate = new com.tomtom.navkit.map.Coordinate(latitude, longitude);
                addPoiCatMarker(coordinate, ftsResult.getPoiName(), category);
                /*PoiCategorySet poiCategorySet = ftsResult.toPlace().poi().categories();
                for (PoiCategory poiCategory : poiCategorySet) {
                    Log.i("Jerry", "*FtsResults**poiCategory:" + poiCategory.id());
                }*/
                //Log.i(TAG, "*****************************************");
            }
        }
    }

    //Jerry@20220324 add:PoiSuggestionsListener
    private class MyPoiSuggestionsListener implements PoiSuggestionsListener {

        @Override
        public void onPoiSuggestionResults(@NonNull PoiSuggestionResults poiSuggestionResults) {

        }
    }

    //Jerry@20220324 add:addPoiCatMarker
    public void addPoiCatMarker(com.tomtom.navkit.map.Coordinate waypoint, String poiName, String category) {
        for (MarkerClass markerClass : poiMarkerClassList) {
            if (waypoint.getLatitude() == markerClass.getWaypoint().getLatitude() &&
                    waypoint.getLongitude() == markerClass.getWaypoint().getLongitude()) {
                return;
            }
        }
        java.util.Map<String, Object> mapObject = getCategoryAttribute(category);
        String url = (String) mapObject.get("url");
        com.tomtom.navkit.map.Color outlineColor = (com.tomtom.navkit.map.Color) mapObject.get("outline-color");
        com.tomtom.navkit.map.Color textColor = (com.tomtom.navkit.map.Color) mapObject.get("text-color");
        final MarkerBuilder markerBuilder = new MarkerBuilder();
        MarkerLabelBuilder markerLabelBuilder = null;
        markerBuilder
                .setCoordinate(waypoint)
                .setPinUri(url)
                .setIconUri(url);
        try {
            markerLabelBuilder = markerBuilder.addLabel();
            markerLabelBuilder
                    .setFontUri(this.getString(R.string.font_style))
                    .setTextAnchoring(MarkerLabelBuilder.TextAnchoring.kCenter)
                    .setTextSize(22)
                    .setWrapText(false)
                    //.setMaximumNumberOfLines(1)
                    //.setLineLengthLimits(4,4)
                    .setOutlineColor(outlineColor)
                    .setOutlineWidth(1)
                    .setOffset(0, -20)
                    .setText(poiName)
                    .setTextColor(textColor);

        } catch (MarkerBuilder.AlreadyHasLabel alreadyHasLabel) {
            alreadyHasLabel.printStackTrace();
        }
        Marker marker = markerLayer.addMarker(markerBuilder);
        //poiCatMarkers.add(marker);
        poiMarkerClassList.add(new MarkerClass(waypoint, marker));
    }

    //Jerry@20220330 add:getCategoryAttribute
    private java.util.Map<String, Object> getCategoryAttribute(String category) {
        java.util.Map<String, Object> mapObject = new HashMap<>();
        mapObject.put("outline-color", new com.tomtom.navkit.map.Color(0x1E1E1E));
        switch (category) {
            case Constants.PARKING:
                mapObject.put("url", getString(R.string.parking_poi_search_position_marker_path));
                mapObject.put("text-color", new com.tomtom.navkit.map.Color(0x54B4F7));
                break;
            case Constants.CHARGING_STATION:
                mapObject.put("url", getString(R.string.charging_station_poi_search_position_marker_path));
                mapObject.put("text-color", new com.tomtom.navkit.map.Color(0x5BC579));
                break;
            case Constants.SUPERMARKET:
                mapObject.put("url", getString(R.string.supermarket_poi_search_position_marker_path));
                mapObject.put("text-color", new com.tomtom.navkit.map.Color(0xEEAD43));
                break;
            case Constants.CAFE:
                mapObject.put("url", getString(R.string.cafe_poi_search_position_marker_path));
                mapObject.put("text-color", new com.tomtom.navkit.map.Color(0xDA82BB));
                break;
            case Constants.RESTAURANT:
                mapObject.put("url", getString(R.string.restaurant_poi_search_position_marker_path));
                mapObject.put("text-color", new com.tomtom.navkit.map.Color(0xEF8082));
                break;
            case Constants.HOTEL:
                mapObject.put("url", getString(R.string.hotel_poi_search_position_marker_path));
                mapObject.put("text-color", new com.tomtom.navkit.map.Color(0xE98F50));
                break;
            case Constants.ATM:
                mapObject.put("url", getString(R.string.atm_poi_search_position_marker_path));
                mapObject.put("text-color", new com.tomtom.navkit.map.Color(0x969696));
                break;
            case Constants.GAS_STATION:
                mapObject.put("url", getString(R.string.gas_station_poi_search_position_marker_path));
                mapObject.put("text-color", new com.tomtom.navkit.map.Color(0x9A86F7));
                break;
            case Constants.HOSPITAL:
                mapObject.put("url", getString(R.string.hospital_poi_search_position_marker_path));
                mapObject.put("text-color", new com.tomtom.navkit.map.Color(0xEC5B57));
                break;
            case Constants.SCHOOL:
                mapObject.put("url", getString(R.string.school_poi_search_position_marker_path));
                mapObject.put("text-color", new com.tomtom.navkit.map.Color(0xB28B74));
                break;
            default:
                mapObject.put("url", getString(R.string.unknown_poi_search_position_marker_path));
                mapObject.put("text-color", new com.tomtom.navkit.map.Color(0xFFFFFF));
                break;
        }
        return mapObject;
    }

    //Jerry@20220324 add:removeAllPoiMarkers
    public void removeAllPoiMarkers(com.tomtom.navkit.map.Coordinate waypoint) {
        /*if (poiCatMarkers.isEmpty()) return;
        for (Marker poiCatMarker : poiCatMarkers) {
            markerLayer.removeMarker(poiCatMarker);
        }
        poiCatMarkers.clear();*/

        if (poiMarkerClassList.isEmpty()) return;
        if (waypoint == null) {
            for (MarkerClass markerClass : poiMarkerClassList) {
                markerLayer.removeMarker(markerClass.getMarker());
            }
            poiMarkerClassList.clear();
            return;
        }
    }

    //Jerry@20220402 add:setMapView move
    private void setMapViewMove(String moveDirection) {
        int moveOffset = map.getViewport().getBottomRight().getX() / 2;
        switch (moveDirection) {
            case Constants.MOVE_RIGHT:
                Point rightBegin = new Point(0, 0);
                map.getInteraction().panBegin(rightBegin);
                Point rightEnd = new Point(moveOffset, 0);
                map.getInteraction().panEnd(rightEnd);
                break;
            case Constants.MOVE_LEFT:
                Point leftBegin = new Point(0, 0);
                map.getInteraction().panBegin(leftBegin);
                Point leftEnd = new Point(-moveOffset, 0);
                map.getInteraction().panEnd(leftEnd);
                break;
        }
    }

    //Jerry@20220406 add:MarkerClass
    private class MarkerClass {
        private com.tomtom.navkit.map.Coordinate waypoint = null;
        private Marker marker = null;

        public MarkerClass(com.tomtom.navkit.map.Coordinate waypoint, Marker marker) {
            this.waypoint = waypoint;
            this.marker = marker;
        }

        public com.tomtom.navkit.map.Coordinate getWaypoint() {
            return waypoint;
        }

        public Marker getMarker() {
            return marker;
        }
    }
}
