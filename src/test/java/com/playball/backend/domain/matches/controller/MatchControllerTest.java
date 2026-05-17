package com.playball.backend.domain.matches.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.playball.backend.common.exception.CustomException;
import com.playball.backend.common.exception.ErrorCode;
import com.playball.backend.common.exception.GlobalExceptionHandler;
import com.playball.backend.common.resolver.CurrentMemberIdArgumentResolver;
import com.playball.backend.domain.matching.dto.MatchedResponse;
import com.playball.backend.domain.matching.service.MatchRealtimeService;
import com.playball.backend.domain.matching.service.MatchingService;
import com.playball.backend.domain.matches.dto.RandomMatchRequest;
import com.playball.backend.domain.matches.dto.RandomMatchResponse;
import com.playball.backend.domain.matches.service.MatchService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MatchControllerTest {

    @Mock MatchService matchService;
    @Mock MatchingService matchingService;
    @Mock MatchRealtimeService matchRealtimeService;

    @InjectMocks MatchController matchController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static final Long MEMBER_ID = 1L;
    private static final Long MATCH_ID  = 10L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(matchController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new CurrentMemberIdArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(MEMBER_ID, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void clearAuth() {
        SecurityContextHolder.clearContext();
    }

    // -------------------------------------------------------
    // 화면 ④: POST /api/matches/random — 랜덤 경기 탐색
    // -------------------------------------------------------

    @Test
    @DisplayName("POST /api/matches/random — 조건에 맞는 경기 반환 200")
    void findRandomMatch_성공_200() throws Exception {
        RandomMatchResponse response = RandomMatchResponse.builder()
                .matchId(MATCH_ID)
                .title("목동 풋살")
                .sportType("SOCCER")
                .matchDate(LocalDateTime.of(2026, 6, 1, 14, 0))
                .locationName("서울 목동운동장")
                .entryFee(5000)
                .currentPlayers(3)
                .maxPlayers(10)
                .distance(1500.0)
                .build();

        given(matchService.findRandomMatch(any(), any())).willReturn(response);

        RandomMatchRequest request = RandomMatchRequest.builder()
                .latitude(37.5263)
                .longitude(126.8967)
                .sportType("SOCCER")
                .build();

        mockMvc.perform(post("/api/matches/random")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.matchId").value(MATCH_ID.intValue()))
                .andExpect(jsonPath("$.data.locationName").value("서울 목동운동장"))
                .andExpect(jsonPath("$.data.sportType").value("SOCCER"))
                .andExpect(jsonPath("$.data.currentPlayers").value(3))
                .andExpect(jsonPath("$.data.maxPlayers").value(10))
                .andExpect(jsonPath("$.data.distance").value(1500.0));
    }

    @Test
    @DisplayName("POST /api/matches/random — 조건에 맞는 경기 없을 때 404")
    void findRandomMatch_경기없음_404() throws Exception {
        given(matchService.findRandomMatch(any(), any()))
                .willThrow(new CustomException(ErrorCode.MATCH_NOT_FOUND));

        RandomMatchRequest request = RandomMatchRequest.builder()
                .latitude(37.5263)
                .longitude(126.8967)
                .sportType("BASKETBALL")
                .build();

        mockMvc.perform(post("/api/matches/random")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.MATCH_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("POST /api/matches/random — latitude 누락 시 400")
    void findRandomMatch_필수값_누락_400() throws Exception {
        String json = """
                {
                  "longitude": 126.8967,
                  "sportType": "SOCCER"
                }
                """;

        mockMvc.perform(post("/api/matches/random")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    // -------------------------------------------------------
    // 화면 ⑤: POST /api/matches/{matchId}/join — 수락(참가)
    // -------------------------------------------------------

    @Test
    @DisplayName("POST /api/matches/{matchId}/join — 정상 참가 200")
    void joinMatch_성공_200() throws Exception {
        MatchedResponse response = MatchedResponse.builder()
                .matchId(MATCH_ID)
                .currentPlayers(4)
                .maxPlayers(10)
                .status("OPEN")
                .build();

        given(matchingService.joinMatch(eq(MATCH_ID), eq(MEMBER_ID))).willReturn(response);

        mockMvc.perform(post("/api/matches/{matchId}/join", MATCH_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.matchId").value(MATCH_ID.intValue()))
                .andExpect(jsonPath("$.data.currentPlayers").value(4))
                .andExpect(jsonPath("$.data.status").value("OPEN"));
    }

    @Test
    @DisplayName("POST /api/matches/{matchId}/join — 인원 초과 409")
    void joinMatch_인원초과_409() throws Exception {
        given(matchingService.joinMatch(eq(MATCH_ID), eq(MEMBER_ID)))
                .willThrow(new CustomException(ErrorCode.MATCH_FULL));

        mockMvc.perform(post("/api/matches/{matchId}/join", MATCH_ID))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.MATCH_FULL.getMessage()));
    }

    @Test
    @DisplayName("POST /api/matches/{matchId}/join — 이미 참가한 경기 409")
    void joinMatch_이미참가_409() throws Exception {
        given(matchingService.joinMatch(eq(MATCH_ID), eq(MEMBER_ID)))
                .willThrow(new CustomException(ErrorCode.ALREADY_JOINED));

        mockMvc.perform(post("/api/matches/{matchId}/join", MATCH_ID))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.ALREADY_JOINED.getMessage()));
    }

    // -------------------------------------------------------
    // 화면 ⑥: DELETE /api/matches/{matchId}/join — 참가 취소
    // -------------------------------------------------------

    @Test
    @DisplayName("DELETE /api/matches/{matchId}/join — 정상 취소 200")
    void leaveMatch_성공_200() throws Exception {
        mockMvc.perform(delete("/api/matches/{matchId}/join", MATCH_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("DELETE /api/matches/{matchId}/join — 존재하지 않는 경기 404")
    void leaveMatch_경기없음_404() throws Exception {
        willThrow(new CustomException(ErrorCode.MATCH_NOT_FOUND))
                .given(matchRealtimeService).leaveMatch(eq(MATCH_ID), eq(MEMBER_ID));

        mockMvc.perform(delete("/api/matches/{matchId}/join", MATCH_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}
