package com.pj.squashrestapp.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/** */
@Getter
public class XpPointsForTable {

    private final String type;
    private final String split;
    private final int numberOfPlayers;
    private final List<XpPointsDto> xpPoints;

    public XpPointsForTable(final String type, final String split, final int numberOfPlayers) {
        this.type = type;
        this.split = split;
        this.numberOfPlayers = numberOfPlayers;
        this.xpPoints = new ArrayList<>();
    }

    public void addPoints(final XpPointsDto xpPointsDto) {
        this.xpPoints.add(xpPointsDto);
    }

    @Override
    public String toString() {
        return split;
    }
}
