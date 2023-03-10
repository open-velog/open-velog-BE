package com.openvelog.openvelogbe.common.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "ranks")
@Getter
@NoArgsConstructor
public class Rank extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Integer rank;
    @Column(nullable = false,length = 20)
    private String keyword;
    @Column(nullable = false)
    private Integer count;
    @Column(nullable = false)
    private LocalDateTime calculatedAt;

}
