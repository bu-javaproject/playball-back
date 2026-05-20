package com.playball.backend.domain.matches.repository;

import com.playball.backend.domain.matches.entity.Match;
import com.playball.backend.domain.matches.entity.MatchStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {

    Optional<Match> findByIdAndStatusNot(Long id, MatchStatus status);

    List<Match> findByStatusNotOrderByMatchDateDesc(MatchStatus status, Pageable pageable);
}