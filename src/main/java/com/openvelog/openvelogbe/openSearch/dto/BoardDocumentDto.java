package com.openvelog.openvelogbe.openSearch.dto;

import com.openvelog.openvelogbe.common.entity.BoardDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BoardDocumentDto {
    @Schema(type = "integer", example = "게시글 아이디")
    private Long id;
    @Schema(example = "게시글 제목")
    private String title;
    @Schema(example = "게시글 내용")
    private String content;
    @Schema(example = "생성 날짜")
    private LocalDateTime createdAt;
    @Schema(example = "수정 날짜")
    private LocalDateTime modifiedAt;
    @Schema(example = "게시글 조회 수")
    private Long viewCount;
    @Schema(example = "게시글 좋아요 수")
    private Integer wishCount;



    public static BoardDocumentDto of(BoardDocument boardDocument) {
        BoardDocumentDtoBuilder builder = BoardDocumentDto.builder()
                .id(boardDocument.getId())
                .title(boardDocument.getTitle())
                .content(boardDocument.getContent())
                .createdAt(boardDocument.getCreated_at())
                .modifiedAt(boardDocument.getModified_at())
                .viewCount(boardDocument.getView_count())
                .wishCount(boardDocument.getWish_count());

        return builder.build();
    }


}
