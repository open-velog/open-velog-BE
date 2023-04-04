package com.openvelog.openvelogbe.common.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.math.BigInteger;


@Getter
@Builder
@RedisHash("BoardViewRecord")
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

    public void addViewCount() {
        this.viewCount += 1L;
    }
}
