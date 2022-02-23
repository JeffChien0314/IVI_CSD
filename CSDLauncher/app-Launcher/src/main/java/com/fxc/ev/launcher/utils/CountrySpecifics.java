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
import java.util.Set;

public class CountrySpecifics {
  private static final Set<String> COUNTRY_CODES_USING_NON_DEFAULT_BACKGROUND_COLOR =
      new HashSet<>(
          Arrays.asList(
              "AUT", "BLR", "BEL", "FRA", "DEU", "HUN", "IRL", "LUX", "NLD", "NOR", "POL", "PRT",
              "ESP", "UKR", "GBR", "ISR", "SAU", "ARE"));

  static final Set<String> COUNTRY_CODES_WITH_YELLOW_EXIT_PANEL =
      new HashSet<>(Arrays.asList("USA", "CAN"));

  public enum PanelBackgroundVariant {
    DEFAULT,
    ALTERNATIVE
  }

  public static PanelBackgroundVariant getBackgroundVariantForCountryCode(String countryCode) {
    if (COUNTRY_CODES_USING_NON_DEFAULT_BACKGROUND_COLOR.contains(countryCode)) {
      return PanelBackgroundVariant.ALTERNATIVE;
    } else {
      return PanelBackgroundVariant.DEFAULT;
    }
  }
}
