package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** */
public interface LeagueRulesRepository extends JpaRepository<LeagueRule, Long> {

    List<LeagueRule> findAllByLeagueOrderByOrderValueAscIdAsc(League league);

    @Query(
            """
          SELECT l.uuid FROM LeagueRule lr
            JOIN lr.league l
              WHERE lr.uuid = :uuid
              """)
    UUID retrieveLeagueUuidOfRule(@Param("uuid") UUID uuid);

    Optional<LeagueRule> findByUuid(UUID uuid);
}
