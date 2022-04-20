package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.dto.XpPointsForTable;
import com.pj.squashrestapp.service.XpPointsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** */
@Slf4j
@RestController
@RequestMapping("/xp-points")
@RequiredArgsConstructor
public class XpPointsController {

    private final XpPointsService xpPointsService;

    @GetMapping
    List<XpPointsForTable> getAllXpPoints() {
        final List<XpPointsForTable> xpPointsForTableList = xpPointsService.buildXpPointsForTableAll();
        return xpPointsForTableList;
    }

    @GetMapping("/{type}")
    List<XpPointsForTable> getAllXpPointsForType(@PathVariable final String type) {
        final List<XpPointsForTable> xpPointsForTableList = xpPointsService.buildXpPointsForTableForType(type);
        return xpPointsForTableList;
    }

    @GetMapping("/types")
    List<String> getAllXpPointsTypes() {
        final List<String> xpPointsTypes = xpPointsService.getTypes();
        return xpPointsTypes;
    }

    @PostMapping
    @PreAuthorize("isAdmin()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void createXpPoints(
            @RequestParam final String type, @RequestParam final int[] split, @RequestParam final String[] points) {
        xpPointsService.addNewXpPoints(type, split, points);
    }

    @DeleteMapping
    @PreAuthorize("isAdmin()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteXpPoints(@RequestParam final String type, @RequestParam final int[] split) {
        xpPointsService.deleteExistingXpPoints(type, split);
    }
}
