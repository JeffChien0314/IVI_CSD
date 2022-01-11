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
package com.fxc.ev.launcher.maps.search;

import android.content.Context;

import com.fxc.ev.launcher.BuildConfig;
import com.tomtom.online.sdk.routing.route.information.FullRoute;
import com.tomtom.online.sdk.search.OnlineSearchApi;
import com.tomtom.online.sdk.search.SearchApi;
import com.tomtom.online.sdk.search.api.alongroute.AlongRouteSearchResultListener;
import com.tomtom.online.sdk.search.data.alongroute.AlongRouteSearchQueryBuilder;

class SearchAlongRouteRequester {

    private static final int SEARCH_MAX_DETOUR_TIME = 3600;
    private static final int SEARCH_MAX_LIMIT = 10;

    private Context context;
    private AlongRouteSearchResultListener alongRouteSearchCallback;

    SearchAlongRouteRequester(Context context, AlongRouteSearchResultListener alongRouteSearchCallback) {
        this.context = context;
        this.alongRouteSearchCallback = alongRouteSearchCallback;
    }

    public void performSearch(String term, FullRoute route) {
        //tag::doc_search_along_route_request[]
        AlongRouteSearchQueryBuilder query = new AlongRouteSearchQueryBuilder(
                term,
                route.getCoordinates(),
                SEARCH_MAX_DETOUR_TIME);

        query.withLimit(SEARCH_MAX_LIMIT);

        SearchApi searchAPI = OnlineSearchApi.create(context, BuildConfig.SEARCH_API_KEY);
        searchAPI.alongRouteSearch(query.build(), alongRouteSearchCallback);
        //end::doc_search_along_route_request[]
    }
}
