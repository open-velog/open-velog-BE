package com.openvelog.openvelogbe.board.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.openvelog.openvelogbe.common.entity.Board;
import com.openvelog.openvelogbe.common.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.HashSet;

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

    @Schema(example = "게시글 좋아요 수")
    private Integer wishCount;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isLike;

    private Long viewCount;

    @Schema(example = "생성 날짜")
    private LocalDateTime createdAt;
    @Schema(example = "수정 날짜")
    private LocalDateTime modifiedAt;

    public static BoardResponseDto of(Board board){
        BoardResponseDtoBuilder builder = BoardResponseDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .createdAt(board.getCreatedAt())
                .modifiedAt(board.getModifiedAt())
                .viewCount(board.getViewCount())
                .wishCount(ObjectUtils.defaultIfNull(board.getWishes(), new HashSet<>()).size());

        return builder.build();
    }

    public static BoardResponseDto of(Board board, Long memberId){
        BoardResponseDtoBuilder builder = BoardResponseDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .createdAt(board.getCreatedAt())
                .modifiedAt(board.getModifiedAt())
                .viewCount(board.getViewCount())
                .wishCount(ObjectUtils.defaultIfNull(board.getWishes(), new HashSet<>()).size());
        if (memberId != null) {
            builder.isLike(board.getBlog().getMember().getId().equals(memberId));
        }

        return builder.build();
    }
}
