package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface RoundRepository extends JpaRepository<Round, Long> {

  Optional<Round> findByUuid(UUID uuid);


  Optional<Round> findBySeasonAndNumber(Season season, int number);


  @Query("""
          SELECT l.uuid FROM Round r
           JOIN Season s ON r.season = s
           JOIN League l ON s.league = l
              WHERE r.uuid = :roundUuid
          """)
  UUID retrieveLeagueUuidOfRound(UUID roundUuid);


  @Query("""
          SELECT r FROM Match m
           JOIN RoundGroup rg ON m.roundGroup = rg
           JOIN Round r ON rg.round = r
              WHERE m.uuid = :matchUuid
          """)
  Round findByMatchUuid(UUID matchUuid);


  @Query("""
          SELECT r.id FROM Round r
              WHERE r.uuid = :roundUuid
          """)
  Long findIdByUuid(UUID roundUuid);

}
