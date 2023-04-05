package com.openvelog.openvelogbe.common.repository;

import com.openvelog.openvelogbe.common.entity.KeywordRecord;
import com.openvelog.openvelogbe.common.entity.SearchLog;
import com.openvelog.openvelogbe.common.entity.enums.AgeRange;
import com.openvelog.openvelogbe.common.entity.enums.Gender;
import com.openvelog.openvelogbe.keywordRecord.dto.KeywordRecordResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.Tuple;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {
    List<SearchLog> findBySearchDateTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    @Query(value = "SELECT keyword, COUNT(*), gender, age_range " +
            "FROM search_logs " +
            "where gender is not null " +
            "and age_range is not null " +
            "and DATE(search_date_time) = :searchDate " +
            "GROUP BY keyword, gender, age_range", nativeQuery = true)
    List<Object[]> getBySearchedDate(LocalDate searchDate);


}
