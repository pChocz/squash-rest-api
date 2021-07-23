package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.AdditionalSetResult;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/** */
public interface AdditionalSetResultRepository extends JpaRepository<AdditionalSetResult, Long>,
    BulkDeletableByLeagueUuid {

  @Modifying
  @Query("DELETE FROM AdditionalSetResult sr WHERE sr.id IN ?1")
  void deleteAllByIdIn(List<Long> ids);

  @Query("""
            SELECT asr.id FROM AdditionalSetResult asr
              INNER JOIN asr.match m
              INNER JOIN m.league l
              WHERE l.uuid = :leagueUuid
            """)
  List<Long> fetchIdsByLeagueUuidRaw(UUID leagueUuid);

}
