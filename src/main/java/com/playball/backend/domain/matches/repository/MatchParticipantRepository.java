package com.playball.backend.domain.matches.repository;


import com.playball.backend.domain.matches.entity.Match;
import com.playball.backend.domain.matching.entity.MatchParticipant;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchParticipantRepository extends JpaRepository<MatchParticipant, Long> {

    List<MatchParticipant> findByMatch(Match match);
}