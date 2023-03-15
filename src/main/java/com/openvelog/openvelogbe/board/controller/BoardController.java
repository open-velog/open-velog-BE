package com.openvelog.openvelogbe.board.controller;

import com.openvelog.openvelogbe.board.dto.BoardRequestDto;
import com.openvelog.openvelogbe.board.dto.BoardResponseDto;
import com.openvelog.openvelogbe.board.service.BoardService;
import com.openvelog.openvelogbe.common.dto.ApiResponse;
import com.openvelog.openvelogbe.common.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Board")
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @PostMapping
    @Operation(summary = "게시글 작성", description ="해당 블로그Id를 갖는 블로그에 게시글 작성")
    public ApiResponse<BoardResponseDto> createBoard (
             @RequestBody @Valid BoardRequestDto.BoardAdd dto,
             @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ApiResponse.successOf(HttpStatus.CREATED, boardService.createBoard(dto, userDetails));
    }


    @GetMapping("/search")
    @Operation(summary = "게시글 검색", description ="게시글 제목, 내용, 해당 블로그의 제목에 키워드가 포함된 게시글 목록 조회, page는 1부터 시작")
    public ApiResponse<List<BoardResponseDto>> searchBoards(
            @RequestParam String keyword,
            @RequestParam Integer page,
            @RequestParam Integer limit,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ApiResponse.successOf(HttpStatus.OK, boardService.searchBoards(keyword, page, limit, userDetails));
    }

    @GetMapping("/{boardId}")
    @Operation(summary = "게시글 조회", description ="특정 boardId를 갖는 단일 게시글 조회")
    public ApiResponse<BoardResponseDto> getBoard(
            @PathVariable Long boardId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ApiResponse.successOf(HttpStatus.OK, boardService.getBoard(boardId, userDetails));
    }

    @PutMapping("/{boardId}")
    @Operation(summary = "게시글 수정", description ="특정 boardId의 게시글 수정")
    public ApiResponse<BoardResponseDto> updateBoard
            (@PathVariable Long boardId,
                    @RequestBody @Valid BoardRequestDto.BoardUpdate dto,
             @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ApiResponse.successOf(HttpStatus.CREATED, boardService.updateBoard(boardId, dto, userDetails));
    }

    @DeleteMapping("/{boardId}")
    @Operation(summary = "게시글 삭제", description ="특정 boardId의 게시글 삭제")
    public ApiResponse<BoardResponseDto> deleteStudyBoard(
            @PathVariable Long boardId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        boardService.deleteBoard(boardId,userDetails);
        return ApiResponse.successOf(HttpStatus.NO_CONTENT, null);
    }

    @GetMapping("/byBlog")
    @SecurityRequirements()
    @Operation(summary = "블로그에 포함된 게시글 목록 조회", description = "page는 1번부터 시작")
    public ApiResponse<List<BoardResponseDto>> getBoardListByBlog(
            @RequestParam Long blogId,
            @RequestParam Integer page,
            @RequestParam Integer limit
    ) {
        return  ApiResponse.successOf(HttpStatus.OK, boardService.getBoardListByBlog(blogId, page, limit));
    }
}
