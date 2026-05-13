package com.playball.backend.config;

import com.playball.backend.domain.matches.dto.MatchCreateRequest.SkillLevel;
import com.playball.backend.domain.matches.entity.Match;
import com.playball.backend.domain.matches.entity.MatchStatus;
import com.playball.backend.domain.matches.entity.SportType;
import com.playball.backend.domain.matching.repository.MatchingRepository;
import com.playball.backend.member.entity.Member;
import com.playball.backend.member.repository.MemberRepository;
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

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        initMembers();
        initMatches();
    }

    private void initMembers() {
        if (memberRepository.count() > 0) {
            log.info("이미 회원 데이터가 존재합니다. 회원 초기화를 건너뜁니다.");
            return;
        }

        List<Member> members = List.of(
                Member.builder()
                        .kakaoId(1001L)
                        .email("test1@playball.com")
                        .nickname("축구왕철수")
                        .name("김철수")
                        .phone("010-1234-5678")
                        .gender("M")
                        .age(28)
                        .skillLevel("INTERMEDIATE")
                        .preferredPosition("FW")
                        .latitude(37.5563)
                        .longitude(126.9236)
                        .address("서울특별시 마포구")
                        .role("USER")
                        .signupCompleted(true)
                        .favoriteSports(List.of("SOCCER", "RUNNING"))
                        .build(),

                Member.builder()
                        .kakaoId(1002L)
                        .email("test2@playball.com")
                        .nickname("농구소녀영희")
                        .name("이영희")
                        .phone("010-2345-6789")
                        .gender("F")
                        .age(25)
                        .skillLevel("BEGINNER")
                        .preferredPosition("PG")
                        .latitude(37.5281)
                        .longitude(126.9344)
                        .address("서울특별시 영등포구")
                        .role("USER")
                        .signupCompleted(true)
                        .favoriteSports(List.of("BASKETBALL"))
                        .build(),

                Member.builder()
                        .kakaoId(1003L)
                        .email("test3@playball.com")
                        .nickname("배드민턴고수민준")
                        .name("박민준")
                        .phone("010-3456-7890")
                        .gender("M")
                        .age(32)
                        .skillLevel("ADVANCED")
                        .preferredPosition(null)
                        .latitude(37.5145)
                        .longitude(127.1059)
                        .address("서울특별시 송파구")
                        .role("USER")
                        .signupCompleted(true)
                        .favoriteSports(List.of("BADMINTON", "SOCCER"))
                        .build(),

                Member.builder()
                        .kakaoId(1004L)
                        .email("test4@playball.com")
                        .nickname("러닝메이트지수")
                        .name("최지수")
                        .phone("010-4567-8901")
                        .gender("F")
                        .age(30)
                        .skillLevel("BEGINNER")
                        .preferredPosition(null)
                        .latitude(37.5124)
                        .longitude(126.9945)
                        .address("서울특별시 서초구")
                        .role("USER")
                        .signupCompleted(true)
                        .favoriteSports(List.of("RUNNING", "BADMINTON"))
                        .build(),

                Member.builder()
                        .kakaoId(1005L)
                        .email("test5@playball.com")
                        .nickname("만능스포츠맨준호")
                        .name("정준호")
                        .phone("010-5678-9012")
                        .gender("M")
                        .age(27)
                        .skillLevel("INTERMEDIATE")
                        .preferredPosition("MF")
                        .latitude(37.4979)
                        .longitude(127.0276)
                        .address("서울특별시 강남구")
                        .role("USER")
                        .signupCompleted(true)
                        .favoriteSports(List.of("SOCCER", "BASKETBALL", "RUNNING"))
                        .build()
        );

        memberRepository.saveAll(members);
        log.info("테스트 회원 데이터 {}건 저장 완료", members.size());
    }

    private void initMatches() {
        if (matchingRepository.count() > 0) {
            log.info("이미 경기 데이터가 존재합니다. 경기 초기화를 건너뜁니다.");
            return;
        }

        List<Match> matches = List.of(
                Match.builder()
                        .title("주말 풋살 같이 하실 분!")
                        .sportType(SportType.SOCCER)
                        .matchDate(LocalDateTime.now().plusDays(3))
                        .locationName("서울 마포구 풋살장")
                        .latitude(37.5563)
                        .longitude(126.9236)
                        .address("서울특별시 마포구 월드컵로 123")
                        .maxPlayers(10)
                        .currentPlayers(3)
                        .skillLevel(SkillLevel.BEGINNER)
                        .entryFee(5000)
                        .description("초보 환영! 즐겁게 뛰어요.")
                        .status(MatchStatus.OPEN)
                        .build(),

                Match.builder()
                        .title("3대3 농구 팀원 모집")
                        .sportType(SportType.BASKETBALL)
                        .matchDate(LocalDateTime.now().plusDays(1))
                        .locationName("한강공원 농구코트")
                        .latitude(37.5281)
                        .longitude(126.9344)
                        .address("서울특별시 영등포구 여의동로 330")
                        .maxPlayers(6)
                        .currentPlayers(4)
                        .skillLevel(SkillLevel.INTERMEDIATE)
                        .entryFee(0)
                        .description("3:3 경기, 중급자 이상 환영합니다.")
                        .status(MatchStatus.OPEN)
                        .build(),

                Match.builder()
                        .title("배드민턴 복식 파트너 구해요")
                        .sportType(SportType.BADMINTON)
                        .matchDate(LocalDateTime.now().plusDays(2))
                        .locationName("송파구 실내 배드민턴장")
                        .latitude(37.5145)
                        .longitude(127.1059)
                        .address("서울특별시 송파구 올림픽로 300")
                        .maxPlayers(4)
                        .currentPlayers(2)
                        .skillLevel(SkillLevel.ADVANCED)
                        .entryFee(3000)
                        .description("복식 경기 파트너 구합니다. 고수 환영!")
                        .status(MatchStatus.OPEN)
                        .build(),

                Match.builder()
                        .title("한강 러닝 크루 모집")
                        .sportType(SportType.RUNNING)
                        .matchDate(LocalDateTime.now().plusDays(5))
                        .locationName("한강 반포지구 출발")
                        .latitude(37.5124)
                        .longitude(126.9945)
                        .address("서울특별시 서초구 반포동 한강공원")
                        .maxPlayers(8)
                        .currentPlayers(1)
                        .skillLevel(SkillLevel.BEGINNER)
                        .entryFee(0)
                        .description("5km 코스, 페이스 무관 누구나 환영!")
                        .status(MatchStatus.OPEN)
                        .build(),

                Match.builder()
                        .title("직장인 풋살 정기 모임")
                        .sportType(SportType.SOCCER)
                        .matchDate(LocalDateTime.now().plusDays(7))
                        .locationName("강남구 풋살 파크")
                        .latitude(37.4979)
                        .longitude(127.0276)
                        .address("서울특별시 강남구 테헤란로 45")
                        .maxPlayers(12)
                        .currentPlayers(7)
                        .skillLevel(SkillLevel.INTERMEDIATE)
                        .entryFee(8000)
                        .description("매주 목요일 정기 모임, 직장인 우선!")
                        .status(MatchStatus.OPEN)
                        .build()
        );

        matchingRepository.saveAll(matches);
        log.info("테스트 경기 데이터 {}건 저장 완료", matches.size());
    }
}
