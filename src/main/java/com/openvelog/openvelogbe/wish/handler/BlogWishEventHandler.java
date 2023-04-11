package com.openvelog.openvelogbe.wish.handler;

import com.openvelog.openvelogbe.common.dto.ErrorMessage;
import com.openvelog.openvelogbe.common.entity.BlogWishRecord;
import com.openvelog.openvelogbe.common.entity.Board;
import com.openvelog.openvelogbe.common.repository.BlogWishRecordRedisRepository;
import com.openvelog.openvelogbe.common.repository.BoardRepository;
import com.openvelog.openvelogbe.wish.service.BlogWishRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlogWishEventHandler {
    private final BoardRepository boardRepository;

    private final BlogWishRecordService blogWishRecordService;

    @Qualifier("wishCountLock")
    private final RedisLockRegistry redisWishCountLockRegistry;

    private final BlogWishRecordRedisRepository blogWishRecordRedisRepository;

    @Scheduled(fixedDelay = 3000) // Run every 3 seconds
    public void handleWishEventScheduler() {
        // Get all view records from Redis
        List<BlogWishRecord> blogWishRecords = blogWishRecordRedisRepository.findAll();
        if (blogWishRecords != null && blogWishRecords.size() == 0) {
            return;
        }

        log.info("Starts updating " + blogWishRecords.size() + " blogs' wish_count_sum from Scheduler...");

        // Update view count to on-disk database
        for (BlogWishRecord boardViewRecord : blogWishRecords) {
            Long blogId = boardViewRecord.getBlogId();

            Lock lock = redisWishCountLockRegistry.obtain(Long.toString(blogId));
            try {
                boolean acquired = lock.tryLock(10000, TimeUnit.MILLISECONDS);
                if (acquired) {
                    blogWishRecordService.updateBlogWishCounts(blogId);
                } else {
                    log.error("Failed to get the lock at handleWishEventScheduler()");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }

        log.info("Finished updating blogs' wish_count_sum from Scheduler!");
    }

    public void handleBlogWishEvent(Long boardId) {
        Board board = boardRepository.findByIdJPQL(boardId).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.BOARD_NOT_FOUND.getMessage())
        );
        Long blogId = board.getBlog().getId();

        Lock lock = redisWishCountLockRegistry.obtain(blogId);
        try {
            boolean acquired = lock.tryLock(5000, TimeUnit.MILLISECONDS);
            if (acquired == false) {
                blogWishRecordService.recordBlogWishCount(blogId);
            } else {
                log.error("Failed to get the lock at handleBlogWishEvent()");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
}
