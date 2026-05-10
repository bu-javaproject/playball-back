package com.playball.backend.domain.matching.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.playball.backend.domain.matches.entity.Match;

public interface MatchRepository extends JpaRepository<Match, Long> {
}