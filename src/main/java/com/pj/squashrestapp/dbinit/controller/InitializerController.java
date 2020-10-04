package com.pj.squashrestapp.dbinit.controller;

import com.google.gson.reflect.TypeToken;
import com.pj.squashrestapp.dbinit.jsondto.JsonFakeLeagueParams;
import com.pj.squashrestapp.dbinit.service.AdminInitializerService;
import com.pj.squashrestapp.dbinit.service.FakeLeagueService;
import com.pj.squashrestapp.util.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
@RequestMapping("/db-initializers")
@RequiredArgsConstructor
public class InitializerController {

  private final FakeLeagueService fakeLeagueService;
  private final AdminInitializerService adminInitializerService;

  @PostMapping(value = "/leagues")
  @ResponseBody
  @PreAuthorize("isAdmin()")
  void createFakeLeague(
          @RequestParam("init-fake-leagues") final MultipartFile initDefaultUsersFile)
          throws IOException {

    final String jsonContent = IOUtils.toString(initDefaultUsersFile.getInputStream(), Charset.defaultCharset());
    final Type listOfMyClassObject = new TypeToken<ArrayList<JsonFakeLeagueParams>>() {}.getType();
    final List<JsonFakeLeagueParams> fakeLeagueParams = GsonUtil.gsonWithDate().fromJson(jsonContent, listOfMyClassObject);

    fakeLeagueService.buildLeagues(fakeLeagueParams);
  }

  @PostMapping(value = "/json")
  @ResponseBody
  void createInitialDatabaseStructure(
          @RequestParam("init-xp-points") final MultipartFile initXpPointsFile,
          @RequestParam("init-all-leagues") final MultipartFile initAllLeaguesFile,
          @RequestParam("init-credentials") final MultipartFile initCredentialsFile) throws Exception {

    final String initXpPointsJsonContent = IOUtils.toString(initXpPointsFile.getInputStream(), Charset.defaultCharset());
    final String initAllLeaguesJsonContent = IOUtils.toString(initAllLeaguesFile.getInputStream(), Charset.defaultCharset());
    final String initCredentialsJsonContent = IOUtils.toString(initCredentialsFile.getInputStream(), Charset.defaultCharset());

    final boolean initialized = adminInitializerService.initialize(
            initXpPointsJsonContent,
            initAllLeaguesJsonContent,
            initCredentialsJsonContent);

    if (initialized) {
      log.info("Database initialized properly");
    } else {
      log.info("It seems that database has already been populated earlier so we leave it as is");
    }
  }

}
