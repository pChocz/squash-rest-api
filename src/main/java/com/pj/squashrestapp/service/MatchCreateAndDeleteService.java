package com.pj.squashrestapp.service;

import com.pj.squashrestapp.config.exceptions.GeneralBadRequestException;
import com.pj.squashrestapp.model.AdditionalMatch;
import com.pj.squashrestapp.model.AdditionalMatchType;
import com.pj.squashrestapp.model.AdditionalSetResult;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.repository.AdditionalMatchRepository;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Common service for creation/deletion of both additional and round matches. */
@Slf4j
@Service
@RequiredArgsConstructor
public class MatchCreateAndDeleteService {

  private final AdditionalMatchRepository additionalMatchRepository;
  private final PlayerRepository playerRepository;
  private final LeagueRepository leagueRepository;

  @Transactional
  public void createNewAdditionalMatchEmpty(
      final UUID firstPlayerUuid,
      final UUID secondPlayerUuid,
      final UUID leagueUuid,
      final int seasonNumber,
      final LocalDate date,
      final AdditionalMatchType type) {

    final Player firstPlayer = playerRepository.findByUuid(firstPlayerUuid);
    final Player secondPlayer = playerRepository.findByUuid(secondPlayerUuid);
    if (firstPlayer == null || secondPlayer == null) {
      throw new GeneralBadRequestException("Players not valid!");
    }

    final Optional<League> leagueOptional = leagueRepository.findByUuid(leagueUuid);
    if (leagueOptional.isEmpty()) {
      throw new GeneralBadRequestException("League not valid!");
    }
    final League league = leagueOptional.get();

    final int numberOfSetsToCreate = 3;
    //    final int numberOfSetsToCreate = league.get().getMatchFormatType().getMaxNumberOfSets();

    final AdditionalMatch match = new AdditionalMatch();
    match.setFirstPlayer(firstPlayer);
    match.setSecondPlayer(secondPlayer);
    match.setDate(date);
    match.setType(type);
    match.setSeasonNumber(seasonNumber);

    for (int setNumber = 1; setNumber <= numberOfSetsToCreate; setNumber++) {
      final AdditionalSetResult setResult = new AdditionalSetResult();
      setResult.setNumber(setNumber);
      setResult.setFirstPlayerScore(null);
      setResult.setSecondPlayerScore(null);

      match.addSetResult(setResult);
    }

    league.addAdditionalMatch(match);
    log.info("Adding additional match!\n\t-> {}", match.detailedInfo());
  }

  public void deleteAdditionalMatchByUuid(final UUID matchUuid) {
    final Optional<AdditionalMatch> matchOptional = additionalMatchRepository.findByUuid(matchUuid);
    if (matchOptional.isEmpty()) {
      throw new GeneralBadRequestException("Additional match not found!");
    }
    final AdditionalMatch match = matchOptional.get();
    additionalMatchRepository.delete(match);
    log.info("Deleting additional match!\n\t-> {}", match.detailedInfo());
  }
}
