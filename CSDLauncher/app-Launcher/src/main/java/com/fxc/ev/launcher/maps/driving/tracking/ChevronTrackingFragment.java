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
package com.fxc.ev.launcher.maps.driving.tracking;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.maps.driving.AbstractTrackingFragment;
import com.fxc.ev.launcher.utils.views.OptionsButtonsView;
import com.tomtom.online.sdk.common.location.LatLng;

public class ChevronTrackingFragment extends AbstractTrackingFragment<ChevronTrackingPresenter> {
    private LatLng origin;
    private LatLng destination;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            origin = (LatLng) bundle.getSerializable("origin");
            destination = (LatLng) bundle.getSerializable("destination");
        }
    }

    @Override
    public void onChange(boolean[] oldValues, boolean[] newValues) {
        if (newValues[0]) {
            presenter.startTracking();
        } else if (newValues[1]) {
            presenter.stopTracking();
        }
        optionsView.selectItem(0, true);
    }

    @Override
    protected ChevronTrackingPresenter createPresenter() {
        return new ChevronTrackingPresenter();
    }

    @Override
    protected void onOptionsButtonsView(final OptionsButtonsView view) {

        view.addOption(R.string.chevron_tracking_start);
        view.addOption(R.string.chevron_tracking_stop);

        view.selectItem(0, true);
    }

    @Override
    protected boolean isDescriptionVisible() {
        return false;
    }

    public LatLng getOrigin() {
        return origin;
    }

    public LatLng getDestination() {
        return destination;
    }
}
