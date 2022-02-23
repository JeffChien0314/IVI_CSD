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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class DistanceConversions {

  private static final double FEET_PER_MILE = 5280.0;
  private static final double YARDS_PER_MILE = 1760.0;
  private static final double METERS_PER_MILE = 1609.344;

  enum DistanceFormat {
    KILOMETERS_AND_METERS,
    MILES_AND_FEET,
    MILES_AND_YARDS
  }

  public static class FormattedDistance {
    public String distance;
    public String unit;

    FormattedDistance(String distance, String unit) {
      this.distance = distance;
      this.unit = unit;
    }

    FormattedDistance() {
      this.distance = "";
      this.unit = "";
    }
  }

  private static final Set<String> COUNTRY_CODES_USING_MILES_AND_YARDS =
      new HashSet<>(Arrays.asList("GBR"));

  private static final Set<String> COUNTRY_CODES_USING_MILES_AND_FEET =
      new HashSet<>(Arrays.asList("USA", "PRI"));

  private static DistanceFormat distanceFormatFromCountryCode(String countryCode) {
    if (COUNTRY_CODES_USING_MILES_AND_FEET.contains(countryCode)) {
      return DistanceFormat.MILES_AND_FEET;
    } else if (COUNTRY_CODES_USING_MILES_AND_YARDS.contains(countryCode)) {
      return DistanceFormat.MILES_AND_YARDS;
    } else {
      return DistanceFormat.KILOMETERS_AND_METERS;
    }
  }

  public static FormattedDistance convert(int distanceInMeters, String countryCode) {
    FormattedDistance result;
    switch (distanceFormatFromCountryCode(countryCode)) {
      case MILES_AND_FEET:
        {
          final double distanceInMiles = distanceInMeters / METERS_PER_MILE;
          final int completeMiles = (int) (distanceInMiles);
          final double fraction = distanceInMiles - (double) completeMiles;
          final double fractionInFeet = (fraction * FEET_PER_MILE);
          if (completeMiles == 0 && fractionInFeet < 30.0) {
            result = new FormattedDistance();
          } else if (completeMiles == 0 && fractionInFeet < 500.0) {
            final int fractionInFeetRounded = (int) Math.round(fractionInFeet / 10.0) * 10;
            result =
                new FormattedDistance(
                    String.format(Locale.getDefault(), "%d", fractionInFeetRounded), "ft");
          } else if (completeMiles == 0 && fractionInFeet < 950) {
            final int fractionInFeetRounded = (int) Math.round(fractionInFeet / 100.0) * 100;
            result =
                new FormattedDistance(
                    String.format(Locale.getDefault(), "%d", fractionInFeetRounded), "ft");
          } else if (completeMiles < 10) {
            final double roundedMiles = Math.round(distanceInMiles / 0.1) * 0.1;
            result =
                new FormattedDistance(
                    String.format(Locale.getDefault(), "%.1f", roundedMiles), "mi");
          } else {
            final int roundedMiles = (int) Math.round(distanceInMiles);
            result = new FormattedDistance(String.format("%d", roundedMiles), "mi");
          }
        }
        break;
      case MILES_AND_YARDS:
        {
          final double distanceInMiles = distanceInMeters / METERS_PER_MILE;
          final int completeMiles = (int) (distanceInMiles);
          final double fraction = distanceInMiles - (double) completeMiles;
          final double fractionInYards = (fraction * YARDS_PER_MILE);
          if (completeMiles == 0 && fractionInYards < 10.0) {
            result = new FormattedDistance();
          } else if (completeMiles == 0 && fractionInYards < 500.0) {
            final int fractionInYardsRounded = (int) Math.round(fractionInYards / 10) * 10;
            result = new FormattedDistance(String.format("%d", fractionInYardsRounded), "yd");
          } else if (completeMiles == 0 && fractionInYards < 850.0) {
            final int fractionInYardsRounded = (int) Math.round(fractionInYards / 100) * 100;
            result = new FormattedDistance(String.format("%d", fractionInYardsRounded), "yd");
          } else if (completeMiles < 10) {
            final double roundedMiles = Math.round(distanceInMiles / 0.1) * 0.1;
            result = new FormattedDistance(String.format("%.1f", roundedMiles), "mi");
          } else {
            final int roundedMiles = (int) Math.round(distanceInMiles);
            result = new FormattedDistance(String.format("%d", roundedMiles), "mi");
          }
        }
        break;
      default:
        {
          if (distanceInMeters < 10.0) {
            result = new FormattedDistance();
          } else if (distanceInMeters < 500.0) {
            final int roundedMeters = (int) Math.round(distanceInMeters / 10.0) * 10;
            result = new FormattedDistance(String.format("%d", roundedMeters), "m");
          } else if (distanceInMeters < 950.0) {
            // Using 950m to avoid displaying 1000m after rounding to nearest 100
            final int roundedMeters = (int) Math.round(distanceInMeters / 100.0) * 100;
            result = new FormattedDistance(String.format("%d", roundedMeters), "m");
          } else if (distanceInMeters < 10000.0) {
            final double roundedKilometers = Math.round(distanceInMeters / 100.0) * 0.1;
            result = new FormattedDistance(String.format("%.1f", roundedKilometers), "km");
          } else {
            final int roundedKilometers = (int) Math.round(distanceInMeters / 1000.0);
            result = new FormattedDistance(String.format("%d", roundedKilometers), "km");
          }
        }
    }
    return result;
  }
}
