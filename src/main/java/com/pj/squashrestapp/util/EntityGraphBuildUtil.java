package com.pj.squashrestapp.util;

import com.google.common.collect.ArrayListMultimap;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.XpPointsForPlace;
import com.pj.squashrestapp.model.XpPointsForRound;
import com.pj.squashrestapp.model.XpPointsForRoundGroup;
import com.pj.squashrestapp.model.entityvisitor.ClassId;
import com.pj.squashrestapp.model.entityvisitor.EntityGraphBuilder;
import com.pj.squashrestapp.model.entityvisitor.EntityVisitor;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

/**
 * Entity Graph Reconstructor for the {@link SetResult} class that
 * allows to build entire structure above, up to league entity.
 */
@UtilityClass
public class EntityGraphBuildUtil {

  public League reconstructLeague(final List<SetResult> setResults, final Long leagueId) {
    final EntityGraphBuilder entityGraphBuilder = new EntityGraphBuilder(new EntityVisitor[]{
            SetResult.ENTITY_VISITOR,
            Match.ENTITY_VISITOR,
            RoundGroup.ENTITY_VISITOR,
            Round.ENTITY_VISITOR,
            Season.ENTITY_VISITOR,
            League.ENTITY_VISITOR_FINAL
    }).build(setResults);
    final ClassId<League> leagueClassId = new ClassId<>(League.class, leagueId);
    return entityGraphBuilder
            .getEntityContext()
            .getObject(leagueClassId);
  }

  public Season reconstructSeason(final List<SetResult> setResults, final Long seasonId) {
    final EntityGraphBuilder entityGraphBuilder = new EntityGraphBuilder(new EntityVisitor[]{
            SetResult.ENTITY_VISITOR,
            Match.ENTITY_VISITOR,
            RoundGroup.ENTITY_VISITOR,
            Round.ENTITY_VISITOR,
            Season.ENTITY_VISITOR_FINAL
    }).build(setResults);
    final ClassId<Season> seasonClassId = new ClassId<>(Season.class, seasonId);
    return entityGraphBuilder
            .getEntityContext()
            .getObject(seasonClassId);
  }

  public Round reconstructRound(final List<SetResult> setResults, final Long roundId) {
    final EntityGraphBuilder entityGraphBuilder = new EntityGraphBuilder(new EntityVisitor[]{
            SetResult.ENTITY_VISITOR,
            Match.ENTITY_VISITOR,
            RoundGroup.ENTITY_VISITOR,
            Round.ENTITY_VISITOR_FINAL
    }).build(setResults);
    final ClassId<Round> roundClassId = new ClassId<>(Round.class, roundId);
    return entityGraphBuilder
            .getEntityContext()
            .getObject(roundClassId);
  }

  public ArrayListMultimap<String, Integer> reconstructXpPointsForRound(final List<XpPointsForRound> allPointsForRounds,
                                                                        final List<XpPointsForPlace> allPoints) {
    final EntityGraphBuilder entityGraphBuilder = new EntityGraphBuilder(new EntityVisitor[]{
            XpPointsForPlace.ENTITY_VISITOR,
            XpPointsForRoundGroup.ENTITY_VISITOR,
            XpPointsForRound.ENTITY_VISITOR_FINAL
    }).build(allPoints);

    final ArrayListMultimap<String, Integer> multimap = ArrayListMultimap.create();

    for (final XpPointsForRound xpPointsForRound : allPointsForRounds) {
      final ClassId<XpPointsForRound> xpPointsForRoundClassId = new ClassId<>(XpPointsForRound.class, xpPointsForRound.getId());

      final XpPointsForRound xpPointsForRoundReconstructed = entityGraphBuilder
              .getEntityContext()
              .getObject(xpPointsForRoundClassId);

      final String split = xpPointsForRoundReconstructed.getSplit();
      final String type = xpPointsForRoundReconstructed.getType();
      for (final XpPointsForRoundGroup xpPointsForRoundGroup : xpPointsForRoundReconstructed.getXpPointsForRoundGroups()) {
        for (final XpPointsForPlace xpPointsForPlaces : xpPointsForRoundGroup.getXpPointsForPlaces()) {
          multimap.put(split + "|" + type, xpPointsForPlaces.getPoints());
        }
      }
    }
    return multimap;
  }

  public List<XpPointsForRound> reconstructXpPointsList(final List<XpPointsForRound> allPointsForRounds,
                                                        final List<XpPointsForPlace> allPoints) {
    final EntityGraphBuilder entityGraphBuilder = new EntityGraphBuilder(new EntityVisitor[]{
            XpPointsForPlace.ENTITY_VISITOR,
            XpPointsForRoundGroup.ENTITY_VISITOR,
            XpPointsForRound.ENTITY_VISITOR_FINAL
    }).build(allPoints);

    final List<XpPointsForRound> xpPointsForRoundList = allPointsForRounds
            .stream()
            .map(xpPointsForRound -> new ClassId<>(XpPointsForRound.class, xpPointsForRound.getId()))
            .map(xpPointsForRoundClassId -> entityGraphBuilder
                    .getEntityContext()
                    .getObject(xpPointsForRoundClassId))
            .collect(Collectors.toList());

    return xpPointsForRoundList;
  }

}
