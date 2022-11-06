package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.MatchScore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MatchScoreRepository
        extends JpaRepository<MatchScore, Long>, SearchableByLeagueUuid, SearchableBySeasonUuid, BulkDeletable {


    @Override
    @Modifying
    @Query("DELETE FROM MatchScore ms WHERE ms.id IN :ids")
    void deleteAllByIdIn(@Param("ids") List<Long> ids);

    @Override
    @Query(
            """
          SELECT ms.id FROM MatchScore ms
            JOIN ms.match m
            JOIN m.roundGroup rg
            JOIN rg.round r
            JOIN r.season s
            JOIN s.league l
              WHERE l.uuid = :leagueUuid
          """)
    List<Long> fetchIdsByLeagueUuidRaw(@Param("leagueUuid") UUID leagueUuid);

    @Override
    @Query(
            """
          SELECT ms.id FROM MatchScore ms
            JOIN ms.match m
            JOIN m.roundGroup rg
            JOIN rg.round r
            JOIN r.season s
              WHERE s.uuid = :seasonUuid
          """)
    List<Long> fetchIdsBySeasonUuidRaw(@Param("seasonUuid") UUID seasonUuid);

}
