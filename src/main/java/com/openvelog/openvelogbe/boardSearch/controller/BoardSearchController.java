package com.openvelog.openvelogbe.boardSearch.controller;

import com.openvelog.openvelogbe.boardSearch.service.BoardSearchService;
import com.openvelog.openvelogbe.common.dto.ApiResponse;
import com.openvelog.openvelogbe.common.dto.ErrorResponseDto;
import com.openvelog.openvelogbe.common.dto.ErrorType;
import com.openvelog.openvelogbe.common.util.KeywordRecordScheduler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag(name = "BoardMigration")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board-search")
public class BoardSearchController {
    private final BoardSearchService boardSearchService;
    private static final Logger logger = LoggerFactory.getLogger(BoardSearchController.class);

    @PostMapping("/index")
    @SecurityRequirements()
    @Operation(summary = "BoardData를 opensearch로 이동", description = "id,title,content,viewCount를 필드로 선언")
    public ApiResponse<?> indexAllBoardsToOpensearch() {
        try {
            boardSearchService.indexAllBoards();
            return ApiResponse.successOf(HttpStatus.CREATED,"색인이 완료되었습니다.");
        } catch (IOException e) {
            logger.error("예외 발생", e);
            return ApiResponse.failOf(HttpStatus.INTERNAL_SERVER_ERROR, ErrorResponseDto.of(ErrorType.EXCEPTION,"색인 중 오류가 발생했습니다."));
        }
    }
}
