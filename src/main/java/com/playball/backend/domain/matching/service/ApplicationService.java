package com.playball.backend.domain.matching.service;

import com.playball.backend.common.exception.CustomException;
import com.playball.backend.common.exception.ErrorCode;
import com.playball.backend.domain.matches.entity.Match;
import com.playball.backend.domain.matches.entity.MatchStatus;
import com.playball.backend.domain.matches.repository.MatchParticipantRepository;
import com.playball.backend.domain.matching.dto.ApplicationListItemResponse;
import com.playball.backend.domain.matching.entity.MatchParticipant;
import com.playball.backend.domain.matching.entity.ParticipantStatus;
import com.playball.backend.domain.matching.repository.MatchingRepository;
import com.playball.backend.domain.member.entity.Member;
import com.playball.backend.domain.member.repository.MemberRepository;
import com.playball.backend.domain.notification.service.NotificationTriggerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final MatchingRepository matchingRepository;
    private final MatchParticipantRepository matchParticipantRepository;
    private final MemberRepository memberRepository;
    private final NotificationTriggerService notificationTriggerService;

    @Transactional
    public Map<String, Object> apply(Long matchId, Long memberId) {
        Match match = matchingRepository.findById(matchId)
                .orElseThrow(() -> new CustomException(ErrorCode.MATCH_NOT_FOUND));

        if (match.getStatus() == MatchStatus.DELETED)
            throw new CustomException(ErrorCode.MATCH_DELETED);
        if (match.getCurrentPlayers() >= match.getMaxPlayers())
            throw new CustomException(ErrorCode.MATCH_FULL);

        boolean alreadyExists = matchParticipantRepository
                .existsByMatch_IdAndMember_MemberIdAndStatusIn(
                        matchId, memberId,
                        List.of(ParticipantStatus.PENDING, ParticipantStatus.APPROVED,
                                ParticipantStatus.REJECTED, ParticipantStatus.CANCELLED));
        if (alreadyExists)
            throw new CustomException(ErrorCode.ALREADY_APPLIED);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        MatchParticipant participant = matchParticipantRepository.save(
                MatchParticipant.builder()
                        .match(match)
                        .member(member)
                        .status(ParticipantStatus.PENDING)
                        .build());

        notificationTriggerService.sendApplicationReceived(
                match.getHostId(), matchId, match.getTitle(), memberId);

        return Map.of("applicationId", participant.getId(), "matchId", matchId, "status", "PENDING");
    }

    @Transactional(readOnly = true)
    public List<ApplicationListItemResponse> getApplications(Long matchId, Long hostId) {
        Match match = matchingRepository.findById(matchId)
                .orElseThrow(() -> new CustomException(ErrorCode.MATCH_NOT_FOUND));
        if(!match.getHostId().equals(hostId))
            throw new CustomException(ErrorCode.FORBIDDEN);

        return matchParticipantRepository
                .findByMatchAndStatus(match, ParticipantStatus.PENDING)
                .stream()
                .map(mp -> ApplicationListItemResponse.builder()
                        .applicationId(mp.getId())
                        .memberId(mp.getMember().getMemberId())
                        .nickname(mp.getMember().getNickname())
                        .profileImage(mp.getMember().getProfileImage())
                        .status(mp.getStatus().name())
                        .appliedAt(mp.getAppliedAt())
                        .build())
                .toList();
    }

    @Transactional
    public Map<String, Object> accept(Long matchId, Long applicationId, Long hostId) {
        Match match = matchingRepository.findById(matchId)
                .orElseThrow(() -> new CustomException(ErrorCode.MATCH_NOT_FOUND));
        if(!match.getHostId().equals(hostId))
            throw new CustomException(ErrorCode.FORBIDDEN);

        MatchParticipant participant = matchParticipantRepository.findById(applicationId)
                .orElseThrow(() -> new CustomException(ErrorCode.APPLICATION_NOT_FOUND));
        if(!participant.getMatch().getId().equals(matchId))
            throw new CustomException(ErrorCode.APPLICATION_NOT_FOUND);
        if(participant.getStatus() != ParticipantStatus.PENDING)
            throw new CustomException(ErrorCode.APPLICATION_ALREADY_PROCESSED);
        if(match.getCurrentPlayers() >= match.getMaxPlayers())
            throw new CustomException(ErrorCode.MATCH_FULL);

        participant.approve();

        MatchStatus newStatus = (match.getCurrentPlayers() + 1 >= match.getMaxPlayers())
                ? MatchStatus.CLOSED : MatchStatus.OPEN;
        match.incrementPlayers(newStatus);

        notificationTriggerService.sendApplicationAccepted(
                participant.getMember().getMemberId(), matchId, match.getTitle());

        return Map.of("applicationId", applicationId,"matchId", matchId,
                "memberId", participant.getMember().getMemberId(), "status", "APPROVED");
    }

    @Transactional
    public Map<String, Object> reject(Long matchId, Long applicationId, Long hostId) {
        Match match = matchingRepository.findById(matchId)
                .orElseThrow(() -> new CustomException(ErrorCode.MATCH_NOT_FOUND));
        if(!match.getHostId().equals(hostId))
            throw new CustomException(ErrorCode.FORBIDDEN);

        MatchParticipant participant = matchParticipantRepository.findById(applicationId)
                .orElseThrow(() -> new CustomException(ErrorCode.APPLICATION_NOT_FOUND));
        if(!participant.getMatch().getId().equals(matchId))
            throw new CustomException(ErrorCode.APPLICATION_NOT_FOUND);
        if(participant.getStatus() != ParticipantStatus.PENDING)
            throw new CustomException(ErrorCode.APPLICATION_ALREADY_PROCESSED);

        participant.reject();

        notificationTriggerService.sendApplicationRejected(
                participant.getMember().getMemberId(), matchId, match.getTitle());

        return Map.of("applicationId", applicationId,"matchId", matchId,
                "memberId", participant.getMember().getMemberId(), "status", "REJECTED");
    }
}
