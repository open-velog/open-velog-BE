package com.openvelog.openvelogbe.common.util;

import com.openvelog.openvelogbe.keywordRecord.service.KeywordRecordService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
@EnableScheduling
public class KeywordRecordScheduler {
    private final KeywordRecordService keywordRecordService;
    private static final Logger logger = LoggerFactory.getLogger(KeywordRecordScheduler.class);

    public KeywordRecordScheduler(KeywordRecordService keywordRecordService) {
        this.keywordRecordService = keywordRecordService;
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void runKeywordRecordJob() {
        logger.info("KeywordRecordScheduler is executed");
        keywordRecordService.getKeywordRecord();
    }
}
