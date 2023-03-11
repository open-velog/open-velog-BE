package com.openvelog.openvelogbe.common.repository;

import com.openvelog.openvelogbe.common.entity.AggregatedSearchTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AggregatedSearchTimeRepository extends JpaRepository<AggregatedSearchTime, Long> {
}
