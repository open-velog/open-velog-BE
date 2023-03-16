package com.openvelog.openvelogbe.crawling.repository;

import com.openvelog.openvelogbe.crawling.entity.CrawledBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CrawledBoardRepository extends JpaRepository<CrawledBoard, Long> {
    Page<CrawledBoard> findAll(Pageable pageable);
}
