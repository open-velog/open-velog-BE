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

    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String content;

    @Field(type = FieldType.Text, analyzer = "standard")
    private LocalDateTime created_at;

    @Field(type = FieldType.Text, analyzer = "standard")
    private LocalDateTime modified_at;

    @Field(type = FieldType.Long, analyzer = "standard")
    private Long view_count;

    public static BoardDocument create (Board board) {
        return builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .view_count(board.getViewCount())
                .created_at(board.getCreatedAt())
                .modified_at(board.getModifiedAt())
                .build();
    }

}
