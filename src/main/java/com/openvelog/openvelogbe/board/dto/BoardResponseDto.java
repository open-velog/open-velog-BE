package com.openvelog.openvelogbe.board.dto;

import com.openvelog.openvelogbe.common.entity.Board;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BoardResponseDto {
    @Schema(type = "integer", example = "3")
    private Long id;
    @Schema(example = "게시글 제목")
    private String title;

    @Schema(example = "게시글 내용")
    private String content;

    @Schema(example = "생성 날짜")
    private LocalDateTime createdAt;
    @Schema(example = "수정 날짜")
    private LocalDateTime modifiedAt;

    public static BoardResponseDto of(Board board){
        return BoardResponseDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .createdAt(board.getCreatedAt())
                .modifiedAt(board.getModifiedAt())
                .build();
    }
}
