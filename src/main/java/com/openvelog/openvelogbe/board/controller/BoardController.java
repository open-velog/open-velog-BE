package com.openvelog.openvelogbe.board.controller;

import com.openvelog.openvelogbe.board.dto.BoardRequestDto;
import com.openvelog.openvelogbe.board.dto.BoardResponseAndCountDto;
import com.openvelog.openvelogbe.board.dto.BoardResponseDto;
import com.openvelog.openvelogbe.board.service.BoardViewRecordService;
import com.openvelog.openvelogbe.board.service.BoardService;
import com.openvelog.openvelogbe.common.dto.ApiResponse;
import com.openvelog.openvelogbe.common.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

@Tag(name = "Board")
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final BoardViewRecordService boardViewRecordService;

    @PostMapping
    @Operation(summary = "게시글 작성", description ="해당 블로그Id를 갖는 블로그에 게시글 작성")
    public ApiResponse<BoardResponseDto> createBoard (
             @RequestBody @Valid BoardRequestDto.BoardAdd dto,
             @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ApiResponse.successOf(HttpStatus.CREATED, boardService.createBoard(dto, userDetails));
    }


    @GetMapping("/search")
    @Operation(summary = "게시글 검색", description ="게시글 제목, 내용, 해당 블로그의 제목에 키워드가 포함된 게시글 목록 조회, page는 1부터 시작")
    public CompletableFuture<ApiResponse<BoardResponseAndCountDto>> searchBoards(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return CompletableFuture.supplyAsync(() ->
             ApiResponse.successOf(HttpStatus.OK, boardService.searchBoards(keyword, page, size, userDetails))
        );
    }

    @GetMapping("/{boardId}")
    @Operation(summary = "게시글 조회", description ="특정 boardId를 갖는 단일 게시글 조회")
    public ApiResponse<BoardResponseDto> getBoard(
            @PathVariable Long boardId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        // update view count
        CompletableFuture.runAsync(() -> boardViewRecordService.recordBoardViewCount(boardId));

        return ApiResponse.successOf(HttpStatus.OK, boardService.getBoard(boardId, userDetails));
    }

    @PutMapping("/{boardId}")
    @Operation(summary = "게시글 수정", description ="특정 boardId의 게시글 수정")
    public ApiResponse<BoardResponseDto> updateBoard(
            @PathVariable Long boardId,
            @RequestBody @Valid BoardRequestDto.BoardUpdate dto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ApiResponse.successOf(HttpStatus.CREATED, boardService.updateBoard(boardId, dto, userDetails));
    }

    @DeleteMapping("/{boardId}")
    @Operation(summary = "게시글 삭제", description ="특정 boardId의 게시글 삭제")
    public ApiResponse<BoardResponseDto> deleteStudyBoard(
            @PathVariable Long boardId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        boardService.deleteBoard(boardId,userDetails);
        return ApiResponse.successOf(HttpStatus.NO_CONTENT, null);
    }

    @GetMapping("/byBlog")
    @SecurityRequirements()
    @Operation(summary = "블로그에 포함된 게시글 목록 조회", description = "page는 1번부터 시작")
    public ApiResponse<Page<BoardResponseDto>> getBoardListByBlog(
            @RequestParam Long blogId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return  ApiResponse.successOf(HttpStatus.OK, boardService.getBoardListByBlog(blogId, page, size));
    }

    @GetMapping("/byBlog/userId")
    @SecurityRequirements()
    @Operation(summary = "블로그에 포함된 게시글 목록 조회", description = "page는 1번부터 시작")
    public ApiResponse<Page<BoardResponseDto>> getBoardListByBlogUserId(
            @RequestParam String userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return  ApiResponse.successOf(HttpStatus.OK, boardService.getBoardListByBlogUserId(userId, page, size));
    }

    @GetMapping("/byBlog/memberId")
    @SecurityRequirements()
    @Operation(summary = "블로그에 포함된 게시글 목록 조회", description = "page는 1번부터 시작")
    public ApiResponse<Page<BoardResponseDto>> getBoardListByBlogMemberId(
            @RequestParam Long memberId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return  ApiResponse.successOf(HttpStatus.OK, boardService.getBoardListByBlogMemberId(memberId, page, size));
    }
}
