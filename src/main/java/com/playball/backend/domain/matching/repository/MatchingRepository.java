package com.playball.backend.domain.matching.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.playball.backend.domain.matches.entity.Match;

public interface MatchingRepository extends JpaRepository<Match, Long> {
}