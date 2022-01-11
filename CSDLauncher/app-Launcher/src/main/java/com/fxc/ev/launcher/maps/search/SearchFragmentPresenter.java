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
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fxc.ev.launcher.utils.LocationProvider;
import com.google.common.collect.ImmutableList;
import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.common.location.LatLngBias;
import com.tomtom.online.sdk.common.util.Contextable;
import com.tomtom.online.sdk.location.LocationUpdateListener;
import com.tomtom.online.sdk.search.SearchException;
import com.tomtom.online.sdk.search.fuzzy.FuzzyLocationDescriptor;
import com.tomtom.online.sdk.search.fuzzy.FuzzyOutcome;
import com.tomtom.online.sdk.search.fuzzy.FuzzyOutcomeCallback;
import com.tomtom.online.sdk.search.fuzzy.FuzzySearchDetails;
import com.tomtom.online.sdk.search.fuzzy.FuzzySearchEngineDescriptor;
import com.tomtom.online.sdk.search.fuzzy.FuzzySearchSpecification;

import java.io.Serializable;

import timber.log.Timber;

public class SearchFragmentPresenter implements SearchPresenter, LocationUpdateListener, Contextable {

    protected final static String LAST_SEARCH_QUERY_BUNDLE_KEY = "LAST_SEARCH_QUERY_BUNDLE_KEY";
    public static final int STANDARD_RADIUS = 30 * 1000; //30 km

    protected SearchView searchView;
    protected ImmutableList<FuzzySearchDetails> lastSearchResult;

    private SearchFragmentService searchFragmentService;
    private LocationProvider locationProvider;


    public SearchFragmentPresenter(SearchView searchView) {
        Timber.d("SearchFragmentPresenter()");
        this.searchView = searchView;
        this.searchFragmentService = new SearchFragmentService();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Serializable serializable = savedInstanceState.getSerializable(LAST_SEARCH_QUERY_BUNDLE_KEY);
            if (serializable instanceof ImmutableList) {
                lastSearchResult = (ImmutableList<FuzzySearchDetails>) serializable;
            }
            if (lastSearchResult != null) {
                searchView.updateSearchResults(lastSearchResult);
            }
        }
    }

    @Override
    public void onCreate(Context context) {
        Timber.d("onCreate(%s)", context);
        locationProvider = new LocationProvider(getContext());
    }

    @Override
    public void onResume() {
        Timber.d("onResume()");
        //tag::doc_location_source_activation[]
        locationProvider.addLocationUpdateListener(this);
        locationProvider.activateLocationSource();
        //end::doc_location_source_activation[]
    }

    @Override
    public void onPause() {
        Timber.d("onPause()");
        //tag::doc_location_source_deactivation[]
        locationProvider.deactivateLocationSource();
        //end::doc_location_source_deactivation[]
    }

    @Override
    public void performSearch(String text) {
        Timber.d("performSearch(): %s", text);

        if (TextUtils.isEmpty(text)) {
            return;
        }

        performSearch(createSimpleQuery(text));
    }

    @Override
    public void performSearch(String query, String lang) {
        Timber.d("performSearch(): %s", query);

        if (TextUtils.isEmpty(query)) {
            return;
        }

        performSearch(createSimpleQuery(query, lang));
    }

    @Override
    public void performSearchWithPosition(String text) {
        Timber.d(";performSearchWithPosition(): %s", text);

        if (TextUtils.isEmpty(text)) {
            return;
        }

        performSearch(createQueryWithPosition(text, locationProvider.getLastKnownPosition()));
    }


    protected void searchFinished() {
        Timber.d("searchFinished()");
        enableSearchUI();
        searchView.getSearchProgressBar().setVisibility(View.GONE);
    }

    protected void performSearch(FuzzySearchSpecification specification) {
        disableSearchUI();
        cancelPreviousSearch();

        performSearchWithoutBlockingUi(specification);
    }

    protected void performSearchWithoutBlockingUi(FuzzySearchSpecification specification) {
        Timber.d("performSearch %s", specification);
        searchView.getSearchProgressBar().setVisibility(View.VISIBLE);

        lastSearchResult = null;

        searchFragmentService.performSearch(specification, fuzzyOutcomeCallback);
    }

    //tag::doc_create_search_callback[]
    private FuzzyOutcomeCallback fuzzyOutcomeCallback = new FuzzyOutcomeCallback() {
        @Override
        public void onSuccess(@NonNull FuzzyOutcome fuzzyOutcome) {
            Timber.d("onSuccess");
            lastSearchResult = ImmutableList.copyOf(fuzzyOutcome.getFuzzyDetailsList());
            searchView.updateSearchResults(ImmutableList.copyOf(fuzzyOutcome.getFuzzyDetailsList()));
            searchFinished();
        }

        @Override
        public void onError(@NonNull SearchException error) {
            searchView.showSearchFailedMessage(error.getMessage());
            searchView.updateSearchResults(ImmutableList.of());
            searchFinished();
        }
    };
    //end::doc_create_search_callback[]

    protected SearchFragmentService getSearchFragmentService() {
        return searchFragmentService;
    }

    protected FuzzySearchSpecification createSimpleQuery(String text) {
        Timber.d("createSimpleQuery(): %s", text);
        //tag::doc_create_basic_specification[]
        return new FuzzySearchSpecification.Builder(text).build();
        //end::doc_create_basic_specification[]
    }

    protected FuzzySearchSpecification createSimpleQuery(String text, String lang) {
        Timber.d("createSimpleQuery(): %s, %s", text, lang);
        //tag::doc_create_simple_specification_with_lang[]
        FuzzySearchEngineDescriptor fuzzySearchEngineDescriptor = new FuzzySearchEngineDescriptor.Builder()
                .language(lang)
                .build();
        return new FuzzySearchSpecification.Builder(text)
                .searchEngineDescriptor(fuzzySearchEngineDescriptor)
                .build();
        //end::doc_create_simple_specification_with_lang[]
    }

    protected FuzzySearchSpecification createQueryWithPosition(String text, LatLng position) {
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }

        //tag::doc_create_specification_with_position[]
        FuzzyLocationDescriptor fuzzyLocationDescriptor = new FuzzyLocationDescriptor.Builder()
                .positionBias(new LatLngBias(position, STANDARD_RADIUS))
                .build();
        return new FuzzySearchSpecification.Builder(text)
                .locationDescriptor(fuzzyLocationDescriptor)
                .build();
        //end::doc_create_specification_with_position[]
    }

    @Override
    public LatLng getLastKnownPosition() {
        return locationProvider.getLastKnownPosition();
    }

    @Override
    public void enableSearchUI() {
        searchView.enableToggleButtons();
        searchView.enableInputField();
    }

    @Override
    public void disableSearchUI() {
        searchView.disableToggleButtons();
        searchView.disableInputField();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (lastSearchResult != null) {
            outState.putSerializable(LAST_SEARCH_QUERY_BUNDLE_KEY, lastSearchResult);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Timber.d("onLocationChanged: "+location);
        searchView.refreshSearchResults();
    }

    public void cancelPreviousSearch() {
        searchFragmentService.cancelSearchIfRunning();
    }

    @Override
    public Context getContext() {
        return searchView.getContext();
    }

}