package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.XpPointsForPlace;
import com.pj.squashrestapp.model.XpPointsForRound;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 *
 */
public interface XpPointsRepository extends JpaRepository<XpPointsForRound, Long> {

    List<XpPointsForRound> findAllByOrderByNumberOfPlayersAscSplitAsc();

    List<XpPointsForRound> findAllByType(String type);

    @EntityGraph(attributePaths = {"xpPointsForRoundGroups.xpPointsForPlaces"})
    Optional<XpPointsForRound> findByTypeAndSplit(String type, String split);

    @Query(
            """
          SELECT xpp.points FROM XpPointsForRound xpr
            JOIN xpr.xpPointsForRoundGroups xpg
            JOIN xpg.xpPointsForPlaces xpp
              WHERE xpr.split = :split
                AND xpr.type = :type
          ORDER BY xpp.placeInRound
          """)
    List<Integer> retrievePointsBySplitAndType(@Param("split") String split, @Param("type") String type);

    @Query(
            """
          SELECT xpp FROM XpPointsForPlace xpp
            JOIN FETCH xpp.xpPointsForRoundGroup xprg
            JOIN FETCH xprg.xpPointsForRound xpr
          """)
    List<XpPointsForPlace> fetchAll();

    @Query("SELECT DISTINCT xpr.type FROM XpPointsForRound xpr")
    List<String> getAllTypes();

    @Query(
            """
          SELECT xpp FROM XpPointsForPlace xpp
            JOIN FETCH xpp.xpPointsForRoundGroup xprg
            JOIN FETCH xprg.xpPointsForRound xpr
              WHERE xpr.type = :type
          """)
    List<XpPointsForPlace> fetchAllByType(@Param("type") String type);
}
