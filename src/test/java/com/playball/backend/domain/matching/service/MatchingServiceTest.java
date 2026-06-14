package com.playball.backend.domain.matching.service;

import com.playball.backend.common.exception.CustomException;
import com.playball.backend.common.exception.ErrorCode;
import com.playball.backend.domain.matching.dto.MatchedResponse;
import com.playball.backend.domain.matching.entity.MatchParticipant;
import com.playball.backend.domain.matching.entity.ParticipantStatus;
import com.playball.backend.domain.matching.repository.MatchingRepository;
import com.playball.backend.domain.matches.entity.Match;
import com.playball.backend.domain.matches.entity.MatchStatus;
import com.playball.backend.domain.matches.entity.SportType;
import com.playball.backend.domain.matches.repository.MatchParticipantRepository;
import com.playball.backend.domain.member.entity.Member;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock MatchingRepository matchRepository;
    @Mock MemberRepository memberRepository;
    @Mock MatchParticipantRepository matchParticipantRepository;

    @InjectMocks MatchingService matchingService;

    private static final Long MATCH_ID  = 1L;
    private static final Long MEMBER_ID = 10L;

    // -------------------------------------------------------
    // 화면 ⑤: POST /api/matches/{matchId}/join — 수락(참가)
    // -------------------------------------------------------

    @Test
    @DisplayName("정상 참가 시 currentPlayers가 증가하고 참가자가 저장된다")
    void joinMatch_성공() {
        Match match = matchOf(3, 10, MatchStatus.OPEN);
        Member member = memberOf(MEMBER_ID);

        given(matchRepository.findById(MATCH_ID)).willReturn(Optional.of(match));
        given(matchParticipantRepository.findByMatch_IdAndMember_MemberId(MATCH_ID, MEMBER_ID))
                .willReturn(Optional.empty());
        given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(member));

        MatchedResponse response = matchingService.joinMatch(MATCH_ID, MEMBER_ID);

        assertThat(response.getMatchId()).isEqualTo(MATCH_ID);
        assertThat(response.getCurrentPlayers()).isEqualTo(4);
        assertThat(response.getStatus()).isEqualTo(MatchStatus.OPEN.name());
        verify(matchParticipantRepository).save(argThat(p ->
                p.getMember().equals(member) && p.getStatus() == ParticipantStatus.APPROVED));
    }

    @Test
    @DisplayName("마지막 자리 참가 시 경기 상태가 CLOSED로 변경된다")
    void joinMatch_마지막자리_CLOSED() {
        Match match = matchOf(9, 10, MatchStatus.OPEN);
        Member member = memberOf(MEMBER_ID);

        given(matchRepository.findById(MATCH_ID)).willReturn(Optional.of(match));
        given(matchParticipantRepository.findByMatch_IdAndMember_MemberId(MATCH_ID, MEMBER_ID))
                .willReturn(Optional.empty());
        given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(member));

        MatchedResponse response = matchingService.joinMatch(MATCH_ID, MEMBER_ID);

        assertThat(response.getCurrentPlayers()).isEqualTo(10);
        assertThat(response.getStatus()).isEqualTo(MatchStatus.CLOSED.name());
    }

    @Test
    @DisplayName("인원이 꽉 찬 경기에 참가하면 MATCH_FULL 예외가 발생한다")
    void joinMatch_인원초과_예외() {
        Match match = matchOf(10, 10, MatchStatus.CLOSED);

        given(matchRepository.findById(MATCH_ID)).willReturn(Optional.of(match));

        assertThatThrownBy(() -> matchingService.joinMatch(MATCH_ID, MEMBER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.MATCH_FULL);
    }

    @Test
    @DisplayName("이미 참가(PENDING/APPROVED)한 경기에 재참가하면 ALREADY_JOINED 예외가 발생한다")
    void joinMatch_중복참가_예외() {
        Match match = matchOf(3, 10, MatchStatus.OPEN);

        given(matchRepository.findById(MATCH_ID)).willReturn(Optional.of(match));
        given(matchParticipantRepository.findByMatch_IdAndMember_MemberId(MATCH_ID, MEMBER_ID))
                .willReturn(Optional.of(mock(MatchParticipant.class)));

        assertThatThrownBy(() -> matchingService.joinMatch(MATCH_ID, MEMBER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ALREADY_JOINED);
    }

    @Test
    @DisplayName("존재하지 않는 경기에 참가하면 MATCH_NOT_FOUND 예외가 발생한다")
    void joinMatch_경기없음_예외() {
        given(matchRepository.findById(MATCH_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> matchingService.joinMatch(MATCH_ID, MEMBER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.MATCH_NOT_FOUND);
    }

    @Test
    @DisplayName("삭제된 경기에 참가하면 MATCH_DELETED 예외가 발생한다")
    void joinMatch_삭제된경기_예외() {
        Match match = matchOf(3, 10, MatchStatus.DELETED);
        given(matchRepository.findById(MATCH_ID)).willReturn(Optional.of(match));

        assertThatThrownBy(() -> matchingService.joinMatch(MATCH_ID, MEMBER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.MATCH_DELETED);
    }

    // -------------------------------------------------------
    // 헬퍼
    // -------------------------------------------------------

    private Match matchOf(int current, int max, MatchStatus status) {
        return Match.builder()
                .id(MATCH_ID)
                .title("테스트 경기")
                .sportType(SportType.SOCCER)
                .matchDate(LocalDateTime.now().plusDays(1))
                .locationName("서울 목동운동장")
                .latitude(37.5263)
                .longitude(126.8967)
                .maxPlayers(max)
                .currentPlayers(current)
                .entryFee(5000)
                .status(status)
                .build();
    }

    private Member memberOf(Long memberId) {
        return Member.builder()
                .memberId(memberId)
                .nickname("테스터")
                .build();
    }
}
