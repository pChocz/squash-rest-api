package com.pj.squashrestapp.dbinit.controller;

import com.google.gson.reflect.TypeToken;
import com.pj.squashrestapp.dbinit.jsondto.JsonFakeLeagueParams;
import com.pj.squashrestapp.dbinit.service.AdminInitializerService;
import com.pj.squashrestapp.dbinit.service.FakeLeagueService;
import com.pj.squashrestapp.util.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

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
  @ResponseStatus(HttpStatus.OK)
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

}
