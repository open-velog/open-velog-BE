package com.openvelog.openvelogbe.common.repository;

import com.openvelog.openvelogbe.common.entity.BoardViewRecord;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface BoardViewRecordRedisRepository extends CrudRepository<BoardViewRecord, Long> {
    List<BoardViewRecord> findAll();

    Optional<BoardViewRecord> findByBoardId(Long blogId);
}
