package com.playball.backend.domain.matches.service;

import com.playball.backend.common.exception.CustomException;
import com.playball.backend.common.exception.ErrorCode;
import com.playball.backend.domain.matches.dto.RandomMatchRequest;
import com.playball.backend.domain.matches.dto.RandomMatchResponse;
import com.playball.backend.domain.matches.dto.RandomMatchView;
import com.playball.backend.domain.matches.repository.MatchParticipantRepository;
import com.playball.backend.domain.matching.repository.MatchingRepository;
import com.playball.backend.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock MatchingRepository matchingRepository;
    @Mock MatchParticipantRepository matchParticipantRepository;
    @Mock MemberRepository memberRepository;

    @InjectMocks MatchService matchService;

    private static final Long MEMBER_ID = 1L;

    // -------------------------------------------------------
    // 화면 ④: POST /api/matches/random — 랜덤 경기 탐색
    // -------------------------------------------------------

    @Test
    @DisplayName("조건에 맞는 경기가 있으면 RandomMatchResponse를 반환한다")
    void findRandomMatch_성공() {
        RandomMatchRequest request = RandomMatchRequest.builder()
                .latitude(37.5263)
                .longitude(126.8967)
                .sportType("SOCCER")
                .build();

        RandomMatchView view = viewStub(1L, "서울 목동운동장", "SOCCER", 3, 10, 1500.0);
        given(matchingRepository.findRandomMatch(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .willReturn(Optional.of(view));

        RandomMatchResponse response = matchService.findRandomMatch(request, MEMBER_ID);

        assertThat(response.getMatchId()).isEqualTo(1L);
        assertThat(response.getLocationName()).isEqualTo("서울 목동운동장");
        assertThat(response.getSportType()).isEqualTo("SOCCER");
        assertThat(response.getCurrentPlayers()).isEqualTo(3);
        assertThat(response.getMaxPlayers()).isEqualTo(10);
        assertThat(response.getDistance()).isEqualTo(1500.0);
    }

    @Test
    @DisplayName("조건에 맞는 경기가 없으면 MATCH_NOT_FOUND 예외가 발생한다")
    void findRandomMatch_경기없음_예외() {
        RandomMatchRequest request = RandomMatchRequest.builder()
                .latitude(37.5263)
                .longitude(126.8967)
                .sportType("SOCCER")
                .build();

        given(matchingRepository.findRandomMatch(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> matchService.findRandomMatch(request, MEMBER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.MATCH_NOT_FOUND);
    }

    @Test
    @DisplayName("이미 참가한 경기는 결과에서 제외된다 (memberId가 쿼리에 전달됨)")
    void findRandomMatch_이미참가한경기_제외() {
        RandomMatchRequest request = RandomMatchRequest.builder()
                .latitude(37.5263)
                .longitude(126.8967)
                .sportType("SOCCER")
                .build();

        RandomMatchView view = viewStub(2L, "다른 경기장", "SOCCER", 2, 8, 600.0);
        given(matchingRepository.findRandomMatch(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), eq(MEMBER_ID)))
                .willReturn(Optional.of(view));

        RandomMatchResponse response = matchService.findRandomMatch(request, MEMBER_ID);

        assertThat(response.getMatchId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("gender/ageRange 필터가 쿼리 파라미터로 전달된다")
    void findRandomMatch_gender_ageRange_필터_전달() {
        RandomMatchRequest request = RandomMatchRequest.builder()
                .latitude(37.5263)
                .longitude(126.8967)
                .sportType("SOCCER")
                .gender(RandomMatchRequest.Gender.M)
                .ageRange(20)
                .skillLevel("INTERMEDIATE")
                .maxFee(10000)
                .build();

        RandomMatchView view = viewStub(3L, "목동 풋살장", "SOCCER", 4, 8, 800.0);
        given(matchingRepository.findRandomMatch(
                eq(37.5263), eq(126.8967), any(),
                eq("SOCCER"), isNull(), eq(10000),
                eq("INTERMEDIATE"), eq("M"), eq(20), eq(MEMBER_ID)))
                .willReturn(Optional.of(view));

        RandomMatchResponse response = matchService.findRandomMatch(request, MEMBER_ID);

        assertThat(response.getMatchId()).isEqualTo(3L);
    }

    @Test
    @DisplayName("gender가 null이면 쿼리에 null로 전달된다")
    void findRandomMatch_gender_null이면_null전달() {
        RandomMatchRequest request = RandomMatchRequest.builder()
                .latitude(37.5263)
                .longitude(126.8967)
                .sportType("SOCCER")
                .build();

        RandomMatchView view = viewStub(4L, "한강 운동장", "SOCCER", 1, 6, 300.0);
        given(matchingRepository.findRandomMatch(
                any(), any(), any(), any(), any(), any(), any(), isNull(), isNull(), any()))
                .willReturn(Optional.of(view));

        RandomMatchResponse response = matchService.findRandomMatch(request, MEMBER_ID);

        assertThat(response).isNotNull();
    }

    // -------------------------------------------------------
    // 헬퍼
    // -------------------------------------------------------

    private RandomMatchView viewStub(Long id, String locationName, String sportType,
                                     int current, int max, double distance) {
        RandomMatchView view = mock(RandomMatchView.class);
        given(view.getMatchId()).willReturn(id);
        given(view.getTitle()).willReturn("테스트 경기");
        given(view.getSportType()).willReturn(sportType);
        given(view.getMatchDate()).willReturn(LocalDateTime.now().plusDays(1));
        given(view.getLocationName()).willReturn(locationName);
        given(view.getEntryFee()).willReturn(5000);
        given(view.getCurrentPlayers()).willReturn(current);
        given(view.getMaxPlayers()).willReturn(max);
        given(view.getDistance()).willReturn(distance);
        return view;
    }
}
