package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.XpPointsForPlace;
import com.pj.squashrestapp.model.XpPointsForRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 *
 */
public interface XpPointsRepository extends JpaRepository<XpPointsForRound, Long> {

  List<XpPointsForRound> findAllByOrderByNumberOfPlayersAscSplitAsc();

  List<XpPointsForRound> findAllByType(String type);

  Optional<XpPointsForRound> findByTypeAndSplit(String type, String split);

  @Query("""
          SELECT xpp.points FROM XpPointsForRound xpr 
            JOIN XpPointsForRoundGroup xpg ON xpr = xpg.xpPointsForRound 
            JOIN XpPointsForPlace xpp ON xpg = xpp.xpPointsForRoundGroup
              WHERE xpr.split = :split
                and xpr.type = :type
                  ORDER BY xpp.placeInRound
                """)
  List<Integer> retrievePointsBySplitAndType(String split, String type);


  @Query("""
          SELECT xpp FROM XpPointsForPlace xpp
            INNER JOIN FETCH xpp.xpPointsForRoundGroup xprg
            INNER JOIN FETCH xprg.xpPointsForRound xpr
            """)
  List<XpPointsForPlace> fetchAll();


  @Query("SELECT DISTINCT xpr.type FROM XpPointsForRound xpr")
  List<String> getAllTypes();


  @Query("""
          SELECT xpp FROM XpPointsForPlace xpp
            INNER JOIN FETCH xpp.xpPointsForRoundGroup xprg
            INNER JOIN FETCH xprg.xpPointsForRound xpr
              WHERE xpr.type = :type
            """)
  List<XpPointsForPlace> fetchAllByType(String type);

}
