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
package com.fxc.ev.launcher.utils;


import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.fxc.ev.launcher.maps.currentlocation.CurrentLocationFragment;
import com.fxc.ev.launcher.activities.FunctionalExampleFragment;

public final class BackButtonDelegate {

    private final BackButtonDelegateCallback backButtonDelegateCallback;
    FragmentManager mFragmentManager;

    public BackButtonDelegate(final BackButtonDelegateCallback callback, FragmentManager fragmentManager) {
        backButtonDelegateCallback = callback;
        mFragmentManager = fragmentManager;
    }

    public boolean onBackPressed(final DrawerLayout drawer) {
        boolean eventConsumed;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            eventConsumed = true;
        } else if (backButtonDelegateCallback.isManeuversOrSearchFragmentOnTop()) {
            eventConsumed = false;
        } else {
            //backButtonDelegateCallback.exitFromFunctionalExample(new CurrentLocationFragment(), 1000);
            if (mFragmentManager.getBackStackEntryCount() != 0) {
                mFragmentManager.popBackStack();
            } else {
                backButtonDelegateCallback.exitFromFunctionalExample(new CurrentLocationFragment(), 1000);
            }
            eventConsumed = true;
        }

        return eventConsumed;
    }

    public boolean onBackPressed() {
        boolean eventConsumed;
        if (backButtonDelegateCallback.isManeuversOrSearchFragmentOnTop()) {
            eventConsumed = false;
        } else {
            if (mFragmentManager.getBackStackEntryCount() != 0) {
                mFragmentManager.popBackStack();
            } else {
                backButtonDelegateCallback.exitFromFunctionalExample(new CurrentLocationFragment(), 1000);
            }
            eventConsumed = true;
        }
        return eventConsumed;
    }

    public interface BackButtonDelegateCallback {
        boolean exitFromFunctionalExample(final FunctionalExampleFragment nextExample, int itemId);

        boolean isManeuversOrSearchFragmentOnTop();
    }

}
