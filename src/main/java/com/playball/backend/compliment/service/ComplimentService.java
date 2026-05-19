package com.playball.backend.compliment.service;

import com.playball.backend.common.exception.CustomException;
import com.playball.backend.common.exception.ErrorCode;
import com.playball.backend.compliment.dto.*;
import com.playball.backend.compliment.entity.Compliment;
import com.playball.backend.compliment.entity.MemberComplimentSummary;
import com.playball.backend.compliment.enums.ComplimentTag;
import com.playball.backend.compliment.repository.ComplimentRepository;
import com.playball.backend.compliment.repository.MemberComplimentSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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

    private final ComplimentRepository complimentRepository;
    private final MemberComplimentSummaryRepository summaryRepository;

    @Transactional
    public int submitBulkCompliments(Long raterId, Long matchId, ComplimentBulkRequest request) {
        List<ComplimentSubmitItem> items = request.getCompliments();

        Set<Long> rateeIds = items.stream()
                .map(ComplimentSubmitItem::getRateeId)
                .collect(Collectors.toSet());
        if (rateeIds.size() != items.size()) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        for (ComplimentSubmitItem item : items) {
            if (raterId.equals(item.getRateeId())) {
                throw new CustomException(ErrorCode.SELF_COMPLIMENT);
            }
            if (complimentRepository.countByMatchIdAndRaterIdAndRateeId(matchId, raterId, item.getRateeId()) > 0) {
                throw new CustomException(ErrorCode.DUPLICATE_COMPLIMENT);
            }
        }

        int created = 0;
        for (ComplimentSubmitItem item : items) {
            Compliment compliment = Compliment.builder()
                    .matchId(matchId)
                    .raterId(raterId)
                    .rateeId(item.getRateeId())
                    .comment(item.getComment())
                    .tags(item.getTags())
                    .build();
            complimentRepository.save(compliment);

            for (ComplimentTag tag : item.getTags()) {
                incrementSummary(item.getRateeId(), tag);
            }
            created++;
        }

        log.info("Bulk compliment 등록: matchId={}, raterId={}, count={}", matchId, raterId, created);
        return created;
    }

    @Transactional(readOnly = true)
    public ComplimentListResponse getReceivedCompliments(Long memberId, Long cursor, int size) {
        List<Compliment> compliments = complimentRepository.findReceivedByMember(
                memberId, cursor, PageRequest.of(0, size));

        List<ComplimentDTO> items = compliments.stream().map(this::toDTO).toList();
        Long nextCursor = items.size() < size ? null : items.get(items.size() - 1).getComplimentId();

        return ComplimentListResponse.builder().items(items).nextCursor(nextCursor).build();
    }

    @Transactional(readOnly = true)
    public Map<String, List<ComplimentDTO>> getMyMatchCompliments(Long memberId, Long matchId) {
        List<ComplimentDTO> given = complimentRepository
                .findByRaterIdAndMatchIdOrderByComplimentIdDesc(memberId, matchId)
                .stream().map(this::toDTO).toList();
        List<ComplimentDTO> received = complimentRepository
                .findByRateeIdAndMatchIdOrderByComplimentIdDesc(memberId, matchId)
                .stream().map(this::toDTO).toList();

        return Map.of("given", given, "received", received);
    }

    @Transactional(readOnly = true)
    public ComplimentSummaryDTO getMemberSummary(Long memberId) {
        List<MemberComplimentSummary> rows = summaryRepository.findByMemberIdOrderByCountDesc(memberId);

        Map<ComplimentTag, Integer> tagCounts = new EnumMap<>(ComplimentTag.class);
        for (ComplimentTag tag : ComplimentTag.values()) {
            tagCounts.put(tag, 0);
        }
        for (MemberComplimentSummary row : rows) {
            tagCounts.put(row.getTag(), row.getCount());
        }
        int total = tagCounts.values().stream().mapToInt(Integer::intValue).sum();

        return ComplimentSummaryDTO.builder()
                .memberId(memberId)
                .totalCount(total)
                .tagCounts(tagCounts)
                .build();
    }

    private void incrementSummary(Long memberId, ComplimentTag tag) {
        summaryRepository.findByMemberIdAndTag(memberId, tag)
                .ifPresentOrElse(
                        s -> { s.increment(); summaryRepository.save(s); },
                        () -> summaryRepository.save(MemberComplimentSummary.builder()
                                .memberId(memberId).tag(tag).count(1).build())
                );
    }

    private ComplimentDTO toDTO(Compliment c) {
        return ComplimentDTO.builder()
                .complimentId(c.getComplimentId())
                .matchId(c.getMatchId())
                .raterId(c.getRaterId())
                .rateeId(c.getRateeId())
                .comment(c.getComment())
                .createdAt(c.getCreatedAt())
                .tags(c.getTags())
                .build();
    }
}