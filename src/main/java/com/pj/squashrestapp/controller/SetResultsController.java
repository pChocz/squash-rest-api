package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.dto.SimpleMatchDto;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import com.pj.squashrestapp.util.TimeLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/set-results")
public class SetResultsController {

  @Autowired
  private SetResultRepository setResultRepository;

  @RequestMapping(
          value = "/byLeagueId",
          params = {"id"},
          method = GET)
  @ResponseBody
  List<SimpleMatchDto> byLeagueId(@RequestParam("id") final Long id) {
    final long startTime = System.nanoTime();

    final List<SetResult> setResults = setResultRepository.fetchByLeagueId(id);
    final League leagueFetched = EntityGraphBuildUtil.reconstructLeague(setResults, id);

    final List<SimpleMatchDto> matches = new ArrayList<>();
    int counter = 0;
    for (final Season season : leagueFetched.getSeasons()) {
      for (final Round round : season.getRounds()) {
        for (final RoundGroup roundGroup : round.getRoundGroups()) {
          for (final Match match : roundGroup.getMatches()) {
            counter++;
            final SimpleMatchDto simpleMatchDto = new SimpleMatchDto(match);
            log.info(simpleMatchDto.toString());
            matches.add(simpleMatchDto);
          }
        }
      }
    }
    TimeLogUtil.logFinish(startTime, counter);
    return matches;
  }

  @RequestMapping(
          value = "/bySeasonId",
          params = {"id"},
          method = GET)
  @ResponseBody
  List<SimpleMatchDto> bySeasonId(@RequestParam("id") final Long id) {
    final long startTime = System.nanoTime();

    final List<SetResult> setResults = setResultRepository.fetchBySeasonId(id);
    final Season seasonFetched = EntityGraphBuildUtil.reconstructSeason(setResults, id);

    final List<SimpleMatchDto> matches = new ArrayList<>();
    int counter = 0;
    for (final Round round : seasonFetched.getRounds()) {
      for (final RoundGroup roundGroup : round.getRoundGroups()) {
        for (final Match match : roundGroup.getMatches()) {
          counter++;
          final SimpleMatchDto simpleMatchDto = new SimpleMatchDto(match);
          log.info(simpleMatchDto.toString());
          matches.add(simpleMatchDto);
        }
      }
    }
    TimeLogUtil.logFinish(startTime, counter);
    return matches;
  }

  @RequestMapping(
          value = "/byRoundId",
          params = {"id"},
          method = GET)
  @ResponseBody
  List<SimpleMatchDto> byRoundId(@RequestParam("id") final Long id) {
    final long startTime = System.nanoTime();

    final List<SetResult> setResults = setResultRepository.fetchByRoundId(id);
    final Round roundFetched = EntityGraphBuildUtil.reconstructRound(setResults, id);

    final List<SimpleMatchDto> matches = new ArrayList<>();
    int counter = 0;
    for (final RoundGroup roundGroup : roundFetched.getRoundGroups()) {
      for (final Match match : roundGroup.getMatches()) {
        counter++;
        final SimpleMatchDto simpleMatchDto = new SimpleMatchDto(match);
        log.info(simpleMatchDto.toString());
        matches.add(simpleMatchDto);
      }
    }
    TimeLogUtil.logFinish(startTime, counter);
    return matches;
  }

}
