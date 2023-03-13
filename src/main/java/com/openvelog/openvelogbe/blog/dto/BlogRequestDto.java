package com.openvelog.openvelogbe.blog.dto;

import com.openvelog.openvelogbe.common.entity.Board;
import com.openvelog.openvelogbe.common.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;

public class BlogRequestDto {

    @Getter
    @AllArgsConstructor
    public static class BlogAdd {

        @NotBlank
        @Size(min = 3, max = 30, message = "제목은 3자 이상 30자 이하이여야합니다.")
        private String title;

        @NotBlank
        private String introduce;
    }

    @Getter
    @AllArgsConstructor
    public static class BlogUpdate {

        @NotBlank
        @Size(min = 3, max = 30, message = "제목은 3자 이상 30자 이하이여야합니다.")
        private String title;

        @NotBlank
        private String introduce;
    }
}
