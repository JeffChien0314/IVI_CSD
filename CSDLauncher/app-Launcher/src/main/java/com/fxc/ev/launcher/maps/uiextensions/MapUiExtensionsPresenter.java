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

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.activities.BaseFunctionalExamplePresenter;
import com.fxc.ev.launcher.activities.FunctionalExampleModel;
import com.fxc.ev.launcher.maps.geofencing.report.GeofencingFencesDrawer;
import com.fxc.ev.launcher.maps.geofencing.report.GeofencingMarkersDrawer;
import com.fxc.ev.launcher.maps.geofencing.report.GeofencingReportRequester;
import com.fxc.ev.launcher.activities.FunctionalExampleFragment;
import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.geofencing.GeofencingException;
import com.tomtom.online.sdk.geofencing.report.Report;
import com.tomtom.online.sdk.geofencing.report.ReportCallback;
import com.tomtom.online.sdk.map.CameraPosition;
import com.tomtom.online.sdk.map.MapConstants;
import com.tomtom.online.sdk.map.Marker;
import com.tomtom.online.sdk.map.TomtomMap;
import com.tomtom.online.sdk.map.TomtomMapCallback;
import com.tomtom.online.sdk.map.ui.arrowbuttons.ArrowButtonsGroup;
import com.tomtom.online.sdk.map.ui.zoom.ZoomButtonsGroup;
import com.tomtom.online.sdk.search.fuzzy.FuzzySearchDetails;

import java.util.UUID;

