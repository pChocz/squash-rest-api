package com.pj.squashrestapp.dto.setresultshistogram;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SetResultsHistogramDataDto {

    private int count;
    private Long winnerId;
    private Long looserId;
    private int winningResult;
    private int loosingResult;
}
