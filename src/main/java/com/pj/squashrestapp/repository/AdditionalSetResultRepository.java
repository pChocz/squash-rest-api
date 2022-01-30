package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.AdditionalSetResult;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** */
public interface AdditionalSetResultRepository extends JpaRepository<AdditionalSetResult, Long>,
    SearchableByLeagueUuid, SearchableBySeasonUuid, BulkDeletable {

  @Override
  @Modifying
  @Query("DELETE FROM AdditionalSetResult sr WHERE sr.id IN :ids")
  void deleteAllByIdIn(@Param("ids") List<Long> ids);

  @Override
  @Query("""
            SELECT asr.id FROM AdditionalSetResult asr
              JOIN asr.match m
              JOIN m.league l
                WHERE l.uuid = :leagueUuid
            """)
  List<Long> fetchIdsByLeagueUuidRaw(@Param("leagueUuid") UUID leagueUuid);

  @Override
  @Query("""
            SELECT asr.id FROM AdditionalSetResult asr
              JOIN asr.match m
              JOIN Season s ON m.seasonNumber = s.number AND m.league = s.league
                WHERE s.uuid = :seasonUuid
            """)
  List<Long> fetchIdsBySeasonUuidRaw(@Param("seasonUuid") UUID seasonUuid);

}
