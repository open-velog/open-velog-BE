package com.openvelog.openvelogbe.redis.controller;

import com.openvelog.openvelogbe.common.dto.ApiResponse;
import com.openvelog.openvelogbe.redis.service.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Ranking")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/redis")
public class RedisRankingController {
    private final RankingService rankingService;

    @GetMapping("/ranking")
    @SecurityRequirements()
    @Operation(summary = "검색순위", description ="검색 순위 별로 블로그 제목 나열")
    public ApiResponse getRankingList() {
        return ApiResponse.successOf(HttpStatus.OK,rankingService.getRankingList());
    }
}
