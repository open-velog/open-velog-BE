package com.openvelog.openvelogbe.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("BlogWishRecord")
public class BlogWishRecord {
    @Id
    private Long blogId;

    public static BlogWishRecord create(Long blogId) {
        return BlogWishRecord.builder()
                .blogId(blogId)
                .build();
    }
}
