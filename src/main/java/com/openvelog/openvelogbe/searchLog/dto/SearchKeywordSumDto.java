package com.openvelog.openvelogbe.searchLog.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchKeywordSumDto {
    private String keyword;
    private int count;
    private String gender;
    private String ageRange;
}
