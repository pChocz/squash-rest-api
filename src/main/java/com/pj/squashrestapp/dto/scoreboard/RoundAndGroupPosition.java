package com.pj.squashrestapp.dto.scoreboard;

import lombok.Value;

@Value
public class RoundAndGroupPosition implements Comparable<RoundAndGroupPosition> {

    String groupCharacter;
    int positionInGroup;
    int positionInRound;
    int xpPoints;
    boolean isLastPlaceInGroup;

    @Override
    public int compareTo(RoundAndGroupPosition that) {
        return that.getXpPoints() - this.getXpPoints();
    }
}
