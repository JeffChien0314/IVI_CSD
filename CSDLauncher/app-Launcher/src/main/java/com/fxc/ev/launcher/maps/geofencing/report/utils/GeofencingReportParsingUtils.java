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
package com.fxc.ev.launcher.maps.geofencing.report.utils;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.tomtom.online.sdk.geofencing.report.FenceDetails;

import java.util.List;

public class GeofencingReportParsingUtils {

    static String fenceDetailsToString(List<FenceDetails> fences) {
        List<String> trimmed = Lists.transform(fences, input -> String.format("\"%1$s\"", input.getFence().getName()));
        return Joiner.on(", ").join(trimmed);
    }
}
