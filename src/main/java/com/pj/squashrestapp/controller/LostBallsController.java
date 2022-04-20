package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.dto.LostBallsDto;
import com.pj.squashrestapp.model.LostBall;
import com.pj.squashrestapp.service.LostBallService;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/** */
@Slf4j
@RestController
@RequestMapping("/lost-balls")
@RequiredArgsConstructor
public class LostBallsController {

    private final LostBallService lostBallService;

    @PostMapping
    @PreAuthorize("hasRoleForSeason(#seasonUuid, 'PLAYER')")
    LostBall createNewLostBall(
            @RequestParam final UUID playerUuid,
            @RequestParam final UUID seasonUuid,
            @RequestParam @DateTimeFormat(pattern = GeneralUtil.DATE_FORMAT) final LocalDate date,
            @RequestParam final int count) {
        final LostBall lostBall = lostBallService.applyLostBallForPlayer(playerUuid, seasonUuid, date, count);
        return lostBall;
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRoleForLostBall(#uuid, 'MODERATOR')")
    void deleteLostBall(@PathVariable final UUID uuid) {
        lostBallService.deleteLostBall(uuid);
    }

    @GetMapping("/seasons/{seasonUuid}")
    List<LostBallsDto> getBonusPointsForSeason(@PathVariable final UUID seasonUuid) {
        final List<LostBall> lostBalls = lostBallService.extractLostBalls(seasonUuid);
        final List<LostBallsDto> lostBallsForSeason =
                lostBalls.stream().map(LostBallsDto::new).collect(Collectors.toList());
        return lostBallsForSeason;
    }
}
