/*
 * Copyright (C) 2019 TomTom NV. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom NV and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * license agreement between you and TomTom NV. If you are the licensee, you are only permitted
 * to use this software in accordance with the terms of your license agreement. If you are
 * not the licensee, you are not authorized to use this software in any manner and should
 * immediately return or destroy it.
 */

package com.fxc.ev.launcher.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.fxc.ev.launcher.BuildConfig;
import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.maps.poicatsearch.Constants;
import com.fxc.ev.launcher.utils.ApplicationPreferences;
import com.fxc.ev.launcher.utils.CameraStackController;
import com.fxc.ev.launcher.utils.NetWorkUtil;
import com.fxc.ev.launcher.utils.view.NextInstructionPanelView;
import com.fxc.ev.launcher.utils.view.SpeedBubbleView;
import com.fxc.ev.launcher.utils.CameraStackController.CameraType;
import com.fxc.ev.launcher.utils.view.ZoomBarView;
import com.tomtom.navkit.map.Event;
import com.tomtom.navkit.map.EventManager;
import com.tomtom.navkit.map.Map;
import com.tomtom.navkit.map.MapHolder;
import com.tomtom.navkit.map.Margins;
import com.tomtom.navkit.map.camera.Camera;
import com.tomtom.navkit.map.camera.CameraUpdate;
import com.tomtom.navkit.map.sdk.MapView;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Basic Map Activity that provides interactivity handling for onscreen widgets
 */
public class InteractiveMapActivity extends BaseActivity {
    private static final String TAG = "InteractiveMapActivity";

    private static final long INTERACTIONMODE_ANIMATION_DURATION = 250l;

    private SpeedBubbleView speedBubbleView;
    private ImageButton recenterButton;
    private NextInstructionPanelView nextInstructionPanelView;
    private TextView etaTextView;
    public ConstraintLayout layout_route_avoids;//Jerry@20220427 add
    public ImageView imageViewAvoid;//Jerry@20220428 add
    public ConstraintLayout totalAvoids;//Jerry@20220428 add

    public RelativeLayout mapLayout;
    private MapView mapView;
    private ZoomBarView zoomBarView;
    private View safeRect;
    private MapHolder mapHolder;
    private Map map;
    private CameraStackController cameraStackController;
    private static final Camera.Interpolation ZOOM_INTERPOLATION = Camera.Interpolation.kLinear;
    private static final long ZOOM_DURATION = 250l;
    public String onboardMapPath;//Jerry@20220408 add for onboard map
    public String onboardKeystorePath;//Jerry@20220408 add for onboard map
    public boolean isMapAlreadyInit = false;//Jerry@20220415 add for map initialization

    private InteractiveMode interactiveMode = InteractiveMode.DISABLED;

    protected SpeedBubbleView getSpeedBubbleView() {
        return speedBubbleView;
    }

    protected NextInstructionPanelView getNextInstructionPanelView() {
        return nextInstructionPanelView;
    }

    protected TextView getEtaTextView() {
        return etaTextView;
    }

    protected MapHolder getMapHolder() {
        return mapHolder;
    }

    protected ZoomBarView getZoomBarView() {
        return zoomBarView;
    }

    protected CameraStackController getCameraStackController() {
        return cameraStackController;
    }

    private enum InteractiveMode {
        ENABLED, // User is interacting with the map
        DISABLED // Use is not interacting with the map
    }

