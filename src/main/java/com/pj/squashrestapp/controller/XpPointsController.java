package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.dto.XpPointsForTable;
import com.pj.squashrestapp.service.XpPointsService;
import com.pj.squashrestapp.util.TimeLogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/xpPoints")
@RequiredArgsConstructor
public class XpPointsController {

  private final XpPointsService xpPointsService;

//  @GetMapping
//  @ResponseBody
//  Map<String, Collection<Integer>> allMultimap() {
//    final long startTime = System.nanoTime();
//
//    final Map<String, Collection<Integer>> multimap = xpPointsService.buildAllAsIntegerMultimap().asMap();
//
//    TimeLogUtil.logFinish(startTime);
//    return multimap;
//  }
//
//  @GetMapping(value = "/{split}")
//  @ResponseBody
//  List<Integer> listForSplit(@PathVariable("split") final String split) {
//    final long startTime = System.nanoTime();
//
//    final List<Integer> list = xpPointsService.buildForGivenSplit(split);
//
//    TimeLogUtil.logFinish(startTime);
//    return list;
//  }

  @GetMapping(value = "/all-for-table")
  @ResponseBody
  List<XpPointsForTable> extractAllForTable() {
    final long startTime = System.nanoTime();

    final List<XpPointsForTable> xpPointsForTableList = xpPointsService.buildXpPointsForTable();

    TimeLogUtil.logQuery(startTime, "XP points");

    return xpPointsForTableList;
  }

}
