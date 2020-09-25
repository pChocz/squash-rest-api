package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.AuthorityType;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.dto.LeagueRoleDto;
import com.pj.squashrestapp.model.dto.PlayerDetailedDto;
import com.pj.squashrestapp.service.PlayerService;
import com.pj.squashrestapp.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenController {

  private final TokenService tokenService;


  @GetMapping(value = "/passwordReset/{passwordResetToken}")
  @ResponseBody
  PlayerDetailedDto getPlayerForPasswordReset(@PathVariable("passwordResetToken") final UUID passwordResetToken) {
    final PlayerDetailedDto player = tokenService.extractPlayerByPasswordResetToken(passwordResetToken);
    return player;
  }

}
