package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.XpPointsForPlace;
import com.pj.squashrestapp.model.XpPointsForRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 *
 */
public interface XpPointsRepository extends JpaRepository<XpPointsForRound, Long> {


  @Query("""
          SELECT xpp.points FROM XpPointsForRound xpr 
            JOIN XpPointsForRoundGroup xpg ON xpr = xpg.xpPointsForRound 
            JOIN XpPointsForPlace xpp ON xpg = xpp.xpPointsForRoundGroup
              WHERE xpr.split = :split
                ORDER BY xpp.placeInRound
                """)
  List<Integer> retrievePointsBySplit(String split);


  @Query("""
          SELECT xpp FROM XpPointsForPlace xpp
            INNER JOIN FETCH xpp.xpPointsForRoundGroup xprg
            INNER JOIN FETCH xprg.xpPointsForRound xpr
            """)
  List<XpPointsForPlace> fetchAll();

}
