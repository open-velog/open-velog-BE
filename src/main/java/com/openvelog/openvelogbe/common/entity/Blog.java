package com.openvelog.openvelogbe.common.entity;


import com.openvelog.openvelogbe.blog.dto.BlogRequestDto;
import com.openvelog.openvelogbe.member.dto.SignupRequestDto;
import lombok.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @OneToMany(mappedBy = "blog")
    private Set<Board> boards = new LinkedHashSet<>();


    public static Blog create(BlogRequestDto.BlogAdd dto, Member member) {
        return builder()
                .introduce(dto.getIntroduce())
                .member(member)
                .build();
    }

    public void update(BlogRequestDto.BlogUpdate dto) {
        this.introduce = dto.getIntroduce();
    }

    @PreRemove
    private void blogIdSetNullAtBoard() {
        this.getBoards().forEach(Board::setBlogNull);
    }
}
