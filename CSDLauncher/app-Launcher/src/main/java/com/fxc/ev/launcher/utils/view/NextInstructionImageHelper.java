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

import android.view.View;
import android.widget.ImageView;

import com.fxc.ev.launcher.BuildConfig;
import com.fxc.ev.launcher.R;
import com.tomtom.navkit2.guidance.Exit;
import com.tomtom.navkit2.guidance.Fork;
import com.tomtom.navkit2.guidance.Instruction;
import com.tomtom.navkit2.guidance.ItineraryPoint;
import com.tomtom.navkit2.guidance.Roundabout;
import com.tomtom.navkit2.guidance.Turn;

public class NextInstructionImageHelper {
  private static class ManeuverImage {
    final Integer primary;
    final Integer secondary;
    boolean shouldFlip = false;

    ManeuverImage(Integer primary, Integer secondary) {
      this.primary = primary;
      this.secondary = secondary;
    }

    void flipHorizontally() {
      this.shouldFlip = true;
    }
  }

  public void setManeuverImageForInstruction(ImageView manouverImageView, Instruction instruction) {
    updateManeuverImage(maneuverImage(instruction), manouverImageView);
  }

  private void updateManeuverImage(ManeuverImage maneuverImage, ImageView maneuverImageView) {
    if (maneuverImage != null) {
      if (maneuverImage.primary != null) {
        maneuverImageView.setImageResource(maneuverImage.primary);
        maneuverImageView.setVisibility(View.VISIBLE);
        if (maneuverImage.secondary != null) {
          maneuverImageView.setBackgroundResource(maneuverImage.secondary);
        } else {
          // Clear the background image
          maneuverImageView.setBackgroundResource(0);
        }
      } else {
        maneuverImageView.setVisibility(View.INVISIBLE);
      }
      if (maneuverImage.shouldFlip) {
        maneuverImageView.setScaleX(-1.0f);
      } else {
        maneuverImageView.setScaleX(1.0f);
      }
    } else {
      maneuverImageView.setVisibility(View.INVISIBLE);
    }
  }

  private ManeuverImage maneuverImage(Instruction instruction) {
    ManeuverImage maneuverImage = null;

    switch (instruction.getType()) {
      case TURN:
        maneuverImage = maneuverImageFromTurn(instruction.getTurn(), instruction.getDrivingSide());
        break;
      case FORK:
        maneuverImage = maneuverImageFromFork(instruction.getFork(), instruction.getDrivingSide());
        break;
      case ROUNDABOUT:
        maneuverImage =
            maneuverImageFromRoundabout(instruction.getRoundabout(), instruction.getDrivingSide());
        break;
      case EXIT_ROUNDABOUT:
        maneuverImage =
            maneuverImageFromRoundabout(
                instruction.getExitRoundabout().getRoundabout(), instruction.getDrivingSide());
        break;
      case EXIT:
        maneuverImage = maneuverImageFromExit(instruction.getExit(), instruction.getDrivingSide());
        break;
      case ROAD_FORM_CHANGE:
        maneuverImage = maneuverImageFromRoadFormChange(instruction);
        break;
      case FOLLOW_ROAD:
        maneuverImage = maneuverImageFromFollowRoad();
        break;
      case ITINERARY_POINT:
        maneuverImage = maneuverImageFromItineraryPoint(instruction.getItineraryPoint());
        break;
    }
    return maneuverImage;
  }

  private ManeuverImage maneuverImageFromTurn(Turn turn, Instruction.DrivingSide drivingSide) {
    ManeuverImage turnImage = null;

    boolean shouldFlipHorizontally = false;

    switch (turn.getDirection()) {
      case GO_STRAIGHT:
        turnImage = new ManeuverImage(R.drawable.nk2_nip_arrow_continue_primary, null);
        break;
      case KEEP_RIGHT:
        shouldFlipHorizontally = true;
        // fall through
      case KEEP_LEFT:
        turnImage = new ManeuverImage(R.drawable.nk2_nip_arrow_bearleft_primary, null);
        break;
      case BEAR_RIGHT:
        shouldFlipHorizontally = true;
        // fall through
      case BEAR_LEFT:
        turnImage = new ManeuverImage(R.drawable.nk2_nip_arrow_bearleft_primary, null);
        break;
      case TURN_RIGHT:
        shouldFlipHorizontally = true;
        // fall through
      case TURN_LEFT:
        turnImage = new ManeuverImage(R.drawable.nk2_nip_arrow_turnleft_primary, null);
        break;
      case SHARP_RIGHT:
        shouldFlipHorizontally = true;
        // fall through
      case SHARP_LEFT:
        turnImage = new ManeuverImage(R.drawable.nk2_nip_arrow_sharpleft_primary, null);
        break;
      case TURN_AROUND:
        turnImage = new ManeuverImage(R.drawable.nk2_nip_arrow_uturn_primary, null);
        shouldFlipHorizontally = drivingSide == (Instruction.DrivingSide.LEFT);
        break;
    }

    if (shouldFlipHorizontally) {
      flipHorizontally(turnImage);
    }
    return turnImage;
  }

