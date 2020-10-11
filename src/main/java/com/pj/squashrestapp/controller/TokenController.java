package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.dto.PlayerDetailedDto;
import com.pj.squashrestapp.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

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
  PlayerDetailedDto getPlayerForPasswordReset(@PathVariable final UUID passwordResetToken) {
    final PlayerDetailedDto player = tokenService.extractPlayerByPasswordResetToken(passwordResetToken);
    return player;
  }

}
