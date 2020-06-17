package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.dto.PlayerDetailedDto;
import com.pj.squashrestapp.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
@RequestMapping("/me")
public class MeController {

  @Autowired
  private PlayerRepository playerRepository;

  @GetMapping
  @ResponseBody
  PlayerDetailedDto aboutMe() {
    final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    final Player player = playerRepository.fetchForAuthorizationByUsernameOrEmail(auth.getName()).get();
    final PlayerDetailedDto userBasicInfo = new PlayerDetailedDto(player);
    return userBasicInfo;
  }

}
