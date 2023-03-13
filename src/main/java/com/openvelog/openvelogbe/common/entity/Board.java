package com.openvelog.openvelogbe.common.entity;


import com.openvelog.openvelogbe.board.dto.BoardRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity(name = "boards")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Board extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    private String imageURL;

    @ManyToOne(fetch = FetchType.LAZY)
    private Blog blog;

    public void setBlogNull() {
        this.blog = null;
    }

    public void update(BoardRequestDto.BoardUpdate dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
    }

    public static Board create(BoardRequestDto.BoardAdd dto, Blog blog) {
        return Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .blog(blog)
                .build();
    }


}
