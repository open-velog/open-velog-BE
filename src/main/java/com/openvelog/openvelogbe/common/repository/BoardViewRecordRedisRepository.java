package com.openvelog.openvelogbe.common.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openvelog.openvelogbe.common.entity.BoardViewRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class BoardViewRecordRedisRepository {
    public static final String BOARD_VIEW_RECORD_KEY_PREFIX = "BoardViewRecord:";

    private final RedisTemplate<String, String> redisTemplate;

    public List<BoardViewRecord> findAll() {
        List<BoardViewRecord> boardViewRecords = new ArrayList<>();
        Set<String> keys = redisTemplate.keys(BOARD_VIEW_RECORD_KEY_PREFIX + "*");
        if (keys != null) {
            List<String> values = redisTemplate.opsForValue().multiGet(keys);
            for (String value : values) {
                if (value == null) {
                    continue;
                }

                BoardViewRecord boardViewRecord = null;
                try {
                    boardViewRecord = new ObjectMapper().readValue(value, BoardViewRecord.class);
                } catch (JsonProcessingException e) {
                    log.error("Error occurs in BoardViewRecordRedisRepository's findAll() method");
                }
                boardViewRecords.add(boardViewRecord);
            }
        }
        return boardViewRecords;
    }

    public Optional<BoardViewRecord> findByBoardId(Long boardId) {
        String value = redisTemplate.opsForValue().get(BOARD_VIEW_RECORD_KEY_PREFIX + boardId);
        if (value != null) {
            BoardViewRecord boardViewRecord = null;
            try {
                boardViewRecord = new ObjectMapper().readValue(value, BoardViewRecord.class);
            } catch (JsonProcessingException e) {
                log.error("Error occurs in BoardViewRecordRedisRepository's findByBoardId() method");
            }
            return Optional.ofNullable(boardViewRecord);
        } else {
            return Optional.empty();
        }
    }

    public void save(BoardViewRecord boardViewRecord) {
        String key = BOARD_VIEW_RECORD_KEY_PREFIX + boardViewRecord.getBoardId();
        try {
            String value = new ObjectMapper().writeValueAsString(boardViewRecord);
            redisTemplate.opsForValue().set(key, value);
        } catch (JsonProcessingException e) {
            log.error("Error occurs in BoardViewRecordRedisRepository's save() method");
        }
    }

    public void deleteById(Long boardId) {
        String key = BOARD_VIEW_RECORD_KEY_PREFIX + boardId;
        redisTemplate.delete(key);
    }
}
