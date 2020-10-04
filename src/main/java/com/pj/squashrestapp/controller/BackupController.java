package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.dbinit.jsondto.JsonLeague;
import com.pj.squashrestapp.dbinit.jsondto.JsonPlayerCredentials;
import com.pj.squashrestapp.dbinit.jsondto.JsonRound;
import com.pj.squashrestapp.dbinit.service.BackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/backup")
@RequiredArgsConstructor
public class BackupController {

  private final BackupService backupService;

  @GetMapping("/rounds/{roundUuid}")
  @PreAuthorize("isAdmin()")
  ResponseEntity<JsonRound> backupRound(@PathVariable final UUID roundUuid) {
    final JsonRound roundJson = backupService.roundToJson(roundUuid);
    return new ResponseEntity<JsonRound>(roundJson, HttpStatus.OK);
  }

  @GetMapping("/leagues/{leagueUuid}")
  @PreAuthorize("isAdmin()")
  ResponseEntity<JsonLeague> backupLeague(@PathVariable final UUID leagueUuid) {
    final JsonLeague leagueJson = backupService.leagueToJson(leagueUuid);
    return new ResponseEntity<JsonLeague>(leagueJson, HttpStatus.OK);
  }

  @GetMapping("/leagues/all")
  @PreAuthorize("isAdmin()")
  ResponseEntity<List<JsonLeague>> backupLeague() {
    final List<JsonLeague> leaguesJson = backupService.allLeagues();
    return new ResponseEntity<List<JsonLeague>>(leaguesJson, HttpStatus.OK);
  }

  @GetMapping("/players/all")
  @PreAuthorize("isAdmin()")
  ResponseEntity<List<JsonPlayerCredentials>> backupPlayerCredentials() {
    final List<JsonPlayerCredentials> playersCredentialsJson = backupService.allPlayersCredentials();
    return new ResponseEntity<List<JsonPlayerCredentials>>(playersCredentialsJson, HttpStatus.OK);
  }

}
