package com.playball.backend.member.service;

import com.playball.backend.common.exception.CustomException;
import com.playball.backend.common.exception.ErrorCode;
import com.playball.backend.member.dto.MemberDTO;
import com.playball.backend.member.dto.SignUpCompleteRequest;
import com.playball.backend.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper memberMapper;

    //추가 정보 입력 완료
    @Transactional
    public MemberDTO completeSignup(Long memberId, SignUpCompleteRequest request) {

        //1. 회원 존재 확인
        MemberDTO member = memberMapper.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        //2. 이미 가입 완료된 회원인지 확인
        if (Boolean.TRUE.equals(member.getSignupCompleted())) {
        throw new CustomException(ErrorCode.SIGNUP_ALREADY_COMPLETED);
        }

        //3. 닉네임 중복 확인
        if (memberMapper.countByNickname(request.getNickname()) > 0) {
            throw new CustomException(ErrorCode.SIGNUP_ALREADY_COMPLETED);
        }

        //4. 추가 정보 업데이트
        MemberDTO updateDto = MemberDTO.builder()
                .memberId(memberId)
                .nickname(request.getNickname())
                .gender(request.getGender())
                .age(request.getAge())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .skillLevel(request.getSkillLevel() != null ? request.getSkillLevel() : "BEGINNER")
                .preferredPosition(request.getPreferedPosition())
                .build();
        memberMapper.completeSignUp(updateDto);

        //5. 즐겨하는 종목 등록
        saveFavoriteSports(memberId, request.getFavoriteSports());

        //6. 완성된 회원 정보 리턴
        return getMemberWithSports(memberId);
    }

    //닉네임 중복 확인
    public boolean isNicknameDuplicate(String nickname) {
        return memberMapper.countByNickname(nickname) > 0;
    }

    //내 프로필 조회
    public MemberDTO getMyProfile(Long memberId) {
        return getMemberWithSports(memberId);
    }

    //다른 회원 프로필 조회
    public MemberDTO getMemberById(Long memberId) {
        return getMemberWithSports(memberId);
    }

    //프로필 수정
    @Transactional
    public MemberDTO updateProfile(Long memberId, MemberDTO updateRequest) {

        // 1. 현재 회원 정보 조회
        MemberDTO existing = memberMapper.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 2. 닉네임 변경 시 중복 확인
        if (updateRequest.getNickname() != null
                && !updateRequest.getNickname().equals(existing.getNickname())
                && memberMapper.countByNickname(updateRequest.getNickname()) > 0) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        // 3. DB 업데이트
        updateRequest.setMemberId(memberId);
        memberMapper.updateMember(updateRequest);

        // 4. 즐겨하는 종목 수정
        if (updateRequest.getFavoriteSports() != null) {
            saveFavoriteSports(memberId, updateRequest.getFavoriteSports());
        }

        // 5. 수정된 정보 리턴
        return getMemberWithSports(memberId);
    }

    //위치 업데이트
    @Transactional
    public void updateLocation(Long memberId, Double latitude, Double longitude, String address) {
        memberMapper.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        memberMapper.updateLocation(memberId, latitude, longitude, address);
    }

    //회원 탈퇴
    @Transactional
    public void deleteMember(Long memberId) {
        memberMapper.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        memberMapper.deleteMember(memberId);
    }

    // 회원 정보 + 즐겨하는 종목 합쳐서 리턴
    private MemberDTO getMemberWithSports(Long memberId) {
        MemberDTO member = memberMapper.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        member.setFavoriteSports(memberMapper.findFavoriteSports(memberId));
        return member;
    }

    // 즐겨하는 종목 저장 (기존 삭제 후 재등록)
    private void saveFavoriteSports(Long memberId, List<String> sports) {
        memberMapper.deleteFavoriteSports(memberId);
        if (sports != null) {
            for (String sport : sports) {
                memberMapper.insertFavoriteSport(memberId, sport);
            }
        }
    }


}
