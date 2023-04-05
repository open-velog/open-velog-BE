package com.openvelog.openvelogbe.common.repository;

import com.openvelog.openvelogbe.common.entity.BlogWishRecord;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@RedisHash("BlogWishRecord")
public interface BlogWishRecordRedisRepository extends CrudRepository<BlogWishRecord, Long> {
    List<BlogWishRecord> findAll();
}
