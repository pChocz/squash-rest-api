package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.service.RedisCacheService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** */
@Slf4j
@RestController
@RequestMapping("/redis-cache")
@RequiredArgsConstructor
public class RedisCacheController {

  private final RedisCacheService redisCacheService;

  @GetMapping(value = "/all")
  @PreAuthorize("isAdmin()")
  Set<String> getAllKeys() {
    return redisCacheService.getAllKeys();
  }

  @DeleteMapping(value = "/all")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("isAdmin()")
  void clearAll() {
    redisCacheService.clearAll();
  }

  @DeleteMapping(value = "/{cacheName}/{key}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("isAdmin()")
  void clearKey(@PathVariable final String cacheName, @PathVariable final String key) {
    redisCacheService.clearSingle(cacheName, key);
  }
}
