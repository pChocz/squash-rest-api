package com.pj.squashrestapp.util;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.util.ClassId;
import com.pj.squashrestapp.model.util.EntityGraphBuilder;
import com.pj.squashrestapp.model.util.EntityVisitor;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * Entity Graph Reconstructor for the {@link SetResult} class that
 * allows to build entire structure above, up to league entity.
 */
@UtilityClass
public class EntityGraphReconstruct {

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

  public RoundGroup reconstructRoundGroup(final List<SetResult> setResults, final Long roundGroupId) {
    final EntityGraphBuilder entityGraphBuilder = new EntityGraphBuilder(new EntityVisitor[]{
            SetResult.ENTITY_VISITOR,
            Match.ENTITY_VISITOR,
            RoundGroup.ENTITY_VISITOR_FINAL
    }).build(setResults);
    final ClassId<RoundGroup> roundGroupClassId = new ClassId<>(RoundGroup.class, roundGroupId);
    return entityGraphBuilder
            .getEntityContext()
            .getObject(roundGroupClassId);
  }

}
