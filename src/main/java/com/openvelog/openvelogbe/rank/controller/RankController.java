package com.openvelog.openvelogbe.rank.controller;


import com.openvelog.openvelogbe.common.dto.ApiResponse;
import com.openvelog.openvelogbe.common.entity.enums.AgeRange;
import com.openvelog.openvelogbe.common.entity.enums.Gender;
import com.openvelog.openvelogbe.rank.dto.RankResponseDto;
import com.openvelog.openvelogbe.rank.service.RankService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Rank")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rank")
public class RankController {

    private final RankService rankService;
    @GetMapping("/keyword")
    @SecurityRequirements()
    public ApiResponse<List<RankResponseDto.RankKeyword>> getRanks(
            @RequestParam(required = false) @Valid AgeRange ageRange,
            @RequestParam(required = false) @Valid Gender gender,
            @RequestParam(required = false, defaultValue = "2023-03-14") String date,
            @RequestParam @Valid @NotNull Integer limit) {

        return ApiResponse.successOf(HttpStatus.OK, rankService.getKeywordRank(ageRange, gender, LocalDate.parse(date), limit));
    }
}
