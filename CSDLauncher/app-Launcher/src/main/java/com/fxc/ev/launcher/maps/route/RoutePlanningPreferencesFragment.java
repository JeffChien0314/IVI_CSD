/*
 * Copyright (C) 2018 TomTom NV. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom NV and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * license agreement between you and TomTom NV. If you are the licensee, you are only permitted
 * to use this software in accordance with the terms of your license agreement. If you are
 * not the licensee, you are not authorized to use this software in any manner and should
 * immediately return or destroy it.
 */

package com.fxc.ev.launcher.maps.route;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.fxc.ev.launcher.R;

public class RoutePlanningPreferencesFragment extends PreferenceFragmentCompat {

  private static final String ALTERNATIVES_PREF = "pref_route_planning_alternatives";

  private final SharedPreferences.OnSharedPreferenceChangeListener changeListener =
      new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
          if (ALTERNATIVES_PREF.equals(s)) {
            Preference preference = findPreference(s);
            preference.setSummary(((ListPreference) preference).getEntry());
          }
        }
      };

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.route_planning_preferences, rootKey);

    ListPreference preference = (ListPreference) findPreference(ALTERNATIVES_PREF);
    final String currentValue = preference.getValue();
    if (currentValue == null) {
      preference.setValue((String) preference.getEntryValues()[0]);
    }
    preference.setSummary(preference.getEntry());
  }

  @Override
  public void onResume() {
    super.onResume();
    getPreferenceManager()
        .getSharedPreferences()
        .registerOnSharedPreferenceChangeListener(changeListener);
  }

  @Override
  public void onPause() {
    getPreferenceManager()
        .getSharedPreferences()
        .unregisterOnSharedPreferenceChangeListener(changeListener);
    super.onPause();
  }
}
