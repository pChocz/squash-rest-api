package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.service.RoundService;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/rounds")
@RequiredArgsConstructor
public class RoundController {

  private final RoundService roundService;


  @PostMapping
  @ResponseBody
//  @PreAuthorize("hasRoleForLeague(#leagueId, 'MODERATOR')")
  UUID newRound(
          @RequestBody
          @RequestParam final int roundNumber,
          @RequestParam @DateTimeFormat(pattern = GeneralUtil.DATE_FORMAT) final LocalDate roundDate,
          @RequestParam final UUID seasonUuid,
          @RequestParam final List<UUID[]> playersUuids) {
    final Round round = roundService.createRound(roundNumber, roundDate, seasonUuid, playersUuids);
    log.info("created round {}", round.getUuid());
    return round.getUuid();
  }

  @PutMapping
//  @PreAuthorize("hasRoleForLeague(#leagueId, 'MODERATOR')")
  void updateRoundFinishState(
          @RequestParam("roundUuid") final UUID roundUuid,
          @RequestParam("finishedState") final boolean finishedState) {
    roundService.updateRoundFinishedState(roundUuid, finishedState);
    log.info("update round {}: finished state: {}", roundUuid, finishedState);
  }


  @GetMapping(value = "{roundUuid}/leagueUuid")
  @ResponseBody
  UUID getLeagueUuidFromRoundUuid(@PathVariable final UUID roundUuid) {
    return roundService.extractLeagueUuid(roundUuid);
  }

  @DeleteMapping(value = "/{roundUuid}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
//  @PreAuthorize("hasRoleForRound(#roundId, 'MODERATOR')")
  void deleteRound(@PathVariable final UUID roundUuid) {
    roundService.deleteRound(roundUuid);
    log.info("Round {} has been deleted", roundUuid);
  }

}
