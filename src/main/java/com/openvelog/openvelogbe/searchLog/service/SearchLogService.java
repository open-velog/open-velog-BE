package com.openvelog.openvelogbe.searchLog.service;

import com.openvelog.openvelogbe.common.entity.Blog;
import com.openvelog.openvelogbe.common.entity.Keyword;
import com.openvelog.openvelogbe.common.entity.KeywordRecord;
import com.openvelog.openvelogbe.common.entity.SearchLog;
import com.openvelog.openvelogbe.common.entity.enums.AgeRange;
import com.openvelog.openvelogbe.common.entity.enums.Gender;
import com.openvelog.openvelogbe.common.repository.KeywordRecordRepository;
import com.openvelog.openvelogbe.common.repository.SearchLogRepository;
import com.openvelog.openvelogbe.keywordRecord.dto.KeywordRecordResponseDto;
import com.openvelog.openvelogbe.searchLog.dto.SearchKeywordSumDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchLogService {

    private final SearchLogRepository searchLogRepository;

    private final KeywordRecordRepository keywordRecordRepository;

    @KafkaListener(topics = "search-log", containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    public void listen(List<ConsumerRecord<String, SearchLog>> records) {
        List<SearchLog> keywordLogList = records.stream().map(ConsumerRecord::value).collect(Collectors.toList());


        searchLogRepository.saveAll(keywordLogList);
    }


    @Scheduled(cron = "0 0 1 * * *")
    public void setKeywordRecordFromSearchLog() {
        LocalDate searchDate = LocalDate.now().minusDays(1);
        List<Object[]> items = searchLogRepository.getBySearchedDate(searchDate);

        log.info("KeywordRecordScheduler is executed");

        List<KeywordRecord> keywordRecords = new ArrayList<>();
        for (Object[] result : items) {
            String keyword = (String) result[0];
            long count = ((Number) result[1]).intValue();
            String gender = (String) result[2];
            String ageRange = (String) result[3];
            KeywordRecord keywordRecord = KeywordRecord.create(keyword, count, Gender.valueOf(gender), AgeRange.valueOf(ageRange), LocalDate.now());
            keywordRecords.add(keywordRecord);
        }

        keywordRecordRepository.saveAll(keywordRecords);
    }
}
