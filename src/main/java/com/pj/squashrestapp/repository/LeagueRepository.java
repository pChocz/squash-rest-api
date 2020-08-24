package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.SetResult;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LeagueRepository extends JpaRepository<League, Long> {

  @EntityGraph(attributePaths = {
          "seasons",
  })
  List<League> findAll();

  League findByName(String name);

}
