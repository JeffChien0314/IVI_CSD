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
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.utils.CountrySpecifics;
import com.tomtom.navkit2.guidance.Instruction;

public class CombinedInstructionPanel extends LinearLayout {
    private static final String TAG = "CombinedInstructionPanel";

    private final ConstraintLayout panelRootView;
    private final ImageView combinedManeuverImageView;

    private final NextInstructionImageHelper nextInstructionImageHelper;

    public CombinedInstructionPanel(Context context) {
        this(context, null);
    }

    public CombinedInstructionPanel(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CombinedInstructionPanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.combined_instruction_panel, this);

        panelRootView = findViewById(R.id.combinedPanelRootContainer);
        combinedManeuverImageView = findViewById(R.id.combinedInstructionManeuverImage);
        nextInstructionImageHelper = new NextInstructionImageHelper();
    }

    public void update(Instruction instruction, String countryCode) {
        boolean primaryContainerShouldBeHidden;

        int panelBackgroundColor =
                getBackgroundColor(CountrySpecifics.getBackgroundVariantForCountryCode(countryCode), getContext());

        ((GradientDrawable) panelRootView.getBackground()).setColor(panelBackgroundColor);

        nextInstructionImageHelper.setManeuverImageForInstruction(
                combinedManeuverImageView, instruction);
    }

    public boolean hasCombinedManeuverImage() {
        // The class's visibility state marks whether the image is available.
        return (combinedManeuverImageView.getVisibility() == VISIBLE);
    }

    private static int getBackgroundColor(
            CountrySpecifics.PanelBackgroundVariant backgroundColor, Context context) {
        if (backgroundColor == CountrySpecifics.PanelBackgroundVariant.ALTERNATIVE) {
            return ContextCompat.getColor(context, R.color.nip_alternative_background);
        } else {
            return ContextCompat.getColor(context, R.color.nip_default_background);
        }
    }
}
