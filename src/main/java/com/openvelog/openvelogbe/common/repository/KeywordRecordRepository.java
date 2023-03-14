package com.openvelog.openvelogbe.common.repository;

import com.openvelog.openvelogbe.common.entity.KeywordRecord;
import com.openvelog.openvelogbe.common.entity.enums.AgeRange;
import com.openvelog.openvelogbe.common.entity.enums.Gender;
import com.openvelog.openvelogbe.rank.RankResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.Tuple;
import java.time.LocalDate;
import java.util.List;

public interface KeywordRecordRepository extends JpaRepository<KeywordRecord, Long> {

    @Query(value = "select k.keyword as keyword, sum(k.count) as kCount from keyword_records k " +
            "where (:ageRange is null or k.ageRange = :ageRange) " +
            "and (:gender is null or k.gender = :gender) " +
            "and k.searchedDate = :date " +
            "group by k.keyword ")
    List<Tuple> getRankOfKeywordJPQL(AgeRange ageRange, Gender gender, LocalDate date, Pageable pageable);
}
