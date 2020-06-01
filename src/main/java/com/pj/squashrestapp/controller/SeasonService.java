package com.pj.squashrestapp.controller;

import com.google.common.collect.Multimap;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.dto.MatchDto;
import com.pj.squashrestapp.model.dto.PlayerDto;
import com.pj.squashrestapp.model.dto.RoundScoreboard;
import com.pj.squashrestapp.model.dto.Scoreboard;
import com.pj.squashrestapp.model.dto.ScoreboardRow;
import com.pj.squashrestapp.model.dto.SeasonScoreboardDto;
import com.pj.squashrestapp.model.dto.SeasonScoreboardRowDto;
import com.pj.squashrestapp.model.dto.SingleSetRowDto;
import com.pj.squashrestapp.repository.MatchRepository;
import com.pj.squashrestapp.repository.SeasonRepository;
import com.pj.squashrestapp.repository.XpPointsRepository;
import com.pj.squashrestapp.util.MatchUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
@Slf4j
@Service
public class SeasonService {

  @Autowired
  private MatchRepository matchRepository;

  @Autowired
  private XpPointsRepository xpPointsRepository;

  @Autowired
  private SeasonRepository seasonRepository;


  public SeasonScoreboardDto overalScoreboard(final Long id) {

    final Season season = seasonRepository.findSeasonById(id);
    final int seasonNumber = season.getNumber();
    final Date seasonStartDate = season.getStartDate();

    final SeasonScoreboardDto seasonScoreboardDto = new SeasonScoreboardDto(id, seasonNumber, seasonStartDate);




    final List<Long> roundsIds = seasonRepository.retrieveFinishedGroupIdsBySeasonId(id);



    final int numberOfFinishedRounds = roundsIds.size();
    seasonScoreboardDto.setFinishedRounds(numberOfFinishedRounds);



    for (final Long roundId : roundsIds) {


      final List<SingleSetRowDto> sets = matchRepository.retrieveByRoundId(roundId);
      final int roundNumber = sets.get(0).getRoundNumber();
      final Multimap<Long, MatchDto> perGroupMatches = MatchUtil.rebuildRoundMatchesPerRoundGroupId(sets);

      final RoundScoreboard roundScoreboard = new RoundScoreboard();
      for (final Long roundGroupId : perGroupMatches.keySet()) {
        roundScoreboard.addRoundGroup(perGroupMatches.get(roundGroupId));
      }

      final List<Integer> playersPerGroup = roundScoreboard.getPlayersPerGroup();
      final String split = MatchUtil.integerListToString(playersPerGroup);
      final List<Integer> xpPoints = xpPointsRepository.retrievePointsBySplit(split);

      roundScoreboard.assignPointsAndPlaces(xpPoints);


      for (final Scoreboard scoreboard : roundScoreboard.getRoundGroupScoreboards()) {
        for (final ScoreboardRow scoreboardRow : scoreboard.getScoreboardRows()) {

          final PlayerDto player = scoreboardRow.getPlayer();

          final SeasonScoreboardRowDto seasonScoreboardRowDto = seasonScoreboardDto
                  .getSeasonScoreboardRows()
                  .stream()
                  .filter(p -> p.getPlayer().equals(player))
                  .findFirst()
                  .orElse(new SeasonScoreboardRowDto(player));

          seasonScoreboardRowDto.addXpForRound(roundNumber, scoreboardRow.getXpEarned());


          final boolean containsPlayer = seasonScoreboardDto.getSeasonScoreboardRows().contains(seasonScoreboardRowDto);
          if (!containsPlayer) {
            seasonScoreboardDto.getSeasonScoreboardRows().add(seasonScoreboardRowDto);
          }
        }
      }

    }


    for (final SeasonScoreboardRowDto seasonScoreboardRowDto : seasonScoreboardDto.getSeasonScoreboardRows()) {
      seasonScoreboardRowDto.calculateFinishedRow(seasonScoreboardDto.getAllRounds(), seasonScoreboardDto.getFinishedRounds());
    }

    seasonScoreboardDto.sortRows();

    return seasonScoreboardDto;
  }


  List<RoundScoreboard> perRoundScoreboard(@RequestParam("id") final Long id) {

    final List<Long> roundsIds = seasonRepository.retrieveFinishedGroupIdsBySeasonId(id);

    final List<RoundScoreboard> roundScoreboards = new ArrayList<>();
    for (final Long roundId : roundsIds) {
      final List<SingleSetRowDto> sets = matchRepository.retrieveByRoundId(roundId);
      final Multimap<Long, MatchDto> perGroupMatches = MatchUtil.rebuildRoundMatchesPerRoundGroupId(sets);

      final RoundScoreboard roundScoreboard = new RoundScoreboard();
      for (final Long roundGroupId : perGroupMatches.keySet()) {
        roundScoreboard.addRoundGroup(perGroupMatches.get(roundGroupId));
      }

      final List<Integer> playersPerGroup = roundScoreboard.getPlayersPerGroup();
      final String split = MatchUtil.integerListToString(playersPerGroup);
      final List<Integer> xpPoints = xpPointsRepository.retrievePointsBySplit(split);

      roundScoreboard.assignPointsAndPlaces(xpPoints);
      roundScoreboards.add(roundScoreboard);
    }

    return roundScoreboards;
  }



}
