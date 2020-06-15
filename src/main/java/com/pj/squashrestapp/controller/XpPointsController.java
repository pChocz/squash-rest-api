package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.XpPointsForRound;
import com.pj.squashrestapp.util.TimeLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/xpPoints")
public class XpPointsController {

  @Autowired
  private XpPointsService xpPointsService;

  @RequestMapping(
          value = "/allMultimap",
          method = GET)
  @ResponseBody
  Map<String, Collection<Integer>> allMultimap() {
    final long startTime = System.nanoTime();

    final Map<String, Collection<Integer>> multimap = xpPointsService.buildAllAsIntegerMultimap().asMap();

    TimeLogUtil.logFinish(startTime);
    return multimap;
  }

  @RequestMapping(
          value = "/listForSplit",
          params = {"split"},
          method = GET)
  @ResponseBody
  List<Integer> listForSplit(@RequestParam("split") final String split) {
    final long startTime = System.nanoTime();

    final List<Integer> list = xpPointsService.buildForGivenSplit(split);

    TimeLogUtil.logFinish(startTime);
    return list;
  }

  @RequestMapping(
          value = "/allNativeObject",
          method = GET)
  @ResponseBody
  List<XpPointsForRound> allNativeObject() {
    final long startTime = System.nanoTime();

    final List<XpPointsForRound> xpPointsForRoundList = xpPointsService.buildAllAsNativeObject();

    TimeLogUtil.logFinish(startTime);
    return xpPointsForRoundList;
  }

}