    @Override
    protected void initContentContainerView() {
        mapLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.activity_home, null);
        mContentContainer.addView(mapLayout);
        //Jerry@20220428 add-->
        layout_route_avoids = (ConstraintLayout) mapLayout.findViewById(R.id.layout_route_avoids);
        imageViewAvoid = layout_route_avoids.findViewById(R.id.avoid_start);
        totalAvoids = layout_route_avoids.findViewById(R.id.total_avoids);
        //<--Jerry@20220428 add
        initMapView();
    }

    private void initMapView() {
        mapView = mapLayout.findViewById(R.id.map);
        mapHolder = mapView.getMapHolder();
        final AssetManager assetManager = getAssets();
        final StringBuilder styleBuilder = new StringBuilder();
        try (Scanner scanner = new Scanner(assetManager.open("style.json"), "UTF-8").useDelimiter("\\Z")) {
            // The Scanner implementation in Android 5 has a 1024 CharBuffer limit which means
            // large tokens get truncated even if they don't hit the delimiter, so we must iterate
            // over them like this
            while (scanner.hasNext()) {
                styleBuilder.append(scanner.next());
            }

            try {
                map = mapHolder.getMap();
                map.setStyle(styleBuilder.toString());
            } catch (Map.InvalidStyleDefinition | Map.NoStyleAvailable ex) {
                Log.e(TAG, "Failed to set style - " + ex);
            }
        } catch (IOException io) {
            Log.e(TAG, "Failed to read style - " + io);
        }

        zoomBarView = mapLayout.findViewById(R.id.zoomBarView);
        zoomBarView.setOnZoomButtonClickedListener(new ZoomBarView.OnZoomButtonClickedListener() {
            @Override
            public void OnZoomIn(ZoomBarView zoomBarView) {
                map.getCamera().updateProperties(new CameraUpdate().zoomIn(), ZOOM_INTERPOLATION, ZOOM_DURATION);
                Log.v(TAG, "OnZoomIn:getScale: " + map.getCamera().getProperties().getScale());
            }

            @Override
            public void OnZoomOut(ZoomBarView zoomBarView) {
                map.getCamera().updateProperties(new CameraUpdate().zoomOut(), ZOOM_INTERPOLATION, ZOOM_DURATION);
                Log.v(TAG, "OnZoomOut:getScale: " + map.getCamera().getProperties().getScale());
            }
        });

        safeRect = mapLayout.findViewById(R.id.safeRect);
        safeRect.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                //mapView = findViewById(R.id.map);
                final Rect mapRect = new Rect();
                mapView.getDrawingRect(mapRect);

                final int marginLeft = mapRect.left + left;
                final int marginTop = mapRect.top + top;
                final int marginRight = Math.max(0, mapRect.right - right);
                final int marginBottom = Math.max(0, mapRect.bottom - bottom);

                map.setSafeAreaMargins(new Margins(marginLeft, marginTop, marginRight, marginBottom));
            }
        });

        cameraStackController = new CameraStackController(map.getCameraOperatorStack());

        speedBubbleView = mapLayout.findViewById(R.id.speedBubble);
        nextInstructionPanelView = mapLayout.findViewById(R.id.nextInstructionContainerView);
        etaTextView = mapLayout.findViewById(R.id.eta);

        cameraStackController.addCameraChangedListener(new CameraStackController.OnCameraChangedListener() {
            @Override
            public void onChange(CameraType oldCameraType, CameraType newCameraType) {
                final InteractiveMode newInteractiveMode = getNewInteractiveMode(interactiveMode, oldCameraType, newCameraType);
                final InteractiveMode oldInteractiveMode = interactiveMode;

                interactiveMode = newInteractiveMode;

                final boolean runInteractiveModeAnimation = !newInteractiveMode.equals(oldInteractiveMode);
                if (runInteractiveModeAnimation) {
                    doEnterExitInteractiveModeAnimation(newInteractiveMode);
                }
            }
        });

        //Jerry@20220415 add for map init completion listener->
        EventManager eventManager = getMapHolder().getEventManager();
        try {
            eventManager.registerListener(new MyEventListener());
        } catch (EventManager.ListenerAlreadyRegistered listenerAlreadyRegistered) {
            //Log.i(TAG, "*EventManager.ListenerAlreadyRegistered*:");
            listenerAlreadyRegistered.printStackTrace();
        }
        //<-Jerry@20220415 add:Map init completion listener
    }

    //Jerry@20220415 add for map init completion listener
    private class MyEventListener extends EventManager.EventListener {
        @Override
        public void onEvent(Event event) {
            //super.onEvent(event);
            //Log.i(TAG, "*MyEventListener*event.getType()**:" + event.getType().name());
            if (!isMapAlreadyInit) {
                isMapAlreadyInit = true;
            }
        }
    }

    private static InteractiveMode getNewInteractiveMode(InteractiveMode currentMode, CameraType previousCameraType, CameraType newCameraType) {
        final InteractiveMode newMode;

        if (currentMode.equals(InteractiveMode.ENABLED) && previousCameraType.equals(CameraType.kManualCamera)) {
            newMode = InteractiveMode.DISABLED;
        } else if (currentMode.equals(InteractiveMode.DISABLED) && newCameraType.equals(CameraType.kManualCamera)) {
            newMode = InteractiveMode.ENABLED;
        } else {
            newMode = currentMode;
        }

        return newMode;
    }

    private void doEnterExitInteractiveModeAnimation(InteractiveMode newInteractiveMode) {
        final boolean enteringInteractiveMode = newInteractiveMode.equals(InteractiveMode.ENABLED);

        final boolean haveRecenterButton = (recenterButton != null);
        final boolean haveSpeedBubbleView = (speedBubbleView != null);
        final boolean haveNextInstructionContainerView = (nextInstructionPanelView != null);
        final boolean haveEtaTextView = (etaTextView != null);

        final int recenterButtonCurrentVisibility =
                (haveRecenterButton ? recenterButton.getVisibility() : View.GONE);
        final int recenterButtonVisibilityStart =
                (enteringInteractiveMode ? recenterButtonCurrentVisibility : View.GONE);
        final int recenterButtonVisibilityEnd = (enteringInteractiveMode ? View.VISIBLE : View.GONE);

        final int speedBubbleViewHeight = (haveSpeedBubbleView ? speedBubbleView.getMeasuredHeight() : 0);
        final int speedBubbleVisibilityStart = View.VISIBLE;
        final int speedBubbleVisibilityEnd = (enteringInteractiveMode ? View.INVISIBLE : View.VISIBLE);
        final float speedBubbleTranslationY = (enteringInteractiveMode ? speedBubbleViewHeight : 0.0f);
        final float nextInstructionContainerViewHeight = (haveNextInstructionContainerView
                ? nextInstructionPanelView.getMeasuredHeight() + nextInstructionPanelView.getTop() : 0.0f);

        final float nextInstructionTranslationY = (enteringInteractiveMode ? -nextInstructionContainerViewHeight : 0.0f);

        final float etaTextViewHeight = (haveEtaTextView ? etaTextView.getMeasuredHeight() + etaTextView.getTop() : 0.0f);
        final float etaTranslationY = (enteringInteractiveMode ? -etaTextViewHeight : 0.0f);

        if (haveSpeedBubbleView) {
            speedBubbleView.animate()
                    .setDuration(INTERACTIONMODE_ANIMATION_DURATION)
                    .translationY(speedBubbleTranslationY)
                    .withStartAction(new Runnable() {
                        @Override
                        public void run() {
                            if (haveRecenterButton) {
                                //recenterButton.setVisibility(recenterButtonVisibilityStart); //metis@0428 mark
                            }
                            speedBubbleView.setVisibility(speedBubbleVisibilityStart);
                        }
                    })
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            if (haveRecenterButton) {
                                //recenterButton.setVisibility(recenterButtonVisibilityEnd); //metis@0428 mark
                            }
                            speedBubbleView.setVisibility(speedBubbleVisibilityEnd);
                        }
                    });
        } else if (haveRecenterButton) {
            //recenterButton.setVisibility(recenterButtonVisibilityEnd); //metis@0428 mark
        }

        //Jerry@20220412 mark:Drag maps without disappearing
        /*if (haveNextInstructionContainerView) {
            nextInstructionPanelView.animate()
                    .setDuration(INTERACTIONMODE_ANIMATION_DURATION)
                    .translationY(nextInstructionTranslationY);
        }

        if (haveEtaTextView) {
            etaTextView.animate()
                    .setDuration(INTERACTIONMODE_ANIMATION_DURATION)
                    .translationY(etaTranslationY);
        }*/
    }

    @Override
    protected void onPause() {
        //mapView.onPause();//Jerry@20220429 mark
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mapView.onResume();//Jerry@20220429 mark
    }

    @Override
    protected void onDestroy() {
        cameraStackController.deactivateAllCameraOperators();
        mapHolder.delete();
        mapHolder = null;
        super.onDestroy();
    }
}
