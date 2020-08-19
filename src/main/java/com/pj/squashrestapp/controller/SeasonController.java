package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.dto.SeasonDto;
import com.pj.squashrestapp.service.SeasonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/seasons")
public class SeasonController {

  @Autowired
  private SeasonService seasonService;

  @GetMapping(value = "/{seasonId}")
//  @PreAuthorize("isAdmin()")
  @ResponseBody
  SeasonDto dummyGetEndpoint(@PathVariable final Long seasonId) {
    final SeasonDto seasonDto = seasonService.extractSeasonDtoById(seasonId);
    return seasonDto;
  }

}
