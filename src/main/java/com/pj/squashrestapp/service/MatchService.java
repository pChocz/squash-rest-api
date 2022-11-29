package com.pj.squashrestapp.service;

import com.pj.squashrestapp.config.exceptions.GeneralBadRequestException;
import com.pj.squashrestapp.dto.match.AdditionalMatchDetailedDto;
import com.pj.squashrestapp.dto.match.AdditionalMatchSimpleDto;
import com.pj.squashrestapp.dto.match.MatchDetailedDto;
import com.pj.squashrestapp.dto.match.MatchDto;
import com.pj.squashrestapp.dto.match.MatchSimpleDto;
import com.pj.squashrestapp.dto.match.MatchesSimplePaginated;
import com.pj.squashrestapp.dto.matchresulthelper.SetScoreHelper;
import com.pj.squashrestapp.dto.matchresulthelper.WrongResultException;
import com.pj.squashrestapp.model.AdditionalMatch;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.repository.AdditionalMatchRepository;
import com.pj.squashrestapp.repository.MatchRepository;
import com.pj.squashrestapp.repository.SeasonRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.util.ErrorCode;
import com.pj.squashrestapp.util.GsonUtil;
import com.pj.squashrestapp.util.JacksonUtil;
import com.pj.squashrestapp.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final AdditionalMatchRepository additionalMatchRepository;
    private final SetResultRepository setResultRepository;
    private final SeasonRepository seasonRepository;
    private final RedisCacheService redisCacheService;

    public List<MatchDto> matchesWithFootageForLeague(final UUID leagueUuid) {
        final List<Match> matches = matchRepository.fetchMatchesWithFootageForLeague(leagueUuid);
        final List<AdditionalMatch> additionalMatches = additionalMatchRepository.fetchMatchesWithFootageForLeague(leagueUuid);
        final List<MatchDto> matchesDtos = Stream
                .concat(
                        matches.stream().map(MatchDetailedDto::new),
                        additionalMatches.stream().map(AdditionalMatchDetailedDto::new))
                .sorted(Comparator.comparing(MatchDto::getDate).reversed())
                .collect(Collectors.toList());
        return matchesDtos;
    }

    public MatchDetailedDto modifySingleScore(
            final UUID matchUuid, final int setNumber, final String player, final Integer looserScore) {
        final Match matchToModify = matchRepository.findMatchByUuidWithScoreSheet(matchUuid).orElseThrow();
        final Object initialMatchResult = JacksonUtil.deepCopy(new MatchSimpleDto(matchToModify));

        if (!matchToModify.getScores().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorCode.MATCH_SCORE_NOT_EMPTY);
        }

        final SetResult setToModify = matchToModify.getSetResults().stream()
                .filter(set -> set.getNumber() == setNumber)
                .findFirst()
                .orElse(null);

        if (looserScore == -1) {
            setToModify.setFirstPlayerScore(null);
            setToModify.setSecondPlayerScore(null);

        } else {

            final Integer winnerScore;
            try {
                if (setIsTiebreak(matchToModify, setNumber)) {
                    winnerScore = SetScoreHelper.computeWinnerScore(
                            looserScore,
                            matchToModify.getTiebreakWinningPoints(),
                            matchToModify.getTiebreakWinningType());
                } else {
                    winnerScore = SetScoreHelper.computeWinnerScore(
                            looserScore,
                            matchToModify.getRegularSetWinningPoints(),
                            matchToModify.getRegularSetWinningType());
                }
            } catch (final WrongResultException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_MATCH_RESULT);
            }

            if (player.equals("FIRST")) {
                setToModify.setFirstPlayerScore(looserScore);
                setToModify.setSecondPlayerScore(winnerScore);

            } else if (player.equals("SECOND")) {
                setToModify.setFirstPlayerScore(winnerScore);
                setToModify.setSecondPlayerScore(looserScore);
            }
        }

        matchToModify.updateAudit();
        matchRepository.save(matchToModify);
        LogUtil.logModify(initialMatchResult, new MatchSimpleDto(matchToModify));

        redisCacheService.evictCacheForRoundMatch(matchToModify);
        return new MatchDetailedDto(matchToModify);
    }

    private boolean setIsTiebreak(final Match match, final int setNumber) {
        return setNumber == match.getMatchFormatType().getMaxNumberOfSets();
    }

    public MatchesSimplePaginated getRoundMatchesPaginated(
            final Pageable pageable,
            final UUID leagueUuid,
            final UUID[] playersUuids,
            final UUID seasonUuid,
            final Integer groupNumber,
            final LocalDate dateFrom,
            final LocalDate dateTo) {

        final Page<Long> roundMatchesIds = (playersUuids.length == 1)
                ? matchRepository.findIdsSingle(pageable, leagueUuid, playersUuids[0], seasonUuid, groupNumber, dateFrom, dateTo)
                : matchRepository.findIdsMultiple(pageable, leagueUuid, playersUuids, seasonUuid, groupNumber, dateFrom, dateTo);

        final List<Match> roundMatches = matchRepository.findByIdIn(roundMatchesIds.getContent());

        final List<MatchDto> roundMatchesDtos =
                roundMatches.stream().map(MatchDetailedDto::new).collect(Collectors.toList());

        final MatchesSimplePaginated matchesDtoPage = new MatchesSimplePaginated(roundMatchesIds, roundMatchesDtos);
        return matchesDtoPage;
    }

    public MatchesSimplePaginated getAdditionalMatchesPaginated(
            final Pageable pageable,
            final UUID leagueUuid,
            final UUID[] playersUuids,
            final UUID seasonUuid,
            final LocalDate dateFrom,
            final LocalDate dateTo) {

        final Integer seasonNumber = seasonUuid == null
                ? null
                : seasonRepository.findByUuid(seasonUuid).get().getNumber();

        final Page<Long> additionalMatchesIds = (playersUuids.length == 1)
                ? additionalMatchRepository.findIdsSingle(pageable, leagueUuid, playersUuids[0], seasonNumber, dateFrom, dateTo)
                : additionalMatchRepository.findIdsMultiple(pageable, leagueUuid, playersUuids, seasonNumber, dateFrom, dateTo);

        final List<AdditionalMatch> additionalMatches =
                additionalMatchRepository.findByIdIn(additionalMatchesIds.getContent());

        final List<MatchDto> additionalMatchesDtos =
                additionalMatches.stream().map(AdditionalMatchSimpleDto::new).collect(Collectors.toList());

        final MatchesSimplePaginated matchesDtoPage =
                new MatchesSimplePaginated(additionalMatchesIds, additionalMatchesDtos);
        return matchesDtoPage;
    }

    public MatchSimpleDto addOrReplaceFootage(final UUID matchUuid, final String footageLink) {
        final Match match = matchRepository
                .findMatchByUuid(matchUuid)
                .orElseThrow(() -> new GeneralBadRequestException(ErrorCode.MATCH_NOT_FOUND));
        final Object matchBefore = JacksonUtil.deepCopy(new MatchSimpleDto(match));
        match.setFootageLink(footageLink);
        matchRepository.save(match);
        LogUtil.logModify(matchBefore, new MatchSimpleDto(match));
        redisCacheService.evictCacheForRoundMatch(match);
        return new MatchSimpleDto(match);
    }
}
