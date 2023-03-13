package com.openvelog.openvelogbe.common.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity(name = "board_wish_members")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardWishMember extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public static BoardWishMember create(Board board, Member member) {
        return builder()
                .member(member)
                .board(board)
                .build();
    }

    public void setMemberNull() {
        this.member = null;
    }
}
