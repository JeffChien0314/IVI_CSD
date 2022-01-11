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
package com.fxc.ev.launcher.maps.driving;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.maps.ExampleFragment;


public abstract class AbstractTrackingFragment<T extends AbstractTrackingPresenter> extends ExampleFragment<T> {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            Log.v("metis", "onViewCreated: savedInstanceState != null");
            presenter.restoreState(savedInstanceState);
        }
        if (isDescriptionVisible()) {
            Log.v("metis", "onViewCreated: isDescriptionVisible");
            showDescription();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        presenter.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    private void showDescription() {
        getInfoBarView().show();
        getInfoBarView().hideLeftIcon();
        getInfoBarView().setLeftText(getResources().getString(R.string.matching_description));
    }

    protected abstract boolean isDescriptionVisible();

}
