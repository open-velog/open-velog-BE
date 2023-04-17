package com.openvelog.openvelogbe.openSearch.controller;

import com.openvelog.openvelogbe.board.dto.BoardResponseAndCountDto;
import com.openvelog.openvelogbe.common.security.UserDetailsImpl;
import com.openvelog.openvelogbe.openSearch.dto.BoardDocumentDto;
import com.openvelog.openvelogbe.openSearch.dto.BoardDocumentResponseAndCountDto;
import com.openvelog.openvelogbe.openSearch.service.OpenSearchDifference;
import com.openvelog.openvelogbe.openSearch.service.OpenSearchService;
import com.openvelog.openvelogbe.common.dto.ApiResponse;
import com.openvelog.openvelogbe.common.dto.ErrorResponseDto;
import com.openvelog.openvelogbe.common.dto.ErrorType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag(name = "OpenSearch")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/opensearch")
public class OpenSearchController {
    private final OpenSearchService openSearchService;
    private final OpenSearchDifference openSearchDifference;
    private static final Logger logger = LoggerFactory.getLogger(OpenSearchController.class);

    @PostMapping("/index")
    @SecurityRequirements()
    @Operation(summary = "테스트용 api = MySql to OpenSearch", description = "입력한 숫자번째 데이터부터 인덱싱 됩니다")
    public ApiResponse<?> indexAllBoardsToOpensearch(@RequestParam int firstDataIndex) {
        try {
            openSearchService.indexAllBoards(firstDataIndex);
            return ApiResponse.successOf(HttpStatus.CREATED,"색인이 완료되었습니다.");
        } catch (IOException e) {
            logger.error("데이터 색인 중 예외 발생", e);
            return ApiResponse.failOf(HttpStatus.INTERNAL_SERVER_ERROR, ErrorResponseDto.of(ErrorType.EXCEPTION,"색인 중 오류가 발생했습니다."));
        }
    }

    @SneakyThrows
    @GetMapping("/search")
    @Operation(summary = "OpenSearch를 이용해 게시글 검색", description = "match_phrase를 이용해 title과 content 2개의 필드 대상으로 검색")
    public CompletableFuture<ApiResponse<?>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
            return CompletableFuture.supplyAsync(() ->
                ApiResponse.successOf(HttpStatus.OK, openSearchService.search(keyword, page, size, userDetails))
            );
    }

    @GetMapping("/compare")
    @SecurityRequirements()
    @Operation(summary = "테스트 용 api = OpenSearch와 MySql 검색결과 비교", description = "검색 방식 비교를 위한 api")
    public void compareSearchResults(@RequestParam String keyword) {
        openSearchDifference.compareSearchResults(keyword);
    }
}
