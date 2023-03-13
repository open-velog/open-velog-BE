package com.openvelog.openvelogbe.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Setter
@Getter
@AllArgsConstructor
public class BoardRequestDto {

    @Getter
    @AllArgsConstructor
    public static class BoardAdd {

        @NotNull
        private Long blogId;
        @NotBlank
        @Size(min=3, max=30, message = "제목은 3자 이상 30자 이하이여야합니다.")
        private String title;
        @NotBlank
        private String content;
    }

    @Getter
    @AllArgsConstructor
    public static class BoardUpdate {

        @NotBlank
        @Size(min=3, max=30, message = "제목은 3자 이상 30자 이하이여야합니다.")
        private String title;
        @NotBlank
        private String content;
    }
}
