package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.dto.XpPointsForTable;
import com.pj.squashrestapp.service.XpPointsService;
import com.pj.squashrestapp.util.TimeLogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
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
    final List<XpPointsForTable> xpPointsForTableList = xpPointsService.buildXpPointsForTable();
    return xpPointsForTableList;
  }

}
