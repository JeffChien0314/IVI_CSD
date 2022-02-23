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

package com.fxc.ev.launcher.utils.view;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.fxc.ev.launcher.R;
import com.tomtom.navkit2.drivingassistance.DrivingAssistanceClientlib;
import com.tomtom.navkit2.drivingassistance.Position;
import com.tomtom.navkit2.drivingassistance.RoadElement;
import com.tomtom.navkit2.place.Address;
import com.tomtom.navkit2.place.Place;
import com.tomtom.navkit2.place.RoadShieldList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class SpeedBubbleView extends LinearLayout {
  private static final String UNITED_KINGDOM_ISO_COUNTRY_CODE = "GBR";
  private static final String UNITED_STATES_ISO_COUNTRY_CODE = "USA";
  private static final String CANADA_ISO_COUNTRY_CODE = "CAN";
  private static final String LIBERIA_ISO_COUNTRY_CODE = "LBR";
  private static final String MYANMAR_ISO_COUNTRY_CODE = "MMR";
  private static final Set<String> COUNTRY_CODES_USING_MILES_PER_HOUR =
      new HashSet<>(
          Arrays.asList(
              UNITED_KINGDOM_ISO_COUNTRY_CODE,
              LIBERIA_ISO_COUNTRY_CODE,
              MYANMAR_ISO_COUNTRY_CODE,
              UNITED_STATES_ISO_COUNTRY_CODE));

  private String lastValidCountryCode;

  private final int speedLimitTextTopPadding;
  private final int primaryContainerPaddingEnd;

  private final LinearLayout rootContainer;
  private final TextView streetNameOverflow;
  private final LinearLayout primaryContainer;
  private final TextView speedLimit;
  private final TextView currentSpeedValue;
  private final TextView currentSpeedUnits;
  private final TextView roadNumbers;

  public SpeedBubbleView(Context context) {
    this(context, null);
  }

  public SpeedBubbleView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public SpeedBubbleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    speedLimitTextTopPadding =
        getResources().getDimensionPixelSize(R.dimen.speedBubbleSpeedLimitTextTopPadding);
    primaryContainerPaddingEnd =
        getResources().getDimensionPixelSize(R.dimen.speedBubblePrimaryContainerPaddingEnd);

    final View root = inflate(context, R.layout.speedbubble, this);

    rootContainer = root.findViewById(R.id.speedBubbleRootContainer);
    streetNameOverflow = root.findViewById(R.id.speedBubbleOverflowStreetName);
    primaryContainer = root.findViewById(R.id.speedBubblePrimaryContainer);
    speedLimit = root.findViewById(R.id.speedBubbleSpeedLimit);
    currentSpeedValue = root.findViewById(R.id.speedBubbleCurrentSpeedValue);
    currentSpeedUnits = root.findViewById(R.id.speedBubbleCurrentSpeedUnits);
    roadNumbers = root.findViewById(R.id.speedBubbleRoadNumbers);

    rootContainer.setVisibility(View.GONE);
  }

  private static int metersPerHourToCountrySpecificSpeedUnit(
      int metersPerHour, boolean isImperial) {
    return isImperial
        ? metersPerHourToMilesPerHour(metersPerHour)
        : metersPerHourToKilometersPerHour(metersPerHour);
  }

  private static int metersPerHourToMilesPerHour(int metersPerHour) {
    return Math.round(metersPerHour / 1609.344f);
  }

  private static int metersPerHourToKilometersPerHour(int metersPerHour) {
    return Math.round(metersPerHour / 1000.0f);
  }

  private static void validateCountryCode(String countryCode) {
    if (TextUtils.isEmpty(countryCode)) {
      throw new IllegalArgumentException("countryCode must not be empty");
    }
  }

  private static boolean hasSquareSpeedLimitShield(String countryCode) {
    validateCountryCode(countryCode);

    return countryCode.equals(UNITED_STATES_ISO_COUNTRY_CODE)
        || countryCode.equals(CANADA_ISO_COUNTRY_CODE);
  }

  private static int getContainerBackgroundResourceId(String countryCode) {
    validateCountryCode(countryCode);

    return hasSquareSpeedLimitShield(countryCode)
        ? R.drawable.nk2_bubble_primary_square_background
        : R.drawable.nk2_bubble_primary_default_background;
  }

  private static int getSpeedLimitBackgroundResourceId(String countryCode) {
    validateCountryCode(countryCode);

    final int backgroundId;

    if (countryCode.equals(UNITED_STATES_ISO_COUNTRY_CODE)) {
      backgroundId = R.drawable.speed_limit_shield_us;
    } else if (countryCode.equals(CANADA_ISO_COUNTRY_CODE)) {
      backgroundId = R.drawable.speed_limit_shield_can;
    } else {
      backgroundId = R.drawable.speed_limit_shield_standard;
    }

    return backgroundId;
  }

  private static String getFormattedSpeedLimit(
      Resources resources, int speedLimitMetersPerHour, String countryCode) {
    validateCountryCode(countryCode);

    final String speedLimit;

    if (speedLimitMetersPerHour == DrivingAssistanceClientlib.UNLIMITED_SPEED_LIMIT) {
      speedLimit = "";
    } else {
      final boolean isImperial = COUNTRY_CODES_USING_MILES_PER_HOUR.contains(countryCode);
      final int speedLimitForCountry =
          metersPerHourToCountrySpecificSpeedUnit(speedLimitMetersPerHour, isImperial);
      speedLimit = String.valueOf(speedLimitForCountry);
    }
    return speedLimit;
  }

  private static int getCurrentSpeedForCountry(
      int currentSpeedInMetersPerHour, String countryCode) {
    validateCountryCode(countryCode);

    final boolean isImperial = COUNTRY_CODES_USING_MILES_PER_HOUR.contains(countryCode);
    return metersPerHourToCountrySpecificSpeedUnit(currentSpeedInMetersPerHour, isImperial);
  }

  private static String getSpeedUnitsForCountry(Resources res, String countryCode) {
    validateCountryCode(countryCode);

    final boolean isImperial = COUNTRY_CODES_USING_MILES_PER_HOUR.contains(countryCode);
    return res.getString(isImperial ? R.string.miles_per_hour : R.string.km_per_hour);
  }

  private static String getFormattedRoadShields(RoadShieldList roadShields) {
    final List<String> roadNumbersList = new ArrayList<>();

    for (int i = 0; i < roadShields.size(); ++i) {
      roadNumbersList.add(roadShields.get(i).fullRoadNumber());
    }

    final String roadNumberDelimiter = " ";
    return TextUtils.join(roadNumberDelimiter, roadNumbersList);
  }

  private void adjustPadding(String countryCode, boolean speedLimitVisible) {
    final int speedLimitTopTextPadding =
        hasSquareSpeedLimitShield(countryCode) ? speedLimitTextTopPadding : 0;

    speedLimit.setPadding(
        speedLimit.getPaddingLeft(),
        speedLimitTopTextPadding,
        speedLimit.getPaddingRight(),
        speedLimit.getPaddingBottom());

    final int primaryContainerLeft = speedLimitVisible ? 0 : primaryContainerPaddingEnd;
    final int primaryContainerRight = primaryContainerPaddingEnd;

    primaryContainer.setPadding(
        primaryContainerLeft,
        primaryContainer.getPaddingTop(),
        primaryContainerRight,
        primaryContainer.getPaddingBottom());
  }

  public void update(Position position, RoadElement roadElement) {
    final int currentVisibility;

    final Place place = position.place();
    final Address address = place.address();
    final String countryCode = address.countryCode();

    if (!TextUtils.isEmpty(countryCode)) {
      lastValidCountryCode = countryCode;
    }

    if (!TextUtils.isEmpty(lastValidCountryCode)) {
      primaryContainer.setBackgroundResource(
          getContainerBackgroundResourceId(lastValidCountryCode));

      final int speedLimitMetersPerHour = roadElement.speedLimitInMetersPerHour();

      final boolean showSpeedLimit =
          ((speedLimitMetersPerHour > 0)
              && (speedLimitMetersPerHour != DrivingAssistanceClientlib.UNLIMITED_SPEED_LIMIT));

      speedLimit.setText(
          getFormattedSpeedLimit(getResources(), speedLimitMetersPerHour, lastValidCountryCode));
      speedLimit.setBackgroundResource(getSpeedLimitBackgroundResourceId(lastValidCountryCode));
      speedLimit.setVisibility(showSpeedLimit ? View.VISIBLE : View.GONE);

      currentSpeedValue.setText(
          String.valueOf(
              getCurrentSpeedForCountry(position.speedInMetersPerHour(), lastValidCountryCode)));
      currentSpeedUnits.setText(getSpeedUnitsForCountry(getResources(), lastValidCountryCode));

      final String formattedRoadShields = getFormattedRoadShields(place.roadShields());
      roadNumbers.setText(formattedRoadShields);
      roadNumbers.setVisibility(formattedRoadShields.isEmpty() ? View.GONE : View.VISIBLE);

      adjustPadding(lastValidCountryCode, showSpeedLimit);

      final String streetName = address.streetName();
      streetNameOverflow.setText(streetName);
      streetNameOverflow.setVisibility(streetName.isEmpty() ? View.INVISIBLE : View.VISIBLE);

      currentVisibility = View.VISIBLE;
    } else {
      currentVisibility = View.GONE;
    }

    rootContainer.setVisibility(currentVisibility);
  }
}
