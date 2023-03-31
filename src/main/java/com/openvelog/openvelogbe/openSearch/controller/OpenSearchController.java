package com.openvelog.openvelogbe.openSearch.controller;

import com.openvelog.openvelogbe.openSearch.dto.BoardDocumentDto;
import com.openvelog.openvelogbe.openSearch.dto.BoardDocumentResponseAndCountDto;
import com.openvelog.openvelogbe.openSearch.service.OpenSearchService;
import com.openvelog.openvelogbe.common.dto.ApiResponse;
import com.openvelog.openvelogbe.common.dto.ErrorResponseDto;
import com.openvelog.openvelogbe.common.dto.ErrorType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag(name = "OpenSearch")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/opensearch")
public class OpenSearchController {
    private final OpenSearchService openSearchService;
    private static final Logger logger = LoggerFactory.getLogger(OpenSearchController.class);

    @PostMapping("/index")
    @SecurityRequirements()
    @Operation(summary = "MySQL to OpenSearch", description = "BoardDocument의 클래스 내부 변수들을 필드로 선언")
    public ApiResponse<?> indexAllBoardsToOpensearch() {
        try {
            openSearchService.indexAllBoards();
            return ApiResponse.successOf(HttpStatus.CREATED,"색인이 완료되었습니다.");
        } catch (IOException e) {
            logger.error("데이터 색인 중 예외 발생", e);
            return ApiResponse.failOf(HttpStatus.INTERNAL_SERVER_ERROR, ErrorResponseDto.of(ErrorType.EXCEPTION,"색인 중 오류가 발생했습니다."));
        }
    }

    @GetMapping("/search")
    @SecurityRequirements()
    @Operation(summary = "OpenSearch를 이용해 게시글 검색", description = "query_string을 이용해 title과 content 2개의 필드 대상으로 검색")
    public ApiResponse<?> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            BoardDocumentResponseAndCountDto result = openSearchService.search(keyword,page,size);
            return ApiResponse.successOf(HttpStatus.OK,result);
        } catch (IOException e) {
            logger.error("OpenSearch 검색 중 예외 발생", e);
            return ApiResponse.failOf(HttpStatus.INTERNAL_SERVER_ERROR, ErrorResponseDto.of(ErrorType.EXCEPTION,"검색 중 오류가 발생했습니다."));
        }
    }
}
