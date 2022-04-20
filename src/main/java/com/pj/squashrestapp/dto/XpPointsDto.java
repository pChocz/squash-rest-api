package com.pj.squashrestapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** */
@Getter
@AllArgsConstructor
public class XpPointsDto {

    private final int placeInRound;
    private final int placeInGroup;
    private final int groupNumber;
    private final int points;
}
