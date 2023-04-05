package com.openvelog.openvelogbe.keyword.service;

import com.openvelog.openvelogbe.common.entity.Keyword;
import com.openvelog.openvelogbe.common.entity.SearchLog;
import com.openvelog.openvelogbe.common.repository.KeywordRedisRepository;
import com.openvelog.openvelogbe.common.repository.SearchLogRepository;
import com.openvelog.openvelogbe.keyword.dto.KeyWordResponseDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class KeywordService {

    private final KeywordRedisRepository redisRepository;

    private final SearchLogRepository searchLogRepository;

    public List<KeyWordResponseDto> getKeywords(){
        List<Keyword> keywords = redisRepository.findAll();
        List<KeyWordResponseDto>list = new ArrayList<>();
        for(Keyword findkeyword : keywords){
            list.add(KeyWordResponseDto.of(findkeyword));
        }
        return list;
    }

    public List<KeyWordResponseDto> getKeywordsByKeyword(String keyword){
        List<Keyword> keywords = redisRepository.findByKeyword(keyword);
        List<KeyWordResponseDto> list = new ArrayList<>();
        for(Keyword findkeyword : keywords){
            list.add(KeyWordResponseDto.of(findkeyword));
        }
        return list;
    }

    public List<Map<String, Object>> keywordRanking(){
        List<Map<String, Object>> result = redisRepository.findAll().stream()
                .collect(Collectors.groupingBy(Keyword::getKeyword, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("keyword", entry.getKey());
                    map.put("count", entry.getValue());
                    return map;
                })
                .sorted((map1, map2) -> Long.compare((Long) map2.get("count"), (Long) map1.get("count")))
                .collect(Collectors.toList());;
        return result;
    }


    @KafkaListener(topics = "search-log", containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    public void listen(List<ConsumerRecord<String, SearchLog>> records) {
        List<SearchLog> keywordLogList = records.stream().map(ConsumerRecord::value).collect(Collectors.toList());

        searchLogRepository.saveAll(keywordLogList);
    }
}