  private ManeuverImage maneuverImageFromFork(Fork fork, Instruction.DrivingSide drivingSide) {
    ManeuverImage forkImage = null;
    boolean shouldFlipHorizontally = false;
    switch (fork.getSelectedChoice()) {
      case RIGHT:
        shouldFlipHorizontally = true;
      case LEFT:
        forkImage = new ManeuverImage(R.drawable.nk2_nip_arrow_bearleft_primary, null);
        break;
    }

    if (shouldFlipHorizontally) {
      flipHorizontally(forkImage);
    }
    return forkImage;
  }

  private ManeuverImage roundaboutDirectionLeft(
      int turnAngleInDegrees, Instruction.DrivingSide drivingSide) {

    if (BuildConfig.DEBUG && !(turnAngleInDegrees < 0 && turnAngleInDegrees > -180)) {
      throw new AssertionError();
    }

    ManeuverImage roundaboutImage;

    if (turnAngleInDegrees <= -112.5) {
      // sharp right
      if (drivingSide == Instruction.DrivingSide.LEFT) {
        roundaboutImage =
            new ManeuverImage(
                R.drawable.nk2_nip_roundaboutleft1_uk_primary,
                R.drawable.nk2_nip_roundaboutleft1_uk_secondary);
      } else {
        roundaboutImage = new ManeuverImage(R.drawable.nk2_nip_roundaboutleft1_primary, null);
      }
    } else if (turnAngleInDegrees <= -67.5) {
      // right
      if (drivingSide == Instruction.DrivingSide.LEFT) {
        roundaboutImage =
            new ManeuverImage(
                R.drawable.nk2_nip_roundaboutleft2_uk_primary,
                R.drawable.nk2_nip_roundaboutleft2_uk_secondary);
      } else {
        roundaboutImage = new ManeuverImage(R.drawable.nk2_nip_roundaboutleft2_primary, null);
      }
    } else {
      // bear right
      if (drivingSide == Instruction.DrivingSide.LEFT) {
        roundaboutImage =
            new ManeuverImage(
                R.drawable.nk2_nip_roundaboutleft3_uk_primary,
                R.drawable.nk2_nip_roundaboutleft3_uk_secondary);
      } else {
        roundaboutImage =
            new ManeuverImage(
                R.drawable.nk2_nip_roundaboutleft3_primary,
                R.drawable.nk2_nip_roundaboutleft3_secondary);
      }
    }

    return roundaboutImage;
  }

  private ManeuverImage roundaboutDirectionRight(
      int turnAngleInDegrees, Instruction.DrivingSide drivingSide) {

    if (BuildConfig.DEBUG && !(turnAngleInDegrees > 0 && turnAngleInDegrees < 180)) {
      throw new AssertionError();
    }

    ManeuverImage roundaboutImage;

    if (turnAngleInDegrees >= 112.5) {
      // sharp right
      if (drivingSide == Instruction.DrivingSide.LEFT) {
        roundaboutImage = new ManeuverImage(R.drawable.nk2_nip_roundaboutleft1_primary, null);
      } else {
        roundaboutImage =
            new ManeuverImage(
                R.drawable.nk2_nip_roundaboutleft1_uk_primary,
                R.drawable.nk2_nip_roundaboutleft1_uk_secondary);
      }
    } else if (turnAngleInDegrees >= 67.5) {
      // right
      if (drivingSide == Instruction.DrivingSide.LEFT) {
        roundaboutImage = new ManeuverImage(R.drawable.nk2_nip_roundaboutleft2_primary, null);
      } else {
        roundaboutImage =
            new ManeuverImage(
                R.drawable.nk2_nip_roundaboutleft2_uk_primary,
                R.drawable.nk2_nip_roundaboutleft2_uk_secondary);
      }
    } else {
      // bear right
      if (drivingSide == Instruction.DrivingSide.LEFT) {
        roundaboutImage =
            new ManeuverImage(
                R.drawable.nk2_nip_roundaboutleft3_primary,
                R.drawable.nk2_nip_roundaboutleft3_secondary);
      } else {
        roundaboutImage =
            new ManeuverImage(
                R.drawable.nk2_nip_roundaboutleft3_uk_primary,
                R.drawable.nk2_nip_roundaboutleft3_uk_secondary);
      }
    }

    flipHorizontally(roundaboutImage);

    return roundaboutImage;
  }

