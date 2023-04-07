package com.openvelog.openvelogbe.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.time.LocalDateTime;


@Document(indexName = "board")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDocument extends Timestamped {

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

    public static BoardDocument create (Board board) {
        return builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .view_count(board.getViewCount())
                .wish_count(board.getWishes().size())
                .created_at(board.getCreatedAt())
                .modified_at(board.getModifiedAt())
                .build();
    }

}
