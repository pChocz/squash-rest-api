package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 */
public interface EmailQueueRepository extends JpaRepository<Email, Long> {

    @Query(
            """
            SELECT e FROM Email e
              WHERE e.sent is false
                AND :datetime BETWEEN e.sendAfterDatetime AND e.sendBeforeDatetime
                AND e.triesCount < :maxTriesCount
            ORDER BY e.id ASC
            """)
    List<Email> findEmailsToSend(
            @Param("datetime") final LocalDateTime datetime, @Param("maxTriesCount") int maxTriesCount);

    @Query("SELECT e FROM Email e WHERE :threshold > e.sendBeforeDatetime")
    List<Email> findEmailsExceedingThreshold(@Param("threshold") LocalDateTime threshold);
}
