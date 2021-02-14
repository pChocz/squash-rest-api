package com.pj.squashrestapp.service;

import com.pj.squashrestapp.repository.LeagueLogoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LeagueLogoService {

  private final LeagueLogoRepository leagueLogoRepository;


  public byte[] extractLeagueLogoBySeasonUuid(final UUID seasonUuid) {
    final byte[] leagueLogoBytes = leagueLogoRepository.extractLogoBlobBySeasonUuid(seasonUuid);
    return leagueLogoBytes;
  }


  public byte[] extractLeagueLogoByRoundUuid(final UUID roundUuid) {
    final byte[] leagueLogoBytes = leagueLogoRepository.extractLogoBlobByRoundUuid(roundUuid);
    return leagueLogoBytes;
  }

}
