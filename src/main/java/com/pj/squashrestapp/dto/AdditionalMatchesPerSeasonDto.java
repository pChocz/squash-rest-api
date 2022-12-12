package com.pj.squashrestapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** */
@Slf4j
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalMatchesPerSeasonDto {
    private Integer seasonNumber;
    private Long numberOfMatches;
}
