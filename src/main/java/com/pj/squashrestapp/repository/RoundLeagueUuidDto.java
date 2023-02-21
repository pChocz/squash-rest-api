package com.pj.squashrestapp.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoundLeagueUuidDto {
    private UUID leagueUuid;
    private UUID roundUuid;
}
