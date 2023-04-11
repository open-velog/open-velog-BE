package com.openvelog.openvelogbe.wish.controller;

import com.openvelog.openvelogbe.common.dto.ApiResponse;
import com.openvelog.openvelogbe.common.security.UserDetailsImpl;
import com.openvelog.openvelogbe.wish.handler.BlogWishEventHandler;
import com.openvelog.openvelogbe.wish.service.BlogWishRecordService;
import com.openvelog.openvelogbe.wish.service.BoardWishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@Tag(name = "BoardWish")
@RestController
@RequestMapping("/api/boards/wishes")
@RequiredArgsConstructor
public class BoardWishController {

    private final BlogWishEventHandler blogWishEventHandler;

    private final BoardWishService boardWishService;

    @PostMapping
    @Operation(summary = "게시글 종아요 등록 삭제", description ="게시글 종아요 등록 삭제 처리")
    public ApiResponse<Boolean> setBoardWish(
            @RequestParam Long boardId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        CompletableFuture.runAsync(() ->
            blogWishEventHandler.handleBlogWishEvent(boardId)
        );

        return ApiResponse.successOf(HttpStatus.OK, boardWishService.setBoardWish(boardId, userDetails));
    }

}

