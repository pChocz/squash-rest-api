package com.pj.squashrestapp.dbinit.jsondto.util;

import com.google.gson.Gson;
import com.pj.squashrestapp.dbinit.jsondto.JsonMatch;
import com.pj.squashrestapp.dbinit.jsondto.JsonPlayer;
import com.pj.squashrestapp.dbinit.jsondto.JsonRound;
import com.pj.squashrestapp.dbinit.jsondto.JsonRoundGroup;
import com.pj.squashrestapp.dbinit.jsondto.JsonSetResult;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.SetResult;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 */
@Slf4j
@UtilityClass
public class JsonExportUtil {

  public String backupRoundToJson(final Round round) {
    final JsonRound jsonRound = new JsonRound();
    jsonRound.setDate(round.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    jsonRound.setNumber(round.getNumber());

    final ArrayList<JsonRoundGroup> jsonRoundGroups = new ArrayList<>();
    for (final RoundGroup roundGroup : round.getRoundGroupsOrdered()) {
      final JsonRoundGroup jsonRoundGroup = new JsonRoundGroup();
      jsonRoundGroup.setNumber(roundGroup.getNumber());

      final Set<JsonPlayer> jsonPlayers = new LinkedHashSet<>();
      for (final Match match : roundGroup.getMatches()) {
        final JsonPlayer jsonPlayer1 = new JsonPlayer();
        jsonPlayer1.setName(match.getFirstPlayer().getUsername());
        jsonPlayers.add(jsonPlayer1);

        final JsonPlayer jsonPlayer2 = new JsonPlayer();
        jsonPlayer2.setName(match.getSecondPlayer().getUsername());
        jsonPlayers.add(jsonPlayer2);
      }
      jsonRoundGroup.setPlayers(new ArrayList<>(jsonPlayers));

      final ArrayList<JsonMatch> jsonMatches = new ArrayList<>();
      for (final Match match : roundGroup.getMatches()) {
        final JsonMatch jsonMatch = new JsonMatch();
        jsonMatch.setFirstPlayer(match.getFirstPlayer().getUsername());
        jsonMatch.setSecondPlayer(match.getSecondPlayer().getUsername());

        final ArrayList<JsonSetResult> jsonSetResults = new ArrayList<>();
        for (final SetResult setResult : match.getSetResults()) {
          if (isNotNull(setResult)) {
            final JsonSetResult jsonSetResult = new JsonSetResult();
            jsonSetResult.setFirstPlayerResult(setResult.getFirstPlayerScore());
            jsonSetResult.setSecondPlayerResult(setResult.getSecondPlayerScore());

            jsonSetResults.add(jsonSetResult);
          }
        }
        jsonMatch.setSets(jsonSetResults);


        jsonMatches.add(jsonMatch);
      }
      jsonRoundGroup.setMatches(jsonMatches);


      jsonRoundGroups.add(jsonRoundGroup);
    }
    jsonRound.setGroups(jsonRoundGroups);

    final String roundJson = new Gson().toJson(jsonRound);
    return roundJson;
  }

  private boolean isNotNull(final SetResult setResult) {
    return setResult.getFirstPlayerScore() != null
           && setResult.getSecondPlayerScore() != null;
  }

}
