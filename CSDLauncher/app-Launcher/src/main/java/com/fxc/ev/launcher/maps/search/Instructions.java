package com.fxc.ev.launcher.maps.search;

import com.tomtom.navkit2.guidance.Instruction;

public class Instructions {
    private int distanceToInstruction;
    private Instruction instruction;

    public Instructions(int distance, Instruction instruction) {
        this.distanceToInstruction = distance;
        this.instruction = instruction;
    }

    public int getDistance() {
        return distanceToInstruction;
    }

    public void setDistance(int distance) {
        this.distanceToInstruction = distance;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public void setInstruction(Instruction instruction) {
        this.instruction = instruction;
    }
}
