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

package com.fxc.ev.launcher.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/** Handles permissions for the application's activities that require them. */
public class PermissionsManager {
  private static final int PERMISSIONS_REQUEST = 0;
  private static final String TAG = "PermissionsManager";
  private String errorText;
  private final Activity activity;

  /**
   * Constructor, binds to the activity
   *
   * @param activity
   */
  public PermissionsManager(Activity activity) {
    this.activity = activity;
  }

  /**
   * Will attempt to acquire required permissions. If permissions already are granted, this method
   * returns true immediately and no other course of action is required. If permissions are not yet
   * granted, the user will be prompted to grant them. In this case a callback will be made to the
   * activity's onRequestPermissionsResult method as per the Android framework usual means. That
   * callback should trigger a call to processRequestPermissionsResult to process the result in this
   * class.
   *
   * @return true if all permissions granted, false if your activity should expect a callback to
   *     onRequestPermissionsResult
   */
  public boolean acquirePermissions() {
    final List<String> missingPermissions = new ArrayList<>();
    for (String permission : getRequiredPermissions()) {
      if (ContextCompat.checkSelfPermission(activity, permission)
          != PackageManager.PERMISSION_GRANTED) {
        missingPermissions.add(permission);
      }
    }

    if (missingPermissions.isEmpty()) {
      return true;
    }
    ActivityCompat.requestPermissions(
        activity, missingPermissions.toArray(new String[0]), PERMISSIONS_REQUEST);
    return false;
  }

  /**
   * Process the callback from onRequestPermissionsResult from your activity
   *
   * @param requestCode
   * @param permissions
   * @param grantResults
   * @return true if all permissions are granted, false otherwise, in which case getErrorText will
   *     return the text of the error
   */
  public boolean processRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    final List<String> notGrantedPermissions = new ArrayList<>();
    for (int i = 0; i < grantResults.length; ++i) {
      if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
        notGrantedPermissions.add(permissions[i]);
      }
    }

    if (requestCode == PERMISSIONS_REQUEST && notGrantedPermissions.isEmpty()) {
      return true;
    } else {
      errorText =
          "Permissions: "
              + notGrantedPermissions
              + " were not granted; driving assistance/position is unavailable.";
    }

    return false;
  }

  /** @return Text of the last error, or null if there has not yet been an error */
  public String getErrorText() {
    return errorText;
  }

  private static List<String> getRequiredPermissions() {
    // positioning extension
    final List<String> permissions = new ArrayList<>();
    permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
    permissions.add(Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS);

    // driving assistance / navigation service
    permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    permissions.add(Manifest.permission.INTERNET);
    permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
    return permissions;
  }
}