public class MapUiExtensionsPresenter extends BaseFunctionalExamplePresenter implements
        TomtomMapCallback.OnMarkerDragListener {
    private MarkerDrawer markerDrawer;
    private MapUiExtensionsFragment mapUiExtensionsFragment;

    //metis@211203 -->
    //tag::doc_projects_ID[]
    private static final UUID PROJECT_UUID_ONE_FENCE = UUID.fromString("94325f2c-69f1-459e-b83d-0dac7546cb79"/*"5bcd8892-52f4-4168-80c3-514b440d412e"*/);
    private static final UUID PROJECT_UUID_TWO_FENCES = UUID.fromString("292bcc78-4379-44f0-85bf-5ae2e9628781"/*"34538e2f-09bd-405c-b6a4-a74f44af79e4"*/);
    //end::doc_projects_ID[]

    private LatLng DEFAULT_MAP_POSITION /*= new LatLng(40.86224, -73.85745)*/;
    private static final double DEFAULT_ZOOM_LEVEL = 12D;

    private UUID currentProjectId;

    private GeofencingFencesDrawer fenceDrawer;
    private GeofencingMarkersDrawer geofencingMarkerDrawer;
    private GeofencingReportRequester reportRequester;

    @Override
    public void bind(final FunctionalExampleFragment view, final TomtomMap map) {
        super.bind(view, map);
        Log.v("metis", "bind");
        markerDrawer = new MarkerDrawer(view.getContext(), tomtomMap);
        mapUiExtensionsFragment = (MapUiExtensionsFragment) view;
        cleanup();
        if (!view.isMapRestored()) {
            Log.v("metis", "!view.isMapRestored()");
            centerOnAmsterdam(mapUiExtensionsFragment.isShowMarker(), mapUiExtensionsFragment.getLatLng(), mapUiExtensionsFragment.getZoomLevel());
            defaultMapComponentIcons();
            show();
        }

        //metis@211203 -->
        DEFAULT_MAP_POSITION = mapUiExtensionsFragment.getCurPosition();
        tomtomMap.addOnMarkerDragListener(this);
        fenceDrawer = new GeofencingFencesDrawer(tomtomMap);
        geofencingMarkerDrawer = new GeofencingMarkersDrawer(getContext(), tomtomMap);
        reportRequester = new GeofencingReportRequester(getContext(), resultListener);
        //metis@211203 <--
    }

    @Override
    public FunctionalExampleModel getModel() {
        return new MapUiExtensionsFunctionalExample();
    }

    @Override
    public void cleanup() {
        centerOn(mapUiExtensionsFragment.getCurPosition());
        defaultMapComponentIcons();
        defaultMapComponentsVisibility();
        if (tomtomMap != null) {
            tomtomMap.removeMarkers();
            tomtomMap.clearRoute();
            tomtomMap.getDrivingSettings().removeChevrons();
            tomtomMap.getOverlaySettings().removeOverlays();
            tomtomMap.getRouteSettings().clearRoute();
        }
    }

    private void defaultMapComponentsVisibility() {
        tomtomMap.getUiSettings().getCompassView().show(); //指南针
        tomtomMap.getUiSettings().getCurrentLocationView().show(); //定位
        tomtomMap.getUiSettings().getPanningControlsView().hide(); //平移控制
        tomtomMap.getUiSettings().getZoomingControlsView().hide(); //缩放控制
    }

    void alignCurrentLocationButton(@NonNull Context context, @NonNull TomtomMap tomtomMap) {
        int currentLocationButtonBottomMargin = context.getResources().getDimensionPixelSize(R.dimen.current_location_default_margin_bottom);
        int currentLocationButtonLeftMargin = context.getResources().getDimensionPixelSize(R.dimen.compass_default_margin_start);

        tomtomMap.getUiSettings().getCurrentLocationView().setMargins(
                currentLocationButtonLeftMargin, 0, 0, currentLocationButtonBottomMargin);
    }

    private void centerOnAmsterdam(Boolean isShowMarker, LatLng latLng, double zoomLevel) {
        Log.v("metis", "centerOnAmsterdam");
        alignCurrentLocationButton(getContext(), tomtomMap);
        tomtomMap.centerOn(CameraPosition.builder()
                .focusPosition(latLng)
                .zoom(zoomLevel)
                .bearing(MapConstants.ORIENTATION_NORTH_WEST)
                .build());
        if (isShowMarker) createSimpleBalloon(mapUiExtensionsFragment.getFuzzySearchDetails());
        //tomtomMap.centerOnMyLocation();
    }

    public void createSimpleBalloon(FuzzySearchDetails fuzzySearchDetails) {
        tomtomMap.removeMarkers();
        //tag::doc_marker_balloon_model[]
        /*BaseMarkerBalloon markerBalloon = new BaseMarkerBalloon();
        markerBalloon.addProperty("key", "value");*/
        //end::doc_marker_balloon_model[]

        markerDrawer.createMarkerWithSimpleMarkerBalloon(mapUiExtensionsFragment.getContext(),
                fuzzySearchDetails.getPosition(), fuzzySearchDetails.getPoi().getName());
    }

    private void centerOnMap(LatLng latLng, double zoomLevel) {
        Log.v("metis", "centerOnMap");
        tomtomMap.centerOn(CameraPosition.builder()
                .focusPosition(latLng)
                .zoom(10)
                .bearing(MapConstants.ORIENTATION_NORTH_WEST)
                .build());
        tomtomMap.centerOnMyLocation();
    }


    public void defaultMapComponentIcons() {
        ImageView compassView = (ImageView) tomtomMap.getUiSettings().getCompassView().getView();
        compassView.setImageResource(R.drawable.btn_compass); //设置指南针icon
        ImageView currentLocationView = (ImageView) tomtomMap.getUiSettings().getCurrentLocationView().getView();
        currentLocationView.setImageResource(R.drawable.btn_current_location); //设置location icon

        //tomtomMap.getUiSettings().getCompassView().show();

        //tag::set_default_panning_controls[]  //设置平移控制上，下，左，右icons
        ArrowButtonsGroup arrowButton = (ArrowButtonsGroup) tomtomMap.getUiSettings().getPanningControlsView().getView();
        arrowButton.getArrowDownButton().setImageResource(R.drawable.btn_down);
        arrowButton.getArrowUpButton().setImageResource(R.drawable.btn_up);
        arrowButton.getArrowLeftButton().setImageResource(R.drawable.btn_left);
        arrowButton.getArrowRightButton().setImageResource(R.drawable.btn_right);

        //tomtomMap.getUiSettings().getPanningControlsView().show();
        //end::set_default_panning_controls[]

        //tag::set_default_zooming_controls[] //设置缩放控制 +/- icons
        ZoomButtonsGroup zoomButtons = (ZoomButtonsGroup) tomtomMap.getUiSettings().getZoomingControlsView().getView();
        zoomButtons.getZoomInButton().setImageResource(R.drawable.btn_zoom_in);
        zoomButtons.getZoomOutButton().setImageResource(R.drawable.btn_zoom_out);

        //tomtomMap.getUiSettings().getZoomingControlsView().show();
        //end::set_default_zooming_controls[]
    }

    public void show() {
        //tag::show_compass[]
        tomtomMap.getUiSettings().getCompassView().show();
        //end::show_compass[]

        //tag::show_current_location[]
        tomtomMap.getUiSettings().getCurrentLocationView().show();
        //end::show_current_location[]

        //tag::show_panning_controls[]
        tomtomMap.getUiSettings().getPanningControlsView().show();
        //end::show_panning_controls[]

        //tag::show_zooming_controls[]
        tomtomMap.getUiSettings().getZoomingControlsView().show();
        //end::show_zooming_controls[]
    }


    public void show(Boolean isShowMarker, LatLng latLng, double zoomLevel) {
        centerOnAmsterdam(isShowMarker, latLng, zoomLevel);
        //tag::show_compass[]
        tomtomMap.getUiSettings().getCompassView().show();
        //end::show_compass[]

        //tag::show_current_location[]
        if (isShowMarker) {
            tomtomMap.getUiSettings().getCurrentLocationView().hide();
        } else {
            tomtomMap.getUiSettings().getCurrentLocationView().show();
        }
        //end::show_current_location[]

        //tag::show_panning_controls[]
        tomtomMap.getUiSettings().getPanningControlsView().show();
        //end::show_panning_controls[]

        //tag::show_zooming_controls[]
        tomtomMap.getUiSettings().getZoomingControlsView().show();
        //end::show_zooming_controls[]
    }

    public void hide(Boolean isShowMarker, LatLng latLng, Double zoomLevel) {
        centerOnAmsterdam(isShowMarker, latLng, zoomLevel);
        //tag::hide_compass[]
        tomtomMap.getUiSettings().getCompassView().hide();
        //end::hide_compass[]

        //tag::hide_current_location[]
        tomtomMap.getUiSettings().getCurrentLocationView().hide();
        //end::hide_current_location[]

        //tag::hide_panning_controls[]
        tomtomMap.getUiSettings().getPanningControlsView().hide();
        //end::hide_panning_controls[]

        //tag::hide_zooming_controls[]
        tomtomMap.getUiSettings().getZoomingControlsView().hide();
        //end::hide_zooming_controls[]
    }

    public void hide() {
        //tag::hide_compass[]
        tomtomMap.getUiSettings().getCompassView().hide();
        //end::hide_compass[]

        //tag::hide_current_location[]
        tomtomMap.getUiSettings().getCurrentLocationView().hide();
        //end::hide_current_location[]

        //tag::hide_panning_controls[]
        tomtomMap.getUiSettings().getPanningControlsView().hide();
        //end::hide_panning_controls[]

        //tag::hide_zooming_controls[]
        tomtomMap.getUiSettings().getZoomingControlsView().hide();
        //end::hide_zooming_controls[]
    }


    public void customMapComponentIcons(Boolean isShowMarker, LatLng latLng, Double zoomLevel) {
        centerOnAmsterdam(isShowMarker, latLng, zoomLevel);
        ImageView compassView = (ImageView) tomtomMap.getUiSettings().getCompassView().getView();
        compassView.setImageResource(R.drawable.ic_custom_compass);

        ImageView currentLocationButton = (ImageView) tomtomMap.getUiSettings().getCurrentLocationView().getView();
        currentLocationButton.setImageResource(R.drawable.ic_map_position);

        //tag::set_custom_panning_controls[]
        ArrowButtonsGroup arrowButtons = (ArrowButtonsGroup) tomtomMap.getUiSettings().getPanningControlsView().getView();
        arrowButtons.getArrowDownButton().setImageResource(R.drawable.btn_down_custom);
        arrowButtons.getArrowUpButton().setImageResource(R.drawable.btn_up_custom);
        arrowButtons.getArrowLeftButton().setImageResource(R.drawable.btn_left_custom);
        arrowButtons.getArrowRightButton().setImageResource(R.drawable.btn_right_custom);
        //end::set_custom_panning_controls[]

        //tag::set_custom_zooming_controls[]
        ZoomButtonsGroup zoomButtons = (ZoomButtonsGroup) tomtomMap.getUiSettings().getZoomingControlsView().getView();
        zoomButtons.getZoomInButton().setImageResource(R.drawable.btn_zoom_in_custom);
        zoomButtons.getZoomOutButton().setImageResource(R.drawable.btn_zoom_out_custom);
        //end::set_custom_zooming_controls[]
    }

    @SuppressWarnings("unused")
    private void confLogoComponent(int gravity, int top, int left, int right, int bottom) {
        //tag::set_custom_logo_gravity[]
        tomtomMap.getUiSettings().getLogoView().setGravity(gravity);
        //end::set_custom_logo_gravity[]

        //tag::set_custom_logo_margins[]
        tomtomMap.getUiSettings().getLogoView().setMargins(left, top, right, bottom);
        //end::set_custom_logo_margins[]

        //tag::restore_default_logo_margins[]
        tomtomMap.getUiSettings().getLogoView().restoreDefaultMargins();
        //end::restore_default_logo_margins[]

        //tag::restore_default_logo_gravity[]
        tomtomMap.getUiSettings().getLogoView().restoreDefaultGravity();
        //end::restore_default_logo_gravity[]

        //tag::apply_default_logo[]
        tomtomMap.getUiSettings().getLogoView().applyDefaultLogo();
        //end::apply_default_logo[]

        //tag::apply_inverted_logo[]
        tomtomMap.getUiSettings().getLogoView().applyInvertedLogo();
        //end::apply_inverted_logo[]
    }

    //metis@211203 add-->
    //tag::doc_register_report_listener[]
    private ReportCallback resultListener = new ReportCallback() {

        @Override
        public void onSuccess(@NonNull Report report) {
            geofencingMarkerDrawer.removeFenceMarkers();
            geofencingMarkerDrawer.updateMarkersFromResponse(report);
        }

        @Override
        public void onError(@NonNull GeofencingException error) {
            Toast.makeText(getContext(), R.string.report_service_request_error, Toast.LENGTH_LONG).show();
        }
    };
    //end::doc_register_report_listener[]

    public void centerOnForFences(LatLng latLng) {
        hide();
        tomtomMap.centerOn(CameraPosition.builder()
                .focusPosition(latLng)
                .zoom(12D)
                .bearing(MapConstants.ORIENTATION_NORTH_WEST)
                .build());
    }

    public void drawTwoFences(LatLng checkPoi) {

        //Clear and set state
        this.currentProjectId = PROJECT_UUID_TWO_FENCES;
        //clearMapAndCenterOnDefault();

        centerOnForFences(checkPoi);

        //Update fences and markers
        geofencingMarkerDrawer.drawDraggableMarkerAtDefaultPosition(checkPoi);
        fenceDrawer.drawPolygonFence();
        fenceDrawer.drawCircularFence();

        //Request report
        reportRequester.requestReport(checkPoi, currentProjectId);
    }

    @Override
    public void onStartDragging(@NonNull Marker marker) {
        geofencingMarkerDrawer.deselectDraggableMarker();
    }

    @Override
    public void onStopDragging(@NonNull Marker marker) {
        reportRequester.requestReport(marker.getPosition(), currentProjectId);
    }

    @Override
    public void onDragging(@NonNull Marker marker) {

    }

    private void clearMap() {
        tomtomMap.getOverlaySettings().removeOverlays();
        tomtomMap.getMarkerSettings().removeMarkers();
    }

    private void clearMapAndCenterOnDefault() {
        clearMap();
        centerOn(DEFAULT_MAP_POSITION, DEFAULT_ZOOM_LEVEL);
    }

    public void setCurrentProjectId(UUID newProjectId) {
        currentProjectId = newProjectId;
    }

    public UUID getCurrentProjectId() {
        return currentProjectId;
    }

    //metis@211203 add <--
}
