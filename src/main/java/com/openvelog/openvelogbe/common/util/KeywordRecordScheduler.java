package com.openvelog.openvelogbe.common.util;

import com.openvelog.openvelogbe.common.entity.KeywordRecord;
import com.openvelog.openvelogbe.common.entity.enums.AgeRange;
import com.openvelog.openvelogbe.common.entity.enums.Gender;
import com.openvelog.openvelogbe.common.repository.KeywordRecordRepository;
import com.openvelog.openvelogbe.common.repository.SearchLogRepository;
import com.openvelog.openvelogbe.keywordRecord.service.KeywordRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class KeywordRecordScheduler {
    private final KeywordRecordService keywordRecordService;

//    @Scheduled(cron = "0 0 1 * * *")
    public void runKeywordRecordJob() {
        log.info("KeywordRecordScheduler is executed");
        keywordRecordService.getKeywordRecord();
    }
}
