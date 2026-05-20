package com.playball.backend.domain.matches.service;

import com.playball.backend.common.exception.CustomException;
import com.playball.backend.common.exception.ErrorCode;
import com.playball.backend.domain.matches.dto.MatchCreateRequest;
import com.playball.backend.domain.matches.dto.MatchCreateResponse;
import com.playball.backend.domain.matches.dto.MatchResponse;
import com.playball.backend.domain.matches.dto.NearbyMatchView;
import com.playball.backend.domain.matches.dto.RandomMatchRequest;
import com.playball.backend.domain.matches.dto.RandomMatchResponse;
import com.playball.backend.domain.matches.dto.RandomMatchView;
import com.playball.backend.domain.matches.entity.Match;
import com.playball.backend.domain.matches.entity.MatchStatus;
import com.playball.backend.domain.matches.entity.SportType;
import com.playball.backend.domain.matches.repository.MatchParticipantRepository;
import com.playball.backend.domain.matching.entity.ParticipantStatus;
import com.playball.backend.domain.matching.repository.MatchingRepository;
import com.playball.backend.domain.member.entity.Member;
import com.playball.backend.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock MatchingRepository matchingRepository;
    @Mock MatchParticipantRepository matchParticipantRepository;
    @Mock MemberRepository memberRepository;

    @InjectMocks MatchService matchService;

    private static final Long MEMBER_ID = 1L;

    // -------------------------------------------------------
    // 지역 매칭 주최자 흐름: POST /api/matches — 경기 생성
    // -------------------------------------------------------

    @Test
    @DisplayName("정상 요청 시 경기가 생성되고 주최자가 APPROVED 참가자로 등록된다")
    void createMatch_성공() {
        Member host = mock(Member.class);
        Match savedMatch = Match.builder()
                .id(100L)
                .hostId(MEMBER_ID)
                .title("목동 풋살")
                .sportType(SportType.SOCCER)
                .matchDate(LocalDateTime.now().plusDays(1))
                .locationName("서울 목동운동장")
                .latitude(37.5263)
                .longitude(126.8967)
                .address("서울특별시 양천구")
                .maxPlayers(10)
                .currentPlayers(1)
                .entryFee(5000)
                .status(MatchStatus.OPEN)
                .build();

        given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(host));
        given(matchingRepository.save(any())).willReturn(savedMatch);

        MatchCreateRequest request = MatchCreateRequest.builder()
                .title("목동 풋살")
                .sportType("SOCCER")
                .matchDate(LocalDateTime.now().plusDays(1))
                .locationName("서울 목동운동장")
                .latitude(37.5263)
                .longitude(126.8967)
                .maxPlayers(10)
                .entryFee(5000)
                .build();

        MatchCreateResponse response = matchService.createMatch(request, MEMBER_ID);

        assertThat(response.getMatchId()).isEqualTo(100L);
        assertThat(response.getSportType()).isEqualTo("SOCCER");
        assertThat(response.getCurrentPlayers()).isEqualTo(1);
        assertThat(response.getStatus()).isEqualTo(MatchStatus.OPEN);
        verify(matchParticipantRepository).save(argThat(p -> p.getStatus() == ParticipantStatus.APPROVED));
    }

    @Test
    @DisplayName("존재하지 않는 memberId로 생성 시 USER_NOT_FOUND — 경기 저장이 선행되지 않는다")
    void createMatch_없는회원_예외() {
        given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.empty());

        MatchCreateRequest request = MatchCreateRequest.builder()
                .title("목동 풋살")
                .sportType("SOCCER")
                .matchDate(LocalDateTime.now().plusDays(1))
                .latitude(37.5263)
                .longitude(126.8967)
                .maxPlayers(10)
                .build();

        assertThatThrownBy(() -> matchService.createMatch(request, MEMBER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);

        verify(matchingRepository, never()).save(any());
    }

    @Test
    @DisplayName("잘못된 sportType 입력 시 INVALID_INPUT 예외가 발생한다")
    void createMatch_잘못된sportType_예외() {
        given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(mock(Member.class)));

        MatchCreateRequest request = MatchCreateRequest.builder()
                .title("목동 풋살")
                .sportType("CRICKET")
                .matchDate(LocalDateTime.now().plusDays(1))
                .latitude(37.5263)
                .longitude(126.8967)
                .maxPlayers(10)
                .build();

        assertThatThrownBy(() -> matchService.createMatch(request, MEMBER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_INPUT);
    }

    @Test
    @DisplayName("entryFee를 생략하면 0으로 저장된다")
    void createMatch_entryFee_null이면_0저장() {
        Member host = mock(Member.class);
        Match savedMatch = Match.builder()
                .id(100L)
                .title("목동 풋살")
                .sportType(SportType.SOCCER)
                .matchDate(LocalDateTime.now().plusDays(1))
                .latitude(37.5263)
                .longitude(126.8967)
                .maxPlayers(10)
                .currentPlayers(1)
                .entryFee(0)
                .status(MatchStatus.OPEN)
                .build();

        given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(host));
        given(matchingRepository.save(any())).willReturn(savedMatch);

        MatchCreateRequest request = MatchCreateRequest.builder()
                .title("목동 풋살")
                .sportType("SOCCER")
                .matchDate(LocalDateTime.now().plusDays(1))
                .latitude(37.5263)
                .longitude(126.8967)
                .maxPlayers(10)
                .build();

        matchService.createMatch(request, MEMBER_ID);

        verify(matchingRepository).save(argThat(m -> Integer.valueOf(0).equals(m.getEntryFee())));
    }

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
    // 화면 ③: GET /api/matches?lat=&lng= — 지도 경기 목록
    // -------------------------------------------------------

    @Test
    @DisplayName("위치 제공 시 반경 내 경기 목록을 거리순으로 반환한다")
    void getMatches_위치기반_결과반환() {
        NearbyMatchView view = nearbyViewStub(1L, "목동 풋살장", "SOCCER", 3, 10);
        given(matchingRepository.findNearbyMatches(eq(37.5263), eq(126.8967), eq(5.0), isNull()))
                .willReturn(List.of(view));

        List<MatchResponse> result = matchService.getMatches(37.5263, 126.8967, 5.0, null, 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMatchId()).isEqualTo(1L);
        assertThat(result.get(0).getSportType()).isEqualTo("SOCCER");
        assertThat(result.get(0).getLatitude()).isEqualTo(37.5263);
    }

    @Test
    @DisplayName("반경 내 경기가 없으면 빈 리스트를 반환한다")
    void getMatches_결과없음_빈리스트반환() {
        given(matchingRepository.findNearbyMatches(any(), any(), any(), any()))
                .willReturn(List.of());

        List<MatchResponse> result = matchService.getMatches(37.5263, 126.8967, 1.0, null, 0, 10);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("sportType 필터가 findNearbyMatches에 전달된다")
    void getMatches_sportType_필터_전달() {
        NearbyMatchView view = nearbyViewStub(2L, "농구장", "BASKETBALL", 2, 8);
        given(matchingRepository.findNearbyMatches(any(), any(), any(), eq("BASKETBALL")))
                .willReturn(List.of(view));

        List<MatchResponse> result = matchService.getMatches(37.5263, 126.8967, 5.0, "BASKETBALL", 0, 10);

        assertThat(result.get(0).getSportType()).isEqualTo("BASKETBALL");
    }

    @Test
    @DisplayName("위치 없으면 페이지네이션으로 전체 목록을 반환한다")
    void getMatches_위치없으면_페이지네이션사용() {
        Match match = Match.builder()
                .id(1L)
                .title("목동 풋살")
                .sportType(SportType.SOCCER)
                .locationName("서울 목동운동장")
                .latitude(37.5263)
                .longitude(126.8967)
                .maxPlayers(10)
                .currentPlayers(3)
                .status(MatchStatus.OPEN)
                .build();
        given(matchingRepository.findByStatusNot(eq(MatchStatus.DELETED), any()))
                .willReturn(List.of(match));

        List<MatchResponse> result = matchService.getMatches(null, null, 5.0, null, 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMatchId()).isEqualTo(1L);
    }

    // -------------------------------------------------------
    // 헬퍼
    // -------------------------------------------------------

    private NearbyMatchView nearbyViewStub(Long id, String locationName, String sportType,
                                           int current, int max) {
        NearbyMatchView view = mock(NearbyMatchView.class);
        given(view.getMatchId()).willReturn(id);
        given(view.getTitle()).willReturn("테스트 경기");
        given(view.getSportType()).willReturn(sportType);
        given(view.getMatchDate()).willReturn(LocalDateTime.now().plusDays(1));
        given(view.getLocationName()).willReturn(locationName);
        given(view.getLatitude()).willReturn(37.5263);
        given(view.getLongitude()).willReturn(126.8967);
        given(view.getMaxPlayers()).willReturn(max);
        given(view.getCurrentPlayers()).willReturn(current);
        given(view.getStatus()).willReturn("OPEN");
        return view;
    }

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
