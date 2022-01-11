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

import androidx.annotation.NonNull;

import com.tomtom.online.sdk.common.rx.RxContext;
import com.tomtom.online.sdk.search.extensions.SearchService;
import com.tomtom.online.sdk.search.extensions.SearchServiceConnectionCallback;
import com.tomtom.online.sdk.search.fuzzy.FuzzyOutcomeCallback;
import com.tomtom.online.sdk.search.fuzzy.FuzzySearchSpecification;

import java.util.concurrent.Executors;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class SearchFragmentService implements RxContext, SearchServiceConnectionCallback {

    private static final int NETWORK_THREADS_NUMBER = 4;

    private SearchService searchService;

    SearchFragmentService() {
        Timber.d("SearchFragmentService()");
    }

    public void performSearch(FuzzySearchSpecification specification, FuzzyOutcomeCallback fuzzyOutcomeCallback) {
        //tag::doc_perform_search[]
        Timber.d("performSearch");
        searchService.search(specification, fuzzyOutcomeCallback);
        //end::doc_perform_search[]
    }

    void cancelSearchIfRunning() {
        //tag::doc_cancel_search[]
        searchService.cancelSearchIfRunning();
        //end::doc_cancel_search[]
    }

    @NonNull
    @Override
    public Scheduler getWorkingScheduler() {
        return Schedulers.from(Executors.newFixedThreadPool(NETWORK_THREADS_NUMBER));
    }

    @NonNull
    @Override
    public Scheduler getResultScheduler() {
        return AndroidSchedulers.mainThread();
    }

    //tag::doc_search_service_connection_callback[]
    @Override
    public void onBindSearchService(SearchService service) {
        Timber.d("onBindSearchService");
        searchService = service;
    }
    //end::doc_search_service_connection_callback[]

    SearchService getSearchService() {
        return searchService;
    }

}
