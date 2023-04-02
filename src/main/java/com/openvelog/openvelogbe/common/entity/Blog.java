package com.openvelog.openvelogbe.common.entity;


import com.openvelog.openvelogbe.blog.dto.BlogRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity(name = "blogs")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Blog extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "text")
    private String introduce;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long viewCountSum;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long wishCountSum;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "blog")
    private Set<Board> boards = new LinkedHashSet<>();


    public static Blog create(BlogRequestDto.BlogAdd dto, Member member) {
        return builder()
                .introduce(dto.getIntroduce())
                .member(member)
                .build();
    }

    public static Blog create(Member member) {
        return builder()
                .member(member)
                .build();
    }

    public void update(BlogRequestDto.BlogUpdate dto) {
        this.introduce = dto.getIntroduce();
    }

    public void updateViewCountSum(Long viewCountSum){
        this.viewCountSum=viewCountSum;
    }

    public void updateWishCountSum(Long wishCountSum){
        this.wishCountSum=wishCountSum;
    }

    @PreRemove
    private void blogIdSetNullAtBoard() {
        this.getBoards().forEach(Board::setBlogNull);
    }
}
