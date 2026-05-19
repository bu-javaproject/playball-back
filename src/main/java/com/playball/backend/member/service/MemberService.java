package com.playball.backend.member.service;

import com.playball.backend.common.exception.CustomException;
import com.playball.backend.common.exception.ErrorCode;
import com.playball.backend.member.dto.MemberDTO;
import com.playball.backend.member.dto.SignUpCompleteRequest;
import com.playball.backend.member.entity.Member;
import com.playball.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberDTO completeSignup(Long memberId, SignUpCompleteRequest request) {
        Member member = findMemberOrThrow(memberId);

        if (Boolean.TRUE.equals(member.getSignupCompleted())) {
            throw new CustomException(ErrorCode.SIGNUP_ALREADY_COMPLETED);
        }
        if (memberRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        member.setNickname(request.getNickname());
        member.setGender(request.getGender());
        member.setAge(request.getAge());
        member.setAddress(request.getAddress());
        member.setLatitude(request.getLatitude());
        member.setLongitude(request.getLongitude());
        member.setSkillLevel(request.getSkillLevel() != null ? request.getSkillLevel() : "BEGINNER");
        member.setPreferredPosition(request.getPreferedPosition());
        member.setSignupCompleted(true);

        if (request.getFavoriteSports() != null) {
            member.getFavoriteSports().clear();
            member.getFavoriteSports().addAll(request.getFavoriteSports());
        }

        return toDTO(memberRepository.save(member));
    }

    public boolean isNicknameDuplicate(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    @Transactional(readOnly = true)
    public MemberDTO getMyProfile(Long memberId) {
        return toDTO(findMemberOrThrow(memberId));
    }

    @Transactional(readOnly = true)
    public MemberDTO getMemberById(Long memberId) {
        return toDTO(findMemberOrThrow(memberId));
    }

    @Transactional
    public MemberDTO updateProfile(Long memberId, MemberDTO updateRequest) {
        Member member = findMemberOrThrow(memberId);

        if (updateRequest.getNickname() != null
                && !updateRequest.getNickname().equals(member.getNickname())
                && memberRepository.existsByNickname(updateRequest.getNickname())) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        if (updateRequest.getNickname() != null) member.setNickname(updateRequest.getNickname());
        if (updateRequest.getName() != null) member.setName(updateRequest.getName());
        if (updateRequest.getPhone() != null) member.setPhone(updateRequest.getPhone());
        if (updateRequest.getAge() != null) member.setAge(updateRequest.getAge());
        if (updateRequest.getProfileImage() != null) member.setProfileImage(updateRequest.getProfileImage());
        if (updateRequest.getSkillLevel() != null) member.setSkillLevel(updateRequest.getSkillLevel());
        if (updateRequest.getPreferredPosition() != null) member.setPreferredPosition(updateRequest.getPreferredPosition());
        if (updateRequest.getAddress() != null) member.setAddress(updateRequest.getAddress());
        if (updateRequest.getLatitude() != null) member.setLatitude(updateRequest.getLatitude());
        if (updateRequest.getLongitude() != null) member.setLongitude(updateRequest.getLongitude());

        if (updateRequest.getFavoriteSports() != null) {
            member.getFavoriteSports().clear();
            member.getFavoriteSports().addAll(updateRequest.getFavoriteSports());
        }

        return toDTO(memberRepository.save(member));
    }

    @Transactional
    public void updateLocation(Long memberId, Double latitude, Double longitude, String address) {
        Member member = findMemberOrThrow(memberId);
        member.setLatitude(latitude);
        member.setLongitude(longitude);
        member.setAddress(address);
        memberRepository.save(member);
    }

    @Transactional
    public void deleteMember(Long memberId) {
        findMemberOrThrow(memberId);
        memberRepository.deleteById(memberId);
    }

    private Member findMemberOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private MemberDTO toDTO(Member m) {
        MemberDTO dto = MemberDTO.builder()
                .memberId(m.getMemberId())
                .kakaoId(m.getKakaoId())
                .email(m.getEmail())
                .nickname(m.getNickname())
                .name(m.getName())
                .phone(m.getPhone())
                .gender(m.getGender())
                .age(m.getAge())
                .profileImage(m.getProfileImage())
                .skillLevel(m.getSkillLevel())
                .preferredPosition(m.getPreferredPosition())
                .latitude(m.getLatitude())
                .longitude(m.getLongitude())
                .address(m.getAddress())
                .role(m.getRole())
                .signupCompleted(m.getSignupCompleted())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build();
        dto.setFavoriteSports(List.copyOf(m.getFavoriteSports()));
        return dto;
    }
}