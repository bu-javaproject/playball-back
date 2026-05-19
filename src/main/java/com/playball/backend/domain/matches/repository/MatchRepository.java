package com.playball.backend.domain.matches.repository;

import com.playball.backend.domain.matches.entity.Match;
import com.playball.backend.domain.matches.entity.MatchStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {

    Optional<Match> findByIdAndStatusNot(Long id, MatchStatus status);

    List<Match> findByStatusNotOrderByMatchDateDesc(MatchStatus status, Pageable pageable);

    @Query(value = """
        SELECT * FROM (
            SELECT m.*,
                (6371 * ACOS(
                    COS(RADIANS(:lat)) * COS(RADIANS(m.latitude))
                    * COS(RADIANS(m.longitude) - RADIANS(:lng))
                    + SIN(RADIANS(:lat)) * SIN(RADIANS(m.latitude))
                )) AS distance
            FROM matches m
            WHERE m.status = 'OPEN'
            AND m.current_players < m.max_players
            AND (:sportType IS NULL OR m.sport_type = :sportType)
            AND (:date IS NULL OR DATE(m.match_date) = :date)
            AND (:maxFee IS NULL OR m.entry_fee <= :maxFee)
            AND (:skillLevel IS NULL OR m.skill_level = :skillLevel)
        ) t
        WHERE t.distance <= :radius
        ORDER BY RAND()
        LIMIT 1
        """, nativeQuery = true)
    Optional<Object[]> findRandomMatch(@Param("lat") Double latitude,
                                       @Param("lng") Double longitude,
                                       @Param("sportType") String sportType,
                                       @Param("date") LocalDate date,
                                       @Param("maxFee") Integer maxFee,
                                       @Param("skillLevel") String skillLevel,
                                       @Param("radius") Double radius);
}