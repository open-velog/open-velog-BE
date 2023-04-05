package com.openvelog.openvelogbe.keyword.controller;

import com.openvelog.openvelogbe.common.dto.ApiResponse;
import com.openvelog.openvelogbe.keyword.service.KeywordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Keyword")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/keywords")
public class KeywordController {
    private final KeywordService keywordService;

    @GetMapping()
    @SecurityRequirements()
    @Operation(summary = "검색기록 확인", description ="게시글 검색 시 redis에 저장된 데이터 확인")
    public ApiResponse getRedisData() {
        return ApiResponse.successOf(HttpStatus.OK,keywordService.getKeywords());
    }

    @GetMapping("/keyword")
    @SecurityRequirements()
    @Operation(summary = "키워드로 검색기록 확인", description ="게시글 검색 시 redis에 저장된 데이터를 keyword로 상세조회")
    public ApiResponse getRedisDataByKeyword(@RequestParam String keyword) {
        return ApiResponse.successOf(HttpStatus.OK,keywordService.getKeywordsByKeyword(keyword));
    }

    @GetMapping("/ranking")
    @SecurityRequirements()
    @Operation(summary = "24시간 내의 키워드 검색 순위", description ="24시간 내의 키워드 검색 순위")
    public ApiResponse keywordRanking() {
        return ApiResponse.successOf(HttpStatus.OK,keywordService.keywordRanking2());
    }
}
