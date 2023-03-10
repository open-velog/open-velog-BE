package com.openvelog.openvelogbe.common.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity(name = "chat_rooms")
@Getter
@NoArgsConstructor
public class ChatRoom extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany
    private Set<ChatMessage> chatMessages = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Member participant1;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member participant2;

    public void setParticipant1Null() {
        participant1 = null;
    }

    public void setParticipant2Null() { participant2 = null; }
}
