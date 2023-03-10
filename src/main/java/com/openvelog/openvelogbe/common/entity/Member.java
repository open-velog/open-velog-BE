package com.openvelog.openvelogbe.common.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity(name = "members")
@Getter
@NoArgsConstructor
public class Member extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String email;

    private String profileImageURL;

    @OneToMany(mappedBy = "member")
    private Set<Board> boards = new LinkedHashSet<>();;

    @OneToMany(mappedBy = "participant1")
    private Set<ChatRoom> chatRoom1s = new LinkedHashSet<>();

    @OneToMany(mappedBy = "participant2")
    private Set<ChatRoom> chatRoom2s = new LinkedHashSet<>();;
}
