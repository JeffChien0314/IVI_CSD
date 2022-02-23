/*
 * Copyright (C) 2019 TomTom NV. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom NV and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * license agreement between you and TomTom NV. If you are the licensee, you are only permitted
 * to use this software in accordance with the terms of your license agreement. If you are
 * not the licensee, you are not authorized to use this software in any manner and should
 * immediately return or destroy it.
 */

package com.fxc.ev.launcher.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Formats Eta based on a Calendar representing Eta. Format is of the form "HH:MM (+TZO)" where TZO
 * is the timezone difference from the device's default (i.e. current) timezone to the Eta's
 * timezone. For example, if the Eta Calendar represented 16:30 CET and the device's timezone was
 * the west coast of the US, the output would be "16:30 (+9)". This class handles fractional
 * timezones too, and displays the fractional part as minutes, e.g. "16:30 (-5:45)".
 */
public class EtaFormatter {

    public static String toString(final GregorianCalendar etaCalendar) {
        return toString(etaCalendar, new GregorianCalendar());
    }

    /**
     * Override which allows the defaultCalendar to be overridden, for testing.
     */
    public static String toString(
            final GregorianCalendar etaCalendar, final GregorianCalendar localDeviceCalendar) {
        final StringBuilder sb = new StringBuilder().append(String.format("%02d:%02d", etaCalendar.get(Calendar.HOUR_OF_DAY), etaCalendar.get(Calendar.MINUTE)));

        final int etaTimeZoneOffset =
                etaCalendar.getTimeZone().getOffset(etaCalendar.getTimeInMillis());
        final int localTimeZoneOffset =
                localDeviceCalendar.getTimeZone().getOffset(localDeviceCalendar.getTimeInMillis());
        final int destinationTimeZoneOffsetFromLocalMillis = etaTimeZoneOffset - localTimeZoneOffset;

        // Append the time zone offset if it is not zero
        if (destinationTimeZoneOffsetFromLocalMillis != 0) {
            sb.append(" (");
            if (destinationTimeZoneOffsetFromLocalMillis > 0) {
                sb.append("+");
            } else {
                // We have to explicitly handle the negative case too here, because we split the offset into
                // whole hours
                // and minutes below, and in the case where the whole hours are zero, the minus sign is lost
                sb.append("-");
            }

            // Append the time zone offset hour as an integer
            // Use absolute value here such that we handle the minus sign above and every case is then the
            // same
            final float absoluteDestinationOffsetFromLocalHours =
                    Math.abs((float) destinationTimeZoneOffsetFromLocalMillis / (1000 * 60 * 60));
            // Truncate by casting to an int to get whole hours
            final int absoluteDestinationOffsetFromLocalHoursInt =
                    (int) absoluteDestinationOffsetFromLocalHours;
            sb.append(absoluteDestinationOffsetFromLocalHoursInt);

            // Turn any fractional timezones into minutes
            final float fraction =
                    absoluteDestinationOffsetFromLocalHours - absoluteDestinationOffsetFromLocalHoursInt;
            if (fraction != 0.0f) {
                final int fractionInMinutes = (int) (60 * fraction);
                sb.append(":").append(fractionInMinutes);
            }

            sb.append(")");
        }

        return sb.toString();
    }
}
