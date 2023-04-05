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
    @Value("${redis.record.wish.count.lock.name}")
    private String wishCountLock;

    private final BlogRepository blogRepository;

    private final BoardRepository boardRepository;

    private final BlogWishRecordRedisRepository blogWishRecordRedisRepository;

    @Qualifier("wishCountLock")
    private final RedisLockRegistry redisWishCountLockRegistry;

    @Transactional
    @Scheduled(fixedDelay = 10000) // Run every 10 seconds
    void updateBlogWishCounts() {
        Lock lock = redisWishCountLockRegistry.obtain(wishCountLock);
        try {
            boolean acquired = lock.tryLock(10000, TimeUnit.MILLISECONDS);
            if (acquired) {
                // Get all wish records from Redis
                List<BlogWishRecord> blogWishRecords = blogWishRecordRedisRepository.findAll();
                if (blogWishRecords != null && blogWishRecords.size() == 0) {
                    return;
                }

                log.info("Starts updating " + blogWishRecords.size() + " boards wish count from Scheduler...");

                // Update wish count to on-disk database
                for (BlogWishRecord blogWishRecord : blogWishRecords) {
                    Long blogId = blogWishRecord.getBlogId();

                    // Update the column `wish_count_sum` in table `blogs` where id=blogId
                    // TODO: Use entity graph due to Lazy Initialization Exception.
                    //  But, Need to know why Lazy Initialization Exception happens.
                    //  So to refactor further more
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

                log.info("Finished updating board wish count from Scheduler!");
            } else {
                log.error("Failed to get the lock at updateBoardWishCounts()");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public void recordBlogWishCount(Long boardId) {
        Lock lock = redisWishCountLockRegistry.obtain(wishCountLock);
        try {
            boolean acquired = lock.tryLock(5000, TimeUnit.MILLISECONDS);
            if (acquired) {
                Board board = boardRepository.findByIdJPQL(boardId).orElseThrow(
                        () -> new EntityNotFoundException(ErrorMessage.BOARD_NOT_FOUND.getMessage())
                );

                Optional<BlogWishRecord> boardWishRecord = blogWishRecordRedisRepository.findById(board.getId());
                if (boardWishRecord.isEmpty()) {
                    blogWishRecordRedisRepository.save(BlogWishRecord.create(board.getBlog().getId()));
                }
            } else {
                log.error("Failed to get the lock at recordBoardWishCount()");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
}
