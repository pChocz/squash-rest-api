package com.pj.squashrestapp.service;

import com.pj.squashrestapp.dto.LostBallsAggregatedForLeague;
import com.pj.squashrestapp.dto.LostBallsAggregatedForSeason;
import com.pj.squashrestapp.dto.LostBallsDto;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.model.LostBall;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.repository.LostBallRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.SeasonRepository;
import com.pj.squashrestapp.util.GsonUtil;
import com.pj.squashrestapp.util.JacksonUtil;
import com.pj.squashrestapp.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class LostBallService {

    private final PlayerRepository playerRepository;
    private final SeasonRepository seasonRepository;
    private final LostBallRepository lostBallRepository;
    private final RedisCacheService redisCacheService;

    public List<LostBall> extractLostBalls(final UUID seasonUuid) {
        final List<LostBall> lostBalls = lostBallRepository.findBySeasonUuid(seasonUuid);
        return lostBalls;
    }

    public LostBallsAggregatedForSeason extractLostBallsAggregatedForSeason(final UUID seasonUuid) {
        final List<LostBall> lostBalls = lostBallRepository.findBySeasonUuid(seasonUuid);
        final LostBallsAggregatedForSeason bonusPointsAggregatedForSeason =
                new LostBallsAggregatedForSeason(seasonUuid, lostBalls);
        return bonusPointsAggregatedForSeason;
    }

    public LostBallsAggregatedForLeague extractLostBallsAggregatedForLeague(final UUID leagueUuid) {
        final List<LostBall> lostBalls = lostBallRepository.findByLeagueUuid(leagueUuid);
        final LostBallsAggregatedForLeague lostBallsAggregatedForLeague =
                new LostBallsAggregatedForLeague(leagueUuid, lostBalls);
        return lostBallsAggregatedForLeague;
    }

    @Transactional
    public LostBall applyLostBallForPlayer(
            final UUID playerUuid, final UUID seasonUuid, final LocalDate date, final int count) {
        final Season season = seasonRepository.findSeasonByUuid(seasonUuid).orElseThrow();
        final Player player = playerRepository.findByUuid(playerUuid);

        final LostBall lostBall = new LostBall();
        lostBall.setPlayer(player);
        lostBall.setDate(date);
        lostBall.setCount(count);
        lostBall.createAudit();

        season.addLostBall(lostBall);
        lostBallRepository.save(lostBall);
        LogUtil.logCreate(new LostBallsDto(lostBall));

        redisCacheService.evictCacheForLostBall(lostBall);
        return lostBall;
    }

    @Transactional
    public void deleteLostBall(final UUID uuid) {
        final LostBall lostBall = lostBallRepository.findByUuid(uuid).orElseThrow();
        lostBallRepository.delete(lostBall);
        LogUtil.logDelete(new LostBallsDto(lostBall));
        redisCacheService.evictCacheForLostBall(lostBall);
    }
}
