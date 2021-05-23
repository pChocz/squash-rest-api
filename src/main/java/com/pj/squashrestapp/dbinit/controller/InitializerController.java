package com.pj.squashrestapp.dbinit.controller;

import com.google.gson.reflect.TypeToken;
import com.pj.squashrestapp.aspects.SecretMethod;
import com.pj.squashrestapp.dbinit.jsondto.JsonFakeLeagueParams;
import com.pj.squashrestapp.dbinit.service.AdminInitializerService;
import com.pj.squashrestapp.dbinit.service.FakeLeagueService;
import com.pj.squashrestapp.util.GsonUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
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

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/init")
@RequiredArgsConstructor
public class InitializerController {

  private final FakeLeagueService fakeLeagueService;
  private final AdminInitializerService adminInitializerService;


  @PostMapping(value = "/leagues")
  @PreAuthorize("isAdmin()")
  void createFakeLeague(@RequestParam final MultipartFile initFakeLeagues) throws IOException {

    final String jsonContent = IOUtils.toString(initFakeLeagues.getInputStream(), Charset.defaultCharset());
    final Type listOfMyClassObject = new TypeToken<ArrayList<JsonFakeLeagueParams>>() {
    }.getType();
    final List<JsonFakeLeagueParams> fakeLeagueParams = GsonUtil.gsonWithDateAndDateTime().fromJson(jsonContent, listOfMyClassObject);

    fakeLeagueService.buildLeagues(fakeLeagueParams);
  }


  @PostMapping(value = "/json")
  void createInitialDatabaseStructure(@RequestParam final MultipartFile initJson) throws IOException {
    final String initAllJsonContent = IOUtils.toString(initJson.getInputStream(), Charset.defaultCharset());
    final boolean initialized = adminInitializerService.initialize(initAllJsonContent);
    if (initialized) {
      log.info("Database initialized properly");
    } else {
      throw new UnsupportedOperationException("It seems that database has already been populated earlier so we leave it as is");
    }
  }

  @GetMapping(value = "/argon2")
  @PreAuthorize("isAdmin()")
  @SecretMethod
  ResponseEntity<String> checkArgon2(@RequestParam final String rawPassword,
                                     @RequestParam final int saltLength,
                                     @RequestParam final int hashLength,
                                     @RequestParam final int parallelism,
                                     @RequestParam final int memory,
                                     @RequestParam final int iterations) {

    final Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder(
            saltLength,
            hashLength,
            parallelism,
            memory,
            iterations);

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

    final String info = "Encoding: [" + encodingTimeMillis + " ms] "
                        + "\tMatching: [" + matchingTimeMillis + " ms] - " + matches
                        + "\n[" + encodedPassword + "]";

    return new ResponseEntity<>(info, HttpStatus.OK);
  }

}
