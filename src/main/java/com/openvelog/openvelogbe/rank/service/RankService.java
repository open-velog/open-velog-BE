package com.openvelog.openvelogbe.rank.service;

import com.openvelog.openvelogbe.common.entity.enums.AgeRange;
import com.openvelog.openvelogbe.common.entity.enums.Gender;
import com.openvelog.openvelogbe.common.repository.KeywordRecordRepository;
import com.openvelog.openvelogbe.rank.dto.RankResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.Tuple;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankService {

    private final KeywordRecordRepository keywordRecordRepository;

    public List<RankResponseDto.RankKeyword> getKeywordRank(AgeRange ageRange, Gender gender, LocalDate date, Integer limit) {

        Sort sort = Sort.by("kCount").descending();

        Pageable pageable = PageRequest.of(0, limit, sort);

        List<Tuple> rankKeywords = keywordRecordRepository.getRankOfKeywordJPQL(ageRange, gender, date, pageable);

        return rankKeywords.stream().map(v -> {
            Long kCount = v.get("kCount", Long.class);
            String keyword = v.get("keyword", String.class);

            return RankResponseDto.RankKeyword.of(keyword, kCount);
        }).collect(Collectors.toList());
    }
}
