package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.SeasonResult;
import com.pj.squashrestapp.model.SetResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@SuppressWarnings({"JavaDoc", "unused"})
public interface SetResultRepository extends JpaRepository<SetResult, Long> {

  @Query("""
          SELECT sr FROM SetResult sr
          JOIN Match m ON sr.match = m
            WHERE m.id = :matchId
            AND sr.number = :setNumber
          """)
  SetResult findByMatchIdAndNumber(
          @Param("matchId") Long matchId,
          @Param("setNumber") int setNumber);

}
