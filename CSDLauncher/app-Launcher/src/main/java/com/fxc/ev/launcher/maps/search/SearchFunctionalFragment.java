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

import androidx.fragment.app.Fragment;

import com.fxc.ev.launcher.activities.FunctionalExampleFragment;

public class SearchFunctionalFragment extends Fragment implements FunctionalExampleFragment {

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onMenuItemSelected() {

    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Override
    public boolean isMapRestored() {
        return false;
    }

    @Override
    public String getFragmentTag() {
        return getClass().getName();
    }

}
