package com.playball.backend.compliment.service;

import com.playball.backend.common.exception.CustomException;
import com.playball.backend.common.exception.ErrorCode;
import com.playball.backend.compliment.dto.*;
import com.playball.backend.compliment.enums.ComplimentTag;
import com.playball.backend.compliment.mapper.ComplimentMapper;
import com.playball.backend.compliment.mapper.ComplimentTagMapper;
import com.playball.backend.compliment.mapper.MemberComplimentSummaryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComplimentService {

    private final ComplimentMapper complimentMapper;
    private final ComplimentTagMapper complimentTagMapper;
    private final MemberComplimentSummaryMapper summaryMapper;


    /**
     * 한 매치에 대해 여러 명에게 칭찬 동시 등록 (Bulk).
     *
     * 트랜잭션 atomic — 1명이라도 검증 실패 시 전체 롤백.
     */
    @Transactional
    public int submitBulkCompliments(Long raterId,
                                     Long matchId,
                                     ComplimentBulkRequest request) {

        List<ComplimentSubmitItem> items = request.getCompliments();

        // ===== 1단계: 사전 검증 (모두 통과 후에야 INSERT 진행) =====

        // 1-1. 같은 batch 안에서 같은 rateeId 중복 체크
        Set<Long> rateeIds = items.stream()
                .map(ComplimentSubmitItem::getRateeId)
                .collect(Collectors.toSet());
        if (rateeIds.size() != items.size()) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
            // 메시지: "같은 사람을 중복으로 칭찬할 수 없습니다"
        }

        // 1-2. 각 칭찬마다 검증
        for (ComplimentSubmitItem item : items) {
            // 자기 자신 차단
            if (raterId.equals(item.getRateeId())) {
                throw new CustomException(ErrorCode.SELF_COMPLIMENT);
                // 또는 ErrorCode.INVALID_INPUT
            }

            // 같은 매치에서 이미 그 사람을 칭찬했는지 (DB UNIQUE 와 별개로 친절한 메시지)
            int existing = complimentMapper.countByMatchRaterRatee(
                    matchId, raterId, item.getRateeId());
            if (existing > 0) {
                throw new CustomException(ErrorCode.DUPLICATE_COMPLIMENT);
            }
        }

        // TODO: 추가 검증 (매치 도메인 안정화 후):
        //   - 매치 상태 = COMPLETED 인지
        //   - 매치 종료 후 7일 이내인지
        //   - rater 와 모든 ratee 가 그 매치 참가자인지

        // ===== 2단계: INSERT 진행 =====

        int created = 0;
        for (ComplimentSubmitItem item : items) {

            // 2-1. compliment 테이블 INSERT
            ComplimentDTO compliment = ComplimentDTO.builder()
                    .matchId(matchId)
                    .raterId(raterId)
                    .rateeId(item.getRateeId())
                    .comment(item.getComment())
                    .build();
            complimentMapper.insertCompliment(compliment);
            // 이 시점에 compliment.complimentId 가 채워짐 (useGeneratedKeys)

            // 2-2. 태그들 INSERT + summary +1
            for (ComplimentTag tag : item.getTags()) {
                complimentTagMapper.insertTag(compliment.getComplimentId(), tag);
                summaryMapper.incrementCount(item.getRateeId(), tag);
            }

            created++;
        }

        log.info("Bulk compliment 등록: matchId={}, raterId={}, count={}",
                matchId, raterId, created);
        return created;
    }


    /**
     * 받은 칭찬 목록 조회 (Cursor 페이지네이션, tags 포함).
     */
    @Transactional(readOnly = true)
    public ComplimentListResponse getReceivedCompliments(Long memberId,
                                                         Long cursor,
                                                         int size) {
        List<ComplimentDTO> compliments = complimentMapper.findReceivedByMember(
                memberId, cursor, size);

        // N+1 패턴: 각 칭찬마다 태그 조회. 학습 단계라 OK.
        for (ComplimentDTO c : compliments) {
            List<ComplimentTag> tags = complimentTagMapper.findByComplimentId(
                    c.getComplimentId());
            c.setTags(tags);
        }

        Long nextCursor = compliments.size() < size
                ? null
                : compliments.get(compliments.size() - 1).getComplimentId();

        return ComplimentListResponse.builder()
                .items(compliments)
                .nextCursor(nextCursor)
                .build();
    }


    /**
     * 특정 매치에서 내가 한/받은 칭찬 조회.
     */
    @Transactional(readOnly = true)
    public Map<String, List<ComplimentDTO>> getMyMatchCompliments(Long memberId,
                                                                  Long matchId) {
        List<ComplimentDTO> given = complimentMapper.findGivenByRaterInMatch(
                memberId, matchId);
        List<ComplimentDTO> received = complimentMapper.findReceivedByRateeInMatch(
                memberId, matchId);

        // 태그 로드 (N+1)
        for (ComplimentDTO c : given) {
            c.setTags(complimentTagMapper.findByComplimentId(c.getComplimentId()));
        }
        for (ComplimentDTO c : received) {
            c.setTags(complimentTagMapper.findByComplimentId(c.getComplimentId()));
        }

        return Map.of(
                "given", given,
                "received", received
        );
    }


    /**
     * 회원의 칭찬 누적 카운트 (프로필용).
     */
    @Transactional(readOnly = true)
    public ComplimentSummaryDTO getMemberSummary(Long memberId) {
        List<MemberComplimentSummaryDTO> rows = summaryMapper.findByMemberId(memberId);

        // 5개 태그 모두 표시 (받은 적 없으면 0)
        Map<ComplimentTag, Integer> tagCounts = new EnumMap<>(ComplimentTag.class);
        for (ComplimentTag tag : ComplimentTag.values()) {
            tagCounts.put(tag, 0);   // 기본값
        }
        for (MemberComplimentSummaryDTO row : rows) {
            tagCounts.put(row.getTag(), row.getCount());
        }

        int total = tagCounts.values().stream().mapToInt(Integer::intValue).sum();

        return ComplimentSummaryDTO.builder()
                .memberId(memberId)
                .totalCount(total)
                .tagCounts(tagCounts)
                .build();
    }
}
