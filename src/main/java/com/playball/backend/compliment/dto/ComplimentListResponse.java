package com.playball.backend.compliment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplimentListResponse {  //받은 칭찬 목록 응답용
    private List<ComplimentDTO> items;
    private Long nextCursor;
}