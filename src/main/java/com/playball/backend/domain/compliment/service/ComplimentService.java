package com.playball.backend.domain.compliment.service;

import com.playball.backend.common.exception.CustomException;
import com.playball.backend.common.exception.ErrorCode;
import com.playball.backend.domain.compliment.dto.*;
import com.playball.backend.domain.compliment.entity.Compliment;
import com.playball.backend.domain.compliment.entity.ComplimentTagEntity;
import com.playball.backend.domain.compliment.entity.MemberComplimentSummary;
import com.playball.backend.domain.compliment.enums.ComplimentTag;
import com.playball.backend.domain.compliment.repository.ComplimentRepository;
import com.playball.backend.domain.compliment.repository.ComplimentTagRepository;
import com.playball.backend.domain.compliment.repository.MemberComplimentSummaryRepository;

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
    private final ComplimentTagRepository complimentTagRepository;
    private final MemberComplimentSummaryRepository summaryRepository;

    @Transactional
    public int submitBulkCompliments(Long raterId, Long matchId, ComplimentBulkRequest request) {
        List<ComplimentSubmitItem> items = request.getCompliments();

        // 같은 batch 안 중복 rateeId 체크
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

            int existing = complimentRepository.countByMatchIdAndRaterIdAndRateeId(
                    matchId, raterId, item.getRateeId());
            if (existing > 0) {
                throw new CustomException(ErrorCode.DUPLICATE_COMPLIMENT);
            }
        }

        int created = 0;
        for (ComplimentSubmitItem item : items) {
            Compliment saved = complimentRepository.save(Compliment.builder()
                    .matchId(matchId)
                    .raterId(raterId)
                    .rateeId(item.getRateeId())
                    .comment(item.getComment())
                    .build());

            for (ComplimentTag tag : item.getTags()) {
                complimentTagRepository.save(ComplimentTagEntity.builder()
                        .complimentId(saved.getComplimentId())
                        .tag(tag)
                        .build());
                summaryRepository.incrementCount(item.getRateeId(), tag.name());
            }

            created++;
        }

        log.info("Bulk compliment 등록: matchId={}, raterId={}, count={}", matchId, raterId, created);
        return created;
    }

    @Transactional(readOnly = true)
    public ComplimentListResponse getReceivedCompliments(Long memberId, Long cursor, int size) {
        List<Compliment> compliments = complimentRepository.findReceivedByMemberWithCursor(
                memberId, cursor, PageRequest.ofSize(size));

        List<Long> ids = compliments.stream().map(Compliment::getComplimentId).toList();
        Map<Long, List<ComplimentTag>> tagMap = complimentTagRepository.findByComplimentIdIn(ids)
                .stream()
                .collect(Collectors.groupingBy(
                        ComplimentTagEntity::getComplimentId,
                        Collectors.mapping(ComplimentTagEntity::getTag, Collectors.toList())
                ));

        List<ComplimentDTO> items = compliments.stream()
                .map(c -> toDto(c, tagMap.getOrDefault(c.getComplimentId(), List.of())))
                .toList();

        Long nextCursor = items.size() < size
                ? null
                : items.get(items.size() - 1).getComplimentId();

        return ComplimentListResponse.builder()
                .items(items)
                .nextCursor(nextCursor)
                .build();
    }

    @Transactional(readOnly = true)
    public Map<String, List<ComplimentDTO>> getMyMatchCompliments(Long memberId, Long matchId) {
        List<Compliment> givenList = complimentRepository
                .findByRaterIdAndMatchIdOrderByComplimentIdDesc(memberId, matchId);
        List<Compliment> receivedList = complimentRepository
                .findByRateeIdAndMatchIdOrderByComplimentIdDesc(memberId, matchId);

        List<Long> allIds = new java.util.ArrayList<>();
        givenList.forEach(c -> allIds.add(c.getComplimentId()));
        receivedList.forEach(c -> allIds.add(c.getComplimentId()));

        Map<Long, List<ComplimentTag>> tagMap = complimentTagRepository.findByComplimentIdIn(allIds)
                .stream()
                .collect(Collectors.groupingBy(
                        ComplimentTagEntity::getComplimentId,
                        Collectors.mapping(ComplimentTagEntity::getTag, Collectors.toList())
                ));

        List<ComplimentDTO> given = givenList.stream()
                .map(c -> toDto(c, tagMap.getOrDefault(c.getComplimentId(), List.of()))).toList();
        List<ComplimentDTO> received = receivedList.stream()
                .map(c -> toDto(c, tagMap.getOrDefault(c.getComplimentId(), List.of()))).toList();

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

    private ComplimentDTO toDto(Compliment c, List<ComplimentTag> tags) {
        return ComplimentDTO.builder()
                .complimentId(c.getComplimentId())
                .matchId(c.getMatchId())
                .raterId(c.getRaterId())
                .rateeId(c.getRateeId())
                .comment(c.getComment())
                .createdAt(c.getCreatedAt())
                .tags(tags)
                .build();
    }
}
