package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.RoundGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RoundGroupRepository
        extends JpaRepository<RoundGroup, Long>, SearchableByLeagueUuid, SearchableBySeasonUuid, BulkDeletable {

    @Query(
            """
          SELECT rg.id FROM Match m
            JOIN m.roundGroup rg
            JOIN rg.round r
            JOIN r.season s
            JOIN s.league l
              WHERE l.uuid = :leagueUuid
              AND (m.firstPlayer.uuid = :playerUuid OR m.secondPlayer.uuid = :playerUuid)
              AND r.finished = true
          """)
    List<Long> retrieveRoundGroupsIdsForPlayer(
            @Param("leagueUuid") UUID leagueUuid, @Param("playerUuid") UUID playerUuid);

    @Override
    @Modifying
    @Query("DELETE FROM RoundGroup rg WHERE rg.id IN :ids")
    void deleteAllByIdIn(@Param("ids") List<Long> ids);

    @Override
    @Query(
            """
          SELECT rg.id FROM RoundGroup rg
            JOIN rg.round r
            JOIN r.season s
            JOIN s.league l
              WHERE l.uuid = :leagueUuid
          """)
    List<Long> fetchIdsByLeagueUuidRaw(@Param("leagueUuid") UUID leagueUuid);

    @Override
    @Query(
            """
          SELECT rg.id FROM RoundGroup rg
            JOIN rg.round r
            JOIN r.season s
              WHERE s.uuid = :seasonUuid
          """)
    List<Long> fetchIdsBySeasonUuidRaw(@Param("seasonUuid") UUID seasonUuid);
}
