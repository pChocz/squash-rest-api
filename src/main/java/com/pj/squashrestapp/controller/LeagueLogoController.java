package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.service.LeagueLogoService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** */
@Slf4j
@RestController
@RequestMapping("/league-logos")
@RequiredArgsConstructor
public class LeagueLogoController {

  private final LeagueLogoService leagueLogoService;

  @PutMapping(value = "/{leagueUuid}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRoleForLeague(#leagueUuid, 'MODERATOR')")
  void replaceLogoForLeague(
      @PathVariable final UUID leagueUuid, @RequestParam final MultipartFile leagueLogoFile) {
    leagueLogoService.replaceLogoForLeague(leagueUuid, leagueLogoFile);
  }

  @DeleteMapping(value = "/{leagueUuid}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRoleForLeague(#leagueUuid, 'MODERATOR')")
  void deleteLogoForLeague(@PathVariable final UUID leagueUuid) {
    leagueLogoService.deleteLogoForLeague(leagueUuid);
  }

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

  @GetMapping(value = "/{leagueUuid}")
  @ResponseBody
  String extractLeagueLogo(@PathVariable final UUID leagueUuid) {
    final byte[] leagueLogoBytes = leagueLogoService.extractLeagueLogo(leagueUuid);
    return Base64Utils.encodeToString(leagueLogoBytes);
  }
}
