package com.openvelog.openvelogbe.openSearch.entity;


import com.openvelog.openvelogbe.common.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.time.LocalDateTime;


@Document(indexName = "board_test")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardTestDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "ngram_analyzer")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ngram_analyzer")
    private String content;

    @Field(type = FieldType.Text)
    private LocalDateTime created_at;

    @Field(type = FieldType.Text)
    private LocalDateTime modified_at;

    @Field(type = FieldType.Long)
    private Long view_count;

    @Field(type = FieldType.Long)
    private Integer wish_count;

    public static BoardTestDocument create (Board board) {
        return builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .view_count(board.getViewCount())
                .wish_count(board.getWishes() != null ? board.getWishes().size() : 0)
                .created_at(board.getCreatedAt())
                .modified_at(board.getModifiedAt())
                .build();
    }

}

