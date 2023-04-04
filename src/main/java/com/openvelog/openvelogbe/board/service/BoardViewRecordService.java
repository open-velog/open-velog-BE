package com.openvelog.openvelogbe.board.service;

import com.openvelog.openvelogbe.common.dto.ErrorMessage;
import com.openvelog.openvelogbe.common.entity.Blog;
import com.openvelog.openvelogbe.common.entity.Board;
import com.openvelog.openvelogbe.common.entity.BoardViewRecord;
import com.openvelog.openvelogbe.common.repository.BlogRepository;
import com.openvelog.openvelogbe.common.repository.BoardRepository;
import com.openvelog.openvelogbe.common.repository.BoardViewRecordRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardViewRecordService {
    private final BoardRepository boardRepository;
    private final BlogRepository blogRepository;
    private final BoardViewRecordRedisRepository boardViewRecordRedisRepository;

    @Transactional
    @Scheduled(fixedDelay = 10000) // Run every 60 seconds, TODO: 10초로된 거 60초로 바꿔
    void updateBoardViewCounts() {
        // Get all view records from Redis
        List<BoardViewRecord> boardViewRecords = boardViewRecordRedisRepository.findAll();

        // update view count to on-disk database
        for (BoardViewRecord boardViewRecord : boardViewRecords) {
            Long viewCount = boardViewRecord.getViewCount();
            Long boardId = boardViewRecord.getBoardId();
            Long blogId = boardViewRecord.getBlogId();

            Board board = boardRepository.findById(boardId).orElseThrow(
                    () -> new EntityNotFoundException(ErrorMessage.BOARD_NOT_FOUND.getMessage())
            );
            Blog blog = blogRepository.findById(blogId).orElseThrow(
                    () -> new EntityNotFoundException(ErrorMessage.BOARD_NOT_FOUND.getMessage())
            );

            // update view_count_sum field of boards table
            board.updateViewCount(viewCount);

            // update view_count field of blogs table
            blog.updateViewCountSum(viewCount);
        }

        // Remove the view count from Redis
        boardViewRecordRedisRepository.deleteAll(boardViewRecords);
    }

    @Transactional
    public void recordBoardViewCount(Long boardId) {
        Board board = boardRepository.findByIdJPQL(boardId).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.BOARD_NOT_FOUND.getMessage())
        );

        BoardViewRecord boardViewRecord = boardViewRecordRedisRepository.findByBoardId(board.getId()).orElse(null);
        if (boardViewRecord == null) {
            boardViewRecord = BoardViewRecord.of(board.getId(), board.getBlog().getId(), 1L);
            boardViewRecordRedisRepository.save(boardViewRecord);
        } else {
            boardViewRecord.addViewCount();
        }
    }
}
