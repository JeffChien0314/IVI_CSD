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

package com.fxc.ev.launcher.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.tomtom.navkit.map.camera.CameraOperator;
import com.tomtom.navkit.map.camera.CameraOperatorStack;
import com.tomtom.navkit.map.camera.NotifyingCameraOperator;
import com.tomtom.navkit2.TripRenderer;
import com.tomtom.navkit2.mapdisplay.trip.OverviewCameraAdapter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class that maintains the Map's CameraOperatorStack with a predefined set of camera operators.
 */
public class CameraStackController {

    public enum CameraType {
        kManualCamera(0),
        kFollowRouteCamera(1),
        kNorthUpCamera(2),
        kOverviewCamera(3);

        private final int value;

        CameraType(int value) {
            this.value = value;
        }

        public final int getValue() {
            return value;
        }
    }

    /**
     * Interface to notify when the Camera Operator has changed.
     */
    public interface OnCameraChangedListener {
        void onChange(CameraType oldCameraType, CameraType newCameraType);
    }

    private interface OnCameraNotificationListener {
        void onActivated(CameraType cameraType);

        void onDeactivated(CameraType cameraType);
    }

    private class CameraNotificationHandler extends NotifyingCameraOperator.Listener {
        private CameraType cameraType;
        private OnCameraNotificationListener listener;

        CameraNotificationHandler(CameraType cameraType, OnCameraNotificationListener listener) {
            this.cameraType = cameraType;
            this.listener = listener;
        }

        @Override
        public void onActivated() {
            listener.onActivated(cameraType);
        }

        @Override
        public void onDeactivated() {
            listener.onDeactivated(cameraType);
        }
    }

    private static final String TAG = "CameraStackController";

    private final CameraOperatorStack cameraOperatorStack;
    private final Map<CameraType, NotifyingCameraOperator> cameraOperatorMap = new HashMap<>();

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Set<OnCameraChangedListener> cameraChangedListenerSet = new HashSet<>();

    private CameraType previousCameraType;
    private CameraType currentCameraType;

    private OverviewCameraAdapter overviewCameraAdapter;
    private int numTrackedTrips = 0;

    public CameraStackController(CameraOperatorStack cameraOperatorStack) {
        if (cameraOperatorStack == null) {
            throw new IllegalArgumentException("Invalid CameraOperatorStack");
        }

        this.cameraOperatorStack = cameraOperatorStack;

        final Runnable cameraChangedListenerRunnable = new Runnable() {
            @Override
            public void run() {
                for (OnCameraChangedListener listener : cameraChangedListenerSet) {
                    listener.onChange(previousCameraType, currentCameraType);
                }
            }
        };

        final OnCameraNotificationListener cameraNotificationListener = new OnCameraNotificationListener() {
            @Override
            public void onActivated(CameraType cameraType) {
                previousCameraType = currentCameraType;
                currentCameraType = cameraType;
                handler.removeCallbacks(cameraChangedListenerRunnable);
                handler.post(cameraChangedListenerRunnable);
            }

            @Override
            public void onDeactivated(CameraType cameraType) {
                previousCameraType = currentCameraType;
                currentCameraType = CameraType.kManualCamera;
                handler.removeCallbacks(cameraChangedListenerRunnable);
                handler.post(cameraChangedListenerRunnable);
            }
        };

        cameraOperatorMap.put(CameraType.kFollowRouteCamera, createCameraOperator(CameraType.kFollowRouteCamera, cameraNotificationListener));
        cameraOperatorMap.put(CameraType.kNorthUpCamera, createCameraOperator(CameraType.kNorthUpCamera, cameraNotificationListener));
        cameraOperatorMap.put(CameraType.kOverviewCamera, createCameraOperator(CameraType.kOverviewCamera, cameraNotificationListener));

        previousCameraType = CameraType.kManualCamera;
        currentCameraType = CameraType.kManualCamera;
    }

    private NotifyingCameraOperator createCameraOperator(CameraType cameraType, OnCameraNotificationListener notificationListener) {

        final CameraOperator cameraOperator;

        switch (cameraType) {
            case kManualCamera:
                throw new IllegalArgumentException("Cannot create a manual CameraOperator");
            case kFollowRouteCamera:
                cameraOperator = cameraOperatorStack.createFollowRouteCameraOperator();
                break;
            case kNorthUpCamera:
                cameraOperator = cameraOperatorStack.createNorthUpFollowCameraOperator();
                break;
            case kOverviewCamera:
                overviewCameraAdapter = new OverviewCameraAdapter();
                cameraOperator = overviewCameraAdapter;
                break;
            default:
                throw new IllegalArgumentException("Unknown CameraOperator");
        }

        final CameraNotificationHandler cameraHandler =
                new CameraNotificationHandler(cameraType, notificationListener);
        final NotifyingCameraOperator notifyingCameraOperator =
                new NotifyingCameraOperator(cameraOperator, cameraHandler);

        return notifyingCameraOperator;
    }

    private CameraType getNextCamera(CameraType currentCameraType) {
        final CameraType nextCameraType;

        switch (currentCameraType) {
            case kManualCamera:
                nextCameraType = CameraType.kFollowRouteCamera;
                break;
            case kFollowRouteCamera:
                nextCameraType = shouldUseOverviewCamera() ? CameraType.kOverviewCamera : CameraType.kNorthUpCamera;
                break;
            default:
                Log.e(TAG, "Unknown CameraType");
                // fall through
            case kNorthUpCamera:
                // fall through
            case kOverviewCamera:
                nextCameraType = CameraType.kFollowRouteCamera;
                break;
        }

        return nextCameraType;
    }

    /**
     * deactivateAllCameraOperators should be called before deleting MapHolder.
     */
    public void deactivateAllCameraOperators() {
        for (NotifyingCameraOperator cameraOperator : cameraOperatorMap.values()) {
            cameraOperatorStack.removeCameraOperator(cameraOperator);
        }
    }

    private void doUpdateCamera(CameraType newCameraType) {
        if (CameraType.kManualCamera == newCameraType) {
            deactivateAllCameraOperators();
        } else {
            cameraOperatorStack.pushCameraOperator(cameraOperatorMap.get(newCameraType));
        }
    }

    public void addCameraChangedListener(OnCameraChangedListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException();
        }

        if (!cameraChangedListenerSet.add(listener)) {
            Log.i(TAG, "Listener already added");
        }

        listener.onChange(previousCameraType, currentCameraType);
    }

    public void removeCameraChangedListener(OnCameraChangedListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException();
        }

        if (!cameraChangedListenerSet.remove(listener)) {
            Log.i(TAG, "Listener was not added; cannot remove it");
        }
    }

    public void nextCamera() {
        doUpdateCamera(getNextCamera(currentCameraType));
    }

    public void setCurrentCamera(CameraType state) {
        doUpdateCamera(state);
    }

    //metis@0429 add
    public CameraType getCurrentCamera() {
        return currentCameraType;
    }

    private boolean shouldUseOverviewCamera() {
        return numTrackedTrips > 0;
    }

    public void addTripToOverviewCamera(TripRenderer renderer) {
        overviewCameraAdapter.addTripRenderer(renderer);
        ++numTrackedTrips;
    }

    public void removeTripFromOverviewCamera(TripRenderer renderer) {
        overviewCameraAdapter.removeTripRenderer(renderer);
        --numTrackedTrips;
    }
}
