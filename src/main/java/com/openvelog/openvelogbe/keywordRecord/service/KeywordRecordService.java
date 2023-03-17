package com.openvelog.openvelogbe.keywordRecord.service;

import com.openvelog.openvelogbe.common.entity.KeywordRecord;
import com.openvelog.openvelogbe.keywordRecord.dto.KeywordRecordResponseDto;
import com.openvelog.openvelogbe.common.entity.Keyword;
import com.openvelog.openvelogbe.common.entity.enums.AgeRange;
import com.openvelog.openvelogbe.common.entity.enums.Gender;
import com.openvelog.openvelogbe.common.repository.KeywordRecordRepository;
import com.openvelog.openvelogbe.common.repository.KeywordRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class KeywordRecordService {
    private final KeywordRedisRepository redisRepository;
    private final KeywordRecordRepository keywordRecordRepository;
    @Transactional
    public List<KeywordRecordResponseDto> getKeywordRecord() {
        List<Keyword> keywords = redisRepository.findAll();
        Map<String, Map<AgeRange, Map<Gender, Map<LocalDate, Long>>>> counts = new HashMap<>();
        for (Keyword keyword : keywords) {
            String keywordStr = keyword.getKeyword();
            AgeRange ageRange = keyword.getAgeRange();
            Gender gender = keyword.getGender();
            LocalDate date = keyword.getCreatedAt();

            if (ageRange == null || gender == null) {
                continue; // Skip if Gender or AgeRange is null
            }

            String key = keywordStr + "," + ageRange + "," + gender + "," + date;

            if (counts.containsKey(key)) {
                Map<AgeRange, Map<Gender, Map<LocalDate, Long>>> ageCounts = counts.get(key);
                Map<Gender, Map<LocalDate, Long>> genderCounts = ageCounts.get(ageRange);
                Map<LocalDate, Long> dateCounts = genderCounts.get(gender);
                Long count = dateCounts.getOrDefault(date, 0L);
                dateCounts.put(date, count + 1);
            } else {
                Map<AgeRange, Map<Gender, Map<LocalDate, Long>>> ageCounts = new HashMap<>();
                Map<Gender, Map<LocalDate, Long>> genderCounts = new HashMap<>();
                Map<LocalDate, Long> dateCounts = new HashMap<>();
                dateCounts.put(date, 1L);
                genderCounts.put(gender, dateCounts);
                ageCounts.put(ageRange, genderCounts);
                counts.put(key, ageCounts);
            }
        }

        // 카운트 정보를 이용하여 KeywordCount 엔티티 생성 후 DB에 저장
        List<KeywordRecord> countEntities = new ArrayList<>();
        for (Map.Entry<String, Map<AgeRange, Map<Gender, Map<LocalDate, Long>>>> entry : counts.entrySet()) {
            String[] keyArr = entry.getKey().split(",");
            String keyword = keyArr[0];
            AgeRange ageRange = AgeRange.valueOf(keyArr[1]);
            Gender gender = Gender.valueOf(keyArr[2]);
            LocalDate date = LocalDate.parse(keyArr[3]);

            if (ageRange == null || gender == null) {
                continue; // Skip if Gender or AgeRange is null
            }

            Map<AgeRange, Map<Gender, Map<LocalDate, Long>>> ageCounts = entry.getValue();
            Map<Gender, Map<LocalDate, Long>> genderCounts = ageCounts.get(ageRange);
            Map<LocalDate, Long> dateCounts = genderCounts.get(gender);

            for (Map.Entry<LocalDate, Long> dateEntry : dateCounts.entrySet()) {
                countEntities.add(KeywordRecord.create(keyword, dateEntry.getValue(),
                        gender, ageRange, dateEntry.getKey()));
            }
        }
        List<KeywordRecord> keywordRecords = keywordRecordRepository.saveAll(countEntities);
        return keywordRecords.stream()
                .map(KeywordRecordResponseDto::of)
                .collect(Collectors.toList());
    }
}
