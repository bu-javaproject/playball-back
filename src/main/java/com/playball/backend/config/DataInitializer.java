package com.playball.backend.config;

import com.playball.backend.domain.matching.entity.MatchParticipant;
import com.playball.backend.domain.matching.entity.ParticipantStatus;
import com.playball.backend.domain.matching.repository.MatchingRepository;
import com.playball.backend.domain.matches.dto.MatchCreateRequest.SkillLevel;
import com.playball.backend.domain.matches.entity.Match;
import com.playball.backend.domain.matches.entity.MatchStatus;
import com.playball.backend.domain.matches.entity.SportType;
import com.playball.backend.domain.matches.repository.MatchParticipantRepository;
import com.playball.backend.domain.member.entity.Member;
import com.playball.backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final MatchingRepository matchingRepository;
    private final MatchParticipantRepository matchParticipantRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (memberRepository.count() > 0) {
            log.info("더미 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        Member m1 = memberRepository.save(Member.builder()
                .kakaoId(1001L).email("test1@playball.com").nickname("풋살킹")
                .name("홍길동").gender("M").age(28).skillLevel("INTERMEDIATE")
                .preferredPosition("FW").latitude(37.3236).longitude(126.8219)
                .address("경기도 안산시 단원구").role("USER").signupCompleted(true)
                .favoriteSports(List.of("SOCCER", "RUNNING")).build());

        Member m2 = memberRepository.save(Member.builder()
                .kakaoId(1002L).email("test2@playball.com").nickname("농구천재")
                .name("김철수").gender("M").age(32).skillLevel("ADVANCED")
                .preferredPosition("C").latitude(37.3236).longitude(126.8219)
                .address("경기도 안산시 단원구").role("USER").signupCompleted(true)
                .favoriteSports(List.of("BASKETBALL")).build());

        Member m3 = memberRepository.save(Member.builder()
                .kakaoId(1003L).email("test3@playball.com").nickname("배드민턴여왕")
                .name("이영희").gender("F").age(26).skillLevel("BEGINNER")
                .preferredPosition(null).latitude(37.3236).longitude(126.8219)
                .address("경기도 안산시 단원구").role("USER").signupCompleted(true)
                .favoriteSports(List.of("BADMINTON")).build());

        // 테스트용 자유 참가 계정 — 아무 경기도 개설하지 않아 6개 경기 모두 참가 가능
        memberRepository.save(Member.builder()
                .kakaoId(1004L).email("test4@playball.com").nickname("테스터")
                .name("테스트유저").gender("M").age(25).skillLevel("BEGINNER")
                .preferredPosition(null).latitude(37.3236).longitude(126.8219)
                .address("경기도 안산시 단원구").role("USER").signupCompleted(true)
                .favoriteSports(List.of("SOCCER", "BASKETBALL", "BADMINTON", "RUNNING")).build());

        log.info("테스트 멤버 저장 완료 — memberId: {}, {}, {}, 4(테스터)", m1.getMemberId(), m2.getMemberId(), m3.getMemberId());

        record MatchSpec(Member host, String title, SportType sport, int daysLater,
                         String locationName, double lat, double lng, String address,
                         int max, String gender, Integer ageRange, SkillLevel skill,
                         int fee, String desc) {}

        List<MatchSpec> specs = List.of(
            new MatchSpec(m1, "주말 풋살 같이 하실 분!", SportType.SOCCER, 7,
                "안산 중앙공원 풋살장", 37.3282, 126.8198, "경기도 안산시 단원구 중앙대로 123",
                10, null, null, SkillLevel.BEGINNER, 0, "초보 환영! 신나게 뛰어봐요"),
            new MatchSpec(m2, "농구 5:5 팀원 구합니다", SportType.BASKETBALL, 8,
                "안산 고잔동 농구코트", 37.3193, 126.8257, "경기도 안산시 단원구 고잔동 456",
                10, "M", 30, SkillLevel.INTERMEDIATE, 5000, "30대 남성 우대, 중급 이상"),
            new MatchSpec(m3, "아침 러닝 크루 모집", SportType.RUNNING, 11,
                "안산 화랑유원지", 37.3318, 126.8283, "경기도 안산시 상록구 화랑로 789",
                20, null, 20, SkillLevel.BEGINNER, 0, "아침 러닝 5km, 페이스 6분대"),
            new MatchSpec(m1, "배드민턴 복식 파트너 구해요", SportType.BADMINTON, 12,
                "안산 실내배드민턴장", 37.3160, 126.8165, "경기도 안산시 단원구 선부동 101",
                4, null, null, SkillLevel.INTERMEDIATE, 3000, "복식 위주, 셔틀콕 제공"),
            new MatchSpec(m2, "풋살 야간 경기 팀원 모집", SportType.SOCCER, 14,
                "안산 초지동 풋살파크", 37.3351, 126.8221, "경기도 안산시 단원구 초지동 202",
                12, "M", null, SkillLevel.ADVANCED, 8000, "야간 조명 완비, 실력자만"),
            new MatchSpec(m3, "주말 농구 3:3 스트리트볼", SportType.BASKETBALL, 15,
                "안산 원곡동 야외농구장", 37.3221, 126.8351, "경기도 안산시 단원구 원곡동 303",
                6, null, null, SkillLevel.BEGINNER, 0, "누구나 환영!")
        );

        for (MatchSpec s : specs) {
            Match match = matchingRepository.save(Match.builder()
                    .hostId(s.host().getMemberId())
                    .title(s.title()).sportType(s.sport())
                    .matchDate(LocalDateTime.now().plusDays(s.daysLater()))
                    .locationName(s.locationName()).latitude(s.lat()).longitude(s.lng())
                    .address(s.address()).maxPlayers(s.max()).currentPlayers(1)
                    .gender(s.gender()).ageRange(s.ageRange()).skillLevel(s.skill())
                    .entryFee(s.fee()).description(s.desc()).status(MatchStatus.OPEN)
                    .build());

            matchParticipantRepository.save(MatchParticipant.builder()
                    .match(match).member(s.host()).status(ParticipantStatus.APPROVED)
                    .build());
        }

        log.info("테스트 경기 6건 저장 완료");
    }
}
