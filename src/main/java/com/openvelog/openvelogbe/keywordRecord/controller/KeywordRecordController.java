package com.openvelog.openvelogbe.keywordRecord.controller;

import com.openvelog.openvelogbe.keywordRecord.dto.KeywordRecordResponseDto;
import com.openvelog.openvelogbe.keywordRecord.service.KeywordRecordService;
import com.openvelog.openvelogbe.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "KeywordRecord")
@RestController
@RequestMapping("/api/aggregated")
@RequiredArgsConstructor
public class KeywordRecordController {

    private final KeywordRecordService keywordService;

    @GetMapping
    @SecurityRequirements()
    @Operation(summary = "redis data 집계", description = "검색 키워드, 성별, 나이대, 집계 날짜를 공통으로 묶어 집계")
    public ApiResponse<List<KeywordRecordResponseDto>> getAggregatedData() {
        return ApiResponse.successOf(HttpStatus.OK, keywordService.getKeywordRecord());
    }

}
