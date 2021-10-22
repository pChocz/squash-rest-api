package com.pj.squashrestapp.dbinit.controller;

import com.google.gson.reflect.TypeToken;
import com.pj.squashrestapp.aspects.SecretMethod;
import com.pj.squashrestapp.dbinit.jsondto.JsonFakeLeagueParams;
import com.pj.squashrestapp.dbinit.jsondto.JsonXpPointsForRound;
import com.pj.squashrestapp.dbinit.service.AdminInitializerService;
import com.pj.squashrestapp.dbinit.service.FakeLeagueService;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.util.GsonUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** */
@Slf4j
@RestController
@RequestMapping("/init")
@RequiredArgsConstructor
public class InitializerController {

  private final FakeLeagueService fakeLeagueService;
  private final AdminInitializerService adminInitializerService;
  private final PlayerRepository playerRepository;

  @PostMapping(value = "/leagues")
  @PreAuthorize("isAdmin()")
  void createFakeLeague(@RequestParam final MultipartFile initFakeLeagues) throws IOException {

    final String jsonContent =
        IOUtils.toString(initFakeLeagues.getInputStream(), StandardCharsets.UTF_8);
    final Type listOfMyClassObject = new TypeToken<ArrayList<JsonFakeLeagueParams>>() {}.getType();
    final List<JsonFakeLeagueParams> fakeLeagueParams =
        GsonUtil.gsonWithDateAndDateTime().fromJson(jsonContent, listOfMyClassObject);

    fakeLeagueService.buildLeagues(fakeLeagueParams);
  }

  @PostMapping(value = "/json")
  void createInitialDatabaseStructure(@RequestParam final MultipartFile initJson)
      throws IOException {
    final String initAllJsonContent =
        IOUtils.toString(initJson.getInputStream(), StandardCharsets.UTF_8);
    final boolean initialized = adminInitializerService.initialize(initAllJsonContent);
    if (initialized) {
      log.info("Database initialized properly");
    } else {
      throw new UnsupportedOperationException(
          "It seems that database has already been populated earlier so we leave it as is");
    }
  }

  @GetMapping(value = "/argon2")
  @PreAuthorize("isAdmin()")
  @SecretMethod
  ResponseEntity<String> checkArgon2(
      @RequestParam final String rawPassword,
      @RequestParam final int saltLength,
      @RequestParam final int hashLength,
      @RequestParam final int parallelism,
      @RequestParam final int memory,
      @RequestParam final int iterations) {

    final Argon2PasswordEncoder argon2PasswordEncoder =
        new Argon2PasswordEncoder(saltLength, hashLength, parallelism, memory, iterations);

    final StopWatch stopWatchEncode = new StopWatch();
    stopWatchEncode.start();
    final String encodedPassword = argon2PasswordEncoder.encode(rawPassword);
    stopWatchEncode.stop();
    final long encodingTimeMillis = stopWatchEncode.getTotalTimeMillis();

    final StopWatch stopWatchMatch = new StopWatch();
    stopWatchMatch.start();
    final boolean matches = argon2PasswordEncoder.matches(rawPassword, encodedPassword);
    stopWatchMatch.stop();
    final long matchingTimeMillis = stopWatchMatch.getTotalTimeMillis();

    final String info =
        "Encoding: ["
            + encodingTimeMillis
            + " ms] "
            + "\tMatching: ["
            + matchingTimeMillis
            + " ms] - "
            + matches
            + "\n["
            + encodedPassword
            + "]";

    return new ResponseEntity<>(info, HttpStatus.OK);
  }

  @PostMapping(value = "/xp-points")
  @PreAuthorize("isAdmin()")
  List<JsonXpPointsForRound> createXpPoints(
      @RequestParam final int firstPlacePoints,
      @RequestParam final int percentToSubstract) {

    final List<JsonXpPointsForRound> list = new ArrayList<>();

    final List<String> allSplits = List.of(
        "4",
        "5",
        "6",
        "7",
        "8",
        "4,4",
        "4,5",
        "5,4",
        "5,5",
        "5,6",
        "6,5",
        "6,6",
        "6,7",
        "7,6",
        "7,7",
        "4,5,5",
        "5,4,5",
        "5,5,4",
        "5,5,5",
        "5,5,6",
        "5,6,5",
        "6,5,5",
        "4,4,4,4",
        "4,4,4,5",
        "4,4,5,5"
    );

    final double multiplier = (100.0-percentToSubstract)/100.0;

    for (final String split : allSplits) {

      JsonXpPointsForRound xpPointsForRound = new JsonXpPointsForRound();
      xpPointsForRound.setType("FIXED_NEW");
      xpPointsForRound.setNumberOfPlayersCsv(split);
      final List<String> pointsPerGroupCsv = new ArrayList<>();

      final String[] countGroups = split.split(",");
      int place = 0;
      for (int i=0; i<countGroups.length; i++) {
        final int count = Integer.valueOf(countGroups[i]);
        final List<Long> points = new ArrayList<>();
        for (int j=0; j<count; j++){
          points.add(Math.round(firstPlacePoints * Math.pow(multiplier, place-i)));
          place++;
        }
        final String pointsPerGroup = longListToString(points).replaceAll(" \\| ", ",");
        pointsPerGroupCsv.add(pointsPerGroup);
      }

      xpPointsForRound.setPointsCsv(pointsPerGroupCsv);

      list.add(xpPointsForRound);

    }
    return list;
  }

  public String longListToString(final List<Long> longList) {
    return longList.stream().map(Object::toString).collect(Collectors.joining(" | "));
  }


}