  private ManeuverImage maneuverImageFromRoundabout(
      Roundabout roundabout, Instruction.DrivingSide drivingSide) {
    ManeuverImage roundaboutImage = null;
    switch (roundabout.getDirection()) {
      case CROSS:
        roundaboutImage =
            new ManeuverImage(
                R.drawable.nk2_nip_roundaboutcross_uk_primary,
                R.drawable.nk2_nip_roundaboutcross_uk_secondary);

        if (drivingSide != Instruction.DrivingSide.LEFT) {
          flipHorizontally(roundaboutImage);
        }
        break;
      case RIGHT:
        roundaboutImage = roundaboutDirectionRight(roundabout.getTurnAngleInDegrees(), drivingSide);
        break;
      case LEFT:
        roundaboutImage = roundaboutDirectionLeft(roundabout.getTurnAngleInDegrees(), drivingSide);
        break;
      case BACK:
        roundaboutImage = new ManeuverImage(R.drawable.nk2_nip_roundaboutaround_primary, null);

        if (drivingSide != Instruction.DrivingSide.RIGHT) {
          flipHorizontally(roundaboutImage);
        }
        break;
    }

    return roundaboutImage;
  }

  private ManeuverImage maneuverImageFromExit(Exit exit, Instruction.DrivingSide drivingSide) {
    ManeuverImage exitImage = null;

    boolean shouldFlipHorizontally = false;

    switch (exit.getDirection()) {
      case NONE:
        // no image
        break;
      case RIGHT:
        shouldFlipHorizontally = true;
        // fall through
      case LEFT:
        exitImage =
            new ManeuverImage(
                R.drawable.nk2_nip_arrow_bearleft_primary,
                R.drawable.nk2_nip_arrow_exitleft_secondary);
        break;
    }

    if (shouldFlipHorizontally) {
      flipHorizontally(exitImage);
    }

    return exitImage;
  }

  private ManeuverImage maneuverImageFromRoadFormChange(Instruction instruction) {
    ManeuverImage roadFormChangeImage = null;
    switch (instruction.getNextSignificantRoad().getType()) {
      case UNKNOWN:
        // do nothing
        break;
      case REGULAR:
        // do nothing
        break;
      case FREEWAY:
        roadFormChangeImage =
            new ManeuverImage(
                R.drawable.nk2_nip_ic_freeway_primary, R.drawable.nk2_nip_ic_freeway_secondary);
        break;
      case FERRY:
        roadFormChangeImage =
            new ManeuverImage(
                R.drawable.nk2_nip_ic_ferry_primary, R.drawable.nk2_nip_ic_ferry_secondary);
        break;
    }
    return roadFormChangeImage;
  }

  private ManeuverImage maneuverImageFromFollowRoad() {
    return new ManeuverImage(R.drawable.nk2_nip_arrow_continue_primary, null);
  }

  private ManeuverImage itineraryPointWaypoint(ItineraryPoint.Side itineraryPointSide) {
    ManeuverImage waypointImage = null;
    switch (itineraryPointSide) {
      case UNKNOWN:
        waypointImage =
            new ManeuverImage(
                R.drawable.nk2_nip_ic_waypoint_primary, R.drawable.nk2_nip_ic_waypoint_secondary);
        break;
      case LEFT:
        waypointImage =
            new ManeuverImage(
                R.drawable.nk2_nip_ic_stop_side_primary,
                R.drawable.nk2_nip_ic_stop_side_left_secondary);
        break;
      case RIGHT:
        waypointImage =
            new ManeuverImage(
                R.drawable.nk2_nip_ic_stop_side_primary,
                R.drawable.nk2_nip_ic_stop_side_right_secondary);
        break;
    }
    return waypointImage;
  }

  private ManeuverImage itineraryPointDestination(ItineraryPoint.Side itineraryPointSide) {
    ManeuverImage destinationImage = null;
    switch (itineraryPointSide) {
      case UNKNOWN:
        destinationImage =
            new ManeuverImage(
                R.drawable.nk2_nip_ic_finish_primary, R.drawable.nk2_nip_ic_finish_secondary);
        break;
      case LEFT:
        destinationImage =
            new ManeuverImage(
                R.drawable.nk2_nip_ic_finish_side_primary,
                R.drawable.nk2_nip_ic_finish_side_left_secondary);
        break;
      case RIGHT:
        destinationImage =
            new ManeuverImage(
                R.drawable.nk2_nip_ic_finish_side_primary,
                R.drawable.nk2_nip_ic_finish_side_right_secondary);
        break;
    }
    return destinationImage;
  }

  private ManeuverImage maneuverImageFromItineraryPoint(ItineraryPoint itineraryPoint) {
    ManeuverImage itineraryPointImage = null;
    switch (itineraryPoint.getType()) {
      case DEPARTURE:
        itineraryPointImage = itineraryPointWaypoint(itineraryPoint.getSide());
        break;
      case DESTINATION:
        itineraryPointImage = itineraryPointDestination(itineraryPoint.getSide());
        break;
    }
    return itineraryPointImage;
  }

  private void flipHorizontally(ManeuverImage image) {
    if (image != null) {
      image.flipHorizontally();
    }
  }
}
