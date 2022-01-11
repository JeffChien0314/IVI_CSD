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
package com.fxc.ev.launcher.utils.formatter;

import com.google.common.base.Joiner;
import com.tomtom.online.sdk.search.fuzzy.FuzzySearchDetails;

public class SearchResultGuavaFormatter implements SearchResultFormatter {

    public static final String SEPARATOR = " ";
    public static final String UNIT = "km";

    @Override
    public String formatTitleWithDistance(FuzzySearchDetails fuzzySearchDetails, double distance) {
        return Joiner.on(SEPARATOR)
                .skipNulls()
                .join(new String[]{getDistanceInBrackets(distance), formatTitle(fuzzySearchDetails)})
                .trim();
    }

    private String getDistanceInBrackets(double distance) {
        return String.format("( %.1f %s )", distance, UNIT);
    }

    @Override
    public String formatTitle(FuzzySearchDetails fuzzySearchDetails) {
        return Joiner.on(SEPARATOR)
                .skipNulls()
                .join(new String[]{
                        fuzzySearchDetails.getPoi().getName(),
                        fuzzySearchDetails.getAddress().getFreeFormAddress(),
                        fuzzySearchDetails.getAddress().getCountry()
                })
                .trim();
    }
}