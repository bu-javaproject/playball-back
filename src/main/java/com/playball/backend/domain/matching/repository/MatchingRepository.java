package com.playball.backend.domain.matching.repository;

import com.playball.backend.domain.matches.dto.RandomMatchView;
import com.playball.backend.domain.matches.entity.Match;
import com.playball.backend.domain.matches.entity.MatchStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MatchingRepository extends JpaRepository<Match, Long> {

    List<Match> findByStatusNot(MatchStatus status, Pageable pageable);

    Optional<Match> findByIdAndStatusNot(Long id, MatchStatus status);

    @Query(value = """
            SELECT matchId, title, sportType, matchDate, locationName, entryFee,
                   currentPlayers, maxPlayers, distance
            FROM (
                SELECT
                    id                AS matchId,
                    title,
                    sport_type        AS sportType,
                    match_date        AS matchDate,
                    location_name     AS locationName,
                    entry_fee         AS entryFee,
                    current_players   AS currentPlayers,
                    max_players       AS maxPlayers,
                    (6371 * ACOS(
                        COS(RADIANS(:latitude))
                        * COS(RADIANS(latitude))
                        * COS(RADIANS(longitude) - RADIANS(:longitude))
                        + SIN(RADIANS(:latitude))
                        * SIN(RADIANS(latitude))
                    )) AS distance
                FROM matches
                WHERE status = 'OPEN'
                  AND current_players < max_players
                  AND (:sportType IS NULL OR sport_type = :sportType)
                  AND (:date IS NULL OR DATE(match_date) = :date)
                  AND (:maxFee IS NULL OR entry_fee <= :maxFee)
                  AND (:skillLevel IS NULL OR skill_level = :skillLevel)
                  AND (:gender IS NULL OR gender = :gender)
                  AND (:ageRange IS NULL OR age_range = :ageRange)
            ) t
            WHERE t.distance <= :radius
            ORDER BY RAND()
            LIMIT 1
            """, nativeQuery = true)
    Optional<RandomMatchView> findRandomMatch(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radius") Double radius,
            @Param("sportType") String sportType,
            @Param("date") LocalDate date,
            @Param("maxFee") Integer maxFee,
            @Param("skillLevel") String skillLevel,
            @Param("gender") String gender,
            @Param("ageRange") Integer ageRange
    );
}
