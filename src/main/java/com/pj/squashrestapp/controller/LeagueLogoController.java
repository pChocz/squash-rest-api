package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.service.LeagueLogoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/league-logos")
@RequiredArgsConstructor
public class LeagueLogoController {

  private final LeagueLogoService leagueLogoService;


  @GetMapping(value = "/season/{seasonUuid}")
  @ResponseBody
  String extractLeagueLogoBySeasonUuid(@PathVariable final UUID seasonUuid) {
    final byte[] leagueLogoBytes = leagueLogoService.extractLeagueLogoBySeasonUuid(seasonUuid);
    return Base64Utils.encodeToString(leagueLogoBytes);
  }


  @GetMapping(value = "/round/{roundUuid}")
  @ResponseBody
  String extractLeagueLogoByRoundUuid(@PathVariable final UUID roundUuid) {
    final byte[] leagueLogoBytes = leagueLogoService.extractLeagueLogoByRoundUuid(roundUuid);
    return Base64Utils.encodeToString(leagueLogoBytes);
  }

}
