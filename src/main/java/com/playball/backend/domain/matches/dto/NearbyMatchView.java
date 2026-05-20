package com.playball.backend.domain.matches.dto;

import java.time.LocalDateTime;

public interface NearbyMatchView {
    Long getMatchId();
    String getTitle();
    String getSportType();
    LocalDateTime getMatchDate();
    String getLocationName();
    Double getLatitude();
    Double getLongitude();
    Integer getMaxPlayers();
    Integer getCurrentPlayers();
    String getStatus();
    Double getDistance();
}
