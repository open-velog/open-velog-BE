package com.openvelog.openvelogbe.common.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.stereotype.Service;

import java.math.BigInteger;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardViewRecord {
    @Id
    private Long boardId;

    private Long blogId;

    private Long viewCount;

    public static BoardViewRecord of(Long boardId, Long blogId, Long viewCount) {
        return BoardViewRecord.builder()
                .boardId(boardId)
                .blogId(blogId)
                .viewCount(viewCount)
                .build();
    }

    public void increaseViewCount() {
        this.viewCount += 1L;
    }

    public void decreaseViewCount(Long viewCount) {
        this.viewCount -= viewCount;
    }
}
