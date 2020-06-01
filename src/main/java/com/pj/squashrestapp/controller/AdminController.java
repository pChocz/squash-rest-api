package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

  @Autowired
  private PlayerRepository playerRepository;

  @RequestMapping
  public String aboutMe() {
    return "Welcome Admin";
  }

}











