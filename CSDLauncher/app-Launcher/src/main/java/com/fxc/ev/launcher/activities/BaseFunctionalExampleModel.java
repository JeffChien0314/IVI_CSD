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
package com.fxc.ev.launcher.activities;


import com.fxc.ev.launcher.R;

public class BaseFunctionalExampleModel implements FunctionalExampleModel {

    @Override
    public int getPlayableTitle() {
        return 0;
    }

    @Override
    public int getPlayableSubtitle() {
        return 0;
    }

    @Override
    public int[] getCurrentLocationMargins() {
        return new int[]{
                R.dimen.compass_default_margin_start,
                R.dimen.current_location_top,
                R.dimen.current_location_right,
                R.dimen.bigger_location_default_margin_bottom
        };
    }

    @Override
    public int[] getCompassMargins() {
        return COMPASS_CLOSE_TO_BAR;
    }

    public static final int[] COMPASS_UNDER_BAR = new int[]{
            R.dimen.compass_default_margin_start,
            R.dimen.compass_bigger_margin_top,
            R.dimen.compass_default_margin_right,
            R.dimen.compass_default_margin_bottom
    };

    public static final int[] COMPASS_CLOSE_TO_BAR = {
            R.dimen.compass_default_margin_start,
            R.dimen.compass_default_margin_top,
            R.dimen.compass_default_margin_right,
            R.dimen.compass_default_margin_bottom
    };

    public static final int[] CURRENT_LOCATION_CLOSE_TO_BOTTOM = new int[]{
            R.dimen.compass_default_margin_start,
            R.dimen.current_location_top,
            R.dimen.current_location_right,
            R.dimen.current_location_default_margin_bottom
    };
}
