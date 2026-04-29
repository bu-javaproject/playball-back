package com.playball.backend.member.mapper;

import com.playball.backend.member.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MemberMapper {

    //회원 등록 (카카오 로그인 시 최초 INSERT)
    void insertMember(MemberDTO member);

    //카카오 ID로 회원 조회
    Optional<MemberDTO> findByKakaoId(@Param("kakaoId") Long kakaoId);

    //ID로 회원 조회
    Optional<MemberDTO> findById(@Param("memberId") Long memberId);

    //닉네임 중복 확인
    int countByNickname(@Param("nickname") String nickname);

    //추가정보 입력 완료 (회원가입 완료)
    void completeSignUp(MemberDTO member);

    //회원 정보 수정
    void updateMember(MemberDTO member);

    //위치 정보 수정
    void updateLocation(@Param("memberId") Long memberId,
                        @Param("latitude") Double latitude,
                        @Param("longitude") Double longitude,
                        @Param("address") String address);

    //회원 탈퇴
    void deleteMember(@Param("memberId") Long memberId);

    //선호하는 종목
    void insertFavoriteSport(@Param("memberId") Long memberId,
                             @Param("sportType") String sportType);

    void deleteFavoriteSport(@Param("memberId") Long memberId);

    List<String> findFavoriteSports(@Param("memberId") Long memberId);

    void deleteFavoriteSports(Long memberId);
}
