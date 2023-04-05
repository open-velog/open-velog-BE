package com.openvelog.openvelogbe.board.service;

import com.openvelog.openvelogbe.common.dto.ErrorMessage;
import com.openvelog.openvelogbe.common.entity.Blog;
import com.openvelog.openvelogbe.common.entity.Board;
import com.openvelog.openvelogbe.common.entity.BoardViewRecord;
import com.openvelog.openvelogbe.common.repository.BlogRepository;
import com.openvelog.openvelogbe.common.repository.BoardRepository;
import com.openvelog.openvelogbe.common.repository.BoardViewRecordRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardViewRecordService {
    private final String VIEW_COUNT_LOCK = "view-count-lock";

    private final BoardRepository boardRepository;

    private final BlogRepository blogRepository;

    private final BoardViewRecordRedisRepository boardViewRecordRedisRepository;

    @Qualifier("viewCountLock")
    private final RedisLockRegistry redisViewCountLockRegistry;

    @Transactional
    @Scheduled(fixedDelay = 10000) // Run every 10 seconds
    void updateBoardViewCounts() {
        Lock lock = redisViewCountLockRegistry.obtain(VIEW_COUNT_LOCK);
        try {
            boolean acquired = lock.tryLock(10000, TimeUnit.MILLISECONDS);
            if (acquired) {
                // Get all view records from Redis
                List<BoardViewRecord> boardViewRecords = boardViewRecordRedisRepository.findAll();
                if (boardViewRecords != null && boardViewRecords.size() == 0) {
                    return;
                }

                log.info("Starts updating " + boardViewRecords.size() + " boards view count from Scheduler...");

                // Update view count to on-disk database
                for (BoardViewRecord boardViewRecord : boardViewRecords) {
                    Long viewCount = boardViewRecord.getViewCount();
                    Long boardId = boardViewRecord.getBoardId();
                    Long blogId = boardViewRecord.getBlogId();

                    Board board = boardRepository.findById(boardId).orElseThrow(
                            () -> new EntityNotFoundException(ErrorMessage.BOARD_NOT_FOUND.getMessage())
                    );
                    Blog blog = blogRepository.findById(blogId).orElseThrow(
                            () -> new EntityNotFoundException(ErrorMessage.BLOG_NOT_FOUND.getMessage())
                    );


                    // Update view_count_sum field of boards table
                    board.updateViewCount(viewCount);
                    boardRepository.save(board);

                    // Update view_count field of blogs table
                    blog.updateViewCountSum(viewCount);
                    blogRepository.save(blog);

                    // Remove the key pair in Redis
                    boardViewRecordRedisRepository.deleteById(boardViewRecord.getBoardId());
                }
                log.info("Finished updating board view count from Scheduler!");
            } else {
                log.error("Failed to get the lock at updateBoardViewCounts()");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public void recordBoardViewCount(Long boardId) {
        Lock lock = redisViewCountLockRegistry.obtain(VIEW_COUNT_LOCK);
        try {
            boolean acquired = lock.tryLock(5000, TimeUnit.MILLISECONDS);
            if (acquired) {
                Board board = boardRepository.findByIdJPQL(boardId).orElseThrow(
                        () -> new EntityNotFoundException(ErrorMessage.BOARD_NOT_FOUND.getMessage())
                );

                BoardViewRecord boardViewRecord = boardViewRecordRedisRepository.findById(board.getId()).orElse(null);
                if (boardViewRecord == null) {
                    boardViewRecord = BoardViewRecord.of(board.getId(), board.getBlog().getId(), 1L);
                } else {
                    boardViewRecord.increaseViewCount();
                }
                boardViewRecordRedisRepository.save(boardViewRecord);
            } else {
                log.error("Failed to get the lock at recordBoardViewCount()");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
}
