package com.openvelog.openvelogbe.crawling.repository;

import com.openvelog.openvelogbe.crawling.entity.CrawledBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CrawledBoardRepository extends JpaRepository<CrawledBoard, Long> {
    @Query(value = "SELECT * FROM board LIMIT :limit", nativeQuery = true)
    List<CrawledBoard> findAllWithLimit(@Param("limit") int limit);
}
