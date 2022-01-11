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

import android.os.Bundle;
import android.widget.Toast;

import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.maps.RoutePlannerFragment;
import com.fxc.ev.launcher.maps.driving.tracking.ChevronTrackingFragment;
import com.fxc.ev.launcher.utils.views.OptionsButtonsView;
import com.tomtom.online.sdk.common.location.LatLng;

public class SearchAlongRouteFragment extends RoutePlannerFragment<SearchAlongRoutePresenter> {
    private LatLng origin;
    private LatLng destination;

    @Override
    protected SearchAlongRoutePresenter createPresenter() {
        return new SearchAlongRoutePresenter(this);
    }

    @Override
    public void onResume() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            origin = (LatLng) bundle.getSerializable("origin");
            destination = (LatLng) bundle.getSerializable("destination");
        } /*else {
            RouteConfigExample routeConfig = new AmsterdamToHaarlemRouteConfig();
            origin = routeConfig.getOrigin();
            destination = routeConfig.getDestination();
        }*/
        super.onResume();
    }

    public LatLng getOrigin() {
        return origin;
    }

    public LatLng getDestination() {
        return destination;
    }

    @Override
    protected void onOptionsButtonsView(OptionsButtonsView view) {
        //view.addOption(R.string.btn_text_car_repair);
        view.addOption(R.string.btn_text_gas_stations);
        view.addOption(R.string.btn_text_ev_stations);

        view.addOption("Start navigation");
    }

    @Override
    public void onChange(boolean[] oldValues, boolean[] newValues) {
        if (newValues[0]) {
            //presenter.performSearch("REPAIR_FACILITY");
            presenter.performSearch("PETROL_STATION");
        } else if (newValues[1]) {
            //presenter.performSearch("PETROL_STATION");
            presenter.performSearch("ELECTRIC_VEHICLE_STATION");
        } else if (newValues[2]) {
            //presenter.performSearch("ELECTRIC_VEHICLE_STATION");
            if (origin != null && destination != null)
                toChevronTrackingFragment(origin, destination);
            else
                Toast.makeText(getContext(), "Don't support navigation", Toast.LENGTH_SHORT).show();
        }
    }

    private void toChevronTrackingFragment(LatLng origin, LatLng destination) {
        ChevronTrackingFragment chevronTrackingFragment = new ChevronTrackingFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("origin", origin);
        bundle.putSerializable("destination", destination);
        chevronTrackingFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.functional_example_control_container, chevronTrackingFragment, chevronTrackingFragment.getFragmentTag())
                .commit();
    }

}
