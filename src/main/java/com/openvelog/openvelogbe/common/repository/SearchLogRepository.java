package com.openvelog.openvelogbe.common.repository;

import com.openvelog.openvelogbe.common.entity.KeywordRecord;
import com.openvelog.openvelogbe.common.entity.SearchLog;
import com.openvelog.openvelogbe.common.entity.enums.AgeRange;
import com.openvelog.openvelogbe.common.entity.enums.Gender;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.Tuple;
import java.time.LocalDate;
import java.util.List;

public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {
}
