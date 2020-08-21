package com.pj.squashrestapp.service;

import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.dto.MatchDto;
import com.pj.squashrestapp.model.entityhelper.MatchHelper;
import com.pj.squashrestapp.model.entityhelper.SetResultHelper;
import com.pj.squashrestapp.repository.MatchRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 */
@Slf4j
@Service
public class MatchService {

  @Autowired
  private MatchRepository matchRepository;

  @Autowired
  private SetResultRepository setResultRepository;


  public MatchDto getMatch(final Long matchId) {
    final Match match = matchRepository.findMatchById(matchId);
    final MatchDto matchDto = new MatchDto(match);
    return matchDto;
  }

  public void modifySingleScore(final Long matchId, final int setNumber, final String player, final Integer newScore) {
    final Match matchToModify = matchRepository.findMatchById(matchId);

    final String initialMatchResult = matchToModify.toString();

    final SetResult setToModify = matchToModify
            .getSetResults()
            .stream()
            .filter(set -> set.getNumber() == setNumber)
            .findFirst()
            .orElse(null);

    if (player.equals("FIRST")) {
      setToModify.setFirstPlayerScore(newScore);

      if (setNumber < 3) {
        if (newScore < 10) {
          setToModify.setSecondPlayerScore(11);
        } else if (newScore < 12) {
          setToModify.setSecondPlayerScore(12);
        }
      } else {
        if (newScore < 9) {
          setToModify.setSecondPlayerScore(9);
        }
      }

    } else if (player.equals("SECOND")) {
      setToModify.setSecondPlayerScore(newScore);

      if (setNumber < 3) {
        if (newScore < 10) {
          setToModify.setFirstPlayerScore(11);
        } else if (newScore < 12) {
          setToModify.setFirstPlayerScore(12);
        }
      } else {
        if (newScore < 9) {
          setToModify.setFirstPlayerScore(9);
        }
      }

    }

    setResultRepository.save(setToModify);

    final String message = "\nSuccesfully updated the match!" +
                           "\n\t-> " + initialMatchResult + "\t- earlier" +
                           "\n\t-> " + matchToModify + "\t- now";
    log.info(message);
  }

  public MatchDto modifyMatch(final Long matchId, final int setNumber, final int p1score, final int p2score) {
    final Match matchToModify = matchRepository.findMatchById(matchId);

    final String initialMatchResult = matchToModify.toString();
    final SetResult setToModify = matchToModify.getSetResults().stream().filter(set -> set.getNumber() == setNumber).findFirst().orElse(null);

    try {

      if (setToModify.getFirstPlayerScore() != 0
          || setToModify.getSecondPlayerScore() != 0) {
        throw new IllegalArgumentException();
      }

      setToModify.setFirstPlayerScore(p1score);
      setToModify.setSecondPlayerScore(p2score);

      final SetResultHelper setResultHelper = new SetResultHelper(setToModify);
      if (!setResultHelper.isValid()) {
        throw new IllegalArgumentException();
      }

      final MatchHelper matchHelper = new MatchHelper(matchToModify);
      if (!matchHelper.isValid()) {
        throw new IllegalArgumentException();
      }

      setResultRepository.save(setToModify);

      final String message = "\nSuccesfully updated the match!" +
                             "\n\t-> " + initialMatchResult + "\t- earlier" +
                             "\n\t-> " + matchToModify + "\t- now";
      log.info(message);


    } catch (final IllegalArgumentException e) {
      final String message = "\nDoes not look like a valid match result after the update!" +
                             "\n\t-> " + matchToModify + "\t- tried to update to look like this" +
                             "\n\t-> " + initialMatchResult + "\t- leaving the old result like this.";
      log.error(message);
    }

    final MatchDto matchDto = new MatchDto(matchToModify);
    return matchDto;
  }

  public MatchDto clearSingleSetOfMatch(final Long matchId, final int setNumber) {
    final Match matchToModify = matchRepository.findMatchById(matchId);

    final SetResult setToModify = matchToModify
            .getSetResults()
            .stream()
            .filter(set -> set.getNumber() == setNumber)
            .findFirst()
            .orElse(null);

    final String initialMatchResult = matchToModify.toString();

    setToModify.setFirstPlayerScore(0);
    setToModify.setSecondPlayerScore(0);

    setResultRepository.save(setToModify);

    final String message = "\nSuccesfully updated the match!" +
                           "\n\t-> " + initialMatchResult + "\t- earlier" +
                           "\n\t-> " + matchToModify + "\t- now";
    log.info(message);

    final MatchDto matchDto = new MatchDto(matchToModify);
    return matchDto;
  }

}
