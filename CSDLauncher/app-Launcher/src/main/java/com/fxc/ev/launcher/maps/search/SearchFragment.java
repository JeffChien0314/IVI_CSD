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
package com.fxc.ev.launcher.maps.search;

import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.maps.uiextensions.MapUiExtensionsFragment;
import com.fxc.ev.launcher.utils.formatter.SearchResultFormatter;
import com.fxc.ev.launcher.utils.formatter.SearchResultGuavaFormatter;
import com.fxc.ev.launcher.utils.views.RadioModifierView;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.common.util.DistanceCalculator;
import com.tomtom.online.sdk.map.TomtomMap;
import com.tomtom.online.sdk.search.extensions.SearchServiceConnectionCallback;
import com.tomtom.online.sdk.search.extensions.SearchServiceManager;
import com.tomtom.online.sdk.search.fuzzy.FuzzySearchDetails;

import timber.log.Timber;

public class SearchFragment extends SearchFunctionalFragment implements SearchView,
        RadioModifierView.RadioModifierListener {

    public static final String PASSED_TEXT = "PASSED_TEXT";
    public static final String SEARCH_MODIFIERS_KEY = "CURRENT_SEARCH_MODIFIER";
    public static final String SEARCH_REQUEST_CODE_KEY = "SEARCH_REQUEST_CODE";
    public static final String ARE_LIST_ITEMS_CLICKABLE_KEY = "ARE_LIST_ITEMS_CLICKABLE";

    protected ImmutableList<FuzzySearchDetails> searchResults = ImmutableList.of();
    protected SearchPresenter searchPresenter; //Available for category search

    protected EditText searchTextView;
    protected RadioModifierView searchModifiersView;

    private SearchAdapter searchAdapter;
    private ListView searchResultsListView;

    private int searchRequestCode = 0;
    private boolean areListItemsClickable = false;

    private ServiceConnection searchServiceConnection;
    private ProgressBar searchProgressBar;
    private TomtomMap tomtomMap;

    public SearchFragment() {
        searchPresenter = new SearchFragmentPresenter(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchPresenter.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("onCreateView()");
        final View view = inflater.inflate(R.layout.search_fragment, container, false);

        initSearchList(view);
        initSearchFieldView(view);
        initSearchModifiersView(view);
        searchProgressBar = view.findViewById(R.id.search_progress_bar);

        restoreArguments();
        restoreSavedInstanceState(savedInstanceState);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchPresenter.onCreate(getContext());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        Timber.d("onResume()");
        super.onResume();
        searchPresenter.onResume();
        startAndBindToSearchService();
        hideSoftwareKeyboard(searchTextView);

        /*MapView mapView = getActivity().findViewById(R.id.map_view);
        mapView.addOnMapReadyCallback(tomtomMap -> {
            this.tomtomMap = tomtomMap;
        });*/
    }

    protected void startAndBindToSearchService() {
        //tag::doc_search_service_binding[]
        searchServiceConnection = SearchServiceManager.createAndBind(getContext(),
                searchServiceConnectionCallback());
        //end::doc_search_service_binding[]
    }

    private SearchServiceConnectionCallback searchServiceConnectionCallback() {
        SearchFragmentPresenter presenter = (SearchFragmentPresenter) searchPresenter;
        return presenter.getSearchFragmentService();
    }

    @Override
    public void onPause() {
        super.onPause();
        searchPresenter.onPause();
        hideSoftwareKeyboard(searchTextView);
        //tag::doc_search_service_unbinding[]
        SearchServiceManager.unbind(getContext(), searchServiceConnection);
        //end::doc_search_service_unbinding[]
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        searchPresenter.onSaveInstanceState(outState);

        outState.putInt(SEARCH_REQUEST_CODE_KEY, searchRequestCode);
        outState.putBoolean(ARE_LIST_ITEMS_CLICKABLE_KEY, areListItemsClickable);

        if (searchModifiersView != null) {
            RadioModifierView.ModifierButton selectedButton = searchModifiersView.getSelectedItem();
            if (selectedButton != null) {
                outState.putSerializable(SEARCH_MODIFIERS_KEY, searchModifiersView.getSelectedItem());
            }
        }
    }

    private void initSearchList(View view) {
        searchResultsListView = view.findViewById(R.id.search_results_list);

        searchAdapter = new SearchAdapter(getContext());
        searchResultsListView.setAdapter(searchAdapter);

        searchResultsListView.setOnItemClickListener(searchListItemClicked);
    }

    protected void initSearchModifiersView(View view) {
        searchModifiersView = view.findViewById(R.id.search_modifier_tabs);
        searchModifiersView.selectItem(RadioModifierView.ModifierButton.LEFT);
        searchModifiersView.setRadioNames(getString(R.string.global_search), getString(R.string.near_me_search));
        searchModifiersView.setSearchModifiersLViewListener(this);
        searchModifiersView.setEnabled(true);
    }

    private void initSearchFieldView(View view) {
        searchTextView = view.findViewById(R.id.search_text);

        if (getArguments() != null) {
            setSearchFieldText(getArguments().getString(PASSED_TEXT));
        }

        searchTextView.setOnEditorActionListener(searchTextViewEditorActionChanged);
        searchTextView.addTextChangedListener(emptySearchBoxWatcher);
    }

    private void restoreArguments() {
        Bundle args = getArguments();
        if (args == null) {
            return;
        }

        searchRequestCode = args.getInt(SEARCH_REQUEST_CODE_KEY, 0);
        areListItemsClickable = args.getBoolean(ARE_LIST_ITEMS_CLICKABLE_KEY, false);
    }

    private void restoreSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        searchRequestCode = savedInstanceState.getInt(SEARCH_REQUEST_CODE_KEY, 0);
        areListItemsClickable = savedInstanceState.getBoolean(ARE_LIST_ITEMS_CLICKABLE_KEY, false);

        RadioModifierView.ModifierButton selectedButton = (RadioModifierView.ModifierButton) savedInstanceState.getSerializable(SEARCH_MODIFIERS_KEY);
        if (selectedButton != null) {
            searchModifiersView.selectItem(selectedButton);
        }
    }

    private void setSearchFieldText(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }

        searchTextView.setText(text);
        searchTextView.selectAll();
    }

    @Override
    public String getSearchFieldText() {
        return searchTextView.getText().toString();
    }

    private void hideSoftwareKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private ListView.OnItemClickListener searchListItemClicked = (parent, view, position, id) -> {
        Timber.d("searchListItemClicked");
        /*if (!areListItemsClickable) {
            return;
        }*/
        FuzzySearchDetails fuzzySearchDetails = searchResults.get(position);
        //LatLng latLng = fuzzySearchDetails.getPosition();
        toMapUIExtensionsFragment(fuzzySearchDetails);
        //getActivity().getSupportFragmentManager().popBackStack();
    };

    private void toMapUIExtensionsFragment(FuzzySearchDetails FuzzySearchDetails) {
        MapUiExtensionsFragment mapUiExtensionsFragment = new MapUiExtensionsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("searchDetail", FuzzySearchDetails);
        bundle.putBoolean("showMarker", true);
        bundle.putDouble("zoomLevel", 15);
        mapUiExtensionsFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.functional_example_control_container, mapUiExtensionsFragment, mapUiExtensionsFragment.getFragmentTag())
                .commit();
    }

    private TextView.OnEditorActionListener searchTextViewEditorActionChanged = (v, actionId, event) -> {
        Timber.d("searchTextViewEditorActionChanged");
        if (actionId != EditorInfo.IME_ACTION_SEARCH || getSearchFieldText().isEmpty()) {
            return false;
        }

        return onToggleSelectionActive();
    };

    private boolean onToggleSelectionActive() {
        Timber.d("onToggleSelectionActive");
        if (searchModifiersView.isLeftBtnSelected()) {
            onLeftButtonClicked();
        } else if (searchModifiersView.isRightBtnSelected()) {
            onRightButtonClicked();
        } else if (searchModifiersView.isFirstMiddleBtnSelected()) {
            onFirstMiddleButtonClicked();
        } else if (searchModifiersView.isSecondMiddleBtnSelected()) {
            onSecondMiddleButtonClicked();
        }

        return true;
    }

    private TextWatcher emptySearchBoxWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence currentEntry, int start, int before, int count) {
            Timber.d("emptySearchBoxWatcher.onTextChanged");
            if (currentEntry.toString().trim().length() == 0) {
                searchModifiersView.setEnabled(false);
            } else {
                searchModifiersView.setEnabled(true);
            }


        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
        }
    };

    @Override
    public void enableToggleButtons() {
        searchModifiersView.setEnabled(true);
    }

    @Override
    public void disableToggleButtons() {
        searchModifiersView.setEnabled(false);
    }

    @Override
    public void enableInputField() {
        searchTextView.setEnabled(true);
    }

    @Override
    public void disableInputField() {
        searchTextView.setEnabled(false);
    }

    @Override
    public void showSearchFailedMessage(String message) {

        Toast.makeText(getContext(), R.string.search_failed_toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateSearchResults(ImmutableList<FuzzySearchDetails> results) {
        searchResults = results;
        searchAdapter.refresh();
    }

    @Override
    public void refreshSearchResults() {
        if (searchAdapter != null) {
            searchAdapter.refresh();
        }
    }

    @Override
    public void onLeftButtonClicked() {
        Timber.d("onLeftButtonClicked()");

        if (isValidSearchInput()) {
            return;
        }

        searchPresenter.performSearch(getSearchFieldText());
        hideSoftwareKeyboard(searchTextView);
    }

    @Nullable
    public boolean isValidSearchInput() {
        return Strings.isNullOrEmpty(getSearchFieldText());
    }

    @Override
    public void onRightButtonClicked() {
        Timber.d("onRightButtonClicked()");

        if (isValidSearchInput()) {
            return;
        }

        searchPresenter.performSearchWithPosition(getSearchFieldText());
        hideSoftwareKeyboard(searchTextView);
    }

    @Override
    public void onFirstMiddleButtonClicked() {
        //We do not use this button in that case
    }

    @Override
    public void onSecondMiddleButtonClicked() {
        //We do not use this button in that case
    }

    public ProgressBar getSearchProgressBar() {
        return searchProgressBar;
    }

    private class SearchAdapter extends ArrayAdapter<FuzzySearchDetails> {

        public SearchAdapter(Context context) {
            super(context, R.layout.search_result_item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final FuzzySearchDetails resultItem = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            }

            final TextView title = convertView.findViewById(android.R.id.text1);
            title.setText(formatResults(resultItem));

            return convertView;
        }

        public String formatResults(FuzzySearchDetails fuzzySearchDetails) {
            SearchResultFormatter formatter = new SearchResultGuavaFormatter();

            LatLng itemLocation = fuzzySearchDetails.getPosition();
            LatLng gpsLocation = searchPresenter.getLastKnownPosition();

            if (gpsLocation == null) {
                return getContext().getResources().getString(R.string.search_distance_calculating);
            }

            double distance = DistanceCalculator.calcDistInKilometers(itemLocation, gpsLocation);

            String resultText = formatter.formatTitleWithDistance(fuzzySearchDetails, distance);
            return resultText;
        }

        public void refresh() {
            clear();
            addAll(searchResults);
        }

    }
}