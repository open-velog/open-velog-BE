package com.openvelog.openvelogbe.common.entity;


import com.openvelog.openvelogbe.board.dto.BoardRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Set;

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

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long viewCount;

    @ManyToOne(fetch = FetchType.LAZY)
    private Blog blog;


    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<BoardWishMember> wishes;

    public void setBlogNull() {
        this.blog = null;
    }

    public void update(BoardRequestDto.BoardUpdate dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
    }

    public void addViewCount() {
        this.viewCount += 1;
    }

    public static Board create(BoardRequestDto.BoardAdd dto, Blog blog) {
        return Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .blog(blog)
                .build();
    }


}
