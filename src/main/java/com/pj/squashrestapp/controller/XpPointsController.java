package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.dto.XpPointsForTable;
import com.pj.squashrestapp.service.XpPointsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/xp-points")
@RequiredArgsConstructor
public class XpPointsController {

  private final XpPointsService xpPointsService;


  @GetMapping
  @ResponseBody
  List<XpPointsForTable> extractAllForTable() {
    final List<XpPointsForTable> xpPointsForTableList = xpPointsService.buildXpPointsForTableAll();
    return xpPointsForTableList;
  }


  @GetMapping("/{type}")
  @ResponseBody
  List<XpPointsForTable> extractAllForTableForType(@PathVariable final String type) {
    final List<XpPointsForTable> xpPointsForTableList = xpPointsService.buildXpPointsForTableForType(type);
    return xpPointsForTableList;
  }


  @GetMapping("/types")
  @ResponseBody
  List<String> extractTypes() {
    final List<String> xpPointsTypes = xpPointsService.getTypes();
    return xpPointsTypes;
  }

}
