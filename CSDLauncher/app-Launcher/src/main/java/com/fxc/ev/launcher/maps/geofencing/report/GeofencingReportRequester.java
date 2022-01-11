/**
 * Copyright (c) 2015-2021 TomTom N.V. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be used
 * for internal evaluation purposes or commercial use strictly subject to separate licensee
 * agreement between you and TomTom. If you are the licensee, you are only permitted to use
 * this Software in accordance with the terms of your license agreement. If you are not the
 * licensee then you are not authorised to use this software in any manner and should
 * immediately return it to TomTom N.V.
 */
package com.fxc.ev.launcher.maps.geofencing.report;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;

import com.fxc.ev.launcher.BuildConfig;
import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.geofencing.GeofencingApi;
import com.tomtom.online.sdk.geofencing.RxGeofencingApi;
import com.tomtom.online.sdk.geofencing.report.ReportCallback;
import com.tomtom.online.sdk.geofencing.report.ReportQuery;

import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GeofencingReportRequester {

    private static final float QUERY_RANGE = 5000; //in meters

    private GeofencingApi geofencingApi;
    private ReportCallback resultListener;

    public GeofencingReportRequester(Context context, ReportCallback resultListener) {
        this.resultListener = resultListener;
        geofencingApi = createGeofencingApi(context);
    }

    public void requestReport(LatLng position, UUID projectId) {
        ReportQuery query = createQuery(position, projectId);
        //tag::doc_obtain_report[]
        geofencingApi.obtainReport(query, resultListener);
        //end::doc_obtain_report[]
    }

    private ReportQuery createQuery(LatLng coordinates, UUID projectId) {
        Location location = coordinates.toLocation();
        //tag::doc_create_report_service_query[]
        return new ReportQuery.Builder(location)
                .projectId(projectId)
                .range(QUERY_RANGE)
                .build();
        //end::doc_create_report_service_query[]
    }

    private GeofencingApi createGeofencingApi(Context context) {
        //tag::doc_initialise_geofencing[]
        GeofencingApi geofencingApi = new GeofencingApi(context, BuildConfig.GEOFENCING_API_KEY);
        //end::doc_initialise_geofencing[]
        return geofencingApi;
    }

    @SuppressLint({"CheckResult", "unused"})
    private RxGeofencingApi obtainReportRx(Context context, ReportQuery reportQuery) {
        //tag::doc_initialise_rx_geofencing[]
        RxGeofencingApi rxGeofencingApi = new RxGeofencingApi(context, BuildConfig.GEOFENCING_API_KEY);
        //end::doc_initialise_rx_geofencing[]
        //tag::doc_obtain_report_rx[]
        rxGeofencingApi.obtainReport(reportQuery)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        report -> { /* Handle result */ },
                        error -> { /* Handle exception */ }
                );
        //end::doc_obtain_report_rx[]
        return rxGeofencingApi;
    }
}
