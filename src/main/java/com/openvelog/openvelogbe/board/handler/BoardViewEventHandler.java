package com.openvelog.openvelogbe.board.handler;

import com.openvelog.openvelogbe.board.service.BoardViewRecordService;
import com.openvelog.openvelogbe.common.entity.BoardViewRecord;
import com.openvelog.openvelogbe.common.repository.BoardViewRecordRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@Component
@RequiredArgsConstructor
public class BoardViewEventHandler {
    private final BoardViewRecordService boardViewRecordService;

    @Qualifier("viewCountLock")
    private final RedisLockRegistry redisViewCountLockRegistry;

    private final BoardViewRecordRedisRepository boardViewRecordRedisRepository;

    @Scheduled(fixedDelay = 3000) // Run every 3 seconds
    public void handleBoardViewEventScheduler() {
        // Get all view records from Redis
        List<BoardViewRecord> boardViewRecords = boardViewRecordRedisRepository.findAll();
        if (boardViewRecords != null && boardViewRecords.size() == 0) {
            return;
        }

        log.info("Starts updating " + boardViewRecords.size() + " boards' view_count & blogs' view_count_sum from Scheduler...");

        // Update view count to on-disk database
        for (BoardViewRecord boardViewRecord : boardViewRecords) {
            Long boardId = boardViewRecord.getBoardId();
            Long blogId = boardViewRecord.getBlogId();

            Lock lock = redisViewCountLockRegistry.obtain(Long.toString(boardId));
            try {
                boolean acquired = lock.tryLock(10000, TimeUnit.MILLISECONDS);
                if (acquired) {
                    boardViewRecordService.updateBoardViewCounts(blogId, boardId);
                } else {
                    log.error("Failed to get the lock at handleBoardViewEventScheduler()");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }

        log.info("Finished updating boards' view_count & blogs' view_count_sum from Scheduler!");
    }

    public void handleBoardViewEvent(Long boardId) {
        Lock lock = redisViewCountLockRegistry.obtain(Long.toString(boardId));
        try {
            boolean acquired = lock.tryLock(5000, TimeUnit.MILLISECONDS);
            if (acquired) {
                boardViewRecordService.recordBoardViewCount(boardId);
            } else {
                log.error("Failed to get the lock at handleBoardViewEvent()");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
}
