package com.openvelog.openvelogbe.wish.handler;

import com.openvelog.openvelogbe.common.dto.ErrorMessage;
import com.openvelog.openvelogbe.common.entity.BlogWishRecord;
import com.openvelog.openvelogbe.common.entity.Board;
import com.openvelog.openvelogbe.common.repository.BlogWishRecordRedisRepository;
import com.openvelog.openvelogbe.common.repository.BoardRepository;
import com.openvelog.openvelogbe.common.util.LockHandler;
import com.openvelog.openvelogbe.wish.service.BlogWishRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlogWishEventHandler {
    private final LockHandler lockHandler;

    private final BoardRepository boardRepository;

    private final BlogWishRecordService blogWishRecordService;

    @Value("${redis.record.wish.count.lock.name}")
    private String blogWishCountKey;

    private final BlogWishRecordRedisRepository blogWishRecordRedisRepository;

    @Scheduled(fixedDelay = 3000) // Run every 3 seconds
    public void handleWishEventScheduler() {
        // Set lock time
        Long waitTime = 10000L;
        Long leaseTime = 21000L;

        // Get all view records from Redis
        List<BlogWishRecord> blogWishRecords = blogWishRecordRedisRepository.findAll();
        if (blogWishRecords != null && blogWishRecords.size() == 0) {
            return;
        }

        log.info("Starts updating " + blogWishRecords.size() + " blogs' wish_count_sum from Scheduler...");

        // Update view count to on-disk database
        for (BlogWishRecord boardViewRecord : blogWishRecords) {
            Long blogId = boardViewRecord.getBlogId();

            lockHandler.runOnLock(blogWishCountKey + blogId, waitTime, leaseTime, () -> blogWishRecordService.updateBlogWishCounts(blogId));
        }

        log.info("Finished updating blogs' wish_count_sum from Scheduler!");
    }

    public void handleBlogWishEvent(Long boardId) {
        // Set lock time
        Long waitTime = 5000L;
        Long leaseTime = 11000L;

        Board board = boardRepository.findByIdJPQL(boardId).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.BOARD_NOT_FOUND.getMessage())
        );
        Long blogId = board.getBlog().getId();

        lockHandler.runOnLock(blogWishCountKey + blogId, waitTime, leaseTime, () -> blogWishRecordService.recordBlogWishCount(blogId));
    }
}
