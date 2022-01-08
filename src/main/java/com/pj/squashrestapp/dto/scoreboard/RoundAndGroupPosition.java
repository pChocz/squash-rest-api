package com.pj.squashrestapp.dto.scoreboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoundAndGroupPosition implements Comparable<RoundAndGroupPosition> {

    private String groupCharacter;
    private int positionInGroup;
    private int positionInRound;
    private int xpPoints;
    private boolean lastPlaceInGroup;

    @Override
    public int compareTo(RoundAndGroupPosition that) {
        return this.getXpPoints() - that.getXpPoints();
    }
}
