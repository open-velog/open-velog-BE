/*
package com.openvelog.openvelogbe.common.util;


import com.openvelog.openvelogbe.openSearch.service.OpenSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@EnableScheduling
public class BoardSearchScheduler {
    private final OpenSearchService openSearchService;
    private static final Logger logger = LoggerFactory.getLogger(BoardSearchScheduler.class);

    public BoardSearchScheduler(OpenSearchService openSearchService) {
        this.openSearchService = openSearchService;
    }

    @Scheduled(fixedRate = 90000)
    public void indexAllBoards() {
        try {
            logger.info("OpenSearchService is executed");
            openSearchService.indexAllBoards();
        } catch (IOException e) {
            logger.error("스케쥴러 실행 시 예외 발생", e);
        }
    }
}
*/
