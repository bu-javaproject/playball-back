package com.playball.backend.domain.member.service;

import com.playball.backend.common.exception.CustomException;
import com.playball.backend.common.exception.ErrorCode;
import com.playball.backend.domain.member.dto.MemberDTO;
import com.playball.backend.domain.member.dto.ProfileResponse;
import com.playball.backend.domain.member.dto.SignUpCompleteRequest;
import com.playball.backend.domain.member.dto.UpdateProfileRequest;
import com.playball.backend.domain.member.entity.Member;
import com.playball.backend.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberDTO completeSignup(Long memberId, SignUpCompleteRequest request) {
        Member member = getMemberEntity(memberId);

        if (Boolean.TRUE.equals(member.getSignupCompleted())) {
            throw new CustomException(ErrorCode.SIGNUP_ALREADY_COMPLETED);
        }

        if (memberRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        member.completeSignup(
                request.getNickname(),
                request.getGender(),
                request.getAge(),
                request.getAddress(),
                request.getLatitude(),
                request.getLongitude(),
                request.getSkillLevel() != null ? request.getSkillLevel() : "BEGINNER",
                request.getPreferredPosition()
        );

        if (request.getFavoriteSports() != null) {
            member.getFavoriteSports().clear();
            member.getFavoriteSports().addAll(request.getFavoriteSports());
        }

        return toDto(member);
    }

    public boolean isNicknameDuplicate(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    public ProfileResponse getMyProfile(Long memberId) {
        return toProfileResponse(getMemberEntity(memberId));
    }

    public ProfileResponse getMemberById(Long memberId) {
        return toProfileResponse(getMemberEntity(memberId));
    }

    @Transactional
    public ProfileResponse updateProfile(Long memberId, UpdateProfileRequest request) {
        Member member = getMemberEntity(memberId);

        if (request.getNickname() != null
                && !request.getNickname().equals(member.getNickname())
                && memberRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        member.updateProfile(
                request.getNickname(),
                request.getAddress(),
                request.getLatitude(),
                request.getLongitude()
        );

        if (request.getFavoriteSports() != null) {
            member.getFavoriteSports().clear();
            member.getFavoriteSports().addAll(request.getFavoriteSports());
        }

        return toProfileResponse(member);
    }

    @Transactional
    public void updateLocation(Long memberId, Double latitude, Double longitude, String address) {
        Member member = getMemberEntity(memberId);
        member.updateLocation(latitude, longitude, address);
    }

    @Transactional
    public void deleteMember(Long memberId) {
        Member member = getMemberEntity(memberId);
        memberRepository.delete(member);
    }

    private Member getMemberEntity(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public ProfileResponse toProfileResponse(Member member) {
        return ProfileResponse.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .gender(member.getGender())
                .age(member.getAge())
                .profileImage(member.getProfileImage())
                .address(member.getAddress())
                .skillLevel(member.getSkillLevel())
                .preferredPosition(member.getPreferredPosition())
                .favoriteSports(new ArrayList<>(member.getFavoriteSports()))
                .build();
    }

    public MemberDTO toDto(Member member) {
        return MemberDTO.builder()
                .memberId(member.getMemberId())
                .kakaoId(member.getKakaoId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .name(member.getName())
                .phone(member.getPhone())
                .gender(member.getGender())
                .age(member.getAge())
                .profileImage(member.getProfileImage())
                .skillLevel(member.getSkillLevel())
                .preferredPosition(member.getPreferredPosition())
                .latitude(member.getLatitude())
                .longitude(member.getLongitude())
                .address(member.getAddress())
                .role(member.getRole())
                .signupCompleted(member.getSignupCompleted())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .favoriteSports(new ArrayList<>(member.getFavoriteSports()))
                .build();
    }
}
