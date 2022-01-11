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

import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.activities.BaseFunctionalExampleModel;

public class SearchAlongRouteFunctionalExample extends BaseFunctionalExampleModel {

    public int getPlayableTitle() {
        return R.string.search_along_route_title;
    }

    public int getPlayableSubtitle() {
        return R.string.empty_string;
    }

    @Override
    public int[] getCompassMargins() {
        return COMPASS_UNDER_BAR;
    }
}
