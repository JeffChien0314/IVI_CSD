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
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.utils.CountrySpecifics;
import com.fxc.ev.launcher.utils.DistanceConversions;
import com.tomtom.navkit2.guidance.Instruction;
import com.tomtom.navkit2.navigation.common.StringVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NextInstructionMainPanel extends LinearLayout {
    static final Set<String> COUNTRY_CODES_WITH_YELLOW_EXIT_PANEL =
            new HashSet<>(Arrays.asList("USA", "CAN"));

    private ConstraintLayout panelRootView;
    private TextView streetnameAndTowards;
    private TextView roadNumber1TextView;
    private TextView distanceTextView;
    private TextView distanceUnitTextView;
    private ImageView primaryManeuverImageView;
    private TextView exitNumberTextView;
    private GradientDrawable exitBackground;
    private final NextInstructionImageHelper nextInstructionImageHelper;

    public NextInstructionMainPanel(Context context) {
        this(context, null);
    }

    public NextInstructionMainPanel(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NextInstructionMainPanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.next_instruction_panel, this);

        panelRootView = findViewById(R.id.nipRootContainer);
        streetnameAndTowards = findViewById(R.id.nipRoadName);
        roadNumber1TextView = findViewById(R.id.nipRoadNumber);
        distanceTextView = findViewById(R.id.nipDistance);
        distanceUnitTextView = findViewById(R.id.nipDistanceUnit);
        primaryManeuverImageView = findViewById(R.id.nipManeuverImage);
        exitNumberTextView = findViewById(R.id.nipExitNumber);
        exitBackground = (GradientDrawable) exitNumberTextView.getBackground();
        nextInstructionImageHelper = new NextInstructionImageHelper();
    }

    public void update(
            Instruction currentInstruction, int distanceToInstructionInMeters, String countryCode) {

        int panelBackgroundColor =
                backgroundColor(CountrySpecifics.getBackgroundVariantForCountryCode(countryCode));

        ((GradientDrawable) panelRootView.getBackground()).setColor(panelBackgroundColor);

        updateDistance(distanceToInstructionInMeters, countryCode);

        nextInstructionImageHelper.setManeuverImageForInstruction(primaryManeuverImageView, currentInstruction);

        streetnameAndTowards.setText(formatStreetnameAndTowards(currentInstruction));

        //updateRoadShieldsView(formattedRoadShields(currentInstruction.getNextSignificantRoad().getRoadNumbers()));//Jerry@20220317 mark:not display

        //updateExitShieldView(currentInstruction, countryCode);//Jerry@20220315 mark:not display
    }

    private void updateDistance(int distanceToInstructionInMeters, String countryCode) {
        DistanceConversions.FormattedDistance distance =
                DistanceConversions.convert(distanceToInstructionInMeters, countryCode);
        distanceTextView.setText(String.valueOf(distance.distance));
        distanceUnitTextView.setText(distance.unit);
    }

    private void updateRoadShieldsView(String formattedRoadShields) {
        if (formattedRoadShields.isEmpty()) {
            roadNumber1TextView.setVisibility(GONE);
        } else {
            roadNumber1TextView.setText(formattedRoadShields);
            roadNumber1TextView.setVisibility(VISIBLE);
        }
    }

    private void updateExitShieldView(Instruction instruction, String countryCode) {
        if (instruction.getType() == Instruction.Type.EXIT
                && instruction.getExit().getExitNumbers().size() > 0) {
            if (COUNTRY_CODES_WITH_YELLOW_EXIT_PANEL.contains(countryCode)) {
                exitNumberTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                exitBackground.setColor(ContextCompat.getColor(getContext(), R.color.exit_us_background));

            } else {
                exitNumberTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                exitBackground.setColor(
                        ContextCompat.getColor(getContext(), R.color.nip_alternative_background));
            }
            String exitLabel =
                    getResources().getString(R.string.next_instruction_panel_exit_shield_label);
            final String styledExitShieldText =
                    String.format("%s %s", exitLabel, instruction.getExit().getExitNumbers().get(0));
            final SpannableStringBuilder ssBuilder = new SpannableStringBuilder(styledExitShieldText);
            ssBuilder.setSpan(
                    new AbsoluteSizeSpan(20, true),
                    exitLabel.length() + 1,
                    styledExitShieldText.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            exitNumberTextView.setText(ssBuilder);
            exitNumberTextView.setVisibility(VISIBLE);
        } else {
            exitNumberTextView.setVisibility(GONE);
        }
    }

    private int backgroundColor(CountrySpecifics.PanelBackgroundVariant backgroundColor) {
        //Jerry@20220315 add:-->
        /*if (backgroundColor == CountrySpecifics.PanelBackgroundVariant.ALTERNATIVE) {
            return ContextCompat.getColor(getContext(), R.color.nip_alternative_background);
        } else {
            return ContextCompat.getColor(getContext(), R.color.nip_default_background);
        }*/
        return ContextCompat.getColor(getContext(), R.color.nip_default_background_0C0C0C);
        //<--Jerry@20220315 add:
    }

    private String formatStreetnameAndTowards(Instruction instruction) {
        final StringBuilder streetnameAndTowards = new StringBuilder();
        final boolean hasStreetName =
                (instruction.getNextSignificantRoad().getStreetName().length() > 0);
        if (hasStreetName) {
            streetnameAndTowards.append(instruction.getNextSignificantRoad().getStreetName());
        }
        if (instruction.getSignPost().getTowardName().length() > 0) {
            if (hasStreetName) {
                streetnameAndTowards.append(" / ");
            }
            streetnameAndTowards.append(instruction.getSignPost().getTowardName());
        }
        return streetnameAndTowards.toString();
    }

    private String formattedRoadShields(StringVector roadShields) {
        final List<String> roadShieldRoadNumbers = new ArrayList<>();
        if (roadShields != null && !roadShields.isEmpty()) {
            for (int i = 0; i < roadShields.size(); i++) {
                roadShieldRoadNumbers.add(roadShields.get(i));
            }
        }
        return TextUtils.join(" ", roadShieldRoadNumbers);
    }
}
