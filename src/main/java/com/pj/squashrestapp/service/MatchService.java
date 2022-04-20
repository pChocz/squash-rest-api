package com.pj.squashrestapp.service;

import com.pj.squashrestapp.config.UserDetailsImpl;
import com.pj.squashrestapp.dto.match.AdditionalMatchSimpleDto;
import com.pj.squashrestapp.dto.match.MatchDetailedDto;
import com.pj.squashrestapp.dto.match.MatchDto;
import com.pj.squashrestapp.dto.match.MatchSimpleDto;
import com.pj.squashrestapp.dto.match.MatchesSimplePaginated;
import com.pj.squashrestapp.dto.matchresulthelper.MatchStatus;
import com.pj.squashrestapp.dto.matchresulthelper.SetScoreHelper;
import com.pj.squashrestapp.dto.matchresulthelper.WrongResultException;
import com.pj.squashrestapp.model.AdditionalMatch;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.repository.AdditionalMatchRepository;
import com.pj.squashrestapp.repository.MatchRepository;
import com.pj.squashrestapp.repository.RoundRepository;
import com.pj.squashrestapp.repository.SeasonRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.util.ErrorCode;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService {

    private final RoundRepository roundRepository;
    private final MatchRepository matchRepository;
    private final AdditionalMatchRepository additionalMatchRepository;
    private final SetResultRepository setResultRepository;
    private final SeasonRepository seasonRepository;
    private final RedisCacheService redisCacheService;

    public MatchSimpleDto modifySingleScore(
            final UUID matchUuid, final int setNumber, final String player, final Integer looserScore) {
        final Match matchToModify = matchRepository.findMatchByUuid(matchUuid).orElseThrow();
        final Round round = matchToModify.getRoundGroup().getRound();
        final UUID roundUuid = round.getUuid();
        final UUID leagueUuid = round.getSeason().getLeague().getUuid();

        final UserDetailsImpl currentUser = GeneralUtil.extractSessionUser();
        final UUID playerUuid = currentUser.getUuid();

        if (currentUser.isAdmin() || currentUser.hasRoleForLeague(leagueUuid, LeagueRole.MODERATOR)) {
            // full modify access for admins and league moderators

        } else if (roundRepository.checkIfPlayerOfRound(roundUuid, playerUuid)) {
            // limited modify access for current round players - only if in progress

            if (isMatchInProgress(matchToModify)) {
                // allow if match is in progress

            } else {
                log.error("No authorization to modify match that is finished\n{}", matchToModify);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorCode.MATCH_MODIFY_ERROR_IS_FINISHED);
            }

        } else {
            log.error("No authorization to modify match in this round\n{}", matchToModify);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorCode.MATCH_MODIFY_ERROR_NOT_ALLOWED);
        }

        final String initialMatchResult = matchToModify.toString();

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

        setResultRepository.save(setToModify);

        redisCacheService.evictCacheForRoundMatch(matchToModify);

        log.info(
                "Succesfully updated the match!\n\t-> {}\t- earlier\n\t-> {}\t- now",
                initialMatchResult,
                matchToModify);

        return new MatchSimpleDto(matchToModify);
    }

    private boolean isMatchInProgress(final Match matchToModify) {
        final MatchStatus matchStatus = new MatchSimpleDto(matchToModify).getStatus();
        return matchStatus != MatchStatus.FINISHED;
    }

    private boolean setIsTiebreak(final Match match, final int setNumber) {
        return setNumber == match.getMatchFormatType().getMaxNumberOfSets();
    }

    private Integer computeWinnerScore(final Integer looserScore, final int setNumber) {
        if (setNumber < 3) {
            return computeWinnerScoreForRegularSet(looserScore);
        } else {
            return 9;
        }
    }

    private Integer computeWinnerScoreForRegularSet(final Integer looserScore) {
        if (looserScore < 10) {
            return 11;
        } else {
            return 12;
        }
    }

    public MatchesSimplePaginated getRoundMatchesPaginated(
            final Pageable pageable,
            final UUID leagueUuid,
            final UUID[] playersUuids,
            final UUID seasonUuid,
            final Integer groupNumber) {

        final Page<Long> roundMatchesIds = (playersUuids.length == 1)
                ? matchRepository.findIdsSingle(pageable, leagueUuid, playersUuids[0], seasonUuid, groupNumber)
                : matchRepository.findIdsMultiple(pageable, leagueUuid, playersUuids, seasonUuid, groupNumber);

        final List<Match> roundMatches = matchRepository.findByIdIn(roundMatchesIds.getContent());

        final List<MatchDto> roundMatchesDtos =
                roundMatches.stream().map(MatchDetailedDto::new).collect(Collectors.toList());

        final MatchesSimplePaginated matchesDtoPage = new MatchesSimplePaginated(roundMatchesIds, roundMatchesDtos);
        return matchesDtoPage;
    }

    public MatchesSimplePaginated getAdditionalMatchesPaginated(
            final Pageable pageable, final UUID leagueUuid, final UUID[] playersUuids, final UUID seasonUuid) {

        final Integer seasonNumber = seasonUuid == null
                ? null
                : seasonRepository.findByUuid(seasonUuid).get().getNumber();

        final Page<Long> additionalMatchesIds = (playersUuids.length == 1)
                ? additionalMatchRepository.findIdsSingle(pageable, leagueUuid, playersUuids[0], seasonNumber)
                : additionalMatchRepository.findIdsMultiple(pageable, leagueUuid, playersUuids, seasonNumber);

        final List<AdditionalMatch> additionalMatches =
                additionalMatchRepository.findByIdIn(additionalMatchesIds.getContent());

        final List<MatchDto> additionalMatchesDtos =
                additionalMatches.stream().map(AdditionalMatchSimpleDto::new).collect(Collectors.toList());

        final MatchesSimplePaginated matchesDtoPage =
                new MatchesSimplePaginated(additionalMatchesIds, additionalMatchesDtos);
        return matchesDtoPage;
    }
}
