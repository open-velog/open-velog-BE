package com.openvelog.openvelogbe.common.repository;

import com.openvelog.openvelogbe.common.entity.MongoSearchLog;
import com.openvelog.openvelogbe.keyword.dto.KeywordRankDto;
import com.openvelog.openvelogbe.searchLog.dto.SearchKeywordSumDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class MongoRepositoryImpl {

    private final MongoTemplate mongoTemplate;

    public List<KeywordRankDto> keywordRank(LocalDate searchDate, Long limit) {
        return mongoTemplate.aggregate(Aggregation.newAggregation(
                Aggregation.match(Criteria
                        .where("searchDateTime").gte(searchDate.atStartOfDay()).lt(searchDate.plusDays(1).atStartOfDay())),
                Aggregation.group("keyword").count().as("count"),
                Aggregation.project("count").and("keyword").previousOperation().andExclude("_id"),
                Aggregation.sort(Sort.Direction.DESC, "count"),
                Aggregation.limit(limit)
        ), "search_logs", KeywordRankDto.class).getMappedResults();
    }

    public List<SearchKeywordSumDto> aggregateSearchLog(LocalDate searchDate) {

        return mongoTemplate.aggregate(Aggregation.newAggregation(
                Aggregation.match(Criteria.where("gender").ne(null)
                        .and("ageRange").ne(null)
                        .and("searchDateTime").gte(searchDate.atStartOfDay()).lt(searchDate.plusDays(1).atStartOfDay())),
                Aggregation.group("keyword", "gender", "ageRange").count().as("count"),
                Aggregation.project("keyword", "gender", "ageRange", "count").andExclude("_id")
        ), "search_logs", SearchKeywordSumDto.class).getMappedResults();
    }
}
