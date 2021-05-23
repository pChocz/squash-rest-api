package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.dbinit.jsondto.JsonAll;
import com.pj.squashrestapp.dbinit.jsondto.JsonLeague;
import com.pj.squashrestapp.dbinit.jsondto.JsonRound;
import com.pj.squashrestapp.dbinit.service.BackupService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that provides methods to perform a backup of the database to a JSON file.
 */
@Slf4j
@RestController
@RequestMapping("/backup")
@RequiredArgsConstructor
public class BackupController {

  private final BackupService backupService;


  @GetMapping("/rounds/{roundUuid}")
  @PreAuthorize("isAdmin()")
  ResponseEntity<JsonRound> backupSingleRound(@PathVariable final UUID roundUuid) {
    final JsonRound roundJson = backupService.roundToJson(roundUuid);
    return new ResponseEntity<>(roundJson, HttpStatus.OK);
  }


  @GetMapping("/leagues/{leagueUuid}")
  @PreAuthorize("isAdmin()")
  ResponseEntity<JsonLeague> backupSingleLeague(@PathVariable final UUID leagueUuid) {
    final JsonLeague leagueJson = backupService.leagueToJson(leagueUuid);
    return new ResponseEntity<>(leagueJson, HttpStatus.OK);
  }


  @GetMapping("/all")
  @PreAuthorize("isAdmin()")
  ResponseEntity<JsonAll> backupAll() {
    final JsonAll jsonAll = JsonAll
            .builder()
            .xpPoints(backupService.allXpPoints())
            .leagues(backupService.allLeagues())
            .credentials(backupService.allPlayersCredentials())
            .refreshTokens(backupService.allRefreshTokens())
            .verificationTokens(backupService.allVerificationTokens())
            .build();
    return new ResponseEntity<>(jsonAll, HttpStatus.OK);
  }

}
