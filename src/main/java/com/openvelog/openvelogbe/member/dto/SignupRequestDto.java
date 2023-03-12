package com.openvelog.openvelogbe.member.dto;

import com.openvelog.openvelogbe.common.entity.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
public class SignupRequestDto {
    @Pattern(regexp = "^(?=.*?[0-9])(?=.*?[a-z]).{6,16}$")
    @Schema(example = "userId", description = "/^(?=.*?[0-9])(?=.*?[a-z]).{6,16}$/")
    private String userId;

    @Pattern(regexp = "^[a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ0-9]{3,10}$")
    @Schema(example = "nickname", description = "/^[a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ0-9]{2,10}$/")
    private String username;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d$@$!%*#?&]{8,}$")
    @Schema(example = "password", description = "/^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$/")
    private String password;

    //@Email
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    @Schema(example = "user2323@gmail.com", description = "/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$/")
    private String email;

    @Pattern(regexp = "^(M|F)$")
    @Schema(example = "M", description = "/^(M|F)$/")
    private Gender gender;
    @Pattern(regexp = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$")
    @Schema(example = "2023 03 13", description = "/^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$/")
    private LocalDate birthday;


}
