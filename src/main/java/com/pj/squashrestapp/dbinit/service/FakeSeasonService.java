package com.pj.squashrestapp.dbinit.service;

import com.google.common.collect.ArrayListMultimap;
import com.pj.squashrestapp.dbinit.fake.FakeAdditionalMatches;
import com.pj.squashrestapp.dbinit.fake.FakeBonusPoints;
import com.pj.squashrestapp.dbinit.fake.FakePlayersSelector;
import com.pj.squashrestapp.dbinit.fake.FakePlayersSelectorRoundGroupAware;
import com.pj.squashrestapp.dbinit.fake.FakeRound;
import com.pj.squashrestapp.dbinit.fake.FakeUtil;
import com.pj.squashrestapp.model.AdditionalMatch;
import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.Season;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class FakeSeasonService {

    private final FakePlayersSelectorRoundGroupAware fakePlayersSelectorRoundGroupAware;

    public Season create(
            final League league,
            final int seasonNumber,
            final LocalDate seasonStartDate,
            final int numberOfRounds,
            final List<Player> allPlayers,
            final int minNumberOfAttendingPlayers,
            final int maxNumberOfAttendingPlayers,
            final String xpPointsType) {

        final Season season = new Season(seasonNumber, seasonStartDate, xpPointsType, league);

        LocalDate roundDate = seasonStartDate;
        for (int roundNumber = 1; roundNumber <= numberOfRounds; roundNumber++) {

            final int numberOfRoundPlayers =
                    FakeUtil.randomBetweenTwoIntegers(minNumberOfAttendingPlayers, maxNumberOfAttendingPlayers);

            // easy way - just randomize
            Collections.shuffle(allPlayers);
            final List<Player> roundPlayers = allPlayers.subList(0, numberOfRoundPlayers);
            final ArrayListMultimap<Integer, Player> attendingPlayersGrouped = FakePlayersSelector.select(roundPlayers);

            // hard way - roundGroup aware assigner
            //      final ArrayListMultimap<Integer, Player> attendingPlayersGrouped =
            // fakePlayersSelectorRoundGroupAware.select(allPlayers, numberOfRoundPlayers, roundNumber,
            // seasonNumber, league);
            //      final List<Player> roundPlayers = new ArrayList<>(attendingPlayersGrouped.values());

            final Round round = FakeRound.create(season, roundNumber, roundDate, attendingPlayersGrouped);
            season.addRound(round);

            final List<BonusPoint> bonusPoints = FakeBonusPoints.create(roundPlayers, round.getDate(), 0, 2, 1, 2);

            for (final BonusPoint bonusPoint : bonusPoints) {
                season.addBonusPoint(bonusPoint);
            }

            final List<AdditionalMatch> additionalMatches =
                    FakeAdditionalMatches.create(league, seasonNumber, roundPlayers, round.getDate(), 0, 2);

            for (final AdditionalMatch additionalMatch : additionalMatches) {
                league.addAdditionalMatch(additionalMatch);
            }

            roundDate = roundDate.plusWeeks(1);
        }

        return season;
    }
}
