package com.pj.squashrestapp.dbinit.controller;

import com.pj.squashrestapp.dbinit.service.AdminInitializerService;
import com.pj.squashrestapp.dbinit.service.FakeLeagueService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/db-initializers")
public class InitializerController {

  @Autowired
  private FakeLeagueService fakeLeagueService;

  @Autowired
  private AdminInitializerService adminInitializerService;


  @PostMapping(value = "/leagues")
  @ResponseBody
  @PreAuthorize("isAdmin()")
  void createFakeLeague(
          @RequestParam("leagueName") final String leagueName,
          @RequestParam("numberOfCompletedSeasons") final int numberOfCompletedSeasons,
          @RequestParam("numberOfRoundsInLastSeason") final int numberOfRoundsInLastSeason,
          @RequestParam("numberOfAllPlayers") final int numberOfAllPlayers,
          @RequestParam("minNumberOfAttendingPlayers") final int minNumberOfAttendingPlayers,
          @RequestParam("maxNumberOfAttendingPlayers") final int maxNumberOfAttendingPlayers,
          @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") final LocalDate startDate,
          @RequestParam("logo") final MultipartFile logoFile) throws IOException {

    final byte[] logoBytes = logoFile.getBytes();

    fakeLeagueService.buildLeague(
            leagueName,
            numberOfCompletedSeasons,
            numberOfRoundsInLastSeason,
            numberOfAllPlayers,
            minNumberOfAttendingPlayers,
            maxNumberOfAttendingPlayers,
            startDate,
            logoBytes);
  }

  @PostMapping(value = "/json")
  @ResponseBody
  void createInitialDatabaseStructure(
          @RequestParam("init-admin") final MultipartFile initAdminXmlFile,
          @RequestParam("init-xp-points") final MultipartFile initXpPointsFile,
          @RequestParam("init-league") final MultipartFile initLeagueFile,
          @RequestParam("init-credentials") final MultipartFile initCredentialsFile) throws Exception {

    final String initAdminJsonContent = IOUtils.toString(initAdminXmlFile.getInputStream(), Charset.defaultCharset());
    final String initXpPointsJsonContent = IOUtils.toString(initXpPointsFile.getInputStream(), Charset.defaultCharset());
    final String initLeagueJsonContent = IOUtils.toString(initLeagueFile.getInputStream(), Charset.defaultCharset());
    final String initCredentialsJsonContent = IOUtils.toString(initCredentialsFile.getInputStream(), Charset.defaultCharset());

    final boolean initialized = adminInitializerService.initialize(
            initAdminJsonContent,
            initXpPointsJsonContent,
            initLeagueJsonContent,
            initCredentialsJsonContent);

    if (initialized) {
      log.info("Database initialized properly");
    } else {
      log.info("It seems that database has already been populated earlier so we leave it as is");
    }
  }

}
