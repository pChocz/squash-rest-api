package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.XpPointsForPlace;
import com.pj.squashrestapp.model.XpPointsForRound;
import com.pj.squashrestapp.model.XpPointsForRoundGroup;
import com.pj.squashrestapp.model.dto.SingleSetRowDto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@SuppressWarnings({"JavaDoc", "unused"})
public interface XpPointsRepository extends JpaRepository<XpPointsForRound, Long> {

  @Query("""
          SELECT xpp.points FROM XpPointsForRound xpr 
            JOIN XpPointsForRoundGroup xpg ON xpr = xpg.xpPointsForRound 
            JOIN XpPointsForPlace xpp ON xpg = xpp.xpPointsForRoundGroup
              WHERE xpr.split = :split
                ORDER BY xpp.placeInRound
          """)
  List<Integer> retrievePointsBySplit(@Param("split") String split);

  @Query("""
          SELECT xpp FROM XpPointsForPlace xpp
            INNER JOIN FETCH xpp.xpPointsForRoundGroup xprg
            INNER JOIN FETCH xprg.xpPointsForRound xpr
          """)
  List<XpPointsForPlace> fetchAll();

}
