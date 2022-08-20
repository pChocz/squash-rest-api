package com.pj.squashrestapp.dto.matchresultsdistribution;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchResultDistributionDataDto {

    private int count;
    private Long winnerId;
    private Long looserId;
    private int gamesWon;
    private int gamesLost;
}
