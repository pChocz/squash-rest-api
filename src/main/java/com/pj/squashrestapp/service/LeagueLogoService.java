package com.pj.squashrestapp.service;

import com.pj.squashrestapp.repository.LeagueLogoRepository;
import com.pj.squashrestapp.repository.LeagueRepository;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class LeagueLogoService {

  private final LeagueLogoRepository leagueLogoRepository;

  public byte[] extractLeagueLogoBySeasonUuid(final UUID seasonUuid) {
    final byte[] leagueLogoBytes =
        leagueLogoRepository
            .extractLogoBlobBySeasonUuid(seasonUuid)
            .orElseThrow(() -> new NoSuchElementException("Logo does not exist!"));
    return leagueLogoBytes;
  }

  public byte[] extractLeagueLogoByRoundUuid(final UUID roundUuid) {
    final byte[] leagueLogoBytes =
        leagueLogoRepository
            .extractLogoBlobByRoundUuid(roundUuid)
            .orElseThrow(() -> new NoSuchElementException("Logo does not exist!"));
    return leagueLogoBytes;
  }

  public byte[] extractLeagueLogo(final UUID leagueUuid) {
    final byte[] leagueLogoBytes =
        leagueLogoRepository
            .extractLogoBlobByLeagueUuid(leagueUuid)
            .orElseThrow(() -> new NoSuchElementException("Logo does not exist!"));
    return leagueLogoBytes;
  }

  //  public void replaceLogoForLeague(final UUID leagueUuid, final MultipartFile leagueLogoFile) {
  //    final byte[] logoBytes;
  //    try {
  //      logoBytes = leagueLogoFile.getBytes();
  //    } catch (final IOException e) {
  //      throw new GeneralBadRequestException("Bad picture!");
  //    }
  //
  //    final LeagueLogo leagueLogo = new LeagueLogo();
  //    leagueLogo.setPicture(logoBytes);
  //
  //    final League league = leagueRepository.findByUuid(leagueUuid).orElseThrow();
  //    league.setLeagueLogo(leagueLogo);
  //    leagueLogo.setLeague(league);
  //
  //    leagueLogoRepository.save(leagueLogo);
  //  }

  //  public void deleteLogoForLeague(final UUID leagueUuid) {
  //    final Optional<League> league = leagueRepository.findByUuid(leagueUuid);
  //    if (league.isEmpty()) {
  //      throw new GeneralBadRequestException("League with UUID [" + leagueUuid + "] not found!");
  //    }
  //
  //    final Optional<LeagueLogo> leagueLogo = leagueLogoRepository.findByLeague(league.get());
  //    if (leagueLogo.isEmpty()) {
  //      throw new GeneralBadRequestException(
  //          "No logo exists for the league [" + league.get().getName());
  //    }
  //
  //    league.get().setLeagueLogo(null);
  //    leagueLogoRepository.delete(leagueLogo.get());
  //    leagueRepository.save(league.get());
  //  }
}
