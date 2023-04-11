package com.openvelog.openvelogbe.board.handler;

import com.openvelog.openvelogbe.board.service.BoardViewRecordService;
import com.openvelog.openvelogbe.common.entity.BoardViewRecord;
import com.openvelog.openvelogbe.common.repository.BoardViewRecordRedisRepository;
import com.openvelog.openvelogbe.common.util.LockHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BoardViewEventHandler {
    private final LockHandler lockHandler;

    private final BoardViewRecordService boardViewRecordService;

    private final BoardViewRecordRedisRepository boardViewRecordRedisRepository;

    @Value("${redis.record.view.count.lock.name}")
    private String boardViewCountKey;

    @Scheduled(fixedDelay = 3000) // Run every 3 seconds
    public void handleBoardViewEventScheduler() {
        // Set lock time
        Long waitTime = 10000L;
        Long leaseTime = 21000L;

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

            lockHandler.runOnLock(boardViewCountKey + boardId, waitTime, leaseTime, () -> boardViewRecordService.updateBoardViewCounts(blogId, boardId));
        }

        log.info("Finished updating boards' view_count & blogs' view_count_sum from Scheduler!");
    }

    public void handleBoardViewEvent(Long boardId) {
        // Set lock time
        Long waitTime = 5000L;
        Long leaseTime = 11000L;

        lockHandler.runOnLock(boardViewCountKey + boardId, waitTime, leaseTime, () -> boardViewRecordService.recordBoardViewCount(boardId));
    }
}
