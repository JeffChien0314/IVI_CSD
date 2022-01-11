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
package com.fxc.ev.launcher.maps;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import com.fxc.ev.launcher.R;

import timber.log.Timber;

public class RoutePlanningInProgressDialog extends DialogFragment {

    public static final String OPERATION_IN_PROGRESS_DIALOG_TAG = "OPERATION_IN_PROGRESS";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.dialog_routing_in_progress_msg)
                .setCancelable(false)
                .create();
    }


    public void show(FragmentManager manager) {
        if (manager == null) {
            return;
        }
        removeDialogFragment(manager);
        showWhenNotAdded(manager);
    }

    void showWhenNotAdded(FragmentManager manager) {
        if (!isAdded()) {
            try {
                super.show(manager, OPERATION_IN_PROGRESS_DIALOG_TAG);
            } catch (IllegalStateException e) {
                Timber.d(e, "dialog not displayed problem with support library");
            }
        }
    }

    void removeDialogFragment(FragmentManager manager) {
        final Fragment fragment = manager.findFragmentByTag(OPERATION_IN_PROGRESS_DIALOG_TAG);
        if (fragment != null) {
            getFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    @Override
    public void dismiss() {
        if (getFragmentManager() == null) {
            return;
        }
        if (isAdded()) {
            super.dismiss();
        }
    }
}
