package com.openvelog.openvelogbe.wish.service;

import com.openvelog.openvelogbe.common.dto.ErrorMessage;
import com.openvelog.openvelogbe.common.entity.Blog;
import com.openvelog.openvelogbe.common.entity.Board;
import com.openvelog.openvelogbe.common.entity.BlogWishRecord;
import com.openvelog.openvelogbe.common.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlogWishRecordService {
    private final BlogRepository blogRepository;

    private final BlogWishRecordRedisRepository blogWishRecordRedisRepository;

    @Transactional
    public void updateBlogWishCounts(Long blogId) {
        // Update the column `wish_count_sum` in table `blogs` where id=blogId
        Blog blog = blogRepository.findByIdJPQL(blogId).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.BLOG_NOT_FOUND.getMessage())
        );

        Long wishCountSum = 0L;
        for (Board board : blog.getBoards()) {
            wishCountSum += board.getWishes().size();
        }
        blog.updateWishCountSum(wishCountSum);

        // Remove the key pair in Redis
        blogWishRecordRedisRepository.deleteById(blogId);
    }

    @Transactional
    public void recordBlogWishCount(Long blogId) {
        Optional<BlogWishRecord> boardWishRecord = blogWishRecordRedisRepository.findById(blogId);
        if (boardWishRecord.isEmpty()) {
            blogWishRecordRedisRepository.save(BlogWishRecord.create(blogId));
        }
    }
}
