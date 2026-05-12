package com.playball.backend.domain.matches.dto;

import java.time.LocalDateTime;

public interface RandomMatchView {
    Long getMatchId();
    String getTitle();
    String getSportType();
    LocalDateTime getMatchDate();
    String getLocationName();
    Integer getEntryFee();
    Double getDistance();
}
