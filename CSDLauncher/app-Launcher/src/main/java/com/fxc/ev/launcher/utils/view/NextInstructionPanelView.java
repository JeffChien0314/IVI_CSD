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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.fxc.ev.launcher.R;
import com.tomtom.navkit2.drivingassistance.Position;
import com.tomtom.navkit2.guidance.Instruction;

import java.util.Date;
import java.util.List;

public class NextInstructionPanelView extends ConstraintLayout {

  private static final String TAG = "NextInstructionPanel";

  private final NextInstructionMainPanel nextInstructionMainPanel;
  private final CombinedInstructionPanel combinedInstructionPanel;
  private final OutlineView outlineViewPanel;
  private Instruction currentInstruction = null;
  private Instruction currentCombinedInstruction = null;
  private int currentDistanceToInstructionInMeters = 0;

  private String currentCountryCode;

  public NextInstructionPanelView(Context context) {
    this(context, null);
  }

  public NextInstructionPanelView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public NextInstructionPanelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    inflate(context, R.layout.next_instruction_container_view, this);
    nextInstructionMainPanel = findViewById(R.id.nextInstructionMainPanel);
    combinedInstructionPanel = findViewById(R.id.nextInstructionCombinedPanel);
    outlineViewPanel = findViewById(R.id.outlineViewText);//Jerry@20220317 add
  }

  public void updatePosition(Position position) {
    if (position != null && position.place() != null && position.place().address() != null) {

      if (!TextUtils.isEmpty(position.place().address().countryCode())) {
        currentCountryCode = position.place().address().countryCode();
      }
    }

    updateInstructionPanels();
  }

  public void update(int distanceToInstructionInMeters, List<Instruction> instructions) {
    if (instructions == null || instructions.isEmpty()) {
      Log.w(TAG, "Invalid Instruction provided");
      return;
    }

    currentInstruction = instructions.get(0);

    if (instructions.size() > 1) {
      currentCombinedInstruction = instructions.get(1);
    } else {
      currentCombinedInstruction = null;
    }

    currentDistanceToInstructionInMeters = distanceToInstructionInMeters;

    updateInstructionPanels();
  }

  public void updateDistance(int distanceToInstructionInMeters) {
    currentDistanceToInstructionInMeters = distanceToInstructionInMeters;
    updateInstructionPanels();
  }

  private void updateInstructionPanels() {
    if (currentCountryCode != null) {
      if (currentInstruction != null) {
        nextInstructionMainPanel.update(
            currentInstruction, currentDistanceToInstructionInMeters, currentCountryCode);
        nextInstructionMainPanel.setVisibility(VISIBLE);
        outlineViewPanel.setVisibility(VISIBLE);//Jerry@20220316 add
        // The CIP is not yet "visible". However, it is not "gone", as it should take space in the
        // layout
        // to ensure that the amount of Y-translation we compute is large enough to hide
        // both panels, were we to enter the interactive mode and stay there over several
        // instruction updates, some of which
        // possibly having combined instructions.
        /*combinedInstructionPanel.setVisibility(INVISIBLE);//Jerry@20220315 marker:not display
        if (currentCombinedInstruction != null) {
          combinedInstructionPanel.update(currentCombinedInstruction, currentCountryCode);
          // Make the CIP visible when the underlying image is indeed around
          if (combinedInstructionPanel.hasCombinedManeuverImage()) {
            combinedInstructionPanel.setVisibility(VISIBLE);
          }
        }*/
      } else {
        // There is no NIP, hence no CIP; by using "gone" we do not take any space in the layout.
        // Note that the children of the NIP panel and of the CIP panel will also seem "gone" as a
        // result.
        nextInstructionMainPanel.setVisibility(GONE);
        combinedInstructionPanel.setVisibility(GONE);
        outlineViewPanel.setVisibility(GONE);//Jerry@20220316 add
      }
    }
  }

  //Jerry220220317 add update navigation distance&time
  public void updateTripOutlineView(String distance, int timeRemaining, Date date){
    outlineViewPanel.updateTripInfo(distance,timeRemaining,date);
  }
}
