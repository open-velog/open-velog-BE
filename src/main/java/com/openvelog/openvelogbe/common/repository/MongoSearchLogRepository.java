package com.openvelog.openvelogbe.common.repository;

import com.openvelog.openvelogbe.common.entity.MongoSearchLog;
import com.openvelog.openvelogbe.keyword.dto.KeywordRankDto;
import com.openvelog.openvelogbe.searchLog.dto.SearchKeywordSumDto;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface MongoSearchLogRepository extends MongoRepository<MongoSearchLog, ObjectId> {
    @Aggregation(pipeline = {
            "{ $match: { searchDateTime: { $gte: {$date: ?0}, $lt: {$date: ?1}}}}",
            "{ $group: {_id: '$keyword', count: {$sum: 1}}}",
            "{ $project: { _id: 0, keyword: '$_id', count: 1}}",
            "{ $sort: {count: -1}}",
            "{ $limit: ?2}"
    })
    List<KeywordRankDto> keywordRank(LocalDateTime start, LocalDateTime end, Long limit);

    @Aggregation(pipeline = {
            "{ $match: { gender: { $ne: null }, ageRange: { $ne: null }, searchDateTime: { $gte: ?0, $lt: ?1 } } }",
            "{ $group: { _id: { keyword: '$keyword', gender: '$gender', ageRange: '$ageRange' }, count: { $sum: 1 } } }",
            "{ $project: { _id: 0, keyword: '$_id.keyword', gender: '$_id.gender', ageRange: '$_id.ageRange', count: 1 } }"})
    List<SearchKeywordSumDto> aggregateSearchKeywordSum(LocalDateTime start, LocalDateTime end);
}
