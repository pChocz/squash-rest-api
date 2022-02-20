package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.SeasonDto;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.service.RedisCacheService;
import com.pj.squashrestapp.service.SeasonService;
import com.pj.squashrestapp.util.GeneralUtil;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** */
@Slf4j
@RestController
@RequestMapping("/seasons")
@RequiredArgsConstructor
public class SeasonController {

  private final RedisCacheService redisCacheService;
  private final SeasonService seasonService;

  @PostMapping
  @PreAuthorize("hasRoleForLeague(#leagueUuid, 'MODERATOR')")
  UUID createSeason(
      @RequestParam final int seasonNumber,
      @RequestParam @DateTimeFormat(pattern = GeneralUtil.DATE_FORMAT) final LocalDate startDate,
      @RequestParam final UUID leagueUuid,
      @RequestParam final String xpPointsType,
      @RequestParam(required = false) final String description) {
    final Season season = seasonService.createNewSeason(seasonNumber, startDate, leagueUuid, xpPointsType, description);
    redisCacheService.evictCacheForSeason(season.getUuid());
    return season.getUuid();
  }

  @GetMapping(value = "/{seasonUuid}")
  SeasonDto getSeason(@PathVariable final UUID seasonUuid) {
    final SeasonDto seasonDto = seasonService.extractSeasonDtoByUuid(seasonUuid);
    return seasonDto;
  }

  @GetMapping(value = "/adjacent/{seasonUuid}")
  Pair<Optional<UUID>, Optional<UUID>> getAdjacentSeasonsUuids(@PathVariable final UUID seasonUuid) {
    final Pair<Optional<UUID>, Optional<UUID>> adjacentSeasonsUuids = seasonService.extractAdjacentSeasonsUuids(seasonUuid);
    return adjacentSeasonsUuids;
  }

  @GetMapping(value = "/players/{seasonUuid}")
  List<PlayerDto> getSeasonPlayers(@PathVariable final UUID seasonUuid) {
    final List<PlayerDto> seasonPlayers = seasonService.extractSeasonPlayers(seasonUuid);
    return seasonPlayers;
  }

  @DeleteMapping(value = "/{seasonUuid}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRoleForSeason(#seasonUuid, 'MODERATOR')")
  void deleteSeason(@PathVariable final UUID seasonUuid) {
    redisCacheService.evictCacheForSeasonMatches(seasonUuid);
    redisCacheService.evictCacheForSeason(seasonUuid);
    seasonService.deleteSeason(seasonUuid);
  }

}
